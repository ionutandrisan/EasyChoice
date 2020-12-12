package com.licenta.andrisan.easychoice.security;

import com.licenta.andrisan.easychoice.models.AuthenticationUser;
import com.licenta.andrisan.easychoice.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    PersonService personService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthenticationUser user = null;
        try {
            if (personService.exists(email)) {
                user = personService.getEmailAndPassword(email);
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return UserPrincipal.create(user.getEmail(), user.getPassword(), new ArrayList<>());
    }



}
