package com.nagornov.CorporateMessenger.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Chat {

    // username = 5-32
    // bio = 0-70
    // firstName = 1-64
    // lastName = 0-64
    // description = 0-255

    private Long id; //                  ALL | primaryKey=true, unique=true, nullable=false, updatable=false
    private String type; //              ALL | length=16, unique=false, nullable=false, updatable=false
    private String username; //          GROUP | length=5-32, unique=true, nullable=true, updatable=true
    private String title; //             GROUP | length=128, unique=false, nullable=true, updatable=true
    private String description; //       GROUP | length=255, unique=false, nullable=true, updatable=true
    private String inviteLink; //        GROUP | length=32, unique=true, nullable=true, updatable=true
    private Boolean joinByRequest; //    GROUP | nullable=true, updatable=true
    private Boolean hasHiddenMembers; // GROUP | nullable=true, updatable=true
    private LocalDateTime createdAt; //  ALL | nullable=false, updatable=false
    private LocalDateTime updatedAt; //  ALL | nullable=false, updatable=true

    // for DTO
    //private String firstName; //         PRIVATE | length = 64, unique=false, nullable=true, updatable=true
    //private String lastName; //          PRIVATE | length = 64, unique=false, nullable=true, updatable=true
    //private ChatPhoto photo; //          ALL | unique=true, nullable=true, updatable=true

}
