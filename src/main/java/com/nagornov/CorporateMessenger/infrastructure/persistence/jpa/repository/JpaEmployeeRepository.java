package com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository;

import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaEmployeeMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.mapper.JpaEmployeePhotoMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.springData.SpringDataJpaEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaEmployeeRepository {

    private final SpringDataJpaEmployeeRepository springDataJpaEmployeeRepository;
    private final JpaEmployeeMapper jpaEmployeeMapper;
    private final JpaEmployeePhotoMapper jpaEmployeePhotoMapper;

    public Employee save(Employee employee) {
        return jpaEmployeeMapper.toDomain(
                springDataJpaEmployeeRepository.save(
                        jpaEmployeeMapper.toEntity(employee)
                )
        );
    }

    public void delete(Employee employee) {
        springDataJpaEmployeeRepository.delete(
                jpaEmployeeMapper.toEntity(employee)
        );
    }

    public void deleteByUserId(UUID userId) {
        springDataJpaEmployeeRepository.deleteByUserId(userId);
    }

    public Optional<Employee> findById(UUID id) {
        return springDataJpaEmployeeRepository.findById(id)
                .map(jpaEmployeeMapper::toDomain);
    }

    public Optional<Employee> findByUserId(UUID userId) {
        return springDataJpaEmployeeRepository.findByUserId(userId)
                .map(jpaEmployeeMapper::toDomain);
    }

    public List<Employee> findAllByLeaderId(UUID leaderId) {
        return springDataJpaEmployeeRepository.findAllByLeaderId(leaderId)
                .stream().map(jpaEmployeeMapper::toDomain).toList();
    }

}
