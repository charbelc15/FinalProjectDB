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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String years="";

        yearComboBox.getItems().removeAll(yearComboBox.getItems());
        yearComboBox.getItems().addAll("1980 - 1989", "1990 - 1999", "2000 - 2004", " 2005 - 2009", "2010 - 2014", "2015 - 2020");

    }

    @FXML
    public void signUpButtonPressed (ActionEvent event) throws IOException, SQLException {
        System.out.println("signup pressed");
        if (studentRadioButton.isSelected()) {
            Student student= new Student(firstNameTextField.getText(),lastNameTextField.getText(),AddressTextField.getText(),emailAddressTextField.getText(),extensionTextField.getText(),
                    phoneNumberTextField.getText(), (String) yearComboBox.getValue(),
                    userNameTextField.getText(),passwordPasswordField.getText());

            student.insertStudentRecord();
            NameHolder.getInstance().setName(userNameTextField.getText());
            NameHolder.getInstance().setRole("student");
        }
        else {
            Instructor instructor = new Instructor(firstNameTextField.getText(), lastNameTextField.getText(), (String)yearComboBox.getValue(), emailAddressTextField.getText(),
                    majorTextField.getText(), AddressTextField.getText(), extensionTextField.getText(), phoneNumberTextField.getText(), userNameTextField.getText(), passwordPasswordField.getText());

            instructor.insertInstructorRecord();
            NameHolder.getInstance().setName(userNameTextField.getText());
            NameHolder.getInstance().setRole("instructor");
        }

        PageLoader goingTo= new PageLoader("TrialBook.fxml", "schedule");
        goingTo.loadPage(event);

    }
}
