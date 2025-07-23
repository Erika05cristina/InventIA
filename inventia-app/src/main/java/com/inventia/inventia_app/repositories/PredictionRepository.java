package com.inventia.inventia_app.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventia.inventia_app.entities.PrediccionDTO;

/**
 * PredictionRepository
 */
@Repository
public interface PredictionRepository extends JpaRepository<PrediccionDTO, Long> {

    PrediccionDTO findByFechaCreacionAndTipo(Date fechaCreacion, String tipo);
    List<PrediccionDTO> findAllByFechaPrediccionAndTipo(String fechaPrediccion, String tipo);

}
