package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraReadMessageByMessageIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

@Table("read_messages_by_message_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraReadMessageByMessageIdEntity implements Serializable {

    @PrimaryKey
    private CassandraReadMessageByMessageIdKey key;

    @Column("user_id")
    private UUID userId;

}
