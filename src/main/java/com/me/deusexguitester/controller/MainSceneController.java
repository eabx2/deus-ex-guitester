package com.me.deusexguitester.controller;

import static com.me.deusexguitester.fileManager.fileManager.getFileManager;

import com.me.deusexguitester.listener.KeyActivityListener;
import com.me.deusexguitester.listener.MouseActivityListener;
import com.me.deusexguitester.model.Command;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.script.Bindings;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * Created by ersinn on 13.07.2020.
 */
public class MainSceneController implements Initializable{

    public ArrayList<Command> commands;

    SimpleBooleanProperty recording = new SimpleBooleanProperty(false);

    // Action Buttons

    @FXML
    Button recordButton;

    @FXML
    Button pauseButton;

    @FXML
    Button stopButton;

    @FXML
    Button playbackButton;

    @FXML
    Button abortButton;

    // Verify Buttons

    @FXML
    Button verifyScreenButton;

    @FXML
    Button verifyValueButton;

    // Content Components

    @FXML
    ListView testsListView;

    @FXML
    CheckBox selectAllCheckBox;

    @FXML
    Button refreshButton;

    public void onRecordButtonClick(ActionEvent event){

        recording.setValue(true);

        // hook the screen
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

    }

    public void onStopButtonClick(ActionEvent event) {

        recording.setValue(false);
        pauseButton.setText("Pause");

        // unhook the screen
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {

        }

        // remove clicks on this button
        if(commands.size()>=2){
            commands.remove(commands.size()-1);
            commands.remove(commands.size()-1);
        }

        getFileManager().createTest(commands);

        // reset commands
        commands.clear();

    }

    public void onPauseButtonClick(ActionEvent event) {


        if(pauseButton.getText().equals("Pause")){
            pauseButton.setText("Resume");

            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }

            // remove clicks on this button
            if(commands.size()>=2){
                commands.remove(commands.size()-1);
                commands.remove(commands.size()-1);
            }

        }
        else if(pauseButton.getText().equals("Resume")){
            pauseButton.setText("Pause");

            // hook the screen
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }

        }

    }

    public void onAbortButtonClick(ActionEvent event) {

        recording.setValue(false);
        pauseButton.setText("Pause");

        // unhook the screen
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {

        }

        // reset commands
        commands.clear();

    }

    public void onVerifyScreenButtonClick(ActionEvent event) {

    }

    public void onVerifyValueButtonClick(ActionEvent event) {

    }

    public void onPlaybackButtonClick(ActionEvent event){

    }

    public void refreshButtonClick(ActionEvent event) {
        File[] tests = getFileManager().getTests();

        // reset
        testsListView.getItems().clear();

        // update
        for (int i = 0; i < tests.length; i++) {
            testsListView.getItems().add(tests[0].getName());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pauseButton.disableProperty().bind(recording.not());
        recordButton.disableProperty().bind(recording);
        stopButton.disableProperty().bind(recording.not());
        abortButton.disableProperty().bind(recording.not());

        refreshButtonClick(new ActionEvent());

        commands = new ArrayList<>();

        // Add listeners
        GlobalScreen.addNativeMouseListener(new MouseActivityListener(commands));
        GlobalScreen.addNativeKeyListener(new KeyActivityListener(commands));
    }

}
