package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.domain.enums.model.ChatType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

@Table("chats_by_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraChatByIdEntity implements Serializable {

    @PrimaryKey("id")
    private Long id;

    @Column("type")
    @Enumerated(EnumType.STRING)
    private ChatType type;

    @Column("username")
    private String username;

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
