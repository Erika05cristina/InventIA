package com.inventia.inventia_app.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inventia.inventia_app.entities.ProductosDTO;


/**
 * ProductoRepository
 */
@Repository
public interface ProductoRepository extends JpaRepository<ProductosDTO, Integer> {

    public interface ProductProjection {
        Integer getId();
        String getNombre();
        String getCategoria();
        Double getPrecio();
    }

    ProductosDTO findByNombre(String nombre);
    ProductosDTO findByPrecio(Double precio);
    ProductosDTO findByCategoria(String categoria);
    List<ProductProjection> findByIdIn(List<Integer> productosIds);
}
