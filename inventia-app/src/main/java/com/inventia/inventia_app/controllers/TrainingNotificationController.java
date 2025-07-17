package com.inventia.inventia_app.controllers;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training")
@CrossOrigin(origins = "*")
public class TrainingNotificationController {

    private final AtomicBoolean trainingCompleted = new AtomicBoolean(false);

    @PostMapping("/finished")
    public ResponseEntity<?> trainingFinished(@RequestBody Map<String, Object> body) {
        String status = (String) body.get("status");

        if ("completed".equalsIgnoreCase(status)) {
            trainingCompleted.set(true);
            System.out.println("✅ Spring Boot recibió notificación: Entrenamiento completado");
            return ResponseEntity.ok(Map.of("message", "Notificación recibida correctamente"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Estado no reconocido"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> trainingStatus() {
        if (trainingCompleted.get()) {
            return ResponseEntity.ok(Map.of("status", "completed"));
        } else {
            return ResponseEntity.ok(Map.of("status", "in_progress"));
        }
    }
}
