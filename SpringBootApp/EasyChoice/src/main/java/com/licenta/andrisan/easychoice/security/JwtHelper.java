package com.licenta.andrisan.easychoice.security;

public class JwtHelper {

    public static String getEmailFromAuthorizationHeader(String authorizationHeader, JwtTokenProvider jwtTokenProvider) {
        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtTokenProvider.extractUsername(jwt);
        }

        return email;
    }

    public static String getJwtFromAuthorizationHeader(String authorizationHeader) {
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        return jwt;
    }

}
