package com.example.finalprojectdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Student extends SQL_Connector {
    public static int counter=1;
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phoneExtenstion;
    private String phoneNb;
    private String dateOfBirth;
    private String username;
    private String pass;
    private Random rdn;

    public Student(String firstName, String lastName, String address, String email,
                   String phoneExtenstion, String phoneNb, String dateOfBirth, String username,
                   String pass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phoneExtenstion = phoneExtenstion;
        this.phoneNb = phoneNb;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.pass = pass;
        this.id= String.valueOf(counter);
    }

    public Student(String id, String firstName, String lastName,
                   String address, String email, String phoneExtenstion, String phoneNb, String dateOfBirth,
                   String username, String pass, Random rdn) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phoneExtenstion = phoneExtenstion;
        this.phoneNb = phoneNb;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.pass = pass;
        this.rdn = rdn;
    }

    public Student() {

    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneExtenstion() {
        return phoneExtenstion;
    }

    public String getPhoneNb() {
        return phoneNb;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public Random getRdn() {
        return rdn;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneExtenstion(String phoneExtenstion) {
        this.phoneExtenstion = phoneExtenstion;
    }

    public void setPhoneNb(String phoneNb) {
        this.phoneNb = phoneNb;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setRdn(Random rdn) {
        this.rdn = rdn;
    }

    public void insertStudentRecord() throws SQLException { //used for sign in
        Connection conn = null;
        try {
            conn = SQL_Connector.getDBConnection();
        } catch (SQLException Throwable) {
            Throwable.printStackTrace();
        }

        String sql = "INSERT INTO student ( studentId,firstName, lastName, address,email, phoneExtenstion, phoneNb, dateOfBirth, username, pass) VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, String.valueOf(counter));
        statement.setString(2,firstName);
        statement.setString(3,lastName);
        statement.setString(4,address);
        statement.setString(5,email);
        statement.setString(6,phoneExtenstion);
        statement.setString(7,phoneNb);
        statement.setString(8,dateOfBirth);
        statement.setString(9,username);
        statement.setString(10,pass);

        counter++;
        statement.execute();
        closeConnection(conn,statement);
    }
}

