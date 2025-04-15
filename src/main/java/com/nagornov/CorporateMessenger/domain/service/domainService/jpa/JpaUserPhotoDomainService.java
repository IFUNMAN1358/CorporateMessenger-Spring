package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.UserPhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserPhotoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserPhotoDomainService {

    private final JpaUserPhotoRepository jpaUserPhotoRepository;

    public void save(@NonNull UserPhoto userPhoto) {
        jpaUserPhotoRepository.save(userPhoto);
    }

    public void delete(@NonNull UserPhoto userPhoto) {
        jpaUserPhotoRepository.delete(userPhoto);
    }

    public void deleteById(@NonNull UUID id) {
        jpaUserPhotoRepository.deleteById(id);
    }

    public UserPhoto getByIdAndUserId(@NonNull UUID id, @NonNull UUID userId) {
        return jpaUserPhotoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id and userId not found"));
    }

    public UserPhoto getById(@NonNull UUID id) {
        return jpaUserPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile photo with this id not found"));
    }

    public Optional<UserPhoto> findMainByUserId(@NonNull UUID userId) {
        return jpaUserPhotoRepository.findMainByUserId(userId);
    }

    public List<UserPhoto> getAllByUserId(@NonNull UUID userId) {
        return jpaUserPhotoRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}