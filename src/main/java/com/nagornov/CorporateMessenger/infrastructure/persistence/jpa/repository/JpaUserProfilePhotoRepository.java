package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserProfilePhotoEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserProfilePhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserProfilePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserProfilePhotoRepository {

    private final SpringDataJpaUserProfilePhotoRepository springDataJpaUserProfilePhotoRepository;
    private final JpaUserProfilePhotoMapper jpaUserProfilePhotoMapper;

    public UserProfilePhoto save(UserProfilePhoto userProfilePhoto) {
        JpaUserProfilePhotoEntity entity = springDataJpaUserProfilePhotoRepository.save(
                jpaUserProfilePhotoMapper.toEntity(userProfilePhoto)
        );
        return jpaUserProfilePhotoMapper.toDomain(entity);
    }

    public void delete(UserProfilePhoto userProfilePhoto) {
        springDataJpaUserProfilePhotoRepository.delete(
                jpaUserProfilePhotoMapper.toEntity(userProfilePhoto)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaUserProfilePhotoRepository.deleteById(id);
    }

    public void modDeleteById(UUID id) {
        springDataJpaUserProfilePhotoRepository.modDeleteJpaUserProfilePhotoEntityById(id);
    }

    public Optional<UserProfilePhoto> findById(UUID id) {
        return springDataJpaUserProfilePhotoRepository
                .findJpaUserProfilePhotoEntityById(id)
                .map(jpaUserProfilePhotoMapper::toDomain);
    }

    public Optional<UserProfilePhoto> findByIdAndUserId(UUID id, UUID userId) {
        return springDataJpaUserProfilePhotoRepository
                .findJpaUserProfilePhotoEntityByIdAndUserId(id, userId)
                .map(jpaUserProfilePhotoMapper::toDomain);
    }

    public Optional<UserProfilePhoto> findMainByUserId(UUID userId) {
        return springDataJpaUserProfilePhotoRepository
                .findMainJpaUserProfilePhotoEntityByUserId(userId)
                .map(jpaUserProfilePhotoMapper::toDomain);
    }

    public List<UserProfilePhoto> findAllByUserIdOrderByCreatedAtDesc(UUID userId) {
        return springDataJpaUserProfilePhotoRepository
                .findAllJpaUserProfilePhotoEntityByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(jpaUserProfilePhotoMapper::toDomain)
                .toList();
    }

}
