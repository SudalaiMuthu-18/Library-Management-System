package com.library.controller;

import com.library.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            return ResponseEntity.ok(statsService.getStats());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
