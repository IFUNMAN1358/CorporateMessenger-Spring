package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatWithChatPhotoResponse;
import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatIdRequest;
import com.nagornov.CorporateMessenger.application.dto.model.chat.CreateGroupChatRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserIdRequest;
import com.nagornov.CorporateMessenger.domain.enums.model.ChatMemberStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.user.UserPhotoService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatMemberService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatPhotoService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatService;
import com.nagornov.CorporateMessenger.domain.service.chat.PrivateChatService;
import com.nagornov.CorporateMessenger.domain.service.message.MessageService;
import com.nagornov.CorporateMessenger.domain.service.message.UnreadMessageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatApplicationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final ChatPhotoService chatPhotoService;
    private final ChatMemberService chatMemberService;
    private final UnreadMessageService unreadMessageService;
    private final ChatService chatService;
    private final PrivateChatService privateChatService;
    private final MessageService messageService;


    @Transactional
    public ChatWithChatPhotoResponse getOrCreatePrivateChat(@NonNull UserIdRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        User user = userService.getById(authInfo.getUserIdAsUUID());
        User partner = userService.getById(request.getUserId());

        String userPairHash = PrivateChat.generateUserPairHash(user.getId(), partner.getId());
        Optional<PrivateChat> optPrivateChat = privateChatService.findByUserPairHash(userPairHash);

        Chat chat;
        if (optPrivateChat.isPresent()) {

            chat = chatService.getById(optPrivateChat.get().getChatId());

        } else {

            chat = chatService.createPrivate();

            privateChatService.create(userPairHash, chat.getId());

            unreadMessageService.create(chat.getId(), user.getId());
            unreadMessageService.create(chat.getId(), partner.getId());

            chatMemberService.create(chat.getId(), user.getId(), ChatMemberStatus.MEMBER);
            chatMemberService.create(chat.getId(), partner.getId(), ChatMemberStatus.MEMBER);
        }

        return new ChatWithChatPhotoResponse().forPrivateChat(
                chat,
                partner,
                userPhotoService.findMainByUserId(partner.getId()),
                messageService.findLastByChatId(chat.getId()),
                unreadMessageService.getByChatIdAndUserId(chat.getId(), user.getId())
        );
    }


    @Transactional
    public ChatWithChatPhotoResponse createGroupChat(@NonNull CreateGroupChatRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        Chat chat = chatService.createGroup(request.getTitle());

        if (request.getFile() != null) {
            chatPhotoService.upload(chat.getId(), request.getFile());
        }

        chatMemberService.create(chat.getId(), user.getId(), ChatMemberStatus.CREATOR);

        unreadMessageService.create(chat.getId(), user.getId());

        return new ChatWithChatPhotoResponse().forGroupChat(
                chat,
                chatPhotoService.findMainByChatId(chat.getId()),
                messageService.findLastByChatId(chat.getId()),
                unreadMessageService.getByChatIdAndUserId(chat.getId(), user.getId())
        );
    }


    // common
    @Transactional(readOnly = true)
    public ChatWithChatPhotoResponse getChat(@NonNull Long chatId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        ChatMember userChatMember = chatMemberService.getByChatIdAndUserId(chatId, user.getId());

        Chat chat = chatService.getById(userChatMember.getChatId());

        if (chat.isPrivateChat()) {

            ChatMember partnerChatMember = chatMemberService.findAllByChatId(chat.getId())
                    .stream().filter(cm -> !cm.getUserId().equals(user.getId())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Partner for private chat not found"));

            User partner = userService.getById(partnerChatMember.getUserId());

            return new ChatWithChatPhotoResponse().forPrivateChat(
                    chat,
                    partner,
                    userPhotoService.findMainByUserId(partner.getId()),
                    messageService.findLastByChatId(chat.getId()),
                    unreadMessageService.getByChatIdAndUserId(chat.getId(), user.getId())
            );

        } else if (chat.isGroupChat()) {
            return new ChatWithChatPhotoResponse().forGroupChat(
                    chat,
                    chatPhotoService.findMainByChatId(chat.getId()),
                    messageService.findLastByChatId(chat.getId()),
                    unreadMessageService.getByChatIdAndUserId(chat.getId(), user.getId())
            );
        } else {
            throw new ResourceConflictException("Chat[chatId=%s] error while try get".formatted(chatId));
        }
    }


    @Transactional(readOnly = true)
    public List<ChatWithChatPhotoResponse> getAllChats() {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        List<ChatMember> userChatMembers = chatMemberService.findAllByUserId(user.getId());

        List<ChatWithChatPhotoResponse> response = new ArrayList<>();
        for (ChatMember userChatMember : userChatMembers) {

            Chat chat = chatService.getById(userChatMember.getChatId());

            if (chat.isPrivateChat()) {

                ChatMember partnerChatMember = chatMemberService.findAllByChatId(chat.getId())
                        .stream().filter(cm -> !cm.getUserId().equals(user.getId())).findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Partner for private chat not found"));

                User partner = userService.getById(partnerChatMember.getUserId());

                response.add(
                        new ChatWithChatPhotoResponse().forPrivateChat(
                                chat,
                                partner,
                                userPhotoService.findMainByUserId(partner.getId()),
                                messageService.findLastByChatId(chat.getId()),
                                unreadMessageService.getByChatIdAndUserId(chat.getId(), user.getId())
                        )
                );

            } else if (chat.isGroupChat()) {
                response.add(
                        new ChatWithChatPhotoResponse().forGroupChat(
                                chat,
                                chatPhotoService.findMainByChatId(chat.getId()),
                                messageService.findLastByChatId(chat.getId()),
                                unreadMessageService.getByChatIdAndUserId(chat.getId(), user.getId())
                        )
                );
            } else {
                throw new ResourceConflictException("Chat[chatId=%s] error while try get".formatted(chat.getId()));
            }
        }
        return response.stream().sorted(
                Comparator.comparing(
                        c -> Objects.requireNonNullElse(
                                c.getLastMessage() != null ? c.getLastMessage().getCreatedAt() : c.getChat().getCreatedAt(),
                                c.getChat().getCreatedAt()
                        ),
                        Comparator.reverseOrder()
                )
        ).toList();
    }


    @Transactional
    public void deleteChat(@NonNull ChatIdRequest request) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();
        User user = userService.getById(authInfo.getUserIdAsUUID());

        ChatMember userChatMember = chatMemberService.getByChatIdAndUserId(request.getChatId(), user.getId());

        Chat chat = chatService.getById(userChatMember.getChatId());

        if (chat.isPrivateChat()) {
            ChatMember partnerChatMember = chatMemberService.findAllByChatId(chat.getId())
                    .stream().filter(cm -> !cm.getUserId().equals(userChatMember.getUserId()))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Partner for private chat not found"));

            chatService.delete(chat);

            String userPairHash = PrivateChat.generateUserPairHash(userChatMember.getUserId(), partnerChatMember.getUserId());
            privateChatService.deleteByUserPairHash(userPairHash);

            chatMemberService.delete(userChatMember);
            chatMemberService.delete(partnerChatMember);

            unreadMessageService.deleteByChatIdAndUserId(chat.getId(), user.getId());
            unreadMessageService.deleteByChatIdAndUserId(chat.getId(), partnerChatMember.getUserId());


        } else if (chat.isGroupChat() && userChatMember.isCreator()) {

            chatService.delete(chat);

            List<ChatMember> chatMembers = chatMemberService.findAllByChatId(chat.getId());
            for (ChatMember chatMember : chatMembers) {
                unreadMessageService.deleteByChatIdAndUserId(chat.getId(), chatMember.getUserId());
                chatMemberService.delete(chatMember);
            }

            List<ChatPhoto> chatPhotos = chatPhotoService.findAllByChatId(chat.getId());
            for (ChatPhoto chatPhoto : chatPhotos) {
                chatPhotoService.delete(chatPhoto);
            }
        }
    }

}
