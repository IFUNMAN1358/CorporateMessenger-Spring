package com.nagornov.CorporateMessenger.domain.service.domainService.kafka;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaUnreadMessageOperation;
import com.nagornov.CorporateMessenger.domain.model.Chat;
import com.nagornov.CorporateMessenger.domain.model.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaUnreadMessageProducerRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaUnreadMessageProducerDomainService {

    private final KafkaUnreadMessageProducerRepository kafkaUnreadMessageProducerRepository;
    private final KafkaUserMapper kafkaUserMapper;
    private final KafkaChatMapper kafkaChatMapper;

    public void sendToIncrementUnreadMessageCountForOther(@NotNull User user, @NotNull Chat chat) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chat);
        KafkaUserEntity userEntity = kafkaUserMapper.toKafkaUserEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    public void sendToDecrementUnreadMessageCountForOther(@NotNull User user, @NotNull Chat chat) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chat);
        KafkaUserEntity userEntity = kafkaUserMapper.toKafkaUserEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    public void sendToIncrementUnreadMessageCountForUser(@NotNull User user, @NotNull Chat chat) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chat);
        KafkaUserEntity userEntity = kafkaUserMapper.toKafkaUserEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    public void sendToDecrementUnreadMessageCountForUser(@NotNull User user, @NotNull Chat chat) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chat);
        KafkaUserEntity userEntity = kafkaUserMapper.toKafkaUserEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    private KafkaChatEntity getChatEntityFromChatInterface(@NotNull Chat chat) {
        KafkaChatEntity chatEntity;

        if (chat instanceof GroupChat) {
            chatEntity = kafkaChatMapper.toChatEntity((GroupChat) chat);
        } else if (chat instanceof PrivateChat) {
            chatEntity = kafkaChatMapper.toChatEntity((PrivateChat) chat);
        } else {
            throw new RuntimeException("Invalid chat type for kafka method to send message in unread message topic");
        }

        chatEntity.setChatType(chat.getChatType());
        return chatEntity;
    }

}
