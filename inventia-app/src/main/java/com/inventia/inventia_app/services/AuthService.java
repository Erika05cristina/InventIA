package com.inventia.inventia_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventia.inventia_app.entities.Usuario;
import com.inventia.inventia_app.repositories.UserRepository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * AuthService
 */
@Service
public class AuthService {

    private UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Usuario> register(String name, String email, String password) {
        return Mono.fromCallable(() -> {
            Usuario usuario = new Usuario(name, email, password);
            Usuario usr = userRepository.save(usuario);
            return usr;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(usuario -> {
            if (usuario.getUserId() == null ) {
                return Mono.just(usuario);
            } else {
                return Mono.error(new RuntimeException("Error al crear el usuario"));
            }
        });
    }

    public Mono<Usuario> login(String email, String password) {
        return Mono.fromCallable(() -> {
            Usuario usuario = userRepository.findByUserEmail(email);
            return usuario;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(usuario -> {
            if (usuario.getPassword().equalsIgnoreCase(password))
                return Mono.just(usuario);
            else
                return Mono.error(new RuntimeException("Contraseña incorrecta"));
        });
    }

}
