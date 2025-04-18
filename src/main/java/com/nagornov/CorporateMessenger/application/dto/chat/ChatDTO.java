package com.nagornov.CorporateMessenger.application.dto.chat;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatPhoto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatDTO {

    private Long id;
    private String type;
    private String username;
    private String title;
    private String firstName;
    private String lastName;
    private ChatPhoto photo;
    private String description;
    private String inviteLink;
    private Boolean joinByRequest;
    private Boolean hasHiddenMembers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
