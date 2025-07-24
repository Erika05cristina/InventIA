package com.inventia.inventia_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventia.inventia_app.repositories.ProductoRepository;

import reactor.core.publisher.Mono;

import com.inventia.inventia_app.entities.ProductosDTO;

/**
 * ProductsController
 */
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductsController {

    private ProductoRepository productoRepository;

    @Autowired
    public ProductsController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/by-id")
    @CrossOrigin(origins = "*")
    public Mono<ProductosDTO> getProductPorId(@RequestParam Integer productId) {
        try {
            ProductosDTO producto = productoRepository.findById(productId).get();
            return Mono.just(producto);
        } catch (Exception e) {
            System.err.println("No existe un producto con el id: " + productId);
            return Mono.error(new RuntimeException("No existe un producto con el id: " + productId));
        }
    }

    @GetMapping("/all")
    @CrossOrigin(origins = "*")
    public Mono<List<ProductosDTO>> getProducts() {
        try {
            List<ProductosDTO> productos = productoRepository.findAll();
            return Mono.just(productos);
        } catch (Exception e) {
            System.err.println("Error inesperado al obtener todos los productos");
            return Mono.error(new RuntimeException("Error inesperado al obtener todos los productos"));
        }
    }

}
