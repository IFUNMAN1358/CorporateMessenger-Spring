package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserProfilePhotoRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserProfilePhotoDomainService {

    private final JpaUserProfilePhotoRepository jpaUserProfilePhotoRepository;

    public void save(@NotNull UserProfilePhoto userProfilePhoto) {
        jpaUserProfilePhotoRepository.findById(userProfilePhoto.getId())
                .ifPresent(e -> {
                    throw new ResourceConflictException("User profile photo already exists during save");
                });
        jpaUserProfilePhotoRepository.save(userProfilePhoto);
    }

    public void update(@NotNull UserProfilePhoto userProfilePhoto) {
        jpaUserProfilePhotoRepository.findById(userProfilePhoto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo not found during update"));
        jpaUserProfilePhotoRepository.save(userProfilePhoto);
    }

    public UserProfilePhoto getByIdAndUserId(@NotNull UUID id, @NotNull UUID userId) {
        return jpaUserProfilePhotoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id and userId not found"));
    };

    public UserProfilePhoto getById(@NotNull UUID id) {
        return jpaUserProfilePhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id not found"));
    };

    public Optional<UserProfilePhoto> findMainByUserId(@NotNull UUID userId) {
        return jpaUserProfilePhotoRepository.findMainByUserId(userId);
    };

    public List<UserProfilePhoto> getAllByUserId(@NotNull UUID userId) {
        return jpaUserProfilePhotoRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    };

    public void deleteById(@NotNull UUID id) {
        jpaUserProfilePhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo not found during delete by id"));
        jpaUserProfilePhotoRepository.deleteById(id);
    };

    public void delete(@NotNull UserProfilePhoto userProfilePhoto) {
        UserProfilePhoto existingUserProfilePhoto = jpaUserProfilePhotoRepository.findById(userProfilePhoto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo not found during delete"));
        jpaUserProfilePhotoRepository.delete(existingUserProfilePhoto);
    };

    public void modDeleteById(@NotNull UUID id) {
        jpaUserProfilePhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo not found during modDelete by id"));
        jpaUserProfilePhotoRepository.modDeleteById(id);
    };
}