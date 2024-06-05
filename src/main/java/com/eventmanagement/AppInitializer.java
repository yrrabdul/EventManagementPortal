package com.eventmanagement;

import animatefx.animation.FadeInDownBig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AppInitializer extends Application {
    Parent root;
    double xOffset, yOffset;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Event Management");
        stage.setScene(scene);
        stage.show();
        new FadeInDownBig(root).play();
    }

    public static void main(String[] args) {
        launch();
    }
}