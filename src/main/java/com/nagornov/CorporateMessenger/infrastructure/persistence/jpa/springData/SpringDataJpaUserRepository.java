package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.*;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    @Query("SELECT u.id FROM JpaUserEntity u WHERE u.id IN :ids AND u.isDeleted = false")
    List<UUID> findAllIdsByIds(@Param("ids") List<UUID> ids);

    @Query("select u from JpaUserEntity u where u.username = :username AND u.isDeleted = false")
    Optional<JpaUserEntity> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM JpaUserEntity u WHERE u.username = :username AND u.isDeleted = false")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM JpaUserEntity u WHERE u.id = :id AND u.isDeleted = false")
    boolean existsById(@Param("id") UUID id);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserPairDTOEntity(u1, u2) " +
            "FROM JpaUserEntity u1, JpaUserEntity u2 " +
            "WHERE u1.id = :id1 AND u2.id = :id2 AND u1.isDeleted = false AND u2.isDeleted = false"
    )
    Optional<UserPairDTOEntity> findUserPairByIds(@Param("id1") UUID id1, @Param("id2") UUID id2);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserSettingsDTOEntity(u, us) " +
            "FROM JpaUserEntity u " +
            "JOIN JpaUserSettingsEntity us ON u.id = us.userId " +
            "WHERE u.id = :id AND u.isDeleted = false"
    )
    Optional<UserWithUserSettingsDTOEntity> findWithUserSettingsById(@Param("id") UUID id);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithEmployeeDTOEntity(u, e) " +
            "FROM JpaUserEntity u " +
            "JOIN JpaEmployeeEntity e ON u.id = e.userId " +
            "WHERE u.id = :id AND u.isDeleted = false"
    )
    Optional<UserWithEmployeeDTOEntity> findWithEmployeeById(@Param("id") UUID id);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserPhotoDTOEntity(u, up)" +
            "FROM JpaUserEntity u " +
            "JOIN JpaUserSettingsEntity us ON u.id = us.userId " +
            "LEFT JOIN JpaUserPhotoEntity up ON u.id = up.userId AND up.isMain = true " +
            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) " +
            "AND u.id <> :myUserId " +
            "AND u.isDeleted = false " +
            "AND us.isSearchable = true"
    )
    Page<UserWithUserPhotoDTOEntity> searchWithMainUserPhotoByUsername(
            @Param("myUserId") UUID myUserId,
            @Param("username") String username,
            Pageable pageable
    );

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserPhotoDTOEntity(u, up) " +
            "FROM JpaUserEntity u " +
            "LEFT JOIN JpaUserPhotoEntity up ON u.id = up.userId AND up.isMain = true " +
            "WHERE u.id IN :ids AND u.isDeleted = false"
    )
    List<UserWithUserPhotoDTOEntity> findAllWithMainUserPhotoByIds(@Param("ids") List<UUID> ids);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserSettingsAndEmployeeDTOEntity(u, us, e) " +
            "FROM JpaUserEntity u " +
            "JOIN JpaUserSettingsEntity us ON u.id = us.userId " +
            "JOIN JpaEmployeeEntity e ON u.id = e.userId " +
            "WHERE u.id = :id AND u.isDeleted = false"
    )
    Optional<UserWithUserSettingsAndEmployeeDTOEntity> findWithUserSettingsAndEmployeeById(@Param("id") UUID id);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserSettingsAndEmployeeAndEmployeePhotoDTOEntity(u, us, e, ep) " +
            "FROM JpaUserEntity u " +
            "JOIN JpaUserSettingsEntity us ON u.id = us.userId " +
            "JOIN JpaEmployeeEntity e ON u.id = e.userId " +
            "LEFT JOIN JpaEmployeePhotoEntity ep ON ep.employeeId = e.id " +
            "WHERE u.id = :id AND u.isDeleted = false"
    )
    Optional<UserWithUserSettingsAndEmployeeAndEmployeePhotoDTOEntity> findWithUserSettingsAndEmployeeAndEmployeePhotoById(@Param("id") UUID id);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserSettingsAndUserPhotoDTOEntity(u, us, up) " +
            "FROM JpaUserEntity u " +
            "JOIN JpaUserSettingsEntity us ON u.id = us.userId " +
            "JOIN JpaUserPhotoEntity up ON u.id = up.userId " +
            "WHERE u.id = :id AND u.isDeleted = false AND up.id = :photoId"
    )
    Optional<UserWithUserSettingsAndUserPhotoDTOEntity> findWithUserSettingsAndUserPhotoByIdAndPhotoId(@Param("id") UUID id, @Param("photoId") UUID photoId);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.UserWithUserSettingsAndPartnerInfoDTOEntity(u, us, ub1, ub2, c1, c2) " +
            "FROM JpaUserEntity u " +
            "JOIN JpaUserSettingsEntity us ON u.id = us.userId " +
            "LEFT JOIN JpaUserBlacklistEntity ub1 ON ub1.userId = :yourId AND ub1.blockedUserId = u.id " +
            "LEFT JOIN JpaUserBlacklistEntity ub2 ON ub2.userId = u.id AND ub2.blockedUserId = :yourId " +
            "LEFT JOIN JpaContactEntity c1 ON c1.userId = :targetId AND c1.contactId = :yourId " +
            "LEFT JOIN JpaContactEntity c2 ON c2.userId = :yourId AND c2.contactId = :targetId " +
            "WHERE u.id = :targetId AND u.isDeleted = false"
    )
    Optional<UserWithUserSettingsAndPartnerInfoDTOEntity> findWithUserSettingsAndPartnerInfoByIds(
            @Param("targetId") UUID targetId,
            @Param("yourId") UUID yourId
    );
}
