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
import java.util.Date;
import java.util.ResourceBundle;

public class PCourseController  extends Application implements Initializable {

    public Label name;
    public ImageView UserImage;

    public Label courseNb, booked, totalSessions, ID; //courseNb : how many he/she registered  //booked: taken by someone else //total
    public ComboBox courseID;
    public TextArea levelTA, curriculumTA, courseTA, instructorTA,studentTA;

    public Button registerBtn, DeregisterBtn;
    public Button SignOutBtn;
    public Button btnTrial;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PCourseBook.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Book Private Course page");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            name.setText("Welcome, " + NameHolder.getInstance().getName());
            Connection conn = SQL_Connector.getDBConnection();


            if(NameHolder.getInstance().getRole()=="student"){
                //Setting courses list  //done
                ResultSet rs = conn.createStatement().executeQuery("select distinct privateID " +
                        "from private_session natural join private_course natural join course natural join track " +
                        "where " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"private\"");
                while (rs.next()) {
                    courseID.getItems().addAll(rs.getString("privateID"));
                }

                //updating how many courses user has already taken  //done
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateId) AS courseNb " +
                        "FROM private_session natural join private_course natural join course natural join track " +
                        " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //displaying total number of courses available (for student, according to age) //done
                ResultSet rsTotal = conn.createStatement().executeQuery("select COUNT(distinct privateID) as courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
                        "where " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"private\"");
                rsTotal.next();
                totalSessions.setText(rsTotal.getString("courseNb"));

                //updating number of already booked private courses by other students
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
                        " WHERE  studentID NOT IN(" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")) AND  " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"private\"");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));


                //updating the displayed picture //done
                Path imageFile = Paths.get("src/main/resources/images/male-student.png");
                UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


                //displaying ID //done
                ResultSet rsID = conn.createStatement().executeQuery("SELECT studentID " +
                        "FROM student " +
                        "WHERE username=\"" + NameHolder.getInstance().getName() +"\"");
                rsID.next();
                ID.setText(rsID.getString("studentID"));
            }
            else{

                //Displaying Courses For Instructor //done
                ResultSet rs = conn.createStatement().executeQuery("select distinct privateID " +
                        "from private_session natural join private_course natural join course natural join track " +
                        "where type=\"private\"");
                while (rs.next()) {
                    courseID.getItems().addAll(rs.getString("privateID"));
                }

                //displaying courses already taken by instructor  ///done
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "FROM private_session natural join private_course natural join course natural join track " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //displaying total number of courses available (for instructor) //done
                ResultSet rsTotal = conn.createStatement().executeQuery("select COUNT(distinct privateID) as courseNb " +
                        "from private_session natural join private_course natural join course natural join track where type=\"private\"");
                rsTotal.next();
                totalSessions.setText(rsTotal.getString("courseNb"));

                //updating number of booked courses by other instructors
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
                        " WHERE  instructorId NOT IN(" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\"))");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));

                //updating the displayed picture //done
                Path imageFile = Paths.get("src/main/resources/images/instructor.png");
                UserImage.setImage(new Image(imageFile.toUri().toURL().toExternalForm()));


                //displaying ID //done
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
            //DONE
            //CANNOT DISPLAY DATE AND TIME AND COST HERE BECAUSE THEY ARE NOT UNIQUE FOR A WHOLE COURSE, JUST FOR SESSION


            ResultSet rsCourse = conn.createStatement().executeQuery("SELECT DISTINCT courseName FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
            rsCourse.next();
            ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT DISTINCT instructorId FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
            rsInstructor.next();
            ResultSet rsDescription = conn.createStatement().executeQuery("SELECT DISTINCT description FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
            rsDescription.next();
            ResultSet rsCurriculum = conn.createStatement().executeQuery("SELECT DISTINCT curriculum FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
            rsCurriculum.next();
            ResultSet rsStudent = conn.createStatement().executeQuery("SELECT studentId FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
            rsStudent.next();

            courseTA.setText(rsCourse.getString("courseName"));
            instructorTA.setText(rsInstructor.getString("instructorId"));
            levelTA.setText(rsDescription.getString("description"));
            curriculumTA.setText(rsCurriculum.getString("curriculum"));
            studentTA.setText(rsStudent.getString("studentID"));

        } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }


    //add to course, According to their username which we used to know if it's a student or instructor
    public void Register(){
        try {
            Connection conn = SQL_Connector.getDBConnection();
            //INSERTING 1 student FOR EACH COURSE --> IN REG_GROUP_COURSE //done
            //cant register more than once of course since STUDENTID is a primary KEY --> Numbers wont change
            if(NameHolder.getInstance().getRole()=="student"){
                String sql = "UPDATE private_course " +
                        "SET studentID=(SELECT studentID from student where username=\"" + NameHolder.getInstance().getName() +"\") " +
                        "WHERE privateID=\""+ courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating studentID on register press //done
                ResultSet rsStudent = conn.createStatement().executeQuery("SELECT studentId FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
                rsStudent.next();
                studentTA.setText(rsStudent.getString("studentId"));


                //updating how many courses user has taken after register press (by himself) //done
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateId) AS courseNb " +
                        "FROM private_session natural join private_course natural join course natural join track " +
                        " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //updating number of already booked private courses by other students
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
                        " WHERE  studentID NOT IN(" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")) AND  " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"private\"");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));

            }
            else{
                //INSERTING 1 INSTRUCTOR FOR EACH COURSE --> IN GROUP COURSE //done
                //cant register more than once since it is the same row in the update statement--> Numbers wont change
                String sql = "UPDATE private_course " +
                        "SET instructorID=(SELECT instructorId from instructor where username=\"" + NameHolder.getInstance().getName() +"\") " +
                        "WHERE privateID=\""+courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating instructorID on register press //done
                ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT distinct(instructorId) FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
                rsInstructor.next();
                instructorTA.setText(rsInstructor.getString("instructorId"));

                //updating courseNb on register press //done
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "FROM private_session natural join private_course natural join course natural join track " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //updating number of booked courses by other instructors
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
                        " WHERE  instructorId NOT IN(" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\"))");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));


            }

            conn.close();

        } catch (SQLException ex) {
            System.err.println("Error"+ex);
        }
    }

    public void Deregister(){
        try {
            Connection conn = SQL_Connector.getDBConnection();
            //DELETING 1 student FOR EACH COURSE --> IN REG_GROUP_COURSE //done
            //cant deregister more than once of course
            if(NameHolder.getInstance().getRole()=="student"){
                String sql = "UPDATE private_course " +
                        "SET studentID=NULL " +
                        "WHERE privateID=\""+ courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating studentID on Deregister press //done
                ResultSet rsStudent = conn.createStatement().executeQuery("SELECT studentId FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
                rsStudent.next();
                studentTA.setText(rsStudent.getString("studentId"));


                //updating how many courses user has taken after Deregister press (by himself) //done
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateId) AS courseNb " +
                        "FROM private_session natural join private_course natural join course natural join track " +
                        " WHERE studentID=" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //updating number of already booked private courses by other students
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
                        " WHERE  studentID NOT IN(" + "(Select studentID from student where username=\"" + NameHolder.getInstance().getName() +"\")) AND  " + getAge() +">= min_age AND " + getAge()+ "<=max_age AND type=\"private\"");
                rsBooked.next();
                booked.setText(rsBooked.getString("courseNb"));

            }
            else{
                //INSERTING 1 INSTRUCTOR FOR EACH COURSE --> IN GROUP COURSE //done
                //cant register more than once since it is the same row in the update statement--> Numbers wont change
                String sql = "UPDATE private_course " +
                        "SET instructorID=NULL " +
                        "WHERE privateID=\""+courseID.getValue()+"\"";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.execute();

                //updating instructorID on deregister press //done
                ResultSet rsInstructor = conn.createStatement().executeQuery("SELECT distinct(instructorId) FROM private_session natural join private_course natural join course natural join track WHERE privateID=\"" + courseID.getValue() +"\"");
                rsInstructor.next();
                instructorTA.setText(rsInstructor.getString("instructorId"));

                //updating courseNb on deregister press //done
                ResultSet rsCourse = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "FROM private_session natural join private_course natural join course natural join track " +
                        " WHERE instructorID=" + "(Select instructorID from instructor where username=\"" + NameHolder.getInstance().getName() +"\")");
                rsCourse.next();
                courseNb.setText(rsCourse.getString("courseNb"));

                //updating number of booked courses by other instructors
                ResultSet rsBooked = conn.createStatement().executeQuery("SELECT COUNT(DISTINCT privateID) AS courseNb " +
                        "from private_session natural join private_course natural join course natural join track " +
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
