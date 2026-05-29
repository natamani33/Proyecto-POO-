package org.example;

import controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainController window = new MainController();
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}