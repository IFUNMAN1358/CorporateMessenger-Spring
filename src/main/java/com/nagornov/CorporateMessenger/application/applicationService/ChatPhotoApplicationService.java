package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.common.FileRequest;
import com.nagornov.CorporateMessenger.application.dto.model.chat.ChatPhotoResponse;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatMemberService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatPhotoService;
import com.nagornov.CorporateMessenger.domain.service.chat.ChatService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatPhotoApplicationService {

    private final JwtService jwtService;
    private final ChatService chatService;
    private final ChatMemberService chatMemberService;
    private final ChatPhotoService chatPhotoService;


    @Transactional
    public ChatPhotoResponse uploadGroupChatPhoto(@NonNull Long chatId, @NonNull FileRequest request) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chatId, authInfo.getUserIdAsUUID());
        authChatMember.ensureIsCreator();

        Optional<ChatPhoto> optCurrentMainChatPhoto = chatPhotoService.findMainByChatId(chat.getId());
        if (optCurrentMainChatPhoto.isPresent()) {
            optCurrentMainChatPhoto.get().unmarkAsMain();
            chatPhotoService.update(optCurrentMainChatPhoto.get());
        }

        ChatPhoto newChatPhoto = chatPhotoService.upload(chatId, request.getFile());
        return new ChatPhotoResponse(newChatPhoto);
    }


    @Transactional(readOnly = true)
    public List<ChatPhotoResponse> getAllGroupChatPhotosByChatId(@NonNull Long chatId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        chatMemberService.getByChatIdAndUserId(chatId, authInfo.getUserIdAsUUID());

        return chatPhotoService.findAllByChatId(chatId).stream().map(ChatPhotoResponse::new).toList();
    }


    @Transactional(readOnly = true)
    public Resource downloadMainGroupChatPhotoByChatId(@NonNull Long chatId, @NonNull String size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        chatMemberService.getByChatIdAndUserId(chatId, authInfo.getUserIdAsUUID());

        Optional<ChatPhoto> optMainChatPhoto = chatPhotoService.findMainByChatId(chat.getId());

        if (optMainChatPhoto.isEmpty()) {
            return null;
        }

        return chatPhotoService.download(optMainChatPhoto.get(), size);
    }


    @Transactional(readOnly = true)
    public Resource downloadGroupChatPhotoByChatIdAndPhotoId(@NonNull Long chatId, @NonNull UUID photoId, @NonNull String size) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        chatMemberService.getByChatIdAndUserId(chatId, authInfo.getUserIdAsUUID());

        ChatPhoto chatPhoto = chatPhotoService.getByIdAndChatId(photoId, chatId);

        return chatPhotoService.download(chatPhoto, size);
    }


    @Transactional
    public ChatPhotoResponse setMainGroupChatPhotoByChatIdAndPhotoId(@NonNull Long chatId, @NonNull UUID photoId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chatId, authInfo.getUserIdAsUUID());
        authChatMember.ensureIsCreator();

        ChatPhoto targetChatPhoto = chatPhotoService.getByIdAndChatId(photoId, chatId);
        ChatPhoto currentMainChatPhoto = chatPhotoService.getMainByChatId(chatId);
        targetChatPhoto.markAsMain();
        currentMainChatPhoto.unmarkAsMain();
        chatPhotoService.updateAll(List.of(targetChatPhoto, currentMainChatPhoto));

        return new ChatPhotoResponse(targetChatPhoto);
    }


    @Transactional
    public void deleteGroupChatPhotoByChatIdAndPhotoId(@NonNull Long chatId, @NonNull UUID photoId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        Chat chat = chatService.getById(chatId);
        chat.ensureIsGroupChat();

        ChatMember authChatMember = chatMemberService.getByChatIdAndUserId(chatId, authInfo.getUserIdAsUUID());
        authChatMember.ensureIsCreator();

        ChatPhoto targetChatPhoto = chatPhotoService.getByIdAndChatId(photoId, chatId);

        if (targetChatPhoto.getIsMain()) {
            for (ChatPhoto chatPhoto : chatPhotoService.findAllByChatId(chatId)) {
                if (!chatPhoto.getId().equals(targetChatPhoto.getId())) {
                    chatPhoto.markAsMain();
                    chatPhotoService.update(chatPhoto);
                    break;
                }
            }
        }
        chatPhotoService.delete(targetChatPhoto);
    }

}
