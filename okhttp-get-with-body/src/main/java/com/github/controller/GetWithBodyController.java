package com.github.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GetWithBodyController {

    @RequestMapping("/hello")
    public ResponseEntity<CustomRequest> hello(@RequestBody CustomRequest request) {
        return ResponseEntity.ok(request);
    }

    @Data
    public static class CustomRequest {

        private String name;
    }
}
