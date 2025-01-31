package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserRepository;
import jakarta.validation.constraints.NotNull;
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

    public void save(@NotNull User user) {
        final JpaUserEntity userEntity = new JpaUserEntity();
        userEntity.setId(user.getId());
        springDataJpaUserRepository.save(
                jpaUserMapper.toEntity(user, userEntity)
        );
    }

    public void delete(@NotNull User user) {
        final JpaUserEntity userEntity = new JpaUserEntity();
        userEntity.setId(user.getId());
        springDataJpaUserRepository.delete(
                jpaUserMapper.toEntity(user, userEntity)
        );
    }

    public void deleteById(@NotNull UUID id) {
        springDataJpaUserRepository.deleteById(id);
    }

    public Optional<User> findById(@NotNull UUID id) {
        return springDataJpaUserRepository
                .findJpaUserEntityById(id)
                .map(jpaUserMapper::toDomain);
    }

    public Optional<User> findByUsername(@NotNull String username) {
        return springDataJpaUserRepository
                .findJpaUserEntityByUsername(username)
                .map(jpaUserMapper::toDomain);
    }

    public List<User> searchByUsername(@NotNull String username, @NotNull int page, @NotNull int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return springDataJpaUserRepository
                .searchByUsername(username, pageable)
                .stream().map(jpaUserMapper::toDomain)
                .toList();
    }
}