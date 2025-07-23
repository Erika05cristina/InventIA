package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.entities.UsuarioDTO;
import com.inventia.inventia_app.services.AuthService;

import reactor.core.publisher.Mono;

/**
 * AuthControllers
 */
@RestController
@RequestMapping("/user")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @CrossOrigin(origins = "*")
    //public Mono<ResponseEntity<String>> login(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
    public Mono<ResponseEntity<String>> register(@RequestBody UsuarioDTO usuario) {
        return authService.register(usuario)
            .map(usr -> ResponseEntity.ok().body(usr.toString()))
            .onErrorResume(RuntimeException.class, ex ->
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ex.getMessage())
                )
            )
            .onErrorResume(Exception.class, ex ->
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error inesperado: " + ex.getMessage())
                )
            );
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public Mono<ResponseEntity<String>> login(@RequestParam String email, @RequestParam String password) {
        System.out.println("Login del usuario: " + email);
        return authService.login(email, password)
            .map(usuario -> ResponseEntity.ok().body(usuario.toString()))
            .onErrorResume(RuntimeException.class, ex ->
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ex.getMessage())
                )
            )
            .onErrorResume(Exception.class, ex ->
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error inesperado: " + ex.getMessage())
                )
            );
    }

}
