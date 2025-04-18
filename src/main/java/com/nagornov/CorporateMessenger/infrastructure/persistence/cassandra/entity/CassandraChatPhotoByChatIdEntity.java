package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraChatPhotoByChatIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("chat_photos_by_chat_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraChatPhotoByChatIdEntity {

    @PrimaryKey
    private CassandraChatPhotoByChatIdKey key;

    @Column("file_name")
    private String fileName;

    @Column("small_file_path")
    private String smallFilePath;

    @Column("big_file_path")
    private String bigFilePath;

    @Column("created_at")
    private Instant createdAt;

}
