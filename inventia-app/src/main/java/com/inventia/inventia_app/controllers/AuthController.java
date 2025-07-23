package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventia.inventia_app.entities.UsuarioDTO;
import com.inventia.inventia_app.services.AuthService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody UsuarioDTO usuario) {
        return authService.register(usuario)
            .map(usr -> ResponseEntity.ok().body(usr.toString()))
            .onErrorResume(RuntimeException.class, ex ->
                Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage()))
            )
            .onErrorResume(Exception.class, ex ->
                Mono.just(ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + ex.getMessage()))
            );
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody UsuarioDTO loginDto) {
        String email = loginDto.getUser();        // obtiene el correo
        String password = loginDto.getPassword(); // obtiene la contraseña

        System.out.println("Login del usuario: " + email);

        return authService.login(email, password)
            .map(usuario -> ResponseEntity.ok().body(usuario.toString()))
            .onErrorResume(RuntimeException.class, ex ->
                Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage()))
            )
            .onErrorResume(Exception.class, ex ->
                Mono.just(ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + ex.getMessage()))
            );
    }
}