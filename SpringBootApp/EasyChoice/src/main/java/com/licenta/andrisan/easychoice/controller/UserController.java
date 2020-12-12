package com.licenta.andrisan.easychoice.controller;

import com.licenta.andrisan.easychoice.payload.AuthenticationRequest;
import com.licenta.andrisan.easychoice.payload.AuthenticationResponse;
import com.licenta.andrisan.easychoice.models.User;
import com.licenta.andrisan.easychoice.payload.ClientsGraphResponse;
import com.licenta.andrisan.easychoice.payload.StatisticsHouseNameResponse;
import com.licenta.andrisan.easychoice.security.JwtHelper;
import com.licenta.andrisan.easychoice.security.JwtTokenProvider;
import com.licenta.andrisan.easychoice.security.CustomUserDetailsService;
import com.licenta.andrisan.easychoice.services.PersonService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
public class UserController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PersonService personService;

    @GetMapping("/prs")
    public User getPrs() throws SQLException, ClassNotFoundException {
        return personService.getPersonByEmail("'dascal@gmail.com'");
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/register")
    public ResponseEntity insertPerson(@RequestBody User user, @NotNull BindingResult bindingResult)
            throws SQLException, ClassNotFoundException {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if(personService.exists(user.getEmail())) {
            return new ResponseEntity("Email already exists", HttpStatus.CONFLICT);
        }

        personService.insertIntoPersonTable(user);
        return new ResponseEntity("Account created", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                            authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.NOT_FOUND);
        }
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtTokenProvider.generateToken(userDetails);
        final String email = authenticationRequest.getEmail();

        return ResponseEntity.ok(new AuthenticationResponse(jwt, email));
    }


    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader)
            throws SQLException, ClassNotFoundException {

        String email = null;
        email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = personService.getPersonByEmail(email);
        if(user != null)
            return new ResponseEntity(user, HttpStatus.OK);
        else
            return new ResponseEntity<>("Eroare incarcare user", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/userimage/{userID}/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable int userID, @PathVariable String imageName) throws IOException {

        MediaType mediaType = MediaType.IMAGE_JPEG;
        String path = "C:/PC/Licenta2020Andrisan/backend/BookExperience/src/main/resources/static/userPhotos/";

        File img = new File(path + userID + "/" + imageName);
        byte[] bytes = Files.readAllBytes(img.toPath());

        if(imageName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(bytes);
    }

    @PostMapping(value = "/changeUserPhoto/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addUserImage(@RequestParam MultipartFile userImage, @PathVariable int userId) {

        personService.addUserImageToDirectory(userId, userImage);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/statistics/getname")
    public ResponseEntity<?> getLastName(@RequestHeader("Authorization") String authorizationHeader) {

        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        String lastName = null;

        try {
            lastName = personService.getLastName(email);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(lastName, HttpStatus.OK);

    }

    @GetMapping(value = "/statistics/getNoClientsPerMonth")
    public ResponseEntity<?> getNoClientsPerMonth(@RequestHeader("Authorization") String authorizationHeader) {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        ClientsGraphResponse clients = null;

        try {
            clients = personService.getNumberOfClientsPerMonth(email);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping(value = "/statistics/userHouses")
    public ResponseEntity<?> getStatisticsForHouse(@RequestHeader("Authorization") String authorizationHeader) {

        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        LinkedList<StatisticsHouseNameResponse> houses;
        try {
            houses = personService.getUserHouseNames(email);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(houses, HttpStatus.OK);

    }

    @GetMapping(value = "/statistics/houseReviewsNumber")
    public ResponseEntity<?> getHouseReviewsForStatistics(@RequestHeader("Authorization") String authorizationHeader) {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        HashMap<Integer, Integer> reviews = null;

        try {
            reviews = personService.getReviewsNumber(email);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping(value = "/statistics/houseReviewRatings")
    public ResponseEntity<?> getHouseRatingsForStatistics(@RequestHeader("Authorization") String authorizationHeader) {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        LinkedList<Integer> ratings = null;

        try {
            ratings = personService.getReviewsRating(email);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(ratings, HttpStatus.OK);

    }

    @GetMapping(value = "/statistics/getNoClientsPerMonth/{houseID}")
    public ResponseEntity<?> getNoClientsPerMonthForHouse(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable int houseID) {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        ClientsGraphResponse clients = null;

        try {
            clients = personService.getNumberOfClientsPerMonthForHouse(email, houseID);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping(value = "/statistics/houseReviewsNumber/{houseID}")
    public ResponseEntity<?> getHouseReviewsForStatisticsForHouse(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int houseID) {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        HashMap<Integer, Integer> reviews = null;

        try {
            reviews = personService.getReviewsNumberForHouse(email, houseID);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping(value = "/statistics/houseReviewRatings/{houseID}")
    public ResponseEntity<?> getHouseRatingsForStatisticsForHouse(@RequestHeader("Authorization") String authorizationHeader,
                                                                  @PathVariable int houseID) {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        LinkedList<Integer> ratings = null;

        try {
            ratings = personService.getReviewsRatingForHouse(email, houseID);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

}
