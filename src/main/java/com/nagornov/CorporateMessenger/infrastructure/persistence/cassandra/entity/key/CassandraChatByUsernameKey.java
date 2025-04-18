package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraChatByUsernameKey {

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private Long id;

    @PrimaryKeyColumn(name = "username", type = PrimaryKeyType.PARTITIONED)
    private String username;
}
