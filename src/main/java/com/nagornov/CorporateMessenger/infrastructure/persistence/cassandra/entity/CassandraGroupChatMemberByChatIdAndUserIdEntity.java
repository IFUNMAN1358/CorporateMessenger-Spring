package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraGroupChatMemberByChatIdAndUserIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("group_chat_members_by_chat_id_and_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraGroupChatMemberByChatIdAndUserIdEntity {

    @PrimaryKey
    private CassandraGroupChatMemberByChatIdAndUserIdKey key;

    @Column("joined_at")
    private Instant joinedAt;

}
