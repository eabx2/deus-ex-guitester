package com.me.deusexguitester;

import com.me.deusexguitester.controller.CliController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.jnativehook.GlobalScreen;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ersinn on 13.07.2020.
 */
public class Main extends Application{

    public static final boolean releaseVersion = false;

    public static void main(String args[]) throws IOException {

        // check if started by console - if not start the app by the console
        if(releaseVersion && System.console() == null && !GraphicsEnvironment.isHeadless()){
            final String jarFile = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            final String decodedPath = URLDecoder.decode(jarFile, "UTF-8");
            new ProcessBuilder(new String[] {"cmd", "/k", "start", "\"" + "Deus Ex GUI-Tester" + "\"", "java", "-jar", decodedPath.substring(1), "run"}).start();
            System.exit(1);
        }

        // Logs OFF
        Logger logger = Logger.getLogger((GlobalScreen.class.getPackage().getName()));
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        // launch
        Application.launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Show GUI
        primaryStage.setTitle("Deus Ex Gui-Tester");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        // Show CLI
        Thread cliThread = new Thread(CliController.getCli()::listen);
        cliThread.setDaemon(true);
        cliThread.start();
    }

}
