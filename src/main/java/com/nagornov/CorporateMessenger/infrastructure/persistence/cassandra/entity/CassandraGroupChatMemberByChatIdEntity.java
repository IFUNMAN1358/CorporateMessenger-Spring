package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraGroupChatMemberByChatIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("group_chat_members_by_chat_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraGroupChatMemberByChatIdEntity {

    @PrimaryKey
    private CassandraGroupChatMemberByChatIdKey key;

    @Column("user_id")
    private UUID userId;

    @Column("user_first_name")
    private String userFirstName;

    @Column("joined_at")
    private Instant joinedAt;

}
