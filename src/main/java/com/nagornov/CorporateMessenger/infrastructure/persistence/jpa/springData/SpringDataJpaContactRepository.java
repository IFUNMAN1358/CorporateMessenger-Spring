package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData;

import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.ContactPairDTOEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.entity.JpaContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaContactRepository extends JpaRepository<JpaContactEntity, UUID> {

//    @Query(
//            "SELECT c FROM JpaContactEntity c " +
//            "JOIN JpaUserContactEntity uc1 ON c.id = uc1.contactId " +
//            "JOIN JpaUserContactEntity uc2 ON c.id = uc2.contactId " +
//            "WHERE uc1.userId = :userId1 AND uc2.userId = :userId2"
//    )
//    Optional<JpaContactEntity> findByUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
//
//    @Query(
//            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.ContactWithUserContactsDTOEntity(c, uc1, uc2) " +
//            "FROM JpaContactEntity c " +
//            "JOIN JpaUserContactEntity uc1 ON c.id = uc1.contactId " +
//            "JOIN JpaUserContactEntity uc2 ON c.id = uc2.contactId " +
//            "WHERE uc1.userId = :userId1 AND uc2.userId = :userId2"
//    )
//    Optional<ContactWithUserContactsDTOEntity> findWithUserContactsByUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
//
//    @Query(
//            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.ContactWithUserContactsDTOEntity(c, uc1, uc2) " +
//            "FROM JpaContactEntity c " +
//            "JOIN JpaUserContactEntity uc1 ON c.id = uc1.contactId " +
//            "JOIN JpaUserContactEntity uc2 ON c.id = uc2.contactId " +
//            "WHERE uc1.userId = :userId"
//    )
//    List<ContactWithUserContactsDTOEntity> findAllWithUserContactsByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM JpaContactEntity c WHERE c.userId = :userId AND c.contactId = :contactId")
    Optional<JpaContactEntity> findByUserIdAndContactId(@Param("userId") UUID userId, @Param("contactId") UUID contactId);

    @Query("SELECT c FROM JpaContactEntity c WHERE c.userId = :userId")
    List<JpaContactEntity> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM JpaContactEntity c WHERE c.userId = :userId")
    Page<JpaContactEntity> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query(
            "SELECT new com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.dto.ContactPairDTOEntity(c1, c2) " +
            "FROM JpaContactEntity c1, JpaContactEntity c2 " +
            "WHERE (c1.userId = :userId1 AND c1.contactId = :userId2) AND (c2.userId = :userId2 AND c2.contactId = :userId1)"
    )
    Optional<ContactPairDTOEntity> findContactPairByUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Modifying
    @Query(
            "DELETE FROM JpaContactEntity c " +
            "WHERE (c.userId = :userId1 AND c.contactId = :userId2) OR (c.userId = :userId2 AND c.contactId = :userId1)"
    )
    void deleteContactPairByUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);

    @Query(
            "SELECT CASE WHEN " +
            "EXISTS (SELECT COUNT(c1) > 0 FROM JpaContactEntity c1 WHERE c1.userId = :userId1 AND c1.contactId = :userId2) AND " +
            "EXISTS (SELECT COUNT(c2) > 0 FROM JpaContactEntity c2 WHERE c2.userId = :userId2 AND c2.contactId = :userId1) " +
            "THEN true ELSE false END"
    )
    boolean existsContactPairByUserIds(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}
