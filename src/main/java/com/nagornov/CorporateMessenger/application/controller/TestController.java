package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.domain.service.businessService.cassandra.CassandraChatBusinessService;
import com.nagornov.CorporateMessenger.domain.service.domainService.cassandra.CassandraPrivateChatDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import com.nagornov.CorporateMessenger.domain.service.domainService.kafka.KafkaUnreadMessageProducerDomainService;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaTestProducerRepository;
import com.nagornov.CorporateMessenger.infrastructure.persistence.kafka.repository.KafkaUnreadMessageProducerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TestController {

    private final CassandraChatBusinessService cassandraChatBusinessService;
    private final CassandraPrivateChatDomainService cassandraPrivateChatDomainService;
    private final JpaUserDomainService jpaUserDomainService;
    private final KafkaUnreadMessageProducerRepository kafkaUnreadMessageProducerRepository;
    private final KafkaTestProducerRepository kafkaTestProducerRepository;
    private final KafkaUnreadMessageProducerDomainService kafkaUnreadMessageProducerDomainService;

    @PostMapping("/api/test/1")
    ResponseEntity<?> firstTest() {

        return ResponseEntity.status(200).body("First test success");
    }

    @PostMapping("/api/test/2")
    ResponseEntity<?> secondTest() {

        return ResponseEntity.status(200).body("Second test success");
    }

    @PostMapping("/api/test/3")
    ResponseEntity<?> thirdTest() {

        return ResponseEntity.status(200).body("Third test success");
    }

}
