package com.me.deusexguitester;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jnativehook.GlobalScreen;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ersinn on 13.07.2020.
 */
public class Main extends Application{

    public static void main(String args[]){

        // Logs OFF
        Logger logger = Logger.getLogger((GlobalScreen.class.getPackage().getName()));
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        // Show GUI
        Application.launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Deus Ex Gui-Tester");

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
