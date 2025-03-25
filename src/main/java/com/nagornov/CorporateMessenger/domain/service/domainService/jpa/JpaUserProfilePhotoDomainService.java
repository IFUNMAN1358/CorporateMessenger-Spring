package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserProfilePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserProfilePhotoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserProfilePhotoDomainService {

    private final JpaUserProfilePhotoRepository jpaUserProfilePhotoRepository;

    public void save(@NonNull UserProfilePhoto userProfilePhoto) {
        jpaUserProfilePhotoRepository.save(userProfilePhoto);
    }

    public void delete(@NonNull UserProfilePhoto userProfilePhoto) {
        jpaUserProfilePhotoRepository.delete(userProfilePhoto);
    }

    public void deleteById(@NonNull UUID id) {
        jpaUserProfilePhotoRepository.deleteById(id);
    }

    public void modDeleteById(@NonNull UUID id) {
        jpaUserProfilePhotoRepository.modDeleteById(id);
    }

    public UserProfilePhoto getByIdAndUserId(@NonNull UUID id, @NonNull UUID userId) {
        return jpaUserProfilePhotoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id and userId not found"));
    }

    public UserProfilePhoto getById(@NonNull UUID id) {
        return jpaUserProfilePhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id not found"));
    }

    public Optional<UserProfilePhoto> findMainByUserId(@NonNull UUID userId) {
        return jpaUserProfilePhotoRepository.findMainByUserId(userId);
    }

    public List<UserProfilePhoto> getAllByUserId(@NonNull UUID userId) {
        return jpaUserProfilePhotoRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}