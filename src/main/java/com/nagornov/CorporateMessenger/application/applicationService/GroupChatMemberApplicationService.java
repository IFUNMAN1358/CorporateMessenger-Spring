package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.HttpResponse;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserResponse;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatMemberService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatService;
import com.nagornov.CorporateMessenger.domain.service.message.UnreadMessageService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupChatMemberApplicationService {

    private final JwtService jwtService;
    private final ChatMemberService chatMemberService;
    private final ChatService chatService;
    private final UnreadMessageService unreadMessageService;
    private final UserService userService;


    @Transactional(readOnly = true)
    public List<ChatMember> getChatMembers(@NonNull Long chatId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        ChatMember chatMember = chatMemberService.getByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());

        if (chat.getHasHiddenMembers() && (!chatMember.isCreator() || !chatMember.isAdministrator())) {
            throw new ResourceBadRequestException("You dont have permission to get chat members");
        }

        return chatMemberService.findAllByChatId(chat.getId());
    }


    @Transactional(readOnly = true)
    public List<UserResponse> getAvailableUsersToAdding(@NonNull Long chatId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();
        UUID userId = authInfo.getUserIdAsUUID();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(userId);

        List<UUID> companionIds =
                cassandraPrivateChatDomainService.getAllAvailableByAllUserId(userId)
                        .stream().map(privateChat ->
                                privateChat.getFirstUserId().equals(userId) ?
                                        privateChat.getSecondUserId() : privateChat.getFirstUserId()
                        ).toList();

        List<UUID> memberIds =
                cassandraGroupChatMemberDomainService.getAllByChatId(groupChat.getId())
                        .stream().map(
                                ChatMember::getUserId
                        ).toList();

        return companionIds
                .stream().filter(companionId -> !memberIds.contains(companionId))
                .map(companionId -> {

                    User userToAdd = userService.getById(companionId);
                    return new UserResponse(userToAdd);

                }).toList();
    }


    @Transactional
    public HttpResponse addMembersToGroupChat(@NonNull Long chatId, @NonNull UserIdsRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        for (String requestUserId : request.getUserIds()) {

            User user = userService.getById(
                    UUID.fromString(requestUserId)
            );

            ChatMember chatMember = new ChatMember(
                UUID.randomUUID(),
                groupChat.getId(),
                user.getId(),
                Instant.now()
            );
            cassandraGroupChatMemberDomainService.save(chatMember);

            UnreadMessage unreadMessage = new UnreadMessage(groupChat.getId(), user.getId(), 0);
            cassandraUnreadMessageDomainService.save(unreadMessage);
        }

        return new HttpResponse("User/s added to group chat", 201);
    }


    @Transactional
    public HttpResponse deleteMembersFromGroupChat(@NonNull Long chatId, @NonNull UserIdsRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );
        groupChat.validateOwnerIdOwnership(authInfo.getUserIdAsUUID());

        for (String requestUserId : request.getUserIds()) {

            ChatMember member = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                    groupChat.getId(), UUID.fromString(requestUserId)
            );
            cassandraGroupChatMemberDomainService.delete(member);

            UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                    groupChat.getId(), member.getUserId()
            );
            cassandraUnreadMessageDomainService.delete(unreadMessage);
        }

        return new HttpResponse("Members of group chat has been deleted", 200);
    }


    @Transactional
    public HttpResponse leaveFromGroupChat(@NonNull Long chatId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        GroupChat groupChat = cassandraGroupChatDomainService.getById(
                UUID.fromString(chatId)
        );

        ChatMember member = cassandraGroupChatMemberDomainService.getByChatIdAndUserId(
                groupChat.getId(), authInfo.getUserIdAsUUID()
        );

        if (member.getUserId().equals(groupChat.getOwnerId())) {
            throw new RuntimeException("Owner of the group chat can't leave it");
        }

        cassandraGroupChatMemberDomainService.delete(member);

        UnreadMessage unreadMessage = cassandraUnreadMessageDomainService.getByChatIdAndUserId(
                groupChat.getId(), member.getUserId()
        );
        cassandraUnreadMessageDomainService.delete(unreadMessage);

        return new HttpResponse("You left the group chat", 200);
    }

}
