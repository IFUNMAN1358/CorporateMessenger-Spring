package com.nagornov.CorporateMessenger.domain.broker.listener;

import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaUnreadMessageOperation;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.message.UnreadMessageService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaChatMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.mapper.KafkaUserMapper;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.transfer.dto.KafkaUnreadMessageDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnreadMessageListener {

    private final UnreadMessageService unreadMessageService;
    private final KafkaUserMapper kafkaUserMapper;
    private final KafkaChatMapper kafkaChatMapper;

    @KafkaListener(
            topics = KafkaTopic.UNREAD_MESSAGE_TOPIC_NAME,
            groupId = KafkaGroup.UNREAD_MESSAGE_GROUP_NAME,
            containerFactory = "kafkaUnreadMessageContainerFactory"
    )
    public void distributor(ConsumerRecord<String, KafkaUnreadMessageDTO> record, Acknowledgment ack) {
        Chat chat = kafkaChatMapper.toDomain(record.value().getChat());
        User user = kafkaUserMapper.toDomain(record.value().getUser());
        String operation = record.value().getOperation();

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
        }
        ack.acknowledge();
    }

}
