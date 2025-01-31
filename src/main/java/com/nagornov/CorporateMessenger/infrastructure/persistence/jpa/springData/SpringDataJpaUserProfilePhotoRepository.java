package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserProfilePhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserProfilePhotoRepository extends JpaRepository<JpaUserProfilePhotoEntity, UUID> {

    @Query("select p from JpaUserProfilePhotoEntity p where p.user.id = :userId and p.isMain = true")
    Optional<JpaUserProfilePhotoEntity> findMainJpaUserProfilePhotoEntityByUserId(@Param("userId") UUID userId);

    @Query("select p from JpaUserProfilePhotoEntity p where p.user.id = :userId order by p.createdAt desc")
    List<JpaUserProfilePhotoEntity> findAllJpaUserProfilePhotoEntityByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Query("select p from JpaUserProfilePhotoEntity p where p.id = :id")
    Optional<JpaUserProfilePhotoEntity> findJpaUserProfilePhotoEntityById(@Param("id") UUID id);

    @Query("select p from JpaUserProfilePhotoEntity p where p.id = :id and p.user.id = :userId")
    Optional<JpaUserProfilePhotoEntity> findJpaUserProfilePhotoEntityByIdAndUserId(
            @Param("id") UUID id, @Param("userId") UUID userId
    );

    @Query("select p from JpaUserProfilePhotoEntity p where p.filePath = :filePath")
    Optional<JpaUserProfilePhotoEntity> findJpaUserProfilePhotoEntityByFilePath(@Param("filePath") String filePath);

    @Modifying
    @Query("delete from JpaUserProfilePhotoEntity p where p.id = :id")
    void modDeleteJpaUserProfilePhotoEntityById(@Param("id") UUID id);

}
