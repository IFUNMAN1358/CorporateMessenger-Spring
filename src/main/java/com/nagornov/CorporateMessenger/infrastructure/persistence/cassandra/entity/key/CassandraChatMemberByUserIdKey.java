package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@PrimaryKeyClass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraChatMemberByUserIdKey implements Serializable {

    @PrimaryKeyColumn(name = "chat_id", type = PrimaryKeyType.CLUSTERED)
    private Long chatId;

    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

}
