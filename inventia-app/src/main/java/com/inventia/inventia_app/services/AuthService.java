package com.inventia.inventia_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventia.inventia_app.entities.UsuarioDTO;
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

    public Mono<UsuarioDTO> register(UsuarioDTO usuario) {
        return Mono.fromCallable(() -> {
            UsuarioDTO usr = userRepository.save(usuario);
            System.out.println(usr);
            return usr;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(usr -> {
            if (!usr.getName().isEmpty()) {
                return Mono.just(usr);
            } else {
                return Mono.error(new RuntimeException("Error al crear el usuario"));
            }
        });
    }

    public Mono<UsuarioDTO> login(String email, String password) {
        return Mono.fromCallable(() -> {
            UsuarioDTO usuario = userRepository.findByUserEmail(email);
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
