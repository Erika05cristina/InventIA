package com.inventia.inventia_app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * DataController
 */
@RestController
public class DataController {

    public DataController() {}

    @PostMapping("/cargardatos")
    public void cargarDatos(@RequestBody String body){
        //TODO: Guardar los datos en la base
    }
}
