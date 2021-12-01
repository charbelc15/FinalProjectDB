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
import java.time.Year;
import java.util.ResourceBundle;

public class GCourseController extends Application implements Initializable {

    public Label name;
    public ImageView UserImage;

    public Label courseNb, booked, totalSessions, ID; //courseNb : how many he/she registered  //booked: taken by someone else //total
    public ComboBox courseID;
    public TextArea levelTA, curriculumTA, courseTA, instructorTA;

    public Button registerBtn;
    public Button SignOutBtn;
    public Button btnTrial;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GCourseBook.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Book Group Course page");
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
                ResultSet rs = conn.createStatement().executeQuery("select distinct groupID " +
                        "from group_session natural join group_course natural join course natural join track " +
                        "where " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"group\"");
                while (rs.next()) {
                    courseID.getItems().addAll(rs.getString("groupID"));
                }

                //updating how many courses user has already taken
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT groupId) AS courseNb " +
                        "FROM reg_group_course " +
                        " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //displaying total number of courses available (for student, according to age)
                ResultSet rsTotal = conn.createStatement().executeQuery("select COUNT(distinct groupID) as courseNb " +
                        "from group_session natural join group_course natural join course natural join track " +
                        "where " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"group\"");
                rsTotal.next();
                totalSessions.setText(rsTotal.getString("courseNb"));


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

                //Displaying Courses For Instructor
                ResultSet rs = conn.createStatement().executeQuery("select distinct groupID " +
                        "from group_session natural join group_course natural join course natural join track " +
                        "where type=\"group\"");
                while (rs.next()) {
                    courseID.getItems().addAll(rs.getString("groupID"));
                }

                //displaying courses already taken by instructor
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT groupID) AS courseNb " +
                        "FROM group_session natural join group_course natural join course natural join track " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //displaying total number of courses available (for instructor)
                ResultSet rsTotal = conn.createStatement().executeQuery("select COUNT(distinct groupID) as courseNb " +
                        "from group_session natural join group_course natural join course natural join track where type=\"group\"");
                rsTotal.next();
                totalSessions.setText(rsTotal.getString("courseNb"));

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



            conn.close();

        } catch (SQLException | MalformedURLException ex) {
            System.err.println("Error"+ex);
        }

    }

    public void fillOthers(){
        try {
            Connection conn = SQL_Connector.getDBConnection();

            //CANNOT DISPLAY DATE AND TIME AND COST HERE BECAUSE THEY ARE NOT UNIQUE FOR A WHOLE COURSE, JUST FOR SESSION
            //CANNOT DISPLAY STUDENTID BECAUSE THE GROUP COURSE IS NOT FOR 1 STUDENT ONLY


            ResultSet rsCourse = conn.createStatement().executeQuery("SELECT DISTINCT courseName FROM group_session natural join group_course natural join course natural join track WHERE groupID=\"" + courseID.getValue() +"\"");
            rsCourse.next();
            ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT DISTINCT instructorId FROM group_session natural join group_course natural join course natural join track WHERE groupID=\"" + courseID.getValue() +"\"");
            rsInstructor.next();
            ResultSet rsDescription = conn.createStatement().executeQuery("SELECT DISTINCT description FROM group_session natural join group_course natural join course natural join track WHERE groupID=\"" + courseID.getValue() +"\"");
            rsDescription.next();
            ResultSet rsCurriculum = conn.createStatement().executeQuery("SELECT DISTINCT curriculum FROM group_session natural join group_course natural join course natural join track WHERE groupID=\"" + courseID.getValue() +"\"");
            rsCurriculum.next();

            courseTA.setText(rsCourse.getString("courseName"));
            instructorTA.setText(rsInstructor.getString("instructorId"));
            levelTA.setText(rsDescription.getString("description"));
            curriculumTA.setText(rsCurriculum.getString("curriculum"));

        } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }


    //add to trial_session, According to their username which we used to know if it's a student or instructor
    public void Register(){
        try {
            Connection conn = SQL_Connector.getDBConnection();
            //INSERTING students FOR EACH COURSE --> IN REG_GROUP_COURSE
            //cant register more than once of course since STUDENTID is a primary KEY --> Numbers wont change
            if(NameHolder.getInstance().getRole()=="student"){
                String sql = "insert into REG_GROUP_COURSE " +
                        "VALUES((SELECT studentID from student where username=\"" + NameHolder.getInstance().getName() +"\"),(\""+courseID.getValue()+"\"), CURRENT_DATE())";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating how many courses user has taken after register press (by himself)
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT groupId) AS courseNb " +
                        "FROM reg_group_course " +
                        " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //NOT USED HERE SINCE MANY STUDENTS CAN REGISTER MUTLIPLE COURSES, it will not display how many courses are left
                //updating number of booked sessions by others
                //ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT studentId) AS courseNb " +
                //        "FROM reg_group_course " +
                //        " WHERE  studentID NOT IN(" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\"))");
                //rsBooked.next();
                //booked.setText(rsBooked.getString("courseNb"));

            }
            else{
                //INSERTING 1 INSTRUCTOR FOR EACH COURSE --> IN GROUP COURSE
                //cant register more than once since it is the same row in the update statement--> Numbers wont change
                String sql = "UPDATE GROUP_COURSE " +
                        "SET instructorID=(SELECT instructorId from instructor where username=\"" + NameHolder.getInstance().getName() +"\") " +
                        "WHERE groupID=\""+courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating instructorID on register press
                ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT distinct(instructorId) FROM group_session natural join group_course natural join course natural join track WHERE groupID=\"" + courseID.getValue() +"\"");
                rsInstructor.next();
                instructorTA.setText(rsInstructor.getString("instructorId"));

                //updating courseNb on register press
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT groupID) AS courseNb " +
                        "FROM group_session natural join group_course natural join course natural join track " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));


            }

            conn.close();

        } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }

    public void Deregister(){
        try {
            Connection conn = SQL_Connector.getDBConnection();
            //DELETING students FOR EACH COURSE --> IN REG_GROUP_COURSE
            //cant Deregister more than once of course
            if(NameHolder.getInstance().getRole()=="student"){
                String sql = "DELETE FROM REG_GROUP_COURSE " +
                        "WHERE studentId=(SELECT studentID from student where username=\"" + NameHolder.getInstance().getName() +"\") AND groupId=\""+courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating how many courses user has taken after register press (by himself)
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT groupId) AS courseNb " +
                        "FROM reg_group_course " +
                        " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));


            }
            else{
                //INSERTING 1 INSTRUCTOR FOR EACH COURSE --> IN GROUP COURSE
                //cant register more than once since it is the same row in the update statement--> Numbers wont change
                String sql = "UPDATE GROUP_COURSE " +
                        "SET instructorID=NULL " +
                        "WHERE groupID=\""+courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating instructorID on register press
                ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT distinct(instructorId) FROM group_session natural join group_course natural join course natural join track WHERE groupID=\"" + courseID.getValue() +"\"");
                rsInstructor.next();
                instructorTA.setText(rsInstructor.getString("instructorId"));

                //updating courseNb on register press
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT groupID) AS courseNb " +
                        "FROM group_session natural join group_course natural join course natural join track " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));


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

    public void onCalendar(ActionEvent event) throws IOException{
        PageLoader goingTo= new PageLoader("ScheduleStudentUI.fxml", "My Calendar");
        goingTo.loadPage(event);
    }

    public int getAge() throws SQLException {
        Connection conn = SQL_Connector.getDBConnection();
        ResultSet rsCourse = conn.createStatement().executeQuery("Select dateOfBirth from student where username=\"" + NameHolder.getInstance().getName() +"\"");
        rsCourse.next();
        int UserYear = Integer.parseInt(rsCourse.getString("dateOfBirth"));

        int currentYear = Year.now().getValue();
        int age =currentYear-UserYear;
        System.out.println(age);
        return age;
    }




}
