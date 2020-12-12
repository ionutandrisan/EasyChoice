package com.licenta.andrisan.easychoice.payload;

import com.licenta.andrisan.easychoice.encryption.PasswordEncryption;
import com.licenta.andrisan.easychoice.interfaces.IUser;

import java.io.Serializable;


public class AuthenticationRequest implements IUser, Serializable {

    private String email;
    private  String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = PasswordEncryption.generateSecurePassword(password);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = (email);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = PasswordEncryption.generateSecurePassword(password);
    }


}
