package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraMessageByChatIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("messages_by_chat_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraMessageByChatIdEntity {

    @PrimaryKey
    private CassandraMessageByChatIdKey key;

    @Column("sender_id")
    private UUID senderId;

    @Column("sender_username")
    private String senderUsername;

    @Column("content")
    private String content;

    @Column("has_files")
    private Boolean hasFiles;

    @Column("is_changed")
    private Boolean isChanged;

    @Column("is_read")
    private Boolean isRead;

    @Column("created_at")
    private Instant createdAt;

}
