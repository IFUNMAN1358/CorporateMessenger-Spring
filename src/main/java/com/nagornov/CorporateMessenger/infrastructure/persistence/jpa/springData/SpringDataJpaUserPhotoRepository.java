package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaUserPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaUserPhotoRepository extends JpaRepository<JpaUserPhotoEntity, UUID> {

    @Query("select p from JpaUserPhotoEntity p where p.userId = :userId and p.isMain = true")
    Optional<JpaUserPhotoEntity> findMainByUserId(@Param("userId") UUID userId);

    @Query("select p from JpaUserPhotoEntity p where p.userId = :userId order by p.createdAt desc")
    List<JpaUserPhotoEntity> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Query("select p from JpaUserPhotoEntity p where p.id = :id and p.userId = :userId")
    Optional<JpaUserPhotoEntity> findByIdAndUserId(
            @Param("id") UUID id, @Param("userId") UUID userId
    );

    @Modifying
    @Query("DELETE FROM JpaUserPhotoEntity up WHERE up.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

}
