package com.me.deusexguitester.controller;

import com.me.deusexguitester.fileManager.FileManager;
import com.me.deusexguitester.listener.KeyActivityListener;
import com.me.deusexguitester.listener.MouseActivityListener;
import com.me.deusexguitester.listener.MouseWheelActivityListener;
import com.me.deusexguitester.model.*;
import com.me.deusexguitester.tester.Tester;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Created by ersinn on 13.07.2020.
 */
public class MainSceneController implements Initializable{

    public static Test newTest;
    private int numberOfVerifyPoints = 0;
    private int numberOfScreenShots = 0;

    private SimpleBooleanProperty recording = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty playbacking = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty paused = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty selectingPortion  = new SimpleBooleanProperty(false);

    public static Pane mainPaneInstance;

    @FXML
    Pane mainPane;

    // Action Buttons

    @FXML
    Button recordButton;

    @FXML
    Button pauseButton;

    @FXML
    Button saveButton;

    @FXML
    Button playbackButton;

    @FXML
    Button abortButton;

    // Recording Action Buttons

    @FXML
    Button verifyScreenButton;

    @FXML
    Button verifyValueButton;

    @FXML
    Button waitButton;

    @FXML
    Button verifyPortionButton;

    // Content Components

    @FXML
    TableView testsTableView;

    @FXML
    TableColumn testNameTableColumn;

    @FXML
    TableColumn windowNameTableColumn;

    @FXML
    TableColumn descriptionNameTableColumn;

    @FXML
    CheckBox selectAllCheckBox;

    @FXML
    Button refreshButton;

    ContextMenu testsTableViewContextMenu;

    // Button Functions

    public void onRecordButtonClick(ActionEvent event){

        // get new test
        displayNewTestScene();

        // if test name is not specified then do not start recording
        if(newTest == null) return;

        recording.setValue(true);

        System.out.println("Recording " + newTest.testInfo.name);

        // hook the screen
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

    }

    public void onSaveButtonClick(ActionEvent event) {

        recording.setValue(false);
        pauseButton.setText("Pause");

        // unhook the screen
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {

        }

        // save test
        newTest.saveTest();

        System.out.println("Saved " + newTest.testInfo.name + "\n");

        // reset newTest
        newTest = null;

        // reset verify points and screenshot number
        numberOfVerifyPoints = 0;
        numberOfScreenShots = 0;

        // update list-view
        onRefreshButtonClick(new ActionEvent());

    }

    public void onPauseButtonClick(ActionEvent event) {

        if(pauseButton.getText().equals("Pause")){
            pauseButton.setText("Resume");
            paused.setValue(true);
            System.out.println("\tPaused");

            // unhook the screen
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }

        }
        else if(pauseButton.getText().equals("Resume")){
            pauseButton.setText("Pause");
            paused.setValue(false);
            System.out.println("\tResuming");

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

        // delete test dir
        newTest.delete();

        System.out.println("Aborted " + newTest.testInfo.name + "\n");

        // reset newTest
        newTest = null;

        // reset verify points and screenshot number
        numberOfVerifyPoints = 0;
        numberOfScreenShots = 0;

    }

