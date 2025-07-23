package com.inventia.inventia_app.repositories;

import org.springframework.stereotype.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.inventia.inventia_app.entities.ProductosDTO;
/**
 * ProductoRepository
 */
@Repository
public interface ProductoRepository extends JpaRepository<ProductosDTO, Long> {

    ProductosDTO findByNombre(String nombre);
    ProductosDTO findByPrecio(Double precio);
    ProductosDTO findByCategoria(String categoria);
}
