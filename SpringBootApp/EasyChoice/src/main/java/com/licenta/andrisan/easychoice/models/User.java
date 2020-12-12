package com.licenta.andrisan.easychoice.models;

import com.licenta.andrisan.easychoice.encryption.PasswordEncryption;
import com.licenta.andrisan.easychoice.interfaces.IUser;


public class User implements IUser {

    private int personID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String photoPath;
    private String birthDate;
    private String phoneNumber;

    public User() {
    }

    public User(int id, String fName, String lName, String email, String pass, String photo, String bDate,
                String phoneNumber) {
        personID = id;
        firstName = fName;
        lastName = lName;
        this.email = (email);
        password = PasswordEncryption.generateSecurePassword(pass);
        photoPath = photo;
        birthDate = bDate;
        this.phoneNumber = phoneNumber;
    }

    public User(User prs) {
        personID = prs.getPersonID();
        firstName = prs.getFirstName();
        lastName = prs.getLastName();
        email = prs.getEmail();
        password = prs.getPassword();
        photoPath = prs.getPhotoPath();
        birthDate = prs.getBirthDate();
        phoneNumber = prs.getPhoneNumber();
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int id) {
        personID = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        lastName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordEncryption.generateSecurePassword(password);
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String path) {
        photoPath = path;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String date) {
        birthDate = date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        phoneNumber = phone;
    }

    public void print() {
        System.out.println(personID + " " + firstName + " " + lastName + " " + email + " " + " " + birthDate +
                phoneNumber);
    }

}
