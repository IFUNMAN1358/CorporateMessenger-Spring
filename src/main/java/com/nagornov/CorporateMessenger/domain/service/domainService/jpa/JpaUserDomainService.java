package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.custom.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.logger.DomainServiceLogger;
import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserDomainService {

    private final JpaUserRepository jpaUserRepository;
    private final DomainServiceLogger domainServiceLogger;

    public void save(@NotNull User user) {
        jpaUserRepository.findById(user.getId())
                .ifPresent(e -> {
                    throw new ResourceConflictException("User already exists during save");
                });
        jpaUserRepository.save(user);
    }

    public void update(@NotNull User user) {
        jpaUserRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found during update"));
        jpaUserRepository.save(user);
    }

    public void deleteById(@NotNull UUID id) {
        jpaUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found during delete by id"));
        jpaUserRepository.deleteById(id);
    }

    public void delete(@NotNull User user) {
        User existingUser = jpaUserRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found during delete"));
        jpaUserRepository.delete(existingUser);
    }

    public User getById(@NotNull UUID id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with this id not found"));
    }

    public User getByUsername(@NotNull String username) {
        try {
            domainServiceLogger.info("Get by username started");

            return jpaUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with this username not found"));

        } catch (Exception e) {
            domainServiceLogger.error("Get by username failed", e);
            throw e;
        }
    }

    public void validateUserDoesNotExistByUsername(@NotNull String username) {
        jpaUserRepository.findByUsername(username)
                .ifPresent(e -> {
                    throw new ResourceConflictException("User with this username already exists");
                });
    }

    public List<User> searchByUsername(@NotNull String username, @NotNull int page, @NotNull int pageSize) {
        return jpaUserRepository.searchByUsername(username, page, pageSize);
    }
}