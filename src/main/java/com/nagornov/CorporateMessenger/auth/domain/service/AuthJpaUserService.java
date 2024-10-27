package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthRole;
import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.jpa.repository.AuthJpaUserRepository;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaUserAlreadyExistsException;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.jpa.JpaUserNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthJpaUserService {

    private final AuthJpaUserRepository jpaUserRepository;

    public AuthUser createUser(@NotNull AuthUser user) {
        jpaUserRepository.findUserById(user.getId())
                .ifPresent(e -> {
                    throw new JpaUserAlreadyExistsException();
                });
        jpaUserRepository.findUserByUsername(user.getUsername())
                .ifPresent(e -> {
                    throw new JpaUserAlreadyExistsException();
                });
        return jpaUserRepository.save(user);
    }

    public void deleteUser(@NotNull AuthUser user) {
        final AuthUser existingUser = jpaUserRepository.findUserById(user.getId())
                .orElseThrow(JpaUserNotFoundException::new);
        jpaUserRepository.delete(existingUser);
    }

    public AuthUser getUserById(@NotNull UUID id) {
        return jpaUserRepository.findUserById(id)
                .orElseThrow(JpaUserNotFoundException::new);
    }

    public AuthUser getUserByUsername(@NotNull String username) {
        return jpaUserRepository.findUserByUsername(username)
                .orElseThrow(JpaUserNotFoundException::new);
    }

    public void addRole(@NotNull AuthUser user, @NotNull AuthRole role) {
        user.getRoles().add(role);
        jpaUserRepository.save(user);
    }

    public void removeRole(@NotNull AuthUser user, @NotNull AuthRole role) {
        user.getRoles().remove(role);
        jpaUserRepository.save(user);
    }

    public void removeSession(@NotNull AuthUser user) {
        jpaUserRepository.save(
                new AuthUser(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getRoles(),
                        null
                )
        );
    }

}
