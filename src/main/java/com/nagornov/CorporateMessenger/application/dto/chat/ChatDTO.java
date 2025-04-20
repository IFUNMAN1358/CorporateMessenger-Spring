package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import com.nagornov.CorporateMessenger.domain.model.interfaces.ChatPhotoInterface;
import com.nagornov.CorporateMessenger.domain.model.message.Message;
import com.nagornov.CorporateMessenger.domain.model.message.UnreadMessage;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import lombok.Data;

import java.time.Instant;

@Data
public class ChatDTO {

    private Long id; // Chat
    private String type; // Chat
    private String username; // Chat
    private String title; // Chat
    private String firstName; // User
    private String lastName; // User
    private ChatPhotoInterface photo; // ChatPhoto | UserPhoto
    private Message lastMessage; // Message
    private UnreadMessage unreadMessage; // UnreadMessage
    private String description; // Chat
    private String inviteLink; // Chat
    private Boolean joinByRequest; // Chat
    private Boolean hasHiddenMembers; // Chat
    private Instant createdAt; // Chat
    private Instant updatedAt; // Chat

    public ChatDTO forPrivateChat(
            Chat chat, User partner, UserPhoto partnerPhoto, Message lastMessage, UnreadMessage unreadMessage
    ) {
        this.id = chat.getId();
        this.type = chat.getType();
        this.username = null;
        this.title = null;
        this.firstName = partner.getFirstName();
        this.lastName = partner.getLastName();
        this.photo = partnerPhoto;
        this.lastMessage = lastMessage;
        this.unreadMessage = unreadMessage;
        this.description = null;
        this.inviteLink = null;
        this.joinByRequest = null;
        this.hasHiddenMembers = null;
        this.createdAt = chat.getCreatedAt();
        this.updatedAt = chat.getUpdatedAt();
        return this;
    }

    public ChatDTO forGroupChat(
            Chat chat, ChatPhoto chatPhoto, Message lastMessage, UnreadMessage unreadMessage
    ) {
        this.id = chat.getId();
        this.type = chat.getType();
        this.username = null;
        this.title = chat.getTitle();
        this.firstName = null;
        this.lastName = null;
        this.photo = chatPhoto;
        this.lastMessage = lastMessage;
        this.unreadMessage = unreadMessage;
        this.description = chat.getDescription();
        this.inviteLink = chat.getInviteLink();
        this.joinByRequest = chat.getJoinByRequest();
        this.hasHiddenMembers = chat.getHasHiddenMembers();
        this.createdAt = chat.getCreatedAt();
        this.updatedAt = chat.getUpdatedAt();
        return this;
    }

}