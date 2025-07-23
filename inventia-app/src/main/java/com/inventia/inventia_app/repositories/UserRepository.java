package com.inventia.inventia_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventia.inventia_app.entities.UsuarioDTO;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<UsuarioDTO, Long>{

    UsuarioDTO findByUserEmail(String userEmail);

}
