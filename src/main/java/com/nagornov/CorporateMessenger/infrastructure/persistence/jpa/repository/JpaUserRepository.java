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

import java.util.List;
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

    public boolean existsById(UUID id) {
        return springDataJpaUserRepository.existsById(id);
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

    public List<UUID> findAllIdsByIds(List<UUID> ids) {
        return springDataJpaUserRepository.findAllIdsByIds(ids);
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

    public Page<UserWithUserPhotoDTO> searchWithMainUserPhotoByUsername(
            UUID myUserId,
            String username,
            int page,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return springDataJpaUserRepository
                .searchWithMainUserPhotoByUsername(myUserId, username, pageable)
                .map(dtoEntity -> {
                    return new UserWithUserPhotoDTO(
                            jpaUserMapper.toDomain(dtoEntity.getUser()),
                            dtoEntity.getUserPhoto().map(jpaUserPhotoMapper::toDomain)
                    );
                });
    }

    public List<UserWithUserPhotoDTO> findAllWithMainUserPhotoByIds(List<UUID> ids) {
        return springDataJpaUserRepository.findAllWithMainUserPhotoByIds(ids)
                .stream().map(dtoEntity -> new UserWithUserPhotoDTO(
                        jpaUserMapper.toDomain(dtoEntity.getUser()),
                        dtoEntity.getUserPhoto().map(jpaUserPhotoMapper::toDomain)
                )).toList();
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

    public Optional<UserWithUserSettingsAndUserPhotoDTO> findWithUserSettingsAndUserPhotoByIdAndPhotoId(UUID id, UUID photoId) {
        return springDataJpaUserRepository.findWithUserSettingsAndUserPhotoByIdAndPhotoId(id, photoId)
                .map(dtoEntity -> new UserWithUserSettingsAndUserPhotoDTO(
                        jpaUserMapper.toDomain(dtoEntity.getUser()),
                        jpaUserSettingsMapper.toDomain(dtoEntity.getUserSettings()),
                        jpaUserPhotoMapper.toDomain(dtoEntity.getUserPhoto())
                ));
    }

    public Optional<UserWithUserSettingsAndPartnerInfoDTO> findWithUserSettingsAndPartnerInfoByIds(UUID targetId, UUID yourId) {
        return springDataJpaUserRepository.findWithUserSettingsAndPartnerInfoByIds(targetId, yourId)
                .map(dtoEntity -> {
                    return new UserWithUserSettingsAndPartnerInfoDTO(
                            jpaUserMapper.toDomain(dtoEntity.getUser()),
                            jpaUserSettingsMapper.toDomain(dtoEntity.getUserSettings()),
                            dtoEntity.getIsUserBlacklisted(),
                            dtoEntity.getIsYouBlacklisted(),
                            dtoEntity.getIsContact()
                    );
                });
    }
}