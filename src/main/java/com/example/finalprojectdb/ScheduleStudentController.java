package com.example.finalprojectdb;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ScheduleStudentController extends Application implements Initializable {

    @FXML
    public Label name;
    @FXML
    public ImageView UserImage;

    @FXML
    public Button SignOutBtn;

    @FXML
    public Button eight;

    @FXML
    public Button nine;

    @FXML
    public Button ten;

    @FXML
    public Button eleven;

    @FXML
    public Button twelve;

    @FXML
    public Button thirteen;

    @FXML
    public Button fourteen;

    @FXML
    public Button fifteen;

    @FXML
    public Button sixteen;

    @FXML
    public Button seventeen;

    @FXML
    public Button eighteen;

    @FXML
    public Button nineteen;

    @FXML
    public Button twenty;

    @FXML
    public DatePicker datePicker;

    @FXML
    public Label locale_date;

    public String studentId;
    public String instructorId;
    LocalDate chosenDate;


    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ScheduleStudentUI.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Calendar page");
        stage.setScene(scene);
        stage.show();
        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());


        //cheching localDate
        System.out.println ("local date: " + datePicker.getValue());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            //set name in toolbar
            name.setText("Welcome, " + NameHolder.getInstance().getName());
            Connection conn = SQL_Connector.getDBConnection();

            chosenDate = datePicker.getValue();
            System.out.println ("picked date: " + chosenDate);
            locale_date.setText(chosenDate +  "");

            if (NameHolder.getInstance().getRole()=="student"){

                //updating the displayed picture student
                Path imageFile = Paths.get("src/main/resources/images/male-student.png");
                UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


            } else {

                //updating the displayed picture instructor
                Path imageFile = Paths.get("src/main/resources/images/instructor.png");
                UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));

                //we should display the time of group session, trial session, private session for Instructors

            }
            conn.close();

        } catch (SQLException | MalformedURLException ex) {
            System.err.println("Error"+ex);
        }
    }

    public void fillingOthers () throws SQLException {
        Connection conn= null;
        try {
            conn = SQL_Connector.getDBConnection();
        }catch (SQLException exception){
           exception.printStackTrace();
        }

        if (NameHolder.getInstance().getRole()=="student"){
            String queryToGetStudentId= "SELECT studentID FROM student WHERE username =\"" + NameHolder.getInstance().getName() + "\"";
            PreparedStatement psGetStudentId = conn.prepareStatement(queryToGetStudentId);
            ResultSet rsStudentIdResults = psGetStudentId.executeQuery(queryToGetStudentId);

            //integrity constraint --> 1 row from the previous query
            while (rsStudentIdResults.next()){
                studentId=rsStudentIdResults.getString(1);
            }
            System.out.println ("student id: " + studentId);

            //we should display the time of group session, trial session, private session for STUDENTS

            String queryGetTrialSessions   = "SELECT trialID,date,time FROM trial_session WHERE studentId =\"" + studentId + "\" AND date =\""+chosenDate + "\"";
            String queryGetGroupSessions   = "SELECT groupsessId, group_session.date, time FROM group_session, group_course, reg_group_course, student WHERE group_course.groupId = group_session.groupId AND reg_group_course.groupId = group_course.groupId AND reg_group_course.studentID = student.studentID AND studentID = \"" + studentId + "\" AND group_session.date = \"" + chosenDate + "\"";
            String queryGetPrivateSessions = "SELECT privatesessId, private_session.date, time FROM private_session, private_course, student WHERE private_course.privateId = private_session.privateId AND private_course.studentID = student.studentID AND private_course.studentID IN (\"" + studentId + "\") AND private_session.date = \"" + chosenDate + "\"";

            //starting with trials
            PreparedStatement psGetTrialSession= conn.prepareStatement(queryGetTrialSessions);
            ResultSet rsTrialSessions = psGetTrialSession.executeQuery(queryGetTrialSessions);

            LinkedList <Combination> arrayTrialSession = new LinkedList<>();
            while (rsTrialSessions.next()){
                String date = "date";
                String time = "time";
                String id= "trialID";
                Date actualDate= rsTrialSessions.getDate(date);
                Time actualTime= rsTrialSessions.getTime(time);
                String trialID = rsTrialSessions.getString(id);
                arrayTrialSession.add(new Combination(actualDate,actualTime,trialID));
            }

            //add trial sessions to schedule
            for (int i=0; i<arrayTrialSession.size();i++){
                if (arrayTrialSession.get(i).date.equals(chosenDate)){
                    Time timing=  arrayTrialSession.get(i).time;
                    String timingStr = timing + "";
                    timingStr= timingStr.substring(0,2);
                    Button timeSlot=timeSelector(timingStr);
                    timeSlot.setText("Trial session: " + arrayTrialSession.get(i).id);
                }
            }

            //group sessions
            PreparedStatement psGetGroupSessions = conn.prepareStatement(queryGetGroupSessions);
            ResultSet rsGroupSessions = psGetGroupSessions.executeQuery(queryGetGroupSessions);

            LinkedList <Combination> arrayGroupSession = new LinkedList<>();
            while (rsGroupSessions.next()){
                String date = "date";
                String time = "time";
                String id= "groupsessId";
                Date actualDate= rsGroupSessions.getDate(date);
                Time actualTime= rsGroupSessions.getTime(time);
                String groupID = rsGroupSessions.getString(id);
                arrayGroupSession.add(new Combination(actualDate,actualTime,groupID));
            }

            for (int i=0; i<arrayGroupSession.size();i++){
                if (arrayGroupSession.get(i).date.equals(chosenDate)){
                    Time timing=  arrayGroupSession.get(i).time;
                    String timingStr = timing + "";
                    timingStr= timingStr.substring(0,2);
                    Button timeSlot=timeSelector(timingStr);
                    timeSlot.setText("Group Session: " + arrayGroupSession.get(i).id);
                }
            }


            //private sessions
            PreparedStatement psGetPrivateSessions = conn.prepareStatement(queryGetPrivateSessions);
            ResultSet rsPrivateSessions = psGetPrivateSessions.executeQuery(queryGetPrivateSessions);

            LinkedList <Combination> arrayPrivateSession = new LinkedList<>();
            while (rsPrivateSessions.next()){
                String date = "date";
                String time = "time";
                String id= "privatesessId";
                Date actualDate= rsGroupSessions.getDate(date);
                Time actualTime= rsGroupSessions.getTime(time);
                String privateID = rsGroupSessions.getString(id);
                arrayGroupSession.add(new Combination(actualDate,actualTime,privateID));
            }

            for (int i=0; i<arrayPrivateSession.size();i++){
                if (arrayPrivateSession.get(i).date.equals(chosenDate)){
                    Time timing=  arrayPrivateSession.get(i).time;
                    String timingStr = timing + "";
                    timingStr= timingStr.substring(0,2);
                    Button timeSlot=timeSelector(timingStr);
                    timeSlot.setText("Private Session: " + arrayGroupSession.get(i).id);

                }
            }

            conn.close();
        }

        //INSTRUCTOR
        else {
            
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



    private class Combination {
        Date date;
        Time time;
        String id;

        public Combination (Date date, Time time, String id){
            this.date=date;
            this.time=time;
            this.id= id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Time getTime() {
            return time;
        }

        public void setTime(Time time) {
            this.time = time;
        }
    }

    //this method will tell us if the fetched trial,group, and private session are in the current semester;
    private boolean semesterChecker (Date date) throws SQLException {
        boolean boo= false;

        Connection conn= null;
        Date actualDate = null;

        String fetchedDateMonth= (date + "").substring((date + "").indexOf('-')+1,(date + "").lastIndexOf('-'));
        String fetchedDateYear= (date + "").substring(0,(date + "").indexOf('-')) ;
        String fetchedDateSemester =null;

        String actualDateMonth =null;
        String actualDateSemester=null;
        String actualDateYear= null;

        if (Integer.parseInt(fetchedDateMonth) >= 9 && Integer.parseInt(fetchedDateMonth)<=12)
            fetchedDateSemester="fall";
        else if (Integer.parseInt(fetchedDateMonth) >= 1 && Integer.parseInt(fetchedDateMonth)<=5)
            fetchedDateSemester="spring";
        else
            fetchedDateSemester="summer";


        try {
            conn = SQL_Connector.getDBConnection();
        }catch (SQLException exception){
            exception.printStackTrace();
        }

        String queryCurrentDate = "SELECT curdate()";

        PreparedStatement psTester = conn.prepareStatement(queryCurrentDate);
        ResultSet resultTester = psTester.executeQuery(queryCurrentDate);

        while (resultTester.next()){
            actualDate = resultTester.getDate(1);
        }

        actualDateMonth= (actualDate + "").substring((actualDate + "").indexOf('-')+1,(actualDate + "").lastIndexOf('-'));
        actualDateYear= (actualDate + "").substring(0,(actualDate + "").indexOf('-')) ;

        if (Integer.parseInt(actualDateMonth) >= 9 && Integer.parseInt(actualDateMonth)<=12)
            fetchedDateSemester="fall";
        else if (Integer.parseInt(actualDateMonth) >= 1 && Integer.parseInt(actualDateMonth)<=5)
            fetchedDateSemester="spring";
        else
            fetchedDateSemester="summer";




        if (actualDateSemester.equals(fetchedDateSemester) && actualDateYear.equals(fetchedDateYear))
            return true;

        else
        return false;
    }

    private Button timeSelector (String twoDigits){
        Button button=null;
        if (twoDigits.equals("08")){
            button=eight;
        }
        else if (twoDigits.equals("09"))
            button=nine;

        else if (twoDigits.equals("10"))
            button=ten;

        else if (twoDigits.equals("11"))
            button=eleven;

        else if (twoDigits.equals("12"))
            button=twelve;

        else if (twoDigits.equals("13"))
            button=thirteen;

        else if (twoDigits.equals("14"))
            button=fourteen;

        else if (twoDigits.equals("15"))
            button=fifteen;

        else if (twoDigits.equals("16"))
            button=sixteen;

        else if (twoDigits.equals("17"))
            button=seventeen;

        else if (twoDigits.equals("18"))
            button=eighteen;

        else if (twoDigits.equals("19"))
            button=nineteen;

        else if (twoDigits.equals("20"))
            button=twenty;

        return button;
    }
}
