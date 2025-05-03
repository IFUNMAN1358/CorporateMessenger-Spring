package com.nagornov.CorporateMessenger.domain.broker.producer;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaUnreadMessageOperation;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaChatEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.entity.KafkaUserEntity;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaUnreadMessageRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UnreadMessageProducer {

    private final KafkaUnreadMessageRepository kafkaUnreadMessageRepository;
    private final KafkaUserMapper kafkaUserMapper;
    private final KafkaChatMapper kafkaChatMapper;

    public void sendToIncrementUnreadMessageCountForOther(@NonNull Chat chat, @NonNull User user) {

        KafkaChatEntity kafkaChatEntity = kafkaChatMapper.toEntity(chat);
        KafkaUserEntity kafkaUserEntity = kafkaUserMapper.toEntity(user);

        final KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                kafkaChatEntity, kafkaUserEntity,
                KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER.getOperation()
        );
        kafkaUnreadMessageRepository.sendMessage(message);
    }

    public void sendToDecrementUnreadMessageCountForOther(@NonNull Chat chat, @NonNull User user) {

        KafkaChatEntity kafkaChatEntity = kafkaChatMapper.toEntity(chat);
        KafkaUserEntity kafkaUserEntity = kafkaUserMapper.toEntity(user);

        KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                kafkaChatEntity, kafkaUserEntity,
                KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER.getOperation()
        );
        kafkaUnreadMessageRepository.sendMessage(message);
    }

    public void sendToIncrementUnreadMessageCountForUser(@NonNull Chat chat, @NonNull User user) {

        KafkaChatEntity kafkaChatEntity = kafkaChatMapper.toEntity(chat);
        KafkaUserEntity kafkaUserEntity = kafkaUserMapper.toEntity(user);

        KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                kafkaChatEntity, kafkaUserEntity,
                KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER.getOperation()
        );
        kafkaUnreadMessageRepository.sendMessage(message);
    }

    public void sendToDecrementUnreadMessageCountForUser(@NonNull Chat chat, @NonNull User user) {

        KafkaChatEntity kafkaChatEntity = kafkaChatMapper.toEntity(chat);
        KafkaUserEntity kafkaUserEntity = kafkaUserMapper.toEntity(user);

        KafkaUnreadMessageDTO message = new KafkaUnreadMessageDTO(
                kafkaChatEntity, kafkaUserEntity,
                KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER.getOperation()
        );
        kafkaUnreadMessageRepository.sendMessage(message);
    }

}
