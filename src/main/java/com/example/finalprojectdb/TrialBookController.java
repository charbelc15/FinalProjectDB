package com.example.finalprojectdb;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TrialBookController extends Application implements Initializable {

    public Label name;
    public ImageView UserImage;

    public Label courseNb, booked, totalSessions, ID; //courseNb : how many he/she registered  //booked: taken by someone else //total
    public ComboBox trialID;
    public TextArea dateTA, timeTA, courseTA, instructorTA, studentTA;

    public Button registerBtn;
    public Button SignOutBtn;


    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("TrialBook.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Sign In page");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            name.setText("Welcome, " + NameHolder.getInstance().getName());
            Connection conn = SQL_Connector.getDBConnection();


            if(NameHolder.getInstance().getRole()=="student"){
                    //Setting courses list
                    // Execute query and store result in a resultset
                    ResultSet rs = conn.createStatement().executeQuery("SELECT DISTINCT(trialID) FROM trial_session");
                    //where studentid is null because ONLY ONE STUDENT CAN ATTEND THE SESSION , ONCE BOOKED, DONT DISPLAY IT ANYMORE!!
                    while (rs.next()) {
                        trialID.getItems().addAll(rs.getString("trialID"));
                        }

                    //updating the displayed picture
                    Path imageFile = Paths.get("src/main/resources/images/male-student.png");
                    UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


                        //updating how many courses he/she already registered in
                    ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                            "FROM trial_session " +
                            " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                    rsCourse.next();
                    courseNb.setText(rsCourse.getString("courseNb"));

                        //updating number of booked sessions
                    ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                            "FROM trial_session " +
                            " WHERE  studentID NOT IN(" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\"))");
                    rsBooked.next();
                    booked.setText(rsBooked.getString("courseNb"));

                        //displaying ID
                    ResultSet rsID = conn.createStatement().executeQuery("SELECT studentID " +
                            "FROM student " +
                            "WHERE username=\"" + NameHolder.getInstance().getName() +"\"");
                    rsID.next();
                    ID.setText(rsID.getString("studentID"));
                }
            else{
                    // Execute query and store result in a resultset
                    ResultSet rs = conn.createStatement().executeQuery("SELECT DISTINCT(trialID) FROM trial_session");
                    //where instructorId is null because ONLY ONE INSTRUCTOR CAN TEACH THE SESSION , ONCE BOOKED, DONT DISPLAY IT ANYMORE!!
                    while (rs.next()) {
                        trialID.getItems().addAll(rs.getString("trialID"));
                    }

                    //updating the displayed picture
                    Path imageFile = Paths.get("src/main/resources/images/instructor.png");
                    UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


                    //updating how many courses he/she already registered in
                    ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                            "FROM trial_session " +
                            " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                    rsCourse.next();
                    courseNb.setText(rsCourse.getString("courseNb"));

                    //updating number of booked sessions
                    ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                            "FROM trial_session " +
                            " WHERE  instructorId NOT IN(" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\"))");
                    rsBooked.next();
                    booked.setText(rsBooked.getString("courseNb"));

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

    public void fillOthers(){
        try {
            Connection conn = SQL_Connector.getDBConnection();
            // Execute query and store result in a resultset
            ResultSet rsDate = conn.createStatement().executeQuery("SELECT date FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
            rsDate.next(); //THIS IS MANDATORY TO DO, result is initially 0, then we push it to the first row!!!!
            ResultSet rsTime = conn.createStatement().executeQuery("SELECT time FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
            rsTime.next();
            ResultSet rsCourse = conn.createStatement().executeQuery("SELECT courseCovered FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
            rsCourse.next();
            ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT instructorId FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
            rsInstructor.next();
            ResultSet rsStudent = conn.createStatement().executeQuery("SELECT studentId FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
            rsStudent.next();

            dateTA.setText(rsDate.getString("date"));
            timeTA.setText(rsTime.getString("time"));
            courseTA.setText(rsCourse.getString("courseCovered"));
            instructorTA.setText(rsInstructor.getString("instructorId"));
            studentTA.setText(rsStudent.getString("studentId"));

        } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }


    //add to trial_session, According to their username which we used to know if it's a student or instructor
    public void Register(){
        try {
            Connection conn = SQL_Connector.getDBConnection();
            if(NameHolder.getInstance().getRole()=="student"){
                String sql = "UPDATE trial_session " +
                        "SET studentID=(SELECT studentID from student where username=\"" + NameHolder.getInstance().getName() +"\") " +
                        "WHERE trialID=\""+trialID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating studentID on register press
                ResultSet rsStudent = conn.createStatement().executeQuery("SELECT studentId FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
                rsStudent.next();
                studentTA.setText(rsStudent.getString("studentId"));

                //updating courseNb on register press
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                                                                               "FROM trial_session " +
                                                                                " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //updating number of booked sessions
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                        "FROM trial_session " +
                        " WHERE  studentID NOT IN(" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\"))");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));

            }
            else{
                String sql = "UPDATE trial_session " +
                        "SET instructorID=(SELECT instructorId from instructor where username=\"" + NameHolder.getInstance().getName() +"\") " +
                        "WHERE trialID=\""+trialID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating instructorID on register press
                ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT instructorId FROM trial_session WHERE trialID=\"" + trialID.getValue() +"\"");
                rsInstructor.next();
                instructorTA.setText(rsInstructor.getString("instructorId"));

                //updating courseNb on register press
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                        "FROM trial_session " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //updating number of booked sessions
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT trialID) AS courseNb " +
                        "FROM trial_session " +
                        " WHERE  instructorId NOT IN(" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\"))");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));
            }

            conn.close();

        } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }

    public void onSignOut(ActionEvent event) throws IOException{
        PageLoader goingTo= new PageLoader("SignInUI.fxml", "Sign In");
        goingTo.loadPage(event);
    }
}
