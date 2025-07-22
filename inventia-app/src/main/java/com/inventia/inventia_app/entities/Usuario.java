package com.inventia.inventia_app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * User
 */
@Entity
public class Usuario {

    @Id
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPass;

    public Usuario(String userName, String userEmail, String userPass) {
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
