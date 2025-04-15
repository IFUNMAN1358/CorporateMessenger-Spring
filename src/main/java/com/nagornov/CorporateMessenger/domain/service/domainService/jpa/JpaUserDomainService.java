package com.nagornov.CorporateMessenger.domain.service.domainService.jpa;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserDomainService {

    private final JpaUserRepository jpaUserRepository;

    public void save(@NonNull User user) {
        jpaUserRepository.save(user);
    }

    public void delete(@NonNull User user) {
        jpaUserRepository.delete(user);
    }

    public void deleteById(@NonNull UUID id) {
        jpaUserRepository.deleteById(id);
    }

    public User getById(@NonNull UUID id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User[id=%s] not found".formatted(id)));
    }

    public User getByUsername(@NonNull String username) {
        return jpaUserRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "User[username=%s] not found".formatted(username),
                    List.of(
                            new FieldError("username", "Пользователь с таким именем не найден")
                    )
            ));
    }

    public Optional<User> findByUsername(@NonNull String username) {
        return jpaUserRepository.findByUsername(username);
    }

    public void validateUserDoesNotExistByUsername(@NonNull String username) {
        jpaUserRepository.findByUsername(username)
                .ifPresent(_ -> {
                    throw new ResourceConflictException("User[username=%s] already exists".formatted(username));
                });
    }

    public boolean existsByUsername(@NonNull String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    public void ensureNotExistsByUsername(@NonNull String username) {
        if (jpaUserRepository.existsByUsername(username)) {
            throw new ResourceConflictException(
                    "User[username=%s] already exists".formatted(username),
                    List.of(
                            new FieldError("username", "Имя пользователя уже занято")
                    )
            );
        }
    }

    public List<User> searchByUsername(@NonNull String username, int page, int pageSize) {
        return jpaUserRepository.searchByUsername(username, page, pageSize);
    }

}