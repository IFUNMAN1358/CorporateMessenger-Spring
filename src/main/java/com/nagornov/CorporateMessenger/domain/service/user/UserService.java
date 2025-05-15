package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.dto.*;
import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    public Optional<UserPairDTO> findUserPairByIds(@NonNull UUID id1, @NonNull UUID id2) {
        return jpaUserRepository.findUserPairByIds(id1, id2);
    }

    public Optional<UserWithUserSettingsDTO> findWithUserSettingsById(@NonNull UUID id) {
        return jpaUserRepository.findWithUserSettingsById(id);
    }

    public Optional<UserWithEmployeeDTO> findWithEmployeeById(@NonNull UUID id) {
        return jpaUserRepository.findWithEmployeeById(id);
    }

    public boolean existsById(@NonNull UUID id) {
        return jpaUserRepository.existsById(id);
    }

    public boolean existsByUsername(@NonNull String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    public Page<UserWithUserPhotoDTO> searchWithMainUserPhotoByUsername(@NonNull String username, int page, int pageSize) {
        return jpaUserRepository.searchWithMainUserPhotoByUsername(username, page, pageSize);
    }

    public List<UserWithUserPhotoDTO> findAllWithMainUserPhotoByIds(@NonNull List<UUID> ids) {
        return jpaUserRepository.findAllWithMainUserPhotoByIds(ids);
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

    public UserPairDTO getUserPairByIds(@NonNull UUID id1, @NonNull UUID id2) {
        return jpaUserRepository.findUserPairByIds(id1, id2)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserPairDTO[User, User] by User[id=%s] or User[id=%s] not found".formatted(id1, id2)
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

    public void ensureExistsById(@NonNull UUID id) {
        if (!jpaUserRepository.existsById(id)) {
            throw new ResourceConflictException("User[id=%s] not exist".formatted(id));
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

    public UserWithUserSettingsAndEmployeeDTO getWithUserSettingsAndEmployeeById(@NonNull UUID id) {
        return jpaUserRepository.findWithUserSettingsAndEmployeeById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserWithUserSettingsAndEmployeeDTO[User, UserSettings, Employee] by User[id=%s] not found"
                                .formatted(id)
                ));
    }

    public UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO getWithUserSettingsAndEmployeeAndEmployeePhotoById(@NonNull UUID id) {
        return jpaUserRepository.findWithUserSettingsAndEmployeeAndEmployeePhotoById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO[User, UserSettings, Employee, Opt(EmployeePhoto)] by User[id=%s] not found"
                                .formatted(id)
                ));
    }

    public UserWithUserSettingsAndUserPhotoDTO getWithUserSettingsAndUserPhotoByIdAndPhotoId(@NonNull UUID id, @NonNull UUID photoId) {
        return jpaUserRepository.findWithUserSettingsAndUserPhotoByIdAndPhotoId(id, photoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserWithUserSettingsAndUserPhotoDTO[User, UserSettings, UserPhoto] by User[id=%s] or UserPhoto[id=%s] not found"
                                .formatted(id, photoId)
                        )
                );
    }

    public void ensureExistsAllByIds(@NonNull List<UUID> ids) {
        if (ids.isEmpty()) {
            throw new ResourceConflictException("List of user ids is empty");
        }
        Set<UUID> existingIds = new HashSet<>(jpaUserRepository.findAllIdsByIds(ids));
        Set<UUID> missingIds = ids.stream().filter(id -> !existingIds.contains(id)).collect(Collectors.toSet());
        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("One of the passed user ids does not exist");
        }
    }

}