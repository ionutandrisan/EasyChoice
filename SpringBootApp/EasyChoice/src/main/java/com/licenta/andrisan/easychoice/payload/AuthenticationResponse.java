package com.licenta.andrisan.easychoice.payload;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private final String jwt;
    private final String email;

    public AuthenticationResponse(String jwt, String email) {
        this.jwt = jwt;
        this.email = email;
    }

    public String getJwt() {
        return jwt;
    }

    public String getEmail() { return email; }

}
