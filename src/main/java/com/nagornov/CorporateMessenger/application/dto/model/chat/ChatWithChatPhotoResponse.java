package com.nagornov.CorporateMessenger.application.dto.model.chat;

import com.nagornov.CorporateMessenger.application.dto.model.user.UserPhotoResponse;
import com.nagornov.CorporateMessenger.application.dto.model.user.UserResponse;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.model.interfaces.ChatPhotoResponseInterface;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
public class ChatWithChatPhotoResponse {

    private ChatResponse chat;
    private UserResponse partner;
    private ChatMember chatMember;
    private ChatPhotoResponseInterface photo;
    private Message lastMessage;
    private UnreadMessage unreadMessage;

    public ChatWithChatPhotoResponse forPrivateChat(
            @NonNull Chat chat,
            @NonNull User partner,
            @NonNull Optional<UserPhoto> userPhoto,
            @NonNull Optional<Message> lastMessage,
            @NonNull UnreadMessage unreadMessage
    ) {
        this.chat = new ChatResponse(chat);
        this.partner = new UserResponse(partner);
        this.chatMember = null;
        userPhoto.ifPresent(up -> this.photo = new UserPhotoResponse(up));
        lastMessage.ifPresent(m -> this.lastMessage = m);
        this.unreadMessage = unreadMessage;
        return this;
    }

    public ChatWithChatPhotoResponse forGroupChat(
            @NonNull Chat chat,
            @NonNull ChatMember chatMember,
            @NonNull Optional<ChatPhoto> chatPhoto,
            @NonNull Optional<Message> lastMessage,
            @NonNull UnreadMessage unreadMessage
    ) {
        this.chat = new ChatResponse(chat);
        this.partner = null;
        this.chatMember = chatMember;
        chatPhoto.ifPresent(cp -> this.photo = new ChatPhotoResponse(cp));
        lastMessage.ifPresent(m -> this.lastMessage = m);
        this.unreadMessage = unreadMessage;
        return this;
    }

}