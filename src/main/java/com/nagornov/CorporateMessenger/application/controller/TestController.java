package com.nagornov.CorporateMessenger.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    @PostMapping("/api/test/1")
    ResponseEntity<?> firstTest() {

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
