package com.nagornov.CorporateMessenger.domain.service.user;

import com.nagornov.CorporateMessenger.domain.dto.EmployeeWithEmployeePhotoDTO;
import com.nagornov.CorporateMessenger.domain.exception.ResourceNotFoundException;
import com.nagornov.CorporateMessenger.domain.model.user.Employee;
import com.nagornov.CorporateMessenger.infrastructure.persistence.jpa.repository.JpaEmployeeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final JpaEmployeeRepository jpaEmployeeRepository;

    @Transactional
    public Employee create(@NonNull UUID userId) {
        Employee employee = new Employee(
                UUID.randomUUID(),
                userId,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );
        return jpaEmployeeRepository.save(employee);
    }

    @Transactional
    public Employee update(@NonNull Employee employee) {
        employee.updateUpdatedAsNow();
        return jpaEmployeeRepository.save(employee);
    }

    @Transactional
    public void delete(@NonNull Employee employee) {
        jpaEmployeeRepository.delete(employee);
    }

    @Transactional
    public void deleteByUserId(@NonNull UUID userId) {
        jpaEmployeeRepository.deleteByUserId(userId);
    }

    public List<Employee> findAllByLeaderId(@NonNull UUID leaderId) {
        return jpaEmployeeRepository.findAllByLeaderId(leaderId);
    }

    public Optional<Employee> findByUserId(@NonNull UUID userId) {
        return jpaEmployeeRepository.findByUserId(userId);
    }

    public Employee getByUserId(@NonNull UUID userId) {
        return jpaEmployeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee[userId=%s] not found".formatted(userId)));
    }

    public EmployeeWithEmployeePhotoDTO getWithEmployeePhotoByUserId(@NonNull UUID userId) {
        return jpaEmployeeRepository.findWithEmployeePhotoByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeWithEmployeePhotoDTO[Employee, Opt(EmployeePhoto)] by Employee[userId=%s] not found"
                                .formatted(userId)
                ));
    }

}
