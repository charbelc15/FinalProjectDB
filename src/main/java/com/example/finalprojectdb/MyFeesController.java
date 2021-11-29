package com.example.finalprojectdb;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MyFeesController extends Application implements Initializable {

    public Label name;
    public ImageView UserImage;

    public Label courseNb, booked, totalSessions, ID; //courseNb : how many he/she registered  //booked: taken by someone else //total
    public ComboBox trialID;
    public TextArea dateTA, timeTA, courseTA, instructorTA, studentTA;

    public Button registerBtn;
    public Button SignOutBtn;
    public Button btnTrial;



    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MyFees.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("MyFees page");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            name.setText("Welcome, " + NameHolder.getInstance().getName());
            Connection conn = SQL_Connector.getDBConnection();


            if(NameHolder.getInstance().getRole()=="student"){


                //hi
                //updating the displayed picture
                Path imageFile = Paths.get("src/main/resources/images/male-student.png");
                UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


                //displaying ID
                ResultSet rsID = conn.createStatement().executeQuery("SELECT studentID " +
                        "FROM student " +
                        "WHERE username=\"" + NameHolder.getInstance().getName() +"\"");
                rsID.next();
                ID.setText(rsID.getString("studentID"));
            }
            else{

                //updating the displayed picture
                Path imageFile = Paths.get("src/main/resources/images/instructor.png");
                UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


                //displaying ID
                ResultSet rsID = conn.createStatement().executeQuery("SELECT instructorID " +
                        "FROM instructor " +
                        "WHERE username=\"" + NameHolder.getInstance().getName() +"\"");
                rsID.next();
                ID.setText(rsID.getString("instructorID"));
            }

            //updating total number of courses (for both student and instructor pages)
            ResultSet rsTotal = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                    "FROM trial_session");
            rsTotal.next();
            totalSessions.setText(rsTotal.getString("courseNb"));


            conn.close();

        } catch (SQLException | MalformedURLException ex) {
            System.err.println("Error"+ex);
        }

    }





    public void onSignOut(ActionEvent event) throws IOException{
        PageLoader goingTo= new PageLoader("SignInUI.fxml", "Sign In");
        goingTo.loadPage(event);
    }

    public void onTrial(ActionEvent event) throws IOException{
        PageLoader goingTo= new PageLoader("TrialBook.fxml", "Book a Trial");
        goingTo.loadPage(event);
    }

    public void onPCourse(ActionEvent event) throws IOException{
        PageLoader goingTo= new PageLoader("PCourseBook.fxml", "Book a Course");
        goingTo.loadPage(event);
    }

    public void onGCourse(ActionEvent event) throws IOException{
        PageLoader goingTo= new PageLoader("GCourseBook.fxml", "Book a Course");
        goingTo.loadPage(event);
    }
}
