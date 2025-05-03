package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.dto.UserWithEmployeeDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithMainUserPhotoDTO;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.dto.UserPairDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserSettingsDTO;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository jpaUserRepository;

    //
    // JPA
    //

    @Transactional
    public User create(
            @NonNull String username,
            @NonNull String encodedPassword,
            @NonNull String firstName,
            @NonNull String lastName
    ) {
        User user = new User(
                UUID.randomUUID(),
                username,
                encodedPassword,
                null,
                null,
                firstName,
                lastName,
                null,
                false,
                Instant.now(),
                Instant.now()
        );
        return jpaUserRepository.save(user);
    }

    @Transactional
    public User update(@NonNull User user) {
        user.updateUpdatedAtAsNow();
        return jpaUserRepository.save(user);
    }

    @Transactional
    public void delete(@NonNull User user) {
        jpaUserRepository.delete(user);
    }

    public Optional<User> findById(@NonNull UUID id) {
        return jpaUserRepository.findById(id);
    }

    public Optional<User> findByUsername(@NonNull String username) {
        return jpaUserRepository.findByUsername(username);
    }

    public Optional<UserPairDTO> findUserPairByUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaUserRepository.findUserPairByUserIds(userId1, userId2);
    }

    public Optional<UserWithUserSettingsDTO> findWithUserSettingsById(@NonNull UUID id) {
        return jpaUserRepository.findWithUserSettingsById(id);
    }

    public Optional<UserWithEmployeeDTO> findWithEmployeeById(@NonNull UUID id) {
        return jpaUserRepository.findWithEmployeeById(id);
    }

    public boolean existsByUsername(@NonNull String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    public Page<UserWithMainUserPhotoDTO> searchWithMainUserPhotoByUsername(@NonNull String username, int page, int pageSize) {
        return jpaUserRepository.searchWithMainUserPhotoByUsername(username, page, pageSize);
    }

    public User getById(@NonNull UUID id) {
        return jpaUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User[id=%s] not found".formatted(id)
                ));
    }

    public User getByUsername(@NonNull String username) {
        return jpaUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User[username=%s] not found".formatted(username),
                        new FieldError("username", "Пользователь с таким именем не найден")
                ));
    }

    public UserPairDTO getUserPairByUserIds(@NonNull UUID userId1, @NonNull UUID userId2) {
        return jpaUserRepository.findUserPairByUserIds(userId1, userId2)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserPairDTO[User, User] by User[id=%s] or User[id=%s] not found".formatted(userId1, userId2)
                ));
    }

    public UserWithEmployeeDTO getWithEmployeeById(@NonNull UUID id) {
        return jpaUserRepository.findWithEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserWithEmployeeDTO[User, Employee] by User[id=%s] or Employee[userId=%s] not found"
                                .formatted(id, id)
                ));
    }

    public UserWithUserSettingsDTO getWithUserSettingsById(@NonNull UUID id) {
        return jpaUserRepository.findWithUserSettingsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserWithUserSettingsDTO[User, UserSettings] by User[id=%s] not found".formatted(id)
                ));
    }

    public void ensureNotExistsByUsername(@NonNull String username) {
        if (jpaUserRepository.existsByUsername(username)) {
            throw new ResourceConflictException(
                    "User[username=%s] already exists".formatted(username),
                    new FieldError("username", "Имя пользователя уже занято")
            );
        }
    }

    public void validateUserNotDeleted(@NonNull User user) {
        if (user.getIsDeleted()) {
            throw new ResourceNotFoundException(
                    "User[username=%s] deleted".formatted(user.getUsername()),
                    new FieldError("username", "Пользователь с таким именем не найден")
            );
        }
    }

}