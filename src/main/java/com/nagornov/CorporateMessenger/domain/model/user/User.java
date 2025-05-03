package com.nagornov.CorporateMessenger.domain.model.user;

import com.nagornov.CorporateMessenger.domain.exception.ResourceConflictException;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
public class User {

    private UUID id;
    private String username;
    private String password;
    private String phone;
    private String mainEmail;
    private String firstName;
    private String lastName;
    private String bio;
    private Boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;

    //
    //
    //

    public User(
            UUID id,
            String username,
            String password,
            String phone,
            String mainEmail,
            String firstName,
            String lastName,
            String bio,
            Boolean isDeleted,
            Instant createdAt,
            Instant updatedAt
    ) {
        // Validation
        validateUsername(username);
        validatePassword(password);
        validatePhone(phone);
        validateMainEmail(mainEmail);
        validateFirstName(firstName);
        validateLastName(lastName);
        validateBio(bio);
        // Setting
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.mainEmail = mainEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //
    //
    //

    public String getIdAsString() {
        return this.id.toString();
    }

    public LocalDateTime getCreatedAtAsLocalDateTimeByZoneIdSystemDefault() {
        return LocalDateTime.ofInstant(this.createdAt, ZoneId.systemDefault());
    }

    public LocalDateTime getUpdatedAtAsLocalDateTimeByZoneIdSystemDefault() {
        return LocalDateTime.ofInstant(this.updatedAt, ZoneId.systemDefault());
    }

    //
    //
    //

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void unmarkAsDeleted() {
        this.isDeleted = false; // pu-pu-pu-pu-pu-pu
    }

    public void updateUsername(String newUsername) {
        validateUsername(newUsername);
        this.username = newUsername;
        this.updatedAt = Instant.now();
    }

    public void updatePassword(String newPassword) {
        validatePassword(newPassword);
        this.password = newPassword;
        this.updatedAt = Instant.now();
    }

    public void updatePhone(String newPhone) {
        validatePhone(newPhone);
        this.phone = newPhone;
        this.updatedAt = Instant.now();
    }

    public void updateMainEmail(String newMainEmail) {
        validateMainEmail(newMainEmail);
        this.mainEmail = newMainEmail;
        this.updatedAt = Instant.now();
    }

    public void updateFirstName(String newFirstName) {
        validateFirstName(newFirstName);
        this.firstName = newFirstName;
        this.updatedAt = Instant.now();
    }

    public void updateLastName(String newLastName) {
        validateLastName(newLastName);
        this.lastName = newLastName;
        this.updatedAt = Instant.now();
    }

    public void updateBio(String newBio) {
        validateBio(newBio);
        this.bio = newBio;
        this.updatedAt = Instant.now();
    }

    public void updateUpdatedAtAsNow() {
        this.updatedAt = Instant.now();
    }

    //
    //
    //

    private void validateUsername(String username) {
        if (username.strip().length() < 5 || username.strip().length() > 32) {
            throw new ResourceConflictException("User[username] should be between 5 and 32");
        }
    }

    private void validatePassword(String password) {
        if (password.strip().length() < 59 || password.strip().length() > 60) {
            throw new ResourceConflictException("User[password] should be between 59 and 60");
        }
    }

    private void validatePhone(String phone) {
        if (phone != null && phone.strip().length() > 20) {
            throw new ResourceConflictException("User[phone] should be between 0 and 20");
        }
    }

    private void validateMainEmail(String mainEmail) {
        if (mainEmail != null && mainEmail.strip().length() > 321) {
            throw new ResourceConflictException("User[mainEmail] should be between 0 and 321");
        }
    }

    private void validateFirstName(String firstName) {
        if (firstName.isBlank() || firstName.strip().length() > 64) {
            throw new ResourceConflictException("User[firstName] should be between 1 and 64");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName != null && lastName.strip().length() > 64) {
            throw new ResourceConflictException("User[lastName] should be between 0 and 64");
        }
    }

    private void validateBio(String bio) {
        if (bio != null && bio.strip().length() > 70) {
            throw new ResourceConflictException("User[bio] should be between 0 and 70");
        }
    }

}