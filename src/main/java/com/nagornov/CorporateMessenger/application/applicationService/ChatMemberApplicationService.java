package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.user.UserIdsRequest;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserWithUserPhotoResponse;
import com.nagornov.CorporateMessenger.domain.enums.model.ChatMemberStatus;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.user.Contact;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatMemberService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatService;
import com.nagornov.CorporateMessenger.domain.service.message.UnreadMessageService;
import com.nagornov.CorporateMessenger.domain.service.user.ContactService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMemberApplicationService {

    private final JwtService jwtService;
    private final ChatMemberService chatMemberService;
    private final ChatService chatService;
    private final UnreadMessageService unreadMessageService;
    private final UserService userService;
    private final ContactService contactService;


    @Transactional(readOnly = true)
    public List<UserWithUserPhotoResponse> getGroupChatMembers(@NonNull Long chatId, int page, int size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember chatMember = chatMemberService.getByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());

        if (chat.getHasHiddenMembers() && (!chatMember.isCreator() || !chatMember.isAdministrator())) {
            throw new ResourceBadRequestException("You dont have permission to get chat members");
        }

        List<ChatMember> chatMembers = chatMemberService.findAllByChatId(chat.getId(), page, size);

        return userService.findAllWithMainUserPhotoByIds(
                    chatMembers.stream().map(ChatMember::getUserId).toList()
                )
                .stream().map(dto -> new UserWithUserPhotoResponse(
                        dto.getUser(),
                        dto.getUserPhoto()
                )).toList();
    }


    @Transactional(readOnly = true)
    public List<UserWithUserPhotoResponse> getAvailableUsersToAdding(@NonNull Long chatId, int page, int size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());
        authChatMember.ensureIsCreator();

        Page<Contact> authContacts = contactService.findAllByUserId(authInfo.getUserIdAsUUID(), page, size); // 20
        List<ChatMember> chatMembers = chatMemberService.findAllByChatId(chat.getId(), page, size); // 20

        Set<UUID> chatMemberIds = chatMembers.stream()
        .map(ChatMember::getUserId)
        .collect(Collectors.toSet());

        List<UUID> availableContactsUserIdsToAdding = authContacts.getContent().stream()
        .map(Contact::getContactId)
        .filter(contactId -> !chatMemberIds.contains(contactId))
        .collect(Collectors.toList());

        return userService.findAllWithMainUserPhotoByIds(availableContactsUserIdsToAdding)
                .stream().map(dto -> new UserWithUserPhotoResponse(
                        dto.getUser(),
                        dto.getUserPhoto()
                )).toList();
    }


    @Transactional
    public void addMembersToGroupChat(@NonNull Long chatId, @NonNull UserIdsRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());
        authChatMember.ensureIsCreator();

        userService.ensureExistsAllByIds(request.getUserIds());
        try {
            chatMemberService.createAll(chat.getId(), request.getUserIds(), ChatMemberStatus.MEMBER);
            unreadMessageService.createAll(chat.getId(), request.getUserIds());
        } catch (Exception e) {
            throw new ResourceBadRequestException("One of user IDs is already a member of the chat");
        }
    }


    @Transactional
    public void deleteMembersFromGroupChat(@NonNull Long chatId, @NonNull UserIdsRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());
        authChatMember.ensureIsCreator();

        userService.ensureExistsAllByIds(request.getUserIds());
        try {
            chatMemberService.deleteAllByChatIdAndUserIds(chat.getId(),request.getUserIds());
            unreadMessageService.deleteAllByUserIds(chat.getId(), request.getUserIds());
        } catch (Exception e) {
            throw new ResourceBadRequestException("One of user IDs is not in the chat");
        }
    }


    @Transactional
    public void leaveFromGroupChat(@NonNull Long chatId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());

        if (authChatMember.isCreator()) {
            throw new RuntimeException("Creator of chat can't leave it");
        }

        try {
            chatMemberService.delete(authChatMember);
            unreadMessageService.deleteByChatIdAndUserId(chat.getId(), authInfo.getUserIdAsUUID());
        } catch (Exception e) {
            throw new ResourceBadRequestException("You are not in the chat");
        }
    }

}
