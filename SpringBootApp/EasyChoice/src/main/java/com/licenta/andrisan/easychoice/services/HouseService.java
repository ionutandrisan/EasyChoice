package com.licenta.andrisan.easychoice.services;

import com.licenta.andrisan.easychoice.database.DatabaseConnection;
import com.licenta.andrisan.easychoice.models.House;
import com.licenta.andrisan.easychoice.models.Location;
import com.licenta.andrisan.easychoice.payload.HouseResponse;
import com.licenta.andrisan.easychoice.payload.HousesResponse;
import com.licenta.andrisan.easychoice.payload.ReservationResponse;
import com.licenta.andrisan.easychoice.payload.SearchHousesResponse;
import oracle.jdbc.OracleTypes;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.*;
import java.util.LinkedList;

@Service
public class HouseService {

    private final String path = "C:/PC/Licenta2020Andrisan/backend/BookExperience/src/main/resources/static/houseImages/";


    public int getLocationID(Location location) throws SQLException, ClassNotFoundException {

        String runOracleFunction = "{ ? = call verify_location_fct(?, ?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, Types.NUMERIC);
            callableStatement.setString(2, location.getCountry());
            callableStatement.setString(3, location.getState());
            callableStatement.setString(4, location.getCity());
            callableStatement.setString(5, location.getZipcode());
            callableStatement.execute();
            int locationId = callableStatement.getInt(1);
            callableStatement.close();
            conn.DisconnectFromDatabase();
            return locationId;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return -1;

    }

    public int insertHouseInfo(House house) throws SQLException, ClassNotFoundException {

        String runStoredProcedure = "{ call licenta_house_pkg.insert_house_procedure(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, house.getCountry());
            callableStatement.setString(2, house.getCity());
            callableStatement.setString(3, house.getStreetName());
            callableStatement.setDouble(4, house.getCostPerNight());
            callableStatement.setString(5, house.getPhoto());
            callableStatement.setString(6, house.getEmail());
            callableStatement.setString(7, house.getHouseName());
            callableStatement.setString(8, house.getDescription());
            callableStatement.registerOutParameter(9, Types.INTEGER);
            callableStatement.execute();
            int houseId = callableStatement.getInt(9);
            callableStatement.close();
            conn.DisconnectFromDatabase();
            return houseId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return -1;
    }


    public int[] addImagesToLocalDirectory(MultipartFile[] images, int houseID) throws IOException {
        int[] addedImages = new int[2];
        int imageNumber = 0;
        String dirPath = path + houseID;
        File houseDirectory = new File(dirPath);
        if(!houseDirectory.exists()) {
            houseDirectory.mkdir();
        }
        if(houseDirectory.isDirectory()) {
            for (MultipartFile image : images) {
                String type = image.getContentType();
                String[] types = type.split("/");
                if (types[0].equals("image")) {
                    try {
                        String filepath = dirPath + "/" + addedImages[0] + "." + types[1];
                        File newImage = new File(filepath);
                        while(newImage.exists()) {
                            imageNumber++;
                        }
                        image.transferTo(newImage);
                        addedImages[0]++;
                        imageNumber++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    addedImages[1]++;
                }
            }
        }
        return addedImages;
    }


    public boolean checkIfHouseExists(int houseID) throws SQLException, ClassNotFoundException {

        String runOracleFunction = "{ ? = call usf_house_exists(?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setInt(2, houseID);
            callableStatement.execute();
            String userExists = callableStatement.getString(1);
            callableStatement.close();
            conn.DisconnectFromDatabase();
            if(userExists.startsWith("true")) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return false;
    }

    public HouseResponse getHouseInfo(int houseID) throws SQLException, ClassNotFoundException {
        HouseResponse house = new HouseResponse();
        String runStoredProcedure = "{ call licenta_house_pkg.get_a_house(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {

            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setInt(1, houseID);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                house.setHouseId(resultSet.getInt("HOUSE_ID"));
                house.setHouseName(resultSet.getString("HOUSE_NAME"));
                house.setDescription(resultSet.getString("DESCRIPTION"));
                house.setCostPerNight(resultSet.getDouble("COST_PER_NIGHT"));
                house.setMainImage(resultSet.getString("MAIN_IMAGE"));
                house.setCity(resultSet.getString("CITY"));
                house.setCountry(resultSet.getString("COUNTRY"));
                house.setRating(resultSet.getDouble("RATING_AVG"));
                house.setReviewNumber(resultSet.getInt("REVIEW_NUMBER"));
                house.setStreetName(resultSet.getString("STREET_NAME"));
                house.setLastName(resultSet.getString("LAST_NAME"));
                house.setPhoto(resultSet.getString("PHOTO"));
                house.setUserID(resultSet.getInt("PERSON_ID"));
            }

            resultSet.close();
            callableStatement.close();
            conn.DisconnectFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return house;
    }

