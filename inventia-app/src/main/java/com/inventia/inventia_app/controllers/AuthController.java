package com.inventia.inventia_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.services.AuthService;

import reactor.core.publisher.Flux;

/**
 * AuthControllers
 */
@RestController
@RequestMapping("/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    /*
    @PostMapping("login")
    @CrossOrigin(origins = "*")
    public Flux<ResponseEntity> login(@RequestParam String user, @RequestParam String password) {
    }
    */

}
