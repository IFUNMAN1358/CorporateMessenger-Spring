package com.nagornov.CorporateMessenger.auth.domain.service;

import com.nagornov.CorporateMessenger.auth.domain.model.AuthUser;
import com.nagornov.CorporateMessenger.auth.infrastructure.persistence.cassandra.repository.AuthCassandraUserRepository;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.cassandra.CassandraUserAlreadyExistsException;
import com.nagornov.CorporateMessenger.sharedKernel.exception.exceptions.cassandra.CassandraUserNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCassandraUserService {

    private final AuthCassandraUserRepository cassandraUserRepository;

    public AuthUser createUser(@NotNull AuthUser user) {
        cassandraUserRepository.findUserById(user.getId())
                .ifPresent(e -> {
                    throw new CassandraUserAlreadyExistsException();
                });
        return cassandraUserRepository.save(user);
    }

    public void deleteUser(@NotNull AuthUser user) {
        final AuthUser existingUser = cassandraUserRepository.findUserById(user.getId())
                .orElseThrow(CassandraUserNotFoundException::new);
        cassandraUserRepository.delete(existingUser);
    }
}
