package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserPhotoEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserPhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserPhotoRepository {

    private final SpringDataJpaUserPhotoRepository springDataJpaUserPhotoRepository;
    private final JpaUserPhotoMapper jpaUserPhotoMapper;

    public UserPhoto save(UserPhoto userPhoto) {
        JpaUserPhotoEntity entity = springDataJpaUserPhotoRepository.save(
                jpaUserPhotoMapper.toEntity(userPhoto)
        );
        return jpaUserPhotoMapper.toDomain(entity);
    }

    public void delete(UserPhoto userPhoto) {
        springDataJpaUserPhotoRepository.delete(
                jpaUserPhotoMapper.toEntity(userPhoto)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaUserPhotoRepository.deleteById(id);
    }

    public Optional<UserPhoto> findById(UUID id) {
        return springDataJpaUserPhotoRepository
                .findById(id)
                .map(jpaUserPhotoMapper::toDomain);
    }

    public Optional<UserPhoto> findByIdAndUserId(UUID id, UUID userId) {
        return springDataJpaUserPhotoRepository
                .findByIdAndUserId(id, userId)
                .map(jpaUserPhotoMapper::toDomain);
    }

    public Optional<UserPhoto> findMainByUserId(UUID userId) {
        return springDataJpaUserPhotoRepository
                .findMainByUserId(userId)
                .map(jpaUserPhotoMapper::toDomain);
    }

    public List<UserPhoto> findAllByUserIdOrderByCreatedAtDesc(UUID userId) {
        return springDataJpaUserPhotoRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(jpaUserPhotoMapper::toDomain)
                .toList();
    }

}
