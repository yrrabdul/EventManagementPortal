/**
 *
 */
module com.eventmanagement {

    requires transitive javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;
    requires AnimateFX;
    requires com.jfoenix;
    requires tray;



    opens com.eventmanagement to javafx.fxml;
    exports com.eventmanagement;
    exports com.eventmanagement.controllers;
    opens com.eventmanagement.controllers to javafx.fxml;
    opens com.eventmanagement.entities to javafx.fxml;
    exports com.eventmanagement.entities;




}