    public String[] getHouseImages(int houseID) {
        String dir = path + houseID;
        File folder = new File(dir);
        if(folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            String[] filenames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                filenames[i] = files[i].getName();
            }
            return filenames;
        }
        return null;
    }


    public LinkedList<SearchHousesResponse> getHousesAfterCity(String city) throws SQLException, ClassNotFoundException {
        LinkedList<SearchHousesResponse> houses = new LinkedList<>();
        if(city == null) {
            city = "";
        }
        String runStoredProcedure = "{ call licenta_house_pkg.get_houses_by_location(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, city);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                SearchHousesResponse housesResponse = new SearchHousesResponse();
                housesResponse.setHouseId(resultSet.getInt("HOUSE_ID"));
                housesResponse.setHouseName(resultSet.getString("HOUSE_NAME"));
                housesResponse.setDescription(resultSet.getString("DESCRIPTION"));
                housesResponse.setCostPerNight(resultSet.getDouble("COST_PER_NIGHT"));
                housesResponse.setMainImage(resultSet.getString("MAIN_IMAGE"));
                housesResponse.setCity(resultSet.getString("CITY"));
                housesResponse.setCountry(resultSet.getString("COUNTRY"));
                housesResponse.setRating(resultSet.getDouble("RATING_AVG"));
                housesResponse.setReviewNumber(resultSet.getInt("REVIEW_NUMBER"));
                houses.add(housesResponse);
            }
            resultSet.close();
            callableStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return houses;
    }

    public LinkedList<SearchHousesResponse> getHousesAfterCityAndDate(String city, String checkInDate, String checkOutDate)
            throws SQLException, ClassNotFoundException {
        LinkedList<SearchHousesResponse> houses = new LinkedList<>();
        String runStoredProcedure = "{ call licenta_house_pkg.get_available_houses(?, ?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, city);
            Date d = Date.valueOf(checkInDate);
            callableStatement.setDate(2, d);
            d = Date.valueOf(checkOutDate);
            callableStatement.setDate(3, d);
            callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                SearchHousesResponse housesResponse = new SearchHousesResponse();
                housesResponse.setHouseId(resultSet.getInt("HOUSE_ID"));
                housesResponse.setHouseName(resultSet.getString("HOUSE_NAME"));
                housesResponse.setDescription(resultSet.getString("DESCRIPTION"));
                housesResponse.setCostPerNight(resultSet.getDouble("COST_PER_NIGHT"));
                housesResponse.setMainImage(resultSet.getString("MAIN_IMAGE"));
                housesResponse.setCity(resultSet.getString("CITY"));
                housesResponse.setCountry(resultSet.getString("COUNTRY"));
                housesResponse.setRating(resultSet.getDouble("RATING_AVG"));
                housesResponse.setReviewNumber(resultSet.getInt("REVIEW_NUMBER"));
                houses.add(housesResponse);
            }
            resultSet.close();
            callableStatement.close();
        } catch (Exception e) {
            conn.DisconnectFromDatabase();
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return houses;
    }


    public LinkedList<HousesResponse> getCheapestHouses() throws SQLException, ClassNotFoundException {
        LinkedList<HousesResponse> houses = new LinkedList<>();
        String runStoredProcedure = "{ call licenta_house_pkg.get_cheapest_houses(?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            while (resultSet.next()) {
                HousesResponse housesResponse = new HousesResponse();
                housesResponse.setHouseId(resultSet.getInt("house_id"));
                housesResponse.setHouseName(resultSet.getString("house_name"));
                housesResponse.setCostPerNight(resultSet.getDouble("cost_per_night"));
                housesResponse.setMainImage(resultSet.getString("main_image"));
                housesResponse.setCountry(resultSet.getString("country"));
                housesResponse.setRating(resultSet.getDouble("rating_avg"));
                housesResponse.setReviewNumber(resultSet.getDouble("review_number"));
                houses.add(housesResponse);
            }

            resultSet.close();
            callableStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return houses;
    }

    public LinkedList<HousesResponse> getMostAppreciatedHouses() throws SQLException, ClassNotFoundException {
        LinkedList<HousesResponse> houses = new LinkedList<>();
        String runStoredProcedure = "{ call licenta_house_pkg.get_most_appreciated_houses(?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            while (resultSet.next()) {
                HousesResponse housesResponse = new HousesResponse();
                housesResponse.setHouseId(resultSet.getInt("house_id"));
                housesResponse.setHouseName(resultSet.getString("house_name"));
                housesResponse.setCostPerNight(resultSet.getDouble("cost_per_night"));
                housesResponse.setMainImage(resultSet.getString("main_image"));
                housesResponse.setCountry(resultSet.getString("country"));
                housesResponse.setRating(resultSet.getDouble("rating_avg"));
                housesResponse.setReviewNumber(resultSet.getDouble("review_number"));
                houses.add(housesResponse);
            }

            resultSet.close();
            callableStatement.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return houses;
    }


    public LinkedList<ReservationResponse> getAllReservations(String email) throws SQLException, ClassNotFoundException {

        LinkedList<ReservationResponse> reservations = new LinkedList<>();

        String runStoredProcedure = "{ call licenta_booking_pkg.get_all_reservations(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                ReservationResponse res = new ReservationResponse();
                res.setBookingID(resultSet.getInt("BOOKING_ID"));
                res.setCheckInDate(resultSet.getString("BOOKING_STARTING_DATE").split(" ")[0]);
                res.setCheckOutDate(resultSet.getString("BOOKING_ENDING_DATE").split(" ")[0]);
                res.setHouseID(resultSet.getInt("HOUSE_ID"));
                res.setMainImage(resultSet.getString("MAIN_IMAGE"));
                res.setCity(resultSet.getString("CITY"));
                res.setHouseName(resultSet.getString("HOUSE_NAME"));
                reservations.add(res);
            }
            resultSet.close();
            callableStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        conn.DisconnectFromDatabase();
        return reservations;
    }


}
