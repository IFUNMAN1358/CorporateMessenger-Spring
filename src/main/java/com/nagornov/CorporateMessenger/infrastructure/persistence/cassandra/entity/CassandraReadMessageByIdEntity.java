package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("read_messages_by_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CassandraReadMessageByIdEntity {

    @PrimaryKey
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("chat_id")
    private UUID chatId;

    @Column("message_id")
    private UUID messageId;

}
