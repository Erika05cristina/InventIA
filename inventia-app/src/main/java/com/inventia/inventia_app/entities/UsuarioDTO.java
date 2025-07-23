package com.inventia.inventia_app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * User
 */
@Entity(name = "usuario")
public class UsuarioDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer userId;
    @Column(name = "usuario_name")
    @JsonProperty("name")
    private String userName;
    @Column(name = "usuario_email")
    @JsonProperty("email")
    private String userEmail;
    @Column(name = "usuario_password")
    @JsonProperty("password")
    private String userPass;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String userName, String userEmail, String userPass) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPass = userPass;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return this.userName;
    }

    public void setUser(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUser() {
        return this.userEmail;
    }

    public void setPassword(String userPass) {
        this.userPass = userPass;
    }

    public String getPassword() {
        return this.userPass;
    }

    @Override
    public String toString() {
        return "User{" +
               "userName='" + userName + '\'' +
               ", userEmail='" + userEmail + '\'' +
               ", userPass='[PROTECTED]'" + // ¡Importante! No expongas la contraseña
               '}';
    }

}
