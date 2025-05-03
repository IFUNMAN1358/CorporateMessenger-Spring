package com.nagornov.CorporateMessenger.domain.service.message;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraChatMemberRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository.CassandraUnreadMessageRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnreadMessageService {

    private final CassandraUnreadMessageRepository cassandraUnreadMessageRepository;
    private final CassandraChatMemberRepository cassandraChatMemberRepository;

    public UnreadMessage create(@NonNull Long chatId, @NonNull UUID userId) {
        UnreadMessage unreadMessage = new UnreadMessage(chatId, userId, 0);
        return cassandraUnreadMessageRepository.save(unreadMessage);
    }

    public UnreadMessage update(@NonNull UnreadMessage unreadMessage) {
        return cassandraUnreadMessageRepository.save(unreadMessage);
    }

    public void delete(@NonNull UnreadMessage unreadMessage) {
        cassandraUnreadMessageRepository.delete(unreadMessage);
    }

    public void deleteByChatIdAndUserId(@NonNull Long chatId, @NonNull UUID userId) {
        cassandraUnreadMessageRepository.deleteByChatIdAndUserId(chatId, userId);
    }

    public UnreadMessage getByChatIdAndUserId(@NonNull Long chatId, @NonNull UUID userId) {
        return cassandraUnreadMessageRepository
                .findByChatIdAndUserId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Unread message with chatId and userId not found"));
    }

    public void incrementUnreadMessageCountForOther(@NonNull Chat chat, @NonNull User user) {
        if (chat.isPrivateChat()) {

            ChatMember partnerChatMember = cassandraChatMemberRepository.findAllByChatId(chat.getId())
                    .stream().filter(cm -> !cm.getUserId().equals(user.getId())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Partner for private chat not found"));

            UnreadMessage unreadMessage = getByChatIdAndUserId(chat.getId(), partnerChatMember.getUserId());
            unreadMessage.incrementUnreadCount();
            cassandraUnreadMessageRepository.save(unreadMessage);

        } else if (chat.isGroupChat()) {

            List<ChatMember> members = cassandraChatMemberRepository.findAllByChatId(chat.getId());
            for (ChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = getByChatIdAndUserId(
                            chat.getId(), member.getUserId()
                    );
                    unreadMessage.incrementUnreadCount();
                    cassandraUnreadMessageRepository.save(unreadMessage);
                }
            }

        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while incrementing the unread message counter"
            );
        }
    }

    public void decrementUnreadMessageCountForOther(@NonNull Chat chat, @NonNull User user) {
        if (chat.isPrivateChat()) {

            ChatMember partnerChatMember = cassandraChatMemberRepository.findAllByChatId(chat.getId())
                    .stream().filter(cm -> !cm.getUserId().equals(user.getId())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Partner for private chat not found"));

            UnreadMessage unreadMessage = getByChatIdAndUserId(chat.getId(), partnerChatMember.getUserId());
            unreadMessage.decrementUnreadCount();
            cassandraUnreadMessageRepository.save(unreadMessage);

        } else if (chat.isGroupChat()) {

            List<ChatMember> members = cassandraChatMemberRepository.findAllByChatId(chat.getId());
            for (ChatMember member : members) {
                if (!member.getUserId().equals(user.getId())) {
                    UnreadMessage unreadMessage = getByChatIdAndUserId(
                            chat.getId(), member.getUserId()
                    );
                    unreadMessage.decrementUnreadCount();
                    cassandraUnreadMessageRepository.save(unreadMessage);
                }
            }

        } else {
            throw new RuntimeException(
                "Chat type is unknown or an error occurred while decrementing the unread message counter"
            );
        }
    }

    public void incrementUnreadMessageCountForUser(@NonNull Chat chatInterface, @NonNull User user) {
        UnreadMessage unreadMessage = getByChatIdAndUserId(chatInterface.getId(), user.getId());
        unreadMessage.incrementUnreadCount();
        update(unreadMessage);
    }

    public void decrementUnreadMessageCountForUser(@NonNull Chat chat, @NonNull User user) {
        final UnreadMessage unreadMessage = getByChatIdAndUserId(chat.getId(), user.getId());
        unreadMessage.decrementUnreadCount();
        update(unreadMessage);
    }

}
