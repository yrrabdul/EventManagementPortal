package com.eventmanagement.controllers;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;


public class DashboardController implements Initializable {

    @FXML
    private Pane context;

    @FXML
    private JFXButton dtnDashBoard;

    @FXML
    private AnchorPane root1;

    //a method to set new scene in "context" pane
    private void setUi(String location) throws IOException {
        context.getChildren().clear();
        context.getChildren().add(load(this.getClass().
                getResource("/com/eventmanagement/"+location+".fxml")));

        /*context.getChildren().add(FXMLLoader.load(this.getClass().
                getResource("/com/eventmanagement/"+location+".fxml")));*/
    }


    @FXML
    void AddItemOnAction(ActionEvent event) throws IOException {
        setUi("stockItems");
        new FadeIn(context).play();

    }

    @FXML
    void DashBoardOnAction() throws IOException {
        setUi("dashboardMain");
        new FadeIn(context).play();




    }

    @FXML
    void btnAboutOnAction(ActionEvent event) {

    }

    @FXML
    void btnEventOnAction(ActionEvent event) throws IOException {
        setUi("events");
        new FadeIn(context).play();

    }

    @FXML
    void btnAddCustomer(ActionEvent event) throws IOException {
        setUi("customers");
        new FadeIn(context).play();

    }

    @FXML
    void btnAddEmployee(ActionEvent event) throws IOException {
        setUi("employees");
        new FadeIn(context).play();

    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) throws IOException {
        setUi("suppliers");
        new FadeIn(context).play();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DashBoardOnAction();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
