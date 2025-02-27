package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.domain.service.domainService.jpa.JpaUserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final JpaUserDomainService jpaUserDomainService;

    @PostMapping("/api/test/1")
    ResponseEntity<?> firstTest() {

        try {
            jpaUserDomainService.getByUsername("asdasdasd");
        } catch (Exception e) {
            log.error("Some error occurred", e);
            throw e;
        }

        return ResponseEntity.status(200).body("1 test success");
    }

    @PostMapping("/api/test/2")
    ResponseEntity<?> secondTest() {

        return ResponseEntity.status(200).body("2 test success");
    }

    @PostMapping("/api/test/3")
    ResponseEntity<?> thirdTest() {

        return ResponseEntity.status(200).body("3 test success");
    }

}
