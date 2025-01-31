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

@Table("group_chats_by_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraGroupChatByIdEntity {

    @PrimaryKey
    private UUID id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("owner_id")
    private UUID ownerId;

    @Column("file_path")
    private String filePath;

    @Column("last_message_id")
    private UUID lastMessageId;

    @Column("is_public")
    private Boolean isPublic;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("created_at")
    private Instant createdAt;
}
