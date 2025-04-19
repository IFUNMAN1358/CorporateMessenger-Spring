package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Table("private_chats_by_user_pair_hash")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraPrivateChatByUserPairHashEntity implements Serializable {

    @PrimaryKey("user_pair_hash")
    private String userPairHash;

    @Column("chat_id")
    private Long chatId;

}
