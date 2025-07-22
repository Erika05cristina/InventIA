package com.inventia.inventia_app.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventia.inventia_app.entities.Usuario;
import com.inventia.inventia_app.databases.CloudPostgresConnection;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<Usuario, Long>{

    Usuario findByUserEmail(String userEmail);

}
