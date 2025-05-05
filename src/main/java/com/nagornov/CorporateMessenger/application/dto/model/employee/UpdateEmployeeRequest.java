package com.nagornov.CorporateMessenger.application.dto.model.employee;

import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.model.error.FieldError;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UpdateEmployeeRequest {

    private UUID newLeaderId;                       // might be null
    private String newDepartment;      // 0-255     // might be null
    private String newPosition;        // 0-255     // might be null
    private String newDescription;     // 0-255     // might be null
    private String newWorkSchedule;    // 0-255     // might be null


    @PostConstruct
    public void validateFields() {
        if (newDepartment != null && newDepartment.length() > 255) {
            throw new ResourceBadRequestException(
                    "UpdateEmployeeRequest[newDepartment] length should be between 0 and 255",
                    new FieldError("newDepartment", "Новый отдел должен иметь длину от 0 до 255 символов")
            );
        }
        if (newPosition != null && newPosition.length() > 255) {
            throw new ResourceBadRequestException(
                    "UpdateEmployeeRequest[newPosition] length should be between 0 and 255",
                    new FieldError("newPosition", "Новая должность должна иметь длину от 0 до 255 символов")
            );
        }
        if (newDescription != null && newDescription.length() > 255) {
            throw new ResourceBadRequestException(
                    "UpdateEmployeeRequest[newDescription] length should be between 0 and 255",
                    new FieldError("newDescription", "Новое описание должно иметь длину от 0 до 255 символов")
            );
        }
        if (newWorkSchedule != null && newWorkSchedule.length() > 255) {
            throw new ResourceBadRequestException(
                    "UpdateEmployeeRequest[newWorkSchedule] length should be between 0 and 255",
                    new FieldError("newWorkSchedule", "Новый график работы должен иметь длину от 0 до 255 символов")
            );
        }
    }

}