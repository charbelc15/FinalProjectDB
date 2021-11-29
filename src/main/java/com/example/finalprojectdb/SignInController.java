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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    private Connection conn;

    {
        try {
            conn = SQL_Connector.getDBConnection();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            System.out.println("error in loading connection");
        }
    }

    @FXML
    private Button SignInButton;

    @FXML
    private Button SignUpButton;

    @FXML
    private Label signInMessage;

    @FXML
    private TextField userNameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Hyperlink forgotPasswordHyperlink;

    @FXML
    private Button AboutUs;

    //added
    @FXML
    private RadioButton studentRadioButton;

    //added
    @FXML
    private RadioButton instructorRadioButton;

    @FXML
    public void onSignUpButtonClick(ActionEvent e) throws IOException {

        Parent signUpPage = FXMLLoader.load(getClass().getResource("SignUpUI.fxml"));

        Scene signUpScene = new Scene(signUpPage);

        Stage signUp_window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        signUp_window.setScene(signUpScene);
        signUp_window.setTitle("Sign In page");
        signUp_window.setResizable(false);
        signUp_window.show();
    }

    @FXML
    public void onBackButtonClick(ActionEvent e) throws IOException {
        System.out.println ("About Us clicked");
        PageLoader loaderSignUp= new PageLoader("SignUpUI.fxml", "sign up page");
        loaderSignUp.loadPage(e);
    }

    @FXML
    public void onSignInButtonClick(ActionEvent e) throws SQLException, IOException {
        System.out.println("sign in pressed");
        String queryStudent = "SELECT * FROM student";
        String queryInstructor = "SELECT * FROM instructor";
        PreparedStatement ps= null;

        if (studentRadioButton.isSelected()){
            try{
                ps=conn.prepareStatement(queryStudent);

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            ResultSet rs= ps.executeQuery(queryStudent);
            System.out.println("query student executed");
            System.out.println(rs);

            while (rs.next()) {
                String username = rs.getString(9);
                String password = rs.getString(10);

                String writtenUsername = userNameTextField.getText();
                String writtenPassword = passwordPasswordField.getText();

                if (writtenUsername.equals(username) && writtenPassword.equals(password)) {

                    NameHolder.getInstance().setName(userNameTextField.getText());
                    NameHolder.getInstance().setRole("student");

                    System.out.println("successfully signed in");
                    signInMessage.setText("successfully signed in");
                    PageLoader loader = new PageLoader("TrialBook.fxml", "Book a trial");
                    loader.loadPage(e);
                    break;

                } else {
                    System.out.println("incorrect username or password");
                    signInMessage.setText("incorrect username or password");
                }
            }
        }
        else if (instructorRadioButton.isSelected()){
            try{
                ps=conn.prepareStatement(queryInstructor);

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            ResultSet rs= ps.executeQuery(queryInstructor);
            System.out.println("query instructor executed");
            System.out.println(rs);
            while (rs.next()) {
                String username = rs.getString(10);
                String password = rs.getString(11);

                String writtenUsername = userNameTextField.getText();
                String writtenPassword = passwordPasswordField.getText();
                if (writtenUsername.equals(username) && writtenPassword.equals(password)) {

                    NameHolder.getInstance().setName(userNameTextField.getText());
                    NameHolder.getInstance().setRole("instructor");


                    System.out.println("successfully signed in");
                    signInMessage.setText("successfully signed in");
                    PageLoader loader = new PageLoader("TrialBook.fxml", "Book a trial");
                    loader.loadPage(e);
                    break;

                } else {
                    System.out.println("incorrect username or password. Check comboBox");
                    signInMessage.setText("incorrect username or password");
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}