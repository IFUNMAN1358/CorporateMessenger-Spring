package com.nagornov.CorporateMessenger.domain.listener;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaUnreadMessageOperation;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.UnreadMessageService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUnreadMessageListener {

    private final UnreadMessageService unreadMessageService;
    private final KafkaUserMapper kafkaUserMapper;
    private final KafkaChatMapper kafkaChatMapper;

    @KafkaListener(
            topics = KafkaTopic.UNREAD_MESSAGE_TOPIC_NAME,
            groupId = KafkaGroup.UNREAD_MESSAGE_GROUP_NAME,
            containerFactory = "kafkaUnreadMessageContainerFactory"
    )
    public void distributor(ConsumerRecord<String, KafkaUnreadMessageDTO> record, Acknowledgment ack) {

        Chat chat = getChatFromRecord(record);
        User user = getUserFromRecord(record);
        String operation = record.value().getOperation();

        try {
            switch (operation) {
                case (KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER_OPERATION):
                    unreadMessageService.incrementUnreadMessageCountForOther(chat, user);
                    break;
                case (KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER_OPERATION):
                    unreadMessageService.decrementUnreadMessageCountForOther(chat, user);
                    break;
                case (KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER_OPERATION):
                    unreadMessageService.incrementUnreadMessageCountForUser(chat, user);
                    break;
                case (KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER_OPERATION):
                    unreadMessageService.decrementUnreadMessageCountForUser(chat, user);
                    break;
                default:
                    throw new RuntimeException("Unsupported operation for unread message kafka distributor");
            }
            ack.acknowledge();
        } catch (Exception e) {
            throw e;
        }
    }

    private Chat getChatFromRecord(@NonNull ConsumerRecord<String, KafkaUnreadMessageDTO> record) {
        try {
            return kafkaChatMapper.toDomain(
                    record.value().getChat()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while trying converting record of message to chat domain model in unread message kafka distributor");
        }


    }

    private User getUserFromRecord(@NonNull ConsumerRecord<String, KafkaUnreadMessageDTO> record) {
        try {
            return kafkaUserMapper.toDomain(
                    record.value().getUser()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while trying converting record of message to user domain model in unread message kafka distributor");
        }
    }

}
