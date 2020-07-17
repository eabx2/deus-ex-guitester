package com.me.deusexguitester.controller;

import static com.me.deusexguitester.fileManager.FileManager.getFileManager;

import com.me.deusexguitester.listener.KeyActivityListener;
import com.me.deusexguitester.listener.MouseActivityListener;
import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.model.Test;
import com.me.deusexguitester.tester.Tester;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by ersinn on 13.07.2020.
 */
public class MainSceneController implements Initializable{

    private Test test;
    private int numberOfVerifyPoints = 0;
    private int numberOfScreenShots = 0;

    private SimpleBooleanProperty recording = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty playbacking= new SimpleBooleanProperty(false);

    public static Pane mainPaneInstance;

    @FXML
    Pane mainPane;

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

    // Button Functions

    public void onRecordButtonClick(ActionEvent event){

        recording.setValue(true);

        // give a name to the test
        test.rollName();

        System.out.println("Recording " + test.name);

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
        if(test.commands.size()>=2){
            test.commands.remove(test.commands.size()-1);
            test.commands.remove(test.commands.size()-1);
        }

        getFileManager().saveTest(test);

        // reset test
        test.clear();

        System.out.println("Saved " + test.name + "\n");

        // reset verify points and screenshot number
        numberOfVerifyPoints = 0;
        numberOfScreenShots = 0;

        // update list-view
        onRefreshButtonClick(new ActionEvent());

    }

    public void onPauseButtonClick(ActionEvent event) {

        if(pauseButton.getText().equals("Pause")){
            pauseButton.setText("Resume");

            System.out.println("\tPaused");

            // unhook the screen
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }

            // remove clicks on this button
            if(test.commands.size()>=2){
                test.commands.remove(test.commands.size()-1);
                test.commands.remove(test.commands.size()-1);
            }

        }
        else if(pauseButton.getText().equals("Resume")){
            pauseButton.setText("Pause");

            System.out.println("\tResuming " + test.name);

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

        // reset commands and screenshots
        test.clear();

        System.out.println("Aborted " + test.name + "\n");

        // reset verify points and screenshot number
        numberOfVerifyPoints = 0;
        numberOfScreenShots = 0;

    }

    public void onVerifyScreenButtonClick(ActionEvent event) {

        // remove clicks on this button
        if(test.commands.size()>=2){
            test.commands.remove(test.commands.size()-1);
            test.commands.remove(test.commands.size()-1);
        }

        Rectangle entireFirstScreen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenshot = Tester.getRobot().createScreenCapture(entireFirstScreen);

        // add verifyValue command
        Command command = new Command();
        command.action = "verifyScreen";
        command.screenshotName = "ss" + numberOfScreenShots++;
        command.verifyNumber = numberOfVerifyPoints++;

        test.commands.add(command);
        test.screenshots.add(screenshot);

        System.out.println("\tVerify Screen is DONE");

    }

    public void onVerifyValueButtonClick(ActionEvent event) {

        // remove clicks on this button
        if(test.commands.size()>=2){
            test.commands.remove(test.commands.size()-1);
            test.commands.remove(test.commands.size()-1);
        }

        // unhook the screen
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        // repeat the last press-release action pair
        Command temp[] = new Command[]{test.commands.get(test.commands.size()-2),test.commands.get(test.commands.size()-1)};

        // copy where the mouse is - on verify button -
        double x = MouseInfo.getPointerInfo().getLocation().getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY();

        // repeat the last press-release action pair
        Tester.getRobot().mouseMove(Integer.parseInt(temp[0].x),Integer.parseInt(temp[0].y));
        Tester.getRobot().mousePress(InputEvent.getMaskForButton(Integer.parseInt(temp[0].buttonNumber)));
        Tester.getRobot().mouseMove(Integer.parseInt(temp[1].x),Integer.parseInt(temp[1].y));
        Tester.getRobot().mouseRelease(InputEvent.getMaskForButton(Integer.parseInt(temp[1].buttonNumber)));

        // copy selected text
        Tester.getRobot().setAutoDelay(50);
        Tester.getRobot().keyPress(KeyEvent.VK_CONTROL);
        Tester.getRobot().keyPress(KeyEvent.VK_C);
        Tester.getRobot().keyRelease(KeyEvent.VK_CONTROL);
        Tester.getRobot().keyRelease(KeyEvent.VK_C);

        // get the selected text from clipboard
        String copiedText = null;
        try {
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            copiedText = systemClipboard.getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        // get back the mouse onto verify button
        Tester.getRobot().mouseMove(((int) x), ((int) y));

        // add verifyValue command
        Command command = new Command();
        command.action = "verifyValue";
        command.value = copiedText;
        command.verifyNumber = numberOfVerifyPoints++;

        test.commands.add(command);

        System.out.println("\tVerify Value is DONE");

        // continue to hook the screen
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

    }

    public void onPlaybackButtonClick(ActionEvent event){
        playbacking.setValue(true);

        testsListView.getSelectionModel().getSelectedItems().forEach(testName -> Tester.getTester().perform((String) testName));

        playbacking.setValue(false);
    }

    public void onSelectAllCheckBoxClick(ActionEvent event) {
        if(selectAllCheckBox.isSelected()){
            testsListView.getSelectionModel().selectAll();
        }
        else {
            testsListView.getSelectionModel().clearSelection();
        }

    }

    public void onRefreshButtonClick(ActionEvent event) {

        File[] tests = getFileManager().getTestFiles();

        // reset list-view
        testsListView.getItems().clear();

        // reset selectAllCheckBox
        selectAllCheckBox.setSelected(false);

        // update
        for (int i = 0; i < tests.length; i++) {
            testsListView.getItems().add(tests[i].getName());
        }
    }

    // First Function to be run

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // disable property
        recordButton.disableProperty().bind(recording.or(playbacking));
        stopButton.disableProperty().bind(recording.not().or(playbacking));
        pauseButton.disableProperty().bind(recording.not().or(playbacking));
        abortButton.disableProperty().bind(recording.not().or(playbacking));

        verifyScreenButton.disableProperty().bind(recording.not().or(playbacking));
        verifyValueButton.disableProperty().bind(recording.not().or(playbacking));

        playbackButton.disableProperty().bind(playbacking.or(recording));

        selectAllCheckBox.disableProperty().bind(playbacking.or(recording));
        testsListView.disableProperty().bind(playbacking.or(recording));
        testsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // bind list-view and checkbox
        testsListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
            if(c.getList().size() == testsListView.getItems().size()) selectAllCheckBox.setSelected(true);
            else selectAllCheckBox.setSelected(false);
        });

        refreshButton.disableProperty().bind(recording.or(playbacking));

        // firstly fill list-view
        onRefreshButtonClick(new ActionEvent());

        test = new Test();

        // Add listeners
        GlobalScreen.addNativeMouseListener(new MouseActivityListener(test.commands));
        GlobalScreen.addNativeKeyListener(new KeyActivityListener(test.commands));

        // let it refer to mainPane
        mainPaneInstance = mainPane;

    }

}
