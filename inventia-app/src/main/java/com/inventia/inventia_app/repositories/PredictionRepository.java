package com.inventia.inventia_app.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventia.inventia_app.entities.PrediccionDTO;

/**
 * PredictionRepository
 */
@Repository
public interface PredictionRepository extends JpaRepository<PrediccionDTO, Long> {

    PrediccionDTO findByFechaCreacionAndTipo(Date fechaCreacion, String tipo);
    PrediccionDTO findByFechaPrediccionAndTipo(String fechaPrediccion, String tipo);

}
