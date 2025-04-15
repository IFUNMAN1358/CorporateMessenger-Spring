package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("jpaUserRepository")
@RequiredArgsConstructor
public class JpaUserRepository {

    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    private final JpaUserMapper jpaUserMapper;

    public User save(User user) {
        JpaUserEntity entity = springDataJpaUserRepository.save(
                jpaUserMapper.toEntity(user)
        );
        return jpaUserMapper.toDomain(entity);
    }

    public void delete(User user) {
        springDataJpaUserRepository.delete(
                jpaUserMapper.toEntity(user)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaUserRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return springDataJpaUserRepository.existsByUsername(username);
    }

    public Optional<User> findById(UUID id) {
        return springDataJpaUserRepository
                .findById(id)
                .map(jpaUserMapper::toDomain);
    }

    public Optional<User> findByUsername(String username) {
        return springDataJpaUserRepository
                .findByUsername(username)
                .map(jpaUserMapper::toDomain);
    }

    public List<User> searchByUsername(String username, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return springDataJpaUserRepository
                .searchByUsername(username, pageable)
                .stream().map(jpaUserMapper::toDomain)
                .toList();
    }
}