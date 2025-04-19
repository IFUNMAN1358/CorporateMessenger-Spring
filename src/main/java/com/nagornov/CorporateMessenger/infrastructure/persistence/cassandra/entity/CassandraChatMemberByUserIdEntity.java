package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatMemberByUserIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

@Table("chat_members_by_user_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraChatMemberByUserIdEntity implements Serializable {

    @PrimaryKey
    private CassandraChatMemberByUserIdKey key;

    @Column("status")
    private String status;

    @Column("joined_at")
    private Instant joinedAt;

    @Column("updated_at")
    private Instant updatedAt;

}
