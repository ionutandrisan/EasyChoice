package com.licenta.andrisan.easychoice.services;

import com.licenta.andrisan.easychoice.config.EmailConfig;
import com.licenta.andrisan.easychoice.database.DatabaseConnection;
import com.licenta.andrisan.easychoice.payload.AvailabilityResponse;
import com.licenta.andrisan.easychoice.payload.BookingRequest;
import com.licenta.andrisan.easychoice.payload.EmailFeedback;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;

import org.springframework.mail.SimpleMailMessage;

@Service
public class BookingService {

    public AvailabilityResponse checkAvailability(BookingRequest verifyDateRequest) throws SQLException, ClassNotFoundException {

        String runOracleFunction = "{ ? = call verify_booking_availability(?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            Date d = Date.valueOf(verifyDateRequest.getCheckInDate());
            callableStatement.setDate(2, d);
            d = Date.valueOf(verifyDateRequest.getCheckOutDate());
            callableStatement.setDate(3, d);
            callableStatement.setInt(4, verifyDateRequest.getHouseID());
            callableStatement.execute();
            String res = callableStatement.getString(1);
            callableStatement.close();
            conn.DisconnectFromDatabase();
            if(res.startsWith("true")) {
                return new AvailabilityResponse(true, Integer.parseInt(res.split(" ")[1]));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.DisconnectFromDatabase();
        return null;
    }

    public String bookHouse(BookingRequest bookingRequest, int houseID, String email) throws SQLException, ClassNotFoundException {
        String runOracleFunction = "{ ? = call book_house(?, ?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();
        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            Date d = Date.valueOf(bookingRequest.getCheckInDate());
            callableStatement.setDate(2, d);
            d = Date.valueOf(bookingRequest.getCheckOutDate());
            callableStatement.setDate(3, d);
            callableStatement.setInt(4, houseID);
            callableStatement.setString(5, email);
            callableStatement.execute();
            String res = callableStatement.getString(1);
            callableStatement.close();
            conn.DisconnectFromDatabase();
            if(res.equals("false")) {
                return null;
            } else {
                return res;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        conn.DisconnectFromDatabase();
        return null;
    }

    public EmailFeedback getDetailsForEmail(String bookingID) throws SQLException, ClassNotFoundException {
        EmailFeedback emailFeedback = new EmailFeedback();
        String runOracleFunction = "{ call licenta_review_pkg.get_details_for_email_send(?, ?, ?, ?, ?) }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            int bID = Integer.parseInt(bookingID);
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.setInt(1, bID);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.VARCHAR);
            callableStatement.registerOutParameter(5, Types.VARCHAR);
            callableStatement.execute();

            emailFeedback.setFirstName(callableStatement.getString(2));
            emailFeedback.setLastName(callableStatement.getString(3));
            emailFeedback.setHouseName(callableStatement.getString(4));
            emailFeedback.setOwnerEmail(callableStatement.getString(5));

            callableStatement.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        conn.DisconnectFromDatabase();
        return emailFeedback;
    }

    @Async
    public void sendEmailToHost(String booked, BookingRequest bookingRequest, EmailConfig emailCfg) throws SQLException, ClassNotFoundException {
        EmailFeedback emailFeedback = getDetailsForEmail(booked);
        String emailText = emailFeedback.getFirstName() + " " + emailFeedback.getLastName() + " wants to book your"
                + " house " + emailFeedback.getHouseName() + " between " + bookingRequest.getCheckInDate() +
                " and " + bookingRequest.getCheckOutDate();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailCfg.getHost());
        mailSender.setPort(emailCfg.getPort());
        mailSender.setUsername(emailCfg.getUsername());
        mailSender.setPassword(emailCfg.getPassword());

        // Create an email instance
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("easychoice@mail.com");
        mailMessage.setTo(emailFeedback.getOwnerEmail());
        mailMessage.setSubject("New booking");
        mailMessage.setText(emailText);

        // Send mail
        mailSender.send(mailMessage);
    }

    public void deleteReservation(int resID) throws SQLException, ClassNotFoundException {
        String runOracleFunction = "{ call licenta_booking_pkg.delete_reservation(?) }";
        DatabaseConnection conn = new DatabaseConnection();

        try {
            CallableStatement callableStatement = conn.getOracleConnection().prepareCall(runOracleFunction);
            callableStatement.setInt(1, resID);
            callableStatement.execute();
            callableStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        conn.DisconnectFromDatabase();
    }


}
