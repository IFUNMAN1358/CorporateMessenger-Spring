package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("private_chats_by_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CassandraPrivateChatByIdEntity {

    @PrimaryKey
    private UUID id;

    @Column("first_user_id")
    private UUID firstUserId;

    @Column("second_user_id")
    private UUID secondUserId;

    @Column("last_message_id")
    private UUID lastMessageId;

    @Column("created_at")
    private Instant createdAt;

    @Column("is_available")
    private Boolean isAvailable = true;
}