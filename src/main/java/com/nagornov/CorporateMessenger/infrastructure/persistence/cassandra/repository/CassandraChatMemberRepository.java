package com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.repository;

import com.nagornov.CorporateMessenger.domain.model.chat.ChatMember;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.entity.CassandraChatMemberByUserIdEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.mapper.CassandraChatMemberMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatMemberByChatIdRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.cassandra.springData.SpringDataCassandraChatMemberByUserIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CassandraChatMemberRepository {

    private final SpringDataCassandraChatMemberByChatIdRepository springDataCassandraChatMemberByChatIdRepository;
    private final SpringDataCassandraChatMemberByUserIdRepository springDataCassandraChatMemberByUserIdRepository;
    private final CassandraChatMemberMapper cassandraChatMemberMapper;

    public List<ChatMember> saveAll(List<ChatMember> chatMembers) {
        // byChatId
        springDataCassandraChatMemberByChatIdRepository.saveAll(
                chatMembers.stream().map(cassandraChatMemberMapper::toChatMemberByChatIdEntity).toList()
        );
        // byUserId
        return springDataCassandraChatMemberByUserIdRepository.saveAll(
                chatMembers.stream().map(cassandraChatMemberMapper::toChatMemberByUserIdEntity).toList()
        )
        .stream().map(cassandraChatMemberMapper::toDomain).toList();
    }

    public ChatMember save(ChatMember chatMember) {
        // byChatId
        springDataCassandraChatMemberByChatIdRepository.save(
                cassandraChatMemberMapper.toChatMemberByChatIdEntity(chatMember)
        );
        // byUserId
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

    public void deleteAllByChatIdAndUserIds(Long chatId, List<UUID> userIds) {
        springDataCassandraChatMemberByUserIdRepository.deleteAllByChatIdAndUserIds(chatId, userIds);
        springDataCassandraChatMemberByChatIdRepository.deleteAllByChatIdAndUserIds(chatId, userIds);
    }

    public List<ChatMember> findAllByChatId(Long chatId) {
        return springDataCassandraChatMemberByChatIdRepository.findAllByChatId(chatId)
                .stream().map(cassandraChatMemberMapper::toDomain).toList();
    }

    public Page<ChatMember> findAllByChatId(Long chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return springDataCassandraChatMemberByChatIdRepository.findAllByChatId(chatId, pageable)
                .map(cassandraChatMemberMapper::toDomain);
    }

    public List<ChatMember> findAllByUserId(UUID userId) {
        return springDataCassandraChatMemberByUserIdRepository.findAllByUserId(userId)
                .stream().map(cassandraChatMemberMapper::toDomain).toList();
    }

    public Optional<ChatMember> findByChatIdAndUserId(Long chatId, UUID userId) {
        return springDataCassandraChatMemberByChatIdRepository.findByChatIdAndUserId(chatId, userId)
                .map(cassandraChatMemberMapper::toDomain);
    }
}
