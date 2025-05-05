package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.dto.*;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.*;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository("jpaUserRepository")
@RequiredArgsConstructor
public class JpaUserRepository {

    private final SpringDataJpaUserRepository springDataJpaUserRepository;
    private final JpaUserMapper jpaUserMapper;
    private final JpaUserPhotoMapper jpaUserPhotoMapper;
    private final JpaUserSettingsMapper jpaUserSettingsMapper;
    private final JpaEmployeeMapper jpaEmployeeMapper;
    private final JpaEmployeePhotoMapper jpaEmployeePhotoMapper;

    public User save(User user) {
        return jpaUserMapper.toDomain(
                springDataJpaUserRepository.save(
                        jpaUserMapper.toEntity(user)
                )
        );
    }

    public void delete(User user) {
        springDataJpaUserRepository.delete(
                jpaUserMapper.toEntity(user)
        );
    }

    public boolean existsByUsername(String username) {
        return springDataJpaUserRepository.existsByUsername(username);
    }

    public Optional<User> findById(UUID id) {
        return springDataJpaUserRepository
                .findById(id)
                .map(jpaUserMapper::toDomain);
    }

    public Optional<User> findByUsername(String username) {
        return springDataJpaUserRepository
                .findByUsername(username)
                .map(jpaUserMapper::toDomain);
    }

    public Optional<UserPairDTO> findUserPairByIds(UUID id1, UUID id2) {
        return springDataJpaUserRepository.findUserPairByIds(id1, id2)
                .map(dtoEntity -> {
                    return new UserPairDTO(
                            jpaUserMapper.toDomain(dtoEntity.getUser1()),
                            jpaUserMapper.toDomain(dtoEntity.getUser2())
                    );
                });
    }

    public Optional<UserWithUserSettingsDTO> findWithUserSettingsById(UUID id) {
        return springDataJpaUserRepository.findWithUserSettingsById(id)
                .map(dtoEntity -> {
                    return new UserWithUserSettingsDTO(
                            jpaUserMapper.toDomain(dtoEntity.getUser()),
                            jpaUserSettingsMapper.toDomain(dtoEntity.getUserSettings())
                    );
                });
    }

    public Optional<UserWithEmployeeDTO> findWithEmployeeById(UUID id) {
        return springDataJpaUserRepository.findWithEmployeeById(id)
                .map(dtoEntity -> {
                    return new UserWithEmployeeDTO(
                            jpaUserMapper.toDomain(dtoEntity.getUser()),
                            jpaEmployeeMapper.toDomain(dtoEntity.getEmployee())
                    );
                });
    }

    public Page<UserWithMainUserPhotoDTO> searchWithMainUserPhotoByUsername(String username, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return springDataJpaUserRepository
                .searchWithMainUserPhotoByUsername(username, pageable)
                .map(dtoEntity -> {
                    return new UserWithMainUserPhotoDTO(
                            jpaUserMapper.toDomain(dtoEntity.getUser()),
                            jpaUserPhotoMapper.toDomain(dtoEntity.getMainUserPhoto())
                    );
                });
    }

    public Optional<UserWithUserSettingsAndEmployeeDTO> findWithUserSettingsAndEmployeeById(UUID id) {
        return springDataJpaUserRepository.findWithUserSettingsAndEmployeeById(id)
                .map(dtoEntity -> new UserWithUserSettingsAndEmployeeDTO(
                        jpaUserMapper.toDomain(dtoEntity.getUser()),
                        jpaUserSettingsMapper.toDomain(dtoEntity.getUserSettings()),
                        jpaEmployeeMapper.toDomain(dtoEntity.getEmployee())
                ));
    }

    public Optional<UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO> findWithUserSettingsAndEmployeeAndEmployeePhotoById(UUID id) {
        return springDataJpaUserRepository.findWithUserSettingsAndEmployeeAndEmployeePhotoById(id)
                .map(dtoEntity -> new UserWithUserSettingsAndEmployeeAndEmployeePhotoDTO(
                        jpaUserMapper.toDomain(dtoEntity.getUser()),
                        jpaUserSettingsMapper.toDomain(dtoEntity.getUserSettings()),
                        jpaEmployeeMapper.toDomain(dtoEntity.getEmployee()),
                        dtoEntity.getEmployeePhoto().map(jpaEmployeePhotoMapper::toDomain)
                ));
    }
}