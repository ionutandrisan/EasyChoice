package com.licenta.andrisan.easychoice.services;

import com.licenta.andrisan.easychoice.database.DatabaseConnection;
import com.licenta.andrisan.easychoice.models.AuthenticationUser;
import com.licenta.andrisan.easychoice.models.User;
import com.licenta.andrisan.easychoice.payload.ClientsGraphResponse;
import com.licenta.andrisan.easychoice.payload.StatisticsHouseNameResponse;
import oracle.jdbc.OracleTypes;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;


@Service
public class PersonService {

    private final String path = "C:/PC/Licenta2020Andrisan/backend/BookExperience/src/main/resources/static/userPhotos/";

    public PersonService() {
    }

    public User getPersonByEmail(String email) throws SQLException, ClassNotFoundException {
        User prs = new User();
        String runStoredProcedure = "{ call licenta_person_pkg.get_person_by_email(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try  {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                prs.setPersonID(resultSet.getInt("PERSON_ID"));
                prs.setFirstName(resultSet.getString("FIRST_NAME"));
                prs.setLastName(resultSet.getString("LAST_NAME"));
                prs.setEmail(resultSet.getString("EMAIL_ID"));
                prs.setPassword(resultSet.getString("PASSWORD"));
                prs.setPhotoPath(resultSet.getString("PHOTO"));
                prs.setBirthDate(resultSet.getString("BIRTH_DATE").split(" ")[0]);
                prs.setPhoneNumber(resultSet.getString("PHONE_NUMBER"));
            }
            resultSet.close();
            callableStatement.close();
            conn.DisconnectFromDatabase();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return prs;
    }

    public AuthenticationUser getEmailAndPassword(String email) throws SQLException, ClassNotFoundException {
        AuthenticationUser prs = new AuthenticationUser();
        String runStoredProcedure = "{ call licenta_person_pkg.get_email_and_password(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                prs.setEmail(resultSet.getString("EMAIL_ID"));
                prs.setPassword(resultSet.getString("PASSWORD"));
            }
            resultSet.close();
            callableStatement.close();
            conn.DisconnectFromDatabase();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return prs;
    }

    public void insertIntoPersonTable(int idPerson, String firstName, String lastName, String email, String password,
                                      String photo, String birthDate, String phoneNumber)
            throws SQLException, ClassNotFoundException {
        User user = new User(idPerson, firstName, lastName, email, password, photo, birthDate, phoneNumber);
        String runStoredProcedure = "{ call licenta_person_pkg.insert_person_procedure(?, ?, ?, ?, ?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try ( CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure); ) {
            callableStatement.setString(1, user.getFirstName());
            callableStatement.setString(2, user.getLastName());
            callableStatement.setString(3, user.getEmail());
            callableStatement.setString(4, user.getPassword());
            callableStatement.setString(5, user.getPhotoPath());
            callableStatement.setString(6, birthDate);
            callableStatement.setString(7, user.getPhoneNumber());
            callableStatement.execute();

            callableStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
    }

    public void insertIntoPersonTable(@NotNull User user) throws SQLException, ClassNotFoundException {
        String runStoredProcedure = "{ call licenta_person_pkg.insert_person_procedure(?, ?, ?, ?, ?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, user.getFirstName());
            callableStatement.setString(2, user.getLastName());
            callableStatement.setString(3, user.getEmail());
            callableStatement.setString(4, user.getPassword());
            callableStatement.setString(5, user.getPhotoPath());
            callableStatement.setString(6, user.getBirthDate());
            callableStatement.setString(7, user.getPhoneNumber());
            callableStatement.execute();

            callableStatement.close();
            connection.DisconnectFromDatabase();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
    }

    public boolean exists(String email) throws SQLException, ClassNotFoundException {
        String runOracleFunction = "{ ? = call usf_email_exists(?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setString(2, email);
            callableStatement.execute();
            String res = callableStatement.getString(1);
            callableStatement.close();
            conn.DisconnectFromDatabase();
            if(res.equals("true")) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(String email, String password) throws SQLException, ClassNotFoundException {
        String runOracleFunction = "{ ? = call usf_user_can_login(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setString(2, email);
            callableStatement.setString(3, password);
            callableStatement.execute();
            if(callableStatement.getString(1).equals("true")) {
                callableStatement.close();
                conn.DisconnectFromDatabase();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return false;
    }

    public void addUserImageToDirectory(int id, MultipartFile file) {
        String dirPath = path + id;
        File userImageDirectory = new File(dirPath);
        if(!userImageDirectory.exists()) {
            userImageDirectory.mkdir();
        }
        if(userImageDirectory.isDirectory()) {
            String type = file.getContentType();
            if(type.startsWith("image")) {
                try {
                    String filepath = dirPath + "/0." + type.split("/")[1];
                    File newImage = new File(filepath);
                    file.transferTo(newImage);
                    updateUserPhoto(id, "0." + type.split("/")[1]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateUserPhoto(int id, String imageName) throws SQLException, ClassNotFoundException {
        String runStoredProcedure = "{ call licenta_person_pkg.set_user_photo(?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runStoredProcedure);
        callableStatement.setString(1, imageName );
        callableStatement.setInt(2, id);
        callableStatement.execute();
        callableStatement.close();
        conn.DisconnectFromDatabase();
    }

    public String getLastName(String email) throws SQLException, ClassNotFoundException {
        String lastName = null;
        String runStoredProcedure = "{ call licenta_person_pkg.get_name(?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.execute();
            lastName = callableStatement.getString(2);
            callableStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
        return lastName;
    }

    public ClientsGraphResponse getNumberOfClientsPerMonth(String email)
            throws SQLException, ClassNotFoundException {
        ClientsGraphResponse clients = new ClientsGraphResponse();
        int year = 2020;
        int nrMonths = 7;

        String runStoredProcedure = "{ call licenta_person_pkg.get_first_half_year_clients(?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.setInt(2, year);
            callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
            callableStatement.execute();

            HashMap<Integer, Integer> nrClients = new HashMap<>();
            HashMap<Integer, Integer> earnings = new HashMap<>();
            HashMap<Integer, Integer> nrReviews = new HashMap<>();
            HashMap<Integer, Double> reviewAvg = new HashMap<>();

            for(int i = 1; i <= nrMonths; i++) {
                nrClients.put(i, 0);
                earnings.put(i, 0);
                nrReviews.put(i, 0);
                reviewAvg.put(i, 0.0);
            }

            ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
            while (resultSet.next()) {
                nrClients.put(resultSet.getInt("C_MONTH"), resultSet.getInt("C_CLIENTS"));
                earnings.put(resultSet.getInt("C_MONTH"), resultSet.getInt("C_EARNINGS"));
                nrReviews.put(resultSet.getInt("C_MONTH"), resultSet.getInt("C_REVIEW_NUMBER"));
                reviewAvg.put(resultSet.getInt("C_MONTH"), resultSet.getDouble("C_RATING_AVG"));
            }
            resultSet.close();
            callableStatement.close();
            connection.DisconnectFromDatabase();

            clients.setClientsHashMap(nrClients);
            clients.setEarningsPerMonth(earnings);
            clients.setNrReviews(nrReviews);
            clients.setReviewAvg(reviewAvg);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return clients;
    }

    public LinkedList<StatisticsHouseNameResponse> getUserHouseNames(String email) throws SQLException, ClassNotFoundException {
        LinkedList<StatisticsHouseNameResponse> houses = new LinkedList<>();

        String runOracleFunction = "{ ? = call get_house_name_for_user(?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);
            callableStatement.setString(2, email);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(1);
            while (resultSet.next()) {
                StatisticsHouseNameResponse house = new StatisticsHouseNameResponse();
                house.setHouseName(resultSet.getString("HOUSE_NAME"));
                house.setHouseID(resultSet.getInt("HOUSE_ID"));
                houses.add(house);
            }
            resultSet.close();
            callableStatement.close();
            conn.DisconnectFromDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return houses;
    }

    public HashMap<Integer, Integer> getReviewsNumber(String email)
            throws SQLException, ClassNotFoundException {
        HashMap<Integer, Integer> reviews = new HashMap<Integer, Integer>();
        int year = 2020;
        int nrMonths = 7;

        for(int i = 1; i <= nrMonths; i++) {
            reviews.put(i, 0);
        }

        String runStoredProcedure = "{ call licenta_person_pkg.get_first_half_year_reviews(?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.setInt(2, year);
            callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
            while (resultSet.next()) {
                reviews.put(resultSet.getInt("R_MONTH"), resultSet.getInt("NR_REVIEWS"));
            }
            resultSet.close();
            callableStatement.close();
            connection.DisconnectFromDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
        return reviews;
    }

    public LinkedList<Integer> getReviewsRating(String email) throws SQLException, ClassNotFoundException {
        LinkedList<Integer> ratings = new LinkedList<>();

        String runStoredProcedure = "{ call licenta_person_pkg.get_first_half_year_ratings(?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(2);
            while (resultSet.next()) {
                ratings.add(resultSet.getInt("RATING"));
            }

            resultSet.close();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
        return ratings;
    }

    public ClientsGraphResponse getNumberOfClientsPerMonthForHouse(String email, int houseID)
            throws SQLException, ClassNotFoundException {
        ClientsGraphResponse clients = new ClientsGraphResponse();
        int year = 2020;
        int nrMonths = 7;

        String runStoredProcedure = "{ call licenta_person_pkg.get_nr_clients_for_house(?, ?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.setInt(2, houseID);
            callableStatement.setInt(3, year);
            callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
            callableStatement.execute();

            HashMap<Integer, Integer> nrClients = new HashMap<>();
            HashMap<Integer, Integer> earnings = new HashMap<>();

            for(int i = 1; i <= nrMonths; i++) {
                nrClients.put(i, 0);
                earnings.put(i, 0);

            }

            ResultSet resultSet = (ResultSet) callableStatement.getObject(4);
            while (resultSet.next()) {
                nrClients.put(resultSet.getInt("C_MONTH"), resultSet.getInt("C_CLIENTS"));
                earnings.put(resultSet.getInt("C_MONTH"), resultSet.getInt("C_EARNINGS"));
            }

            clients.setClientsHashMap(nrClients);
            clients.setEarningsPerMonth(earnings);

            resultSet.close();
            callableStatement.close();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
        return clients;
    }

    public HashMap<Integer, Integer> getReviewsNumberForHouse(String email, int houseID)
            throws SQLException, ClassNotFoundException {
        HashMap<Integer, Integer> reviews = new HashMap<Integer, Integer>();
        int year = 2020;
        int nrMonths = 7;

        for(int i = 1; i <= nrMonths; i++) {
            reviews.put(i, 0);
        }

        String runStoredProcedure = "{ call licenta_person_pkg.get_reviews_for_house(?, ?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.setInt(2, houseID);
            callableStatement.setInt(3, year);
            callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(4);
            while (resultSet.next()) {
                reviews.put(resultSet.getInt("R_MONTH"), resultSet.getInt("NR_REVIEWS"));
            }

            resultSet.close();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
        return reviews;
    }

    public LinkedList<Integer> getReviewsRatingForHouse(String email, int houseID) throws SQLException, ClassNotFoundException {
        LinkedList<Integer> ratings = new LinkedList<>();

        String runStoredProcedure = "{ call licenta_person_pkg.get_ratings_for_house(?, ?, ?) }";
        DatabaseConnection connection = new DatabaseConnection();
        try {
            CallableStatement callableStatement = connection.getOracleConnection().prepareCall(runStoredProcedure);
            callableStatement.setString(1, email);
            callableStatement.setInt(2, houseID);
            callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
            callableStatement.execute();

            ResultSet resultSet = (ResultSet) callableStatement.getObject(3);
            while (resultSet.next()) {
                ratings.add(resultSet.getInt("RATING"));
            }

            resultSet.close();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.DisconnectFromDatabase();
        return ratings;
    }

}
