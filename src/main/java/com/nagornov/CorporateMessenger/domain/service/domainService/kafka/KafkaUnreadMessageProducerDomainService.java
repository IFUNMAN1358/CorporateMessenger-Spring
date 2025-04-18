package com.nagornov.CorporateMessenger.domain.service.domainService.kafka;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaUnreadMessageOperation;
import com.nagornov.CorporateMessenger.domain.model.chat.ChatInterface;
import com.nagornov.CorporateMessenger.domain.model.chat.GroupChat;
import com.nagornov.CorporateMessenger.domain.model.chat.PrivateChat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaUnreadMessageProducerRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaUnreadMessageProducerDomainService {

    private final KafkaUnreadMessageProducerRepository kafkaUnreadMessageProducerRepository;
    private final KafkaUserMapper kafkaUserMapper;
    private final KafkaChatMapper kafkaChatMapper;

    public void sendToIncrementUnreadMessageCountForOther(@NonNull User user, @NonNull ChatInterface chatInterface) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chatInterface);
        KafkaUserEntity userEntity = kafkaUserMapper.toEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    public void sendToDecrementUnreadMessageCountForOther(@NonNull User user, @NonNull ChatInterface chatInterface) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chatInterface);
        KafkaUserEntity userEntity = kafkaUserMapper.toEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    public void sendToIncrementUnreadMessageCountForUser(@NonNull User user, @NonNull ChatInterface chatInterface) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chatInterface);
        KafkaUserEntity userEntity = kafkaUserMapper.toEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    public void sendToDecrementUnreadMessageCountForUser(@NonNull User user, @NonNull ChatInterface chatInterface) {

        KafkaChatEntity chatEntity = getChatEntityFromChatInterface(chatInterface);
        KafkaUserEntity userEntity = kafkaUserMapper.toEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                userEntity, chatEntity,
                KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER.getOperation()
        );
        kafkaUnreadMessageProducerRepository.sendMessage(message);
    }

    private KafkaChatEntity getChatEntityFromChatInterface(@NonNull ChatInterface chatInterface) {
        KafkaChatEntity chatEntity;

        if (chatInterface instanceof GroupChat) {
            chatEntity = kafkaChatMapper.toChatEntity((GroupChat) chatInterface);
        } else if (chatInterface instanceof PrivateChat) {
            chatEntity = kafkaChatMapper.toChatEntity((PrivateChat) chatInterface);
        } else {
            throw new RuntimeException("Invalid chat type for kafka method to send message in unread message topic");
        }

        chatEntity.setChatType(chatInterface.getChatType());
        return chatEntity;
    }

}
