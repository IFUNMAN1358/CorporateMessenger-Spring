package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatMemberByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatMemberByUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraChatMemberRepository {

    private final SpringDataCassandraChatMemberByChatIdRepository springDataCassandraChatMemberByChatIdRepository;
    private final SpringDataCassandraChatMemberByUserIdRepository springDataCassandraChatMemberByUserIdRepository;
    private final CassandraChatMemberMapper cassandraChatMemberMapper;

    public ChatMember save(ChatMember chatMember) {
        springDataCassandraChatMemberByChatIdRepository.save(
                cassandraChatMemberMapper.toChatMemberByChatIdEntity(chatMember)
        );
        CassandraChatMemberByUserIdEntity entity =
                springDataCassandraChatMemberByUserIdRepository.save(
                        cassandraChatMemberMapper.toChatMemberByUserIdEntity(chatMember)
                );
        return cassandraChatMemberMapper.toDomain(entity);
    }

    public void delete(ChatMember chatMember) {
        springDataCassandraChatMemberByChatIdRepository.delete(
                cassandraChatMemberMapper.toChatMemberByChatIdEntity(chatMember)
        );
        springDataCassandraChatMemberByUserIdRepository.delete(
                cassandraChatMemberMapper.toChatMemberByUserIdEntity(chatMember)
        );
    }

    public Optional<ChatMember> findByChatId(Long chatId) {
        return springDataCassandraChatMemberByChatIdRepository.findByChatId(chatId)
                .map(cassandraChatMemberMapper::toDomain);
    }

    public Optional<ChatMember> findByUserId(UUID userId) {
        return springDataCassandraChatMemberByUserIdRepository.findByUserId(userId)
                .map(cassandraChatMemberMapper::toDomain);
    }

    public Optional<ChatMember> findByChatIdAndUserId(Long chatId, UUID userId) {
        return springDataCassandraChatMemberByChatIdRepository.findByChatIdAndUserId(chatId, userId)
                .map(cassandraChatMemberMapper::toDomain);
    }
}
