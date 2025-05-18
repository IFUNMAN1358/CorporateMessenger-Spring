package com.nagornov.CorporateMessenger.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TestController {

    @PostMapping(path = "/api/test/1")
    ResponseEntity<?> firstTest() {

        return ResponseEntity.status(200).body("1 test success");
    }

    @PostMapping(path = "/api/test/2")
    ResponseEntity<?> secondTest() {

        return ResponseEntity.status(200).body("2 test success");
    }

    @GetMapping(path = "/api/test/3")
    ResponseEntity<?> thirdTest() {

        return ResponseEntity.status(200).body("3 test success");
    }

    @GetMapping(path = "/api/test/4")
    ResponseEntity<?> fourthTest() {

        return ResponseEntity.status(200).body("4 test success");
    }

}