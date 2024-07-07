package com.moment.test.controller;

import com.moment.common.exception.RestApiException;
import com.moment.common.exception.test.TestErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class testController {

    @GetMapping("/test")
    @Operation(summary = "test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("test success!", HttpStatus.OK);
    }
    @GetMapping("/test1")
    @Operation(summary = "custom err test")
    public ResponseEntity<String> test1(){
        throw new RestApiException(TestErrorCode.TEST_ERROR_1);
    }
    @GetMapping("/test2")
    @Operation(summary = "runtime err test")
    public ResponseEntity<String> test2(){
        throw new RuntimeException("test npe");
    }
}
