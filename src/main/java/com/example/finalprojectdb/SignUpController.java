package com.example.finalprojectdb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.example.finalprojectdb.Student.counter;

public class SignUpController implements Initializable {

    Connection conn;

    {
        try {
            conn = SQL_Connector.getDBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button signUpButton;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField extensionTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField emailAddressTextField;

    @FXML
    private ComboBox yearComboBox;

    @FXML
    private TextField AddressTextField;

    @FXML
    private RadioButton studentRadioButton;

    @FXML
    private RadioButton instructorRadioButton;

    @FXML
    private TextField majorTextField;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Label OutputtedLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String years="";

        yearComboBox.getItems().removeAll(yearComboBox.getItems());
        for (int i= 1960; i<2021 ; i++) {
            yearComboBox.getItems().add (i + "");
        }
    }

    @FXML
    public void signUpButtonPressed (ActionEvent event) throws IOException, SQLException {
        System.out.println("signup pressed");

        String queryCheckStudent    = "SELECT * FROM student";
        String queryCheckInstructor = "SELECT * FROM instructor";
        PreparedStatement ps = null;
        Boolean checking= true; //boolean to be used to check if username used or not
        Boolean emptyUsername =  false;

        String tester = userNameTextField.getText();

        if (tester.equals("")){
            OutputtedLabel.setText("invalid username");
            emptyUsername = true;
        }

        else {
            OutputtedLabel.setText("");
            emptyUsername = false;
        }


        if (studentRadioButton.isSelected() && !emptyUsername) { //if student: check if username is used
            try {
                conn = SQL_Connector.getDBConnection();
            } catch (SQLException exception) {
                System.out.println("connection to DB failed");
            }

            try {
                ps = conn.prepareStatement(queryCheckStudent);
            } catch (SQLException exception){
                System.out.println ("preparation failed");
            }

            ResultSet rs= ps.executeQuery(queryCheckStudent);

            while (rs.next()){
                String enteredUsername = userNameTextField.getText();
                String username = rs.getString(9);

                if (enteredUsername.equals(username)){
                    OutputtedLabel.setText("Already Used username");
                    checking=false;
                    userNameTextField.clear();
                    break;
                }

                else {
                    OutputtedLabel.setText("Successfully Signed Up as a student");
                    checking = true;
                }
            }
        }
        else if (instructorRadioButton.isSelected() && !emptyUsername){ //check if the username entered by the instructor is used
            try {
                conn = SQL_Connector.getDBConnection();
            } catch (SQLException exception) {
                System.out.println("connection to DB failed");
            }

            try {
                ps = conn.prepareStatement(queryCheckInstructor);
            } catch (SQLException exception){
                System.out.println ("preparation failed");
            }

            ResultSet rs= ps.executeQuery(queryCheckInstructor);

            while (rs.next()){
                String enteredUsername = userNameTextField.getText();
                String username = rs.getString(10);

                if (enteredUsername.equals(username)){
                    OutputtedLabel.setText("Already Used username");
                    checking=false;
                    userNameTextField.clear();
                    break;
                }

                else {
                    OutputtedLabel.setText("Successfully Signed Up as an instructor");
                    checking = true;
                }
            }
        }
        if (studentRadioButton.isSelected() && checking && !emptyUsername) {

            Student student= new Student(firstNameTextField.getText(),lastNameTextField.getText(),AddressTextField.getText(),emailAddressTextField.getText(),extensionTextField.getText(),
                    phoneNumberTextField.getText(),  Integer.parseInt((String)yearComboBox.getValue()),
                    userNameTextField.getText(),passwordPasswordField.getText());

            student.insertStudentRecord(); System.out.println("query student executed");
            NameHolder.getInstance().setName(userNameTextField.getText());
            NameHolder.getInstance().setRole("student");

            PageLoader goingTo= new PageLoader("TrialBook.fxml", "schedule");
            goingTo.loadPage(event);
        }
        else if (instructorRadioButton.isSelected() && checking && !emptyUsername){
            Instructor instructor = new Instructor(firstNameTextField.getText(), lastNameTextField.getText(), (String)yearComboBox.getValue(), emailAddressTextField.getText(),
                    majorTextField.getText(), AddressTextField.getText(), extensionTextField.getText(), phoneNumberTextField.getText(), userNameTextField.getText(), passwordPasswordField.getText());

            instructor.insertInstructorRecord(); System.out.println("query student executed");
            NameHolder.getInstance().setName(userNameTextField.getText());
            NameHolder.getInstance().setRole("instructor");

            PageLoader goingTo= new PageLoader("TrialBook.fxml", "schedule");
            goingTo.loadPage(event);
        }
    }
}
