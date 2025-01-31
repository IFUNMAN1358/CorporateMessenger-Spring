package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaUserProfilePhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserProfilePhotoRepository;
import jakarta.validation.constraints.NotNull;
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

    public void save(@NotNull UserProfilePhoto userProfilePhoto) {
        springDataJpaUserProfilePhotoRepository.save(
                jpaUserProfilePhotoMapper.toEntity(userProfilePhoto)
        );
    }

    public Optional<UserProfilePhoto> findById(@NotNull UUID id) {
        return springDataJpaUserProfilePhotoRepository
                .findJpaUserProfilePhotoEntityById(id)
                .map(jpaUserProfilePhotoMapper::toDomain);
    }

    public Optional<UserProfilePhoto> findByIdAndUserId(@NotNull UUID id, @NotNull UUID userId) {
        return springDataJpaUserProfilePhotoRepository
                .findJpaUserProfilePhotoEntityByIdAndUserId(id, userId)
                .map(jpaUserProfilePhotoMapper::toDomain);
    }

    public Optional<UserProfilePhoto> findMainByUserId(@NotNull UUID userId) {
        return springDataJpaUserProfilePhotoRepository
                .findMainJpaUserProfilePhotoEntityByUserId(userId)
                .map(jpaUserProfilePhotoMapper::toDomain);
    }

    public List<UserProfilePhoto> findAllByUserIdOrderByCreatedAtDesc(@NotNull UUID userId) {
        return springDataJpaUserProfilePhotoRepository
                .findAllJpaUserProfilePhotoEntityByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(jpaUserProfilePhotoMapper::toDomain)
                .toList();
    }

    public void deleteById(@NotNull UUID id) {
        springDataJpaUserProfilePhotoRepository.deleteById(id);
    }

    public void modDeleteById(@NotNull UUID id) {
        springDataJpaUserProfilePhotoRepository.modDeleteJpaUserProfilePhotoEntityById(id);
    }

    public void delete(@NotNull UserProfilePhoto userProfilePhoto) {
        springDataJpaUserProfilePhotoRepository.delete(
                jpaUserProfilePhotoMapper.toEntity(userProfilePhoto)
        );
    }

}
