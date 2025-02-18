package com.nagornov.CorporateMessenger.application.controller;

import com.nagornov.CorporateMessenger.domain.logger.ControllerLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ControllerLogger controllerLogger;

    @PostMapping("/api/test/1")
    ResponseEntity<?> firstTest() {

        controllerLogger.info("AGSGASFASFGGAFS");

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