    public void onVerifyScreenButtonClick(ActionEvent event) {

        Rectangle rect = null;
        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
            if(MainSceneController.newTest.testInfo.testedWindow.equals(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)))){
                rect = desktopWindow.getLocAndSize();
                break;
            }
        }
        BufferedImage screenshot = Tester.getRobot().createScreenCapture(rect);

        // add verifyValue command
        Command command = new Command();
        command.action = "verifyScreen";
        command.screenshotName = "ss" + numberOfScreenShots++;
        command.verifyNumber = numberOfVerifyPoints++;

        newTest.commands.add(command);
        newTest.addScreenshot(screenshot,command.screenshotName);

        System.out.println("\tVerify Screen is DONE");

    }

    public void onVerifyValueButtonClick(ActionEvent event) {

        // unhook the screen
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        Rectangle rect = null;

        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
            if(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)).equals(MainSceneController.newTest.testInfo.testedWindow)){
                rect = desktopWindow.getLocAndSize();
                break;
            }
        }

        // copy where the mouse is - on verify button -
        double oldX = MouseInfo.getPointerInfo().getLocation().getX();
        double oldY = MouseInfo.getPointerInfo().getLocation().getY();

        if(newTest.commands.get(newTest.commands.size()-1).action == "mouseDoubleClicked"){
            Tester.getRobot().mouseMove(rect.x + newTest.commands.get(newTest.commands.size()-1).mouseActionX, rect.y + newTest.commands.get(newTest.commands.size()-1).mouseActionY);
            Tester.robotMouseDoubleClick(newTest.commands.get(newTest.commands.size()-1).mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
        }

        // repeat the last press-release action pair
        else {
            Command temp[] = new Command[]{newTest.commands.get(newTest.commands.size()-2),newTest.commands.get(newTest.commands.size()-1)};

            Tester.getRobot().setAutoDelay(50);

            // repeat the last press-release action pair
            Tester.getRobot().mouseMove(rect.x + temp[0].mouseActionX,rect.y + temp[0].mouseActionY);
            Tester.getRobot().mousePress(newTest.commands.get(newTest.commands.size()-1).mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
            Tester.getRobot().mouseMove(rect.x + temp[1].mouseActionX,rect.y + temp[1].mouseActionY);
            Tester.getRobot().mouseRelease(newTest.commands.get(newTest.commands.size()-1).mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
        }

        // copy selected text
        Tester.getRobot().setAutoDelay(50);
        Tester.getRobot().keyPress(KeyEvent.VK_CONTROL);
        Tester.getRobot().keyPress(KeyEvent.VK_C);
        Tester.getRobot().keyRelease(KeyEvent.VK_CONTROL);
        Tester.getRobot().keyRelease(KeyEvent.VK_C);

        // get the selected text from clipboard
        String copiedText = Tester.getClipboardText();

        // get back the mouse onto verify button
        Tester.getRobot().mouseMove(((int) oldX), ((int) oldY));

        // add verifyValue command
        Command command = new Command();
        command.action = "verifyValue";
        command.value = copiedText;
        command.verifyNumber = numberOfVerifyPoints++;

        newTest.commands.add(command);

        System.out.println("\tVerify Value is DONE");

        // continue to hook the screen
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

    }

    public void onWaitButtonClick(ActionEvent event) {

        Command command = new Command();
        command.action = "wait";
        command.milisec = System.currentTimeMillis() - MouseActivityListener.lastMousePressedTime;

        newTest.commands.add(command);

        System.out.println("Wait for " + command.milisec);

    }

    public void onVerifyPortionButtonClick(ActionEvent event) {

        // unhook
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        // get the area
        displaySelectPortionScene();

        // hook again
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        // if no rectangle is specified return
        if(SelectPortionController.selectedAreaRectangle == null) return;

        int x1 = (int) SelectPortionController.selectedAreaRectangle.getX();
        int y1 = (int) SelectPortionController.selectedAreaRectangle.getY();

        int x2 = (int) (SelectPortionController.selectedAreaRectangle.getX() + SelectPortionController.selectedAreaRectangle.getWidth());
        int y2 = (int) (SelectPortionController.selectedAreaRectangle.getY() + SelectPortionController.selectedAreaRectangle.getHeight());

        // create rect with two points
        Rectangle rect = new Rectangle(new Point(x1,y1));
        rect.add(new Point(x2,y2));

        // move rect relative into the app window
        Rectangle windowRect = Tester.getRectangleOfWindowByTitle(newTest.testInfo.testedWindow);
        rect.setLocation(rect.x + windowRect.x, rect.y + windowRect.y);

        // get ss
        BufferedImage screenshot = Tester.getRobot().createScreenCapture(rect);

        // add verifyPortion command
        Command command = new Command();
        command.action = "verifyPortion";
        command.screenshotName = "ss" + numberOfScreenShots++;
        command.x1 = x1;
        command.y1 = y1;
        command.x2 = x2;
        command.y2 = y2;
        command.verifyNumber = numberOfVerifyPoints++;

        // add
        newTest.commands.add(command);
        newTest.addScreenshot(screenshot,command.screenshotName);

        System.out.println("\tVerify Portion is DONE");

    }

    public void onPlaybackButtonClick(ActionEvent event){
        playbacking.setValue(true);

        testsTableView.getSelectionModel().getSelectedItems().forEach((o -> {
            System.out.println("\nTesting " + ((TestInfoProperty) o).getName());
            Test test = Test.loadTest(((TestInfoProperty) o).getName());
            Report report = Tester.getTester().perform(test);
            System.out.println("Done " + ((TestInfoProperty) o).getName());
            report.printConsole();
        }));

        playbacking.setValue(false);
    }

    public void onSelectAllCheckBoxClick(ActionEvent event) {
        if(selectAllCheckBox.isSelected()){
            testsTableView.getSelectionModel().selectAll();
        }
        else {
            testsTableView.getSelectionModel().clearSelection();
        }

    }

    public void onRefreshButtonClick(ActionEvent event) {

        ObservableList<TestInfoProperty> testInfoPropertyList = FileManager.getFileManager().getTestInfoPropertyList();

        testsTableView.setItems(testInfoPropertyList);

        // reset selectAllCheckBox
        selectAllCheckBox.setSelected(false);

    }

    public void onTestsTableViewContextMenuRequested(ContextMenuEvent contextMenuEvent) {
        testsTableViewContextMenu.show(testsTableView,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());
    }

    // External functions

    public void displaySelectPortionScene(){

        selectingPortion.setValue(true);

        Stage window = new Stage();
        window.initStyle(StageStyle.TRANSPARENT); // hide the top bar
        window.initModality(Modality.APPLICATION_MODAL);

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/SelectPortionScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        window.setX(Tester.getRectangleOfWindowByTitle(newTest.testInfo.testedWindow).getX());
        window.setY(Tester.getRectangleOfWindowByTitle(newTest.testInfo.testedWindow).getY());
        window.setWidth(Tester.getRectangleOfWindowByTitle(newTest.testInfo.testedWindow).getWidth());
        window.setHeight(Tester.getRectangleOfWindowByTitle(newTest.testInfo.testedWindow).getHeight());

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // frame color

        window.setScene(scene);
        window.initStyle(StageStyle.TRANSPARENT);  // make it transparent

        window.setResizable(false);
        window.showAndWait();

        selectingPortion.setValue(false);
    }

    public void displayNewTestScene(){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("New Test");

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/NewTestScene.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        window.setX(recordButton.getScene().getWindow().getX() + recordButton.getScene().getWindow().getWidth() / 2);
        window.setY(recordButton.getScene().getWindow().getY());

        window.setScene(new Scene(root));
        window.setResizable(false);
        window.showAndWait();

    }

    // First function to be run

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // disable property
        recordButton.disableProperty().bind(recording.or(playbacking));
        saveButton.disableProperty().bind(recording.not().or(playbacking));
        pauseButton.disableProperty().bind(recording.not().or(playbacking));
        abortButton.disableProperty().bind(recording.not().or(playbacking));

        verifyScreenButton.disableProperty().bind(recording.not().or(playbacking).or(paused).or(selectingPortion));
        verifyValueButton.disableProperty().bind(recording.not().or(playbacking).or(paused).or(selectingPortion));
        waitButton.disableProperty().bind(recording.not().or(playbacking).or(paused).or(selectingPortion));
        verifyPortionButton.disableProperty().bind(recording.not().or(playbacking).or(paused).or(selectingPortion));

        playbackButton.disableProperty().bind(playbacking.or(recording));

        selectAllCheckBox.disableProperty().bind(playbacking.or(recording));
        testsTableView.disableProperty().bind(playbacking.or(recording));

        testsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        testsTableViewContextMenu = new ContextMenu();

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setDisable(true); // initially disabled

        deleteMenuItem.setOnAction(event -> {
            testsTableView.getSelectionModel().getSelectedItems().forEach(o -> {
                FileManager.getFileManager().deleteTestDirectory(((TestInfoProperty) o).getName());
            });
            onRefreshButtonClick(new ActionEvent());
        });

        testsTableViewContextMenu.getItems().add(deleteMenuItem);

        // bind list-view and checkbox
        testsTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) c -> {
            if(c.getList().size() == 0) deleteMenuItem.setDisable(true);
            else deleteMenuItem.setDisable(false);

            if(c.getList().size() ==  testsTableView.getItems().size()) selectAllCheckBox.setSelected(true);
            else selectAllCheckBox.setSelected(false);
        });

        refreshButton.disableProperty().bind(recording.or(playbacking));

        // set factory of columns
        testNameTableColumn.setCellValueFactory(new PropertyValueFactory<TestInfo,String>("name"));
        windowNameTableColumn.setCellValueFactory(new PropertyValueFactory<TestInfo,String>("testedWindow"));
        descriptionNameTableColumn.setCellValueFactory(new PropertyValueFactory<TestInfo,String>("description"));

        // firstly fill list-view
        onRefreshButtonClick(new ActionEvent());

        // Add listeners
        GlobalScreen.addNativeMouseListener(new MouseActivityListener());
        GlobalScreen.addNativeMouseWheelListener(new MouseWheelActivityListener());
        GlobalScreen.addNativeKeyListener(new KeyActivityListener());

        // let it refer to mainPane
        mainPaneInstance = mainPane;

    }

}
