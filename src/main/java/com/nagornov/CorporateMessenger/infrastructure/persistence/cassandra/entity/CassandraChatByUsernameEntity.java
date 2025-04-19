package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatByUsernameKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

@Table("chats_by_username")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraChatByUsernameEntity implements Serializable {

    @PrimaryKey
    private CassandraChatByUsernameKey key;

    @Column("type")
    private String type;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("invite_link")
    private String inviteLink;

    @Column("join_by_request")
    private Boolean joinByRequest;

    @Column("has_hidden_members")
    private Boolean hasHiddenMembers;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

}
