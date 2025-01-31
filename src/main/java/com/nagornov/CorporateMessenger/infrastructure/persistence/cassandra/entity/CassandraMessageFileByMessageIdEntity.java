package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity;

import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.key.CassandraMessageFileByMessageIdKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("message_files_by_message_id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CassandraMessageFileByMessageIdEntity {

    @PrimaryKey
    private CassandraMessageFileByMessageIdKey key;

    @Column("file_name")
    private String fileName;

    @Column("file_path")
    private String filePath;

    @Column("content_type")
    private String contentType;

    @Column("created_at")
    private Instant createdAt;

}
