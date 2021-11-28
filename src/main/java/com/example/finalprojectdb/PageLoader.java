package com.example.finalprojectdb;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageLoader {
    private String path;
    private String title;
    private int width;
    private int height;
    private Scene scene =null;

    public PageLoader (String path, String title, int width, int height){
        this.path=path; this.title=title; this.width= width; this. height= height;
    }

    public PageLoader(String path, String title) {
        this.path = path;
        this.title = title;
    }
    public void loadPage (ActionEvent e) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(path));
         if (width != 0 || height != 0) {
             scene = new Scene(parent, this.width, this.height);
         }
         else
         {
             scene = new Scene(parent);
         }

        Stage AboutPage_window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        AboutPage_window.setScene(scene);
        AboutPage_window.setTitle(title);
        AboutPage_window.setResizable(false);
        AboutPage_window.show();
    }



}
