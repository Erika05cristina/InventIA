package com.inventia.inventia_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/training")
@CrossOrigin(origins = "*")
public class TrainingNotificationController {

    @PostMapping("/finished")
    public ResponseEntity<?> trainingFinished(@RequestBody Map<String, Object> body) {
        String status = (String) body.get("status");

        if ("completed".equalsIgnoreCase(status)) {
            System.out.println("✅ Spring Boot recibió notificación: Entrenamiento completado");
            // Aquí puedes actualizar estado interno, base de datos, o notificar al frontend
            return ResponseEntity.ok(Map.of("message", "Notificación recibida correctamente"));
        } else {
            System.out.println("⚠️ Spring Boot recibió un estado desconocido: " + status);
            return ResponseEntity.badRequest().body(Map.of("message", "Estado no reconocido"));
        }
    }
}
