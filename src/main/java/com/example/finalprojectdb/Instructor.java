package com.example.finalprojectdb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Instructor extends SQL_Connector {
    public static int counter;
    private String instructorId;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phone_ext;
    private String phone_Nb;
    private String dateOfBirth;
    private String username;
    private String pass;
    private String major;

    public Instructor(String firstName, String lastName, String dateOfBirth, String email,
                   String major, String address, String phone_ext, String phone_Nb, String username,
                   String pass) throws SQLException {

        String getCountSQL = "SELECT COUNT(instructorId) FROM instructor";
        Connection conn = null;
        ResultSet rs= null;
        try {
            conn = SQL_Connector.getDBConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        PreparedStatement ps = conn.prepareStatement(getCountSQL);
            rs = ps.executeQuery(getCountSQL);

        while (rs.next()) {
            counter = rs.getInt(1) + 1;
        }

        SQL_Connector.closeConnection(conn,ps);

        this.pass=pass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phone_ext = phone_ext;
        this.phone_Nb = phone_Nb;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.pass = pass;
        this.major=major;
        this.instructorId= String.valueOf(counter);


    }

    public Instructor(String instructorId,String firstName, String lastName, String dateOfBirth, String email,
                      String major, String address, String phone_ext, String phone_Nb, String username,
                      String pass) {
        this.instructorId = instructorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phone_ext = phone_ext;
        this.phone_Nb = phone_Nb;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.pass = pass;
        this.major=major;
    }

    public Instructor() {

    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Instructor.counter = counter;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_ext() {
        return phone_ext;
    }

    public void setPhone_ext(String phone_ext) {
        this.phone_ext = phone_ext;
    }

    public String getPhone_Nb() {
        return phone_Nb;
    }

    public void setPhone_Nb(String phone_Nb) {
        this.phone_Nb = phone_Nb;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = password;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void insertInstructorRecord() throws SQLException { //used for sign in
        Connection conn = null;
        try {
            conn = SQL_Connector.getDBConnection();
        } catch (SQLException Throwable) {
            Throwable.printStackTrace();
        }

        String sql = "INSERT INTO instructor (instructorId,firstName, lastName, dateOfBirth, email,\n" +
                "                      major,address, phone_ext,phone_Nb, username,\n" +
                "                      pass) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, String.valueOf(counter));
        statement.setString(2,firstName);
        statement.setString(3,lastName);
        statement.setString(4,dateOfBirth);
        statement.setString(5,email);
        statement.setString(6,major);
        statement.setString(7,address);
        statement.setString(8,phone_ext);
        statement.setString(9,phone_Nb);
        statement.setString(10,username);
        statement.setString(11,pass);

        counter++;
        statement.execute();
        closeConnection(conn,statement);
    }
}