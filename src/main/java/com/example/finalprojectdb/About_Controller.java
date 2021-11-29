package com.example.finalprojectdb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class About_Controller {
    @FXML
    private Button Back;

    @FXML
    protected void onBackButtonClick(ActionEvent e) throws IOException {
        System.out.println ("About Us clicked");
        PageLoader loaderSignUp= new PageLoader("SignInUI.fxml", "Sign In");
        loaderSignUp.loadPage(e);
    }
}