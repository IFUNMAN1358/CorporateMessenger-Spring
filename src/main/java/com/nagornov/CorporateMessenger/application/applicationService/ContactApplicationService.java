package com.nagornov.CorporateMessenger.application.applicationService;

import com.nagornov.CorporateMessenger.application.dto.model.user.UserWithUserPhotoResponse;
import com.nagornov.CorporateMessenger.domain.broker.producer.NotificationProducer;
import com.nagornov.CorporateMessenger.domain.dto.ContactPairDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserPairDTO;
import com.nagornov.CorporateMessenger.domain.dto.UserWithUserSettingsDTO;
import com.nagornov.CorporateMessenger.domain.enums.model.ContactRole;
import com.nagornov.CorporateMessenger.domain.enums.model.ContactStatus;
import com.nagornov.CorporateMessenger.domain.enums.model.ContactsVisibility;
import com.nagornov.CorporateMessenger.domain.exception.ResourceBadRequestException;
import com.nagornov.CorporateMessenger.domain.model.auth.JwtAuthentication;
import com.nagornov.CorporateMessenger.domain.model.user.*;
import com.nagornov.CorporateMessenger.domain.service.auth.JwtService;
import com.nagornov.CorporateMessenger.domain.service.user.ContactService;
import com.nagornov.CorporateMessenger.domain.service.user.UserBlacklistService;
import com.nagornov.CorporateMessenger.domain.service.user.UserPhotoService;
import com.nagornov.CorporateMessenger.domain.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactApplicationService {

    private static final Duration NOTIFICATION_COOLDOWN = Duration.ofHours(24);

    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final JwtService jwtService;
    private final ContactService contactService;
    private final UserBlacklistService userBlacklistService;
    private final NotificationProducer notificationProducer;


    @Transactional
    public Contact addContactByUserId(@NonNull UUID userId) {

        JwtAuthentication authInfo = jwtService.getAuthInfo();

        User initiatorUser = userService.getById(authInfo.getUserIdAsUUID());

        UserWithUserSettingsDTO recipientDto = userService.getWithUserSettingsById(userId);
        User recipientUser = recipientDto.getUser();
        UserSettings recipientUserSettings = recipientDto.getUserSettings();

        userBlacklistService.ensureAnyMatchBetweenUserIds(initiatorUser.getId(), recipientUser.getId());

        Optional<ContactPairDTO> contactPairDto = contactService.findContactPairByUserIds(initiatorUser.getId(), recipientUser.getId());

        if (contactPairDto.isPresent()) {
            Contact initiatorContact = contactPairDto.get().getContact1();
            Contact recipientContact = contactPairDto.get().getContact2();

            if (recipientContact.isConfirmed()) {
                throw new ResourceBadRequestException("Contact request is not pending or already confirmed");
            }

            if (!recipientUserSettings.getIsConfirmContactRequests()) {
                initiatorContact.confirm();
                recipientContact.confirm();
                initiatorContact.updateAddedAsNow();
                recipientContact.updateAddedAsNow();
                initiatorContact.updateLastRequestSentAtAsNow();
                contactService.updateAll(List.of(initiatorContact, recipientContact));
                notificationProducer.sendToKafkaAsJoinContact(recipientUser.getId(), initiatorUser.getId());
                return initiatorContact;
            }

            Instant dayInterval = Instant.now().minus(NOTIFICATION_COOLDOWN);
            if (initiatorContact.getLastRequestSentAt() == null || initiatorContact.getLastRequestSentAt().isBefore(dayInterval)) {
                initiatorContact.updateLastRequestSentAtAsNow();
                contactService.update(initiatorContact);
                notificationProducer.sendToKafkaAsRequestToJoinContact(recipientUser.getId(), initiatorUser.getId());
                return initiatorContact;
            }

            return initiatorContact;
        }

        if (!recipientUserSettings.getIsConfirmContactRequests()) {
            notificationProducer.sendToKafkaAsJoinContact(recipientUser.getId(), initiatorUser.getId());
            contactService.create(
                    recipientUser.getId(), initiatorUser.getId(), ContactRole.RECIPIENT, ContactStatus.CONFIRMED, null, Instant.now()
            );
            return contactService.create(
                    initiatorUser.getId(), recipientUser.getId(), ContactRole.INITIATOR, ContactStatus.CONFIRMED, Instant.now(), Instant.now()
            );
        }

        notificationProducer.sendToKafkaAsRequestToJoinContact(recipientUser.getId(), initiatorUser.getId());
        contactService.create(
                recipientUser.getId(), initiatorUser.getId(), ContactRole.RECIPIENT, ContactStatus.PENDING, null, null
        );
        return contactService.create(
                initiatorUser.getId(), recipientUser.getId(), ContactRole.INITIATOR, ContactStatus.PENDING, Instant.now(), null
        );
    }


    @Transactional(readOnly = true)
    public Optional<Contact> findContactByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), userId);
        User authUser = userPairDTO.getUser1();
        User targetUser = userPairDTO.getUser2();

        if (userBlacklistService.existsByUserIdAndBlockedUserId(userId, authInfo.getUserIdAsUUID())) {
            return Optional.empty();
        }

        return contactService.findByUserIdAndContactId(authUser.getId(), targetUser.getId());
    }


    @Transactional(readOnly = true)
    public List<UserWithUserPhotoResponse> findAllMyContactUsers(int page, int size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        List<UUID> myContactUserIds = contactService.findAllByUserId(authInfo.getUserIdAsUUID(), page, size)
                .map(Contact::getContactId).toList();

        return userService.findAllWithMainUserPhotoByIds(myContactUserIds)
                .stream().map(
                        dto -> new UserWithUserPhotoResponse(
                                dto.getUser(),
                                dto.getUserPhoto()
                        )
                ).toList();
    }


    @Transactional(readOnly = true)
    public List<UserWithUserPhotoResponse> findAllContactUsersByUserId(@NonNull UUID userId, int page, int size) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        User authUser = userService.getById(authInfo.getUserIdAsUUID());

        UserWithUserSettingsDTO targetUserDTO = userService.getWithUserSettingsById(userId);
        User targetUser = targetUserDTO.getUser();
        UserSettings targetUserSettings = targetUserDTO.getUserSettings();

        if (userBlacklistService.existsByUserIdAndBlockedUserId(targetUser.getId(), authUser.getId())) {
            throw new ResourceBadRequestException("You can't get user's contacts because he blocked you");
        }

        Optional<Contact> optTargetUserContact = contactService.findByUserIdAndContactId(targetUser.getId(), authUser.getId());
        boolean contactIsPresentAndConfirmed = optTargetUserContact.isPresent() && optTargetUserContact.get().isConfirmed();

        if (targetUserSettings.isContactsVisibility(ContactsVisibility.EVERYONE)) {
            List<UUID> targetContactUserIds = contactService.findAllByUserId(targetUser.getId(), page, size)
                    .stream().map(Contact::getContactId).toList();
            return userService.findAllWithMainUserPhotoByIds(targetContactUserIds).stream().map(
                    dto -> new UserWithUserPhotoResponse(
                            dto.getUser(),
                            dto.getUserPhoto()
                    )
            ).toList();
        }
        if (targetUserSettings.isContactsVisibility(ContactsVisibility.CONTACTS) && contactIsPresentAndConfirmed) {
            List<UUID> targetContactUserIds = contactService.findAllByUserId(targetUser.getId(), page, size)
                    .stream().map(Contact::getContactId).toList();
            return userService.findAllWithMainUserPhotoByIds(targetContactUserIds).stream().map(
                    dto -> new UserWithUserPhotoResponse(
                            dto.getUser(),
                            dto.getUserPhoto()
                    )
            ).toList();
        }
        if (targetUserSettings.isContactsVisibility(ContactsVisibility.ONLY_ME) && contactIsPresentAndConfirmed) {
            return List.of(new UserWithUserPhotoResponse(targetUser, userPhotoService.findMainByUserId(targetUser.getId())));
        }
        return List.of();
    }


    @Transactional
    public void confirmContactByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), userId);
        User authUser = userPairDTO.getUser1();
        User initiatorUser = userPairDTO.getUser2();

        Contact authUserContact = contactService.getByUserIdAndContactId(authUser.getId(), initiatorUser.getId());
        Contact initiatorUserContact = contactService.getByUserIdAndContactId(initiatorUser.getId(), authUser.getId());

        if (userBlacklistService.existsByUserIdAndBlockedUserId(authUser.getId(), initiatorUser.getId())) {
            throw new ResourceBadRequestException("You can't confirm a blocked contact");
        }
        if (!(authUserContact.isRecipient() && initiatorUserContact.isInitiator())) {
            throw new ResourceBadRequestException("You have no right to confirm contact");
        }

        authUserContact.confirm();
        initiatorUserContact.confirm();

        notificationProducer.sendToKafkaAsConfirmContactRequest(initiatorUser.getId(), authUser.getId());
        contactService.updateAll(List.of(authUserContact, initiatorUserContact));
    }


    @Transactional
    public void rejectContactByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), userId);
        User authUser = userPairDTO.getUser1();
        User initiatorUser = userPairDTO.getUser2();

        Contact authUserContact = contactService.getByUserIdAndContactId(authUser.getId(), initiatorUser.getId());
        Contact initiatorUserContact = contactService.getByUserIdAndContactId(initiatorUser.getId(), authUser.getId());

        if (userBlacklistService.existsByUserIdAndBlockedUserId(authUser.getId(), initiatorUser.getId())) {
            throw new ResourceBadRequestException("You can't reject a blocked contact");
        }
        if (!(authUserContact.isRecipient() && initiatorUserContact.isInitiator())) {
            throw new ResourceBadRequestException("You have no right to reject contact");
        }
        notificationProducer.sendToKafkaAsRejectContactRequest(initiatorUser.getId(), authUser.getId());
        contactService.deleteAll(List.of(authUserContact, initiatorUserContact));
    }


    @Transactional
    public void deleteContactByUserId(@NonNull UUID userId) {
        JwtAuthentication authInfo = jwtService.getAuthInfo();

        UserPairDTO userPairDTO = userService.getUserPairByIds(authInfo.getUserIdAsUUID(), userId);
        User authUser = userPairDTO.getUser1();
        User targetUser = userPairDTO.getUser2();

        contactService.deleteContactPairByUserIds(authUser.getId(), targetUser.getId());
    }
}