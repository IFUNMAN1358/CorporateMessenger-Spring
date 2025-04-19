package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraUnreadMessageByChatIdAndUserIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Table("unread_messages_by_chat_id_and_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraUnreadMessageByChatIdAndUserIdEntity implements Serializable {

    @PrimaryKey
    private CassandraUnreadMessageByChatIdAndUserIdKey key;

    @Column("unread_count")
    private Integer unreadCount;

}
