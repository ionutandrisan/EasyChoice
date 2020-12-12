package com.licenta.andrisan.easychoice.models;

import com.licenta.andrisan.easychoice.interfaces.IUser;

public class AuthenticationUser implements IUser {

    private String email;
    private String password;

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
