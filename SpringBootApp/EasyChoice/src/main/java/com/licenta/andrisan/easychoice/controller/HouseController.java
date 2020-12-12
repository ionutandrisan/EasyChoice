package com.licenta.andrisan.easychoice.controller;

import com.licenta.andrisan.easychoice.config.EmailConfig;
import com.licenta.andrisan.easychoice.models.House;
import com.licenta.andrisan.easychoice.models.Location;
import com.licenta.andrisan.easychoice.payload.*;
import com.licenta.andrisan.easychoice.security.JwtHelper;
import com.licenta.andrisan.easychoice.security.JwtTokenProvider;
import com.licenta.andrisan.easychoice.services.BookingService;
import com.licenta.andrisan.easychoice.services.FacilityService;
import com.licenta.andrisan.easychoice.services.HouseService;
import com.licenta.andrisan.easychoice.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.LinkedList;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
public class HouseController {

    @Autowired
    HouseService houseService;

    @Autowired
    BookingService bookingService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailConfig emailCfg;


    @PostMapping("addHouse/location")
    public ResponseEntity<?> getLocationId(@RequestBody Location location) throws SQLException, ClassNotFoundException {

        int locId = houseService.getLocationID(location);

        if(locId == -1) {
            return new ResponseEntity<>(new LocationResponse(locId), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new LocationResponse(locId), HttpStatus.OK);
    }

    @PostMapping(value = "/addHouse/houseInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setHouseInfo(@RequestParam MultipartFile[] houseImages, @RequestParam String country,
                                          @RequestParam String city, @RequestParam String streetName,
                                          @RequestParam String costPerNight, @RequestParam String houseName,
                                          @RequestParam String description, @RequestParam String[] facilities,
                                          @RequestHeader("Authorization") String authorizationHeader)
            throws SQLException, ClassNotFoundException, IOException {
        String email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if(email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        double cost = Double.parseDouble(costPerNight);

        String mainImg = null;
        if(houseImages.length > 0) {
            mainImg = "0." + houseImages[0].getContentType().substring(houseImages[0].getContentType().lastIndexOf("/") + 1);
        }
        House house = new House(country, city, streetName, cost, email, mainImg, houseName, description);

        int houseId = houseService.insertHouseInfo(house);
        if(houseId == -1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        facilityService.addHouseFacilities(houseId, facilities);

        int[] imagesAdded = houseService.addImagesToLocalDirectory(houseImages, houseId);

        if(imagesAdded[0] == 0) {
            return new ResponseEntity<>(imagesAdded[0] + " successfully images added\n" + imagesAdded[1] +
                    " failed images added", HttpStatus.CREATED);
        }

        return new ResponseEntity<>(imagesAdded[0] + " successfully images added\n" + imagesAdded[1] +
                " failed images added", HttpStatus.OK);
    }

    @GetMapping("/house/getAllFacilities")
    public ResponseEntity<?> getAllFacilities() throws SQLException, ClassNotFoundException {
        LinkedList<String> facilities = facilityService.getAllFacilities();

        return ResponseEntity
                .ok()
                .body(facilities);
    }


    @GetMapping(value = "/houseimg/{houseID}/{image}")
    public ResponseEntity<byte[]> getImage(@PathVariable int houseID, @PathVariable String image) throws IOException {

        MediaType mediaType = MediaType.IMAGE_JPEG;
        String path = "C:/PC/Licenta2020Andrisan/backend/BookExperience/src/main/resources/static/houseImages/";

        File img = new File(path + + houseID + "/" + image);
        byte[] bytes = Files.readAllBytes(img.toPath());

        if(image.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(bytes);
    }

    @GetMapping(value = "/house/{houseID}")
    public ResponseEntity<?> getHouseInfo(@PathVariable int houseID)
            throws SQLException, ClassNotFoundException {

        boolean exists = houseService.checkIfHouseExists(houseID);
        if (exists) {
            HouseResponse house = houseService.getHouseInfo(houseID);
            house.setHouseImages(houseService.getHouseImages(houseID));

            return ResponseEntity
                    .ok()
                    .body(house);
        }

        return ResponseEntity
                .notFound()
                .build();
    }

    @PostMapping(value = "/house/verifyAvailability/{houseID}")
    public ResponseEntity<?> verifyIfAvailable(@RequestBody BookingRequest verify, @PathVariable int houseID)
            throws SQLException, ClassNotFoundException {

        AvailabilityResponse houseAvailable = bookingService.checkAvailability(verify);
        if(houseAvailable == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity
                .ok()
                .body(houseAvailable);
    }


    @GetMapping(value = "/house/getAllHouseReviews/{houseID}")
    public ResponseEntity<?> getHouseReviews(@PathVariable int houseID) throws SQLException, ClassNotFoundException {

        LinkedList<ReviewRequest> reviews = reviewService.getHouseReviews(houseID);

        return ResponseEntity
                .ok()
                .body(reviews);
    }


    @GetMapping(value = "/house/getHouseFacilities/{houseID}")
    public ResponseEntity<?> getHouseFacilities(@PathVariable int houseID) throws SQLException, ClassNotFoundException {

        LinkedList<String> facilities = facilityService.getHouseFacilities(houseID);

        return ResponseEntity
                .ok()
                .body(facilities);
    }


    @PostMapping(value = "/house/addNewReview/{houseID}")
    public ResponseEntity<?> addNewReview(@RequestBody WriteReviewRequest reviewComment, @PathVariable int houseID,
                                          @RequestHeader("Authorization") String authorizationHeader)
            throws ClassNotFoundException {

        String email = null;
        email = JwtHelper.getEmailFromAuthorizationHeader(authorizationHeader, jwtTokenProvider);
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        try {
            reviewService.addNewReview(reviewComment, houseID, email);
        } catch (SQLException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/house/bookHouse/{houseID}")
    public ResponseEntity<?> bookHouse(@RequestBody BookingRequest bookingRequest, @PathVariable int houseID,
                                       @RequestHeader("Authorization") String authHeader)
            throws SQLException, ClassNotFoundException {

        String email = null;
        email = JwtHelper.getEmailFromAuthorizationHeader(authHeader, jwtTokenProvider);
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String booked = bookingService.bookHouse(bookingRequest, houseID, email);

        if(booked != null) {

            bookingService.sendEmailToHost(booked, bookingRequest, emailCfg);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/searchHouse/{city}")
    public ResponseEntity<?> searchHouses(@PathVariable String city) {

        LinkedList<SearchHousesResponse> houses = null;
        try {
            houses = houseService.getHousesAfterCity(city);
        } catch (SQLException | ClassNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return ResponseEntity
                .ok()
                .body(houses);
    }

    @GetMapping(value = "/home/cheapestHouses")
    public ResponseEntity<?> getCheapestHouses()  {

        LinkedList<HousesResponse> houses = null;


        try {
            houses = houseService.getCheapestHouses();
        } catch (SQLException | ClassNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return ResponseEntity
                .ok()
                .body(houses);
    }

    @GetMapping(value = "/home/mostAppreciatedHouses")
    public ResponseEntity<?> getMostAppreciatedHouses()  {

        LinkedList<HousesResponse> houses = null;


        try {
            houses = houseService.getMostAppreciatedHouses();
        } catch (SQLException | ClassNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return ResponseEntity
                .ok()
                .body(houses);
    }

    @GetMapping(value = "/myreservations")
    public ResponseEntity<?> getAllReservations(@RequestHeader("Authorization") String authHeader) {

        LinkedList<ReservationResponse> reservations = null;
        String email = null;
        email = JwtHelper.getEmailFromAuthorizationHeader(authHeader, jwtTokenProvider);
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            reservations = houseService.getAllReservations(email);
        } catch (SQLException | ClassNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteReservation/{resID}")
    public ResponseEntity<?> deleteReservation(@PathVariable int resID, @RequestHeader("Authorization") String authHeader) {

        String email = null;
        email = JwtHelper.getEmailFromAuthorizationHeader(authHeader, jwtTokenProvider);
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            bookingService.deleteReservation(resID);
        } catch (SQLException | ClassNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
