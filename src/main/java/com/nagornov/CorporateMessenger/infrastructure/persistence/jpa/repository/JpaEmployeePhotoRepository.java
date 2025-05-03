package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.EmployeePhoto;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaEmployeePhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaEmployeePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaEmployeePhotoRepository {

    private final SpringDataJpaEmployeePhotoRepository springDataJpaEmployeePhotoRepository;
    private final JpaEmployeePhotoMapper jpaEmployeePhotoMapper;

    public EmployeePhoto save(EmployeePhoto employeePhoto) {
        return jpaEmployeePhotoMapper.toDomain(
                springDataJpaEmployeePhotoRepository.save(
                        jpaEmployeePhotoMapper.toEntity(employeePhoto)
                )
        );
    }

    public void delete(EmployeePhoto employeePhoto) {
        springDataJpaEmployeePhotoRepository.delete(
                jpaEmployeePhotoMapper.toEntity(employeePhoto)
        );
    }

    public void deleteById(UUID id) {
        springDataJpaEmployeePhotoRepository.deleteById(id);
    }

    public void deleteByEmployeeId(UUID employeeId) {
        springDataJpaEmployeePhotoRepository.deleteByEmployeeId(employeeId);
    }

    public Optional<EmployeePhoto> findById(UUID id) {
        return springDataJpaEmployeePhotoRepository.findById(id)
                .map(jpaEmployeePhotoMapper::toDomain);
    }

    public Optional<EmployeePhoto> findByEmployeeId(UUID employeeId) {
        return springDataJpaEmployeePhotoRepository.findByEmployeeId(employeeId)
                .map(jpaEmployeePhotoMapper::toDomain);
    }

}
