package com.nagornov.CorporateMessenger.domain.service.listenerService;

import com.nagornov.CorporateMessenger.domain.enums.ChatType;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaGroup;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaTopic;
import com.nagornov.CorporateMessenger.domain.enums.kafka.KafkaUnreadMessageOperation;
import com.nagornov.CorporateMessenger.domain.model.chat.Chat;
import com.nagornov.CorporateMessenger.domain.model.user.User;
import com.nagornov.CorporateMessenger.domain.service.businessService.cassandra.CassandraUnreadMessageBusinessService;
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

    private final CassandraUnreadMessageBusinessService cassandraUnreadMessageBusinessService;
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
                    cassandraUnreadMessageBusinessService.incrementUnreadMessageCountForOther(chat, user);
                    break;
                case (KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_OTHER_OPERATION):
                    cassandraUnreadMessageBusinessService.decrementUnreadMessageCountForOther(chat, user);
                    break;
                case (KafkaUnreadMessageOperation.INCREMENT_UNREAD_MESSAGE_COUNT_FOR_USER_OPERATION):
                    cassandraUnreadMessageBusinessService.incrementUnreadMessageCountForUser(chat, user);
                    break;
                case (KafkaUnreadMessageOperation.DECREMENT_UNREAD_MESSAGE_COUNT_FOR_USER_OPERATION):
                    cassandraUnreadMessageBusinessService.decrementUnreadMessageCountForUser(chat, user);
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
            if (record.value().getChat().getChatType().equals(ChatType.PRIVATE_CHAT.getType())) {

                return kafkaChatMapper.toPrivateChatDomain(record.value().getChat());

            } else if (record.value().getChat().getChatType().equals(ChatType.GROUP_CHAT.getType())) {

                return kafkaChatMapper.toGroupChatDomain(record.value().getChat());

            } else {
                throw new IllegalArgumentException("Illegal chat type in unread message kafka record");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while trying converting record of message to user domain model in unread message kafka distributor");
        }


    }

    private User getUserFromRecord(@NonNull ConsumerRecord<String, KafkaUnreadMessageDTO> record) {
        try {
            return kafkaUserMapper.toDomain(
                    record.value().getUser()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while trying converting record of message to chat domain model in unread message kafka distributor");
        }
    }

}
