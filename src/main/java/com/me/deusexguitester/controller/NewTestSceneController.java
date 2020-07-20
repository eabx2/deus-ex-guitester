package com.me.deusexguitester.controller;

import com.me.deusexguitester.model.Test;
import com.me.deusexguitester.model.TestInfo;

import com.sun.jna.platform.WindowUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ersinn on 18.07.2020.
 */
public class NewTestSceneController implements Initializable {

    @FXML
    ComboBox windowComboBox;

    @FXML
    TextField testNameTextField;

    @FXML
    TextArea testDescriptionTextArea;

    @FXML
    Button doneButton;

    public void doneButtonOnClick(ActionEvent event) {

        TestInfo testInfo = new TestInfo();

        if(testDescriptionTextArea.getText() == "") return;
        testInfo.name = testNameTextField.getText();

        if(windowComboBox.getSelectionModel().getSelectedItem() == null) return;
        testInfo.testedWindow = windowComboBox.getSelectionModel().getSelectedItem().toString();

        testInfo.description = testDescriptionTextArea.getText();

        Test test = Test.createTest(testInfo);

        MainSceneController.newTest = test;

        Stage stage = (Stage) doneButton.getScene().getWindow();
        stage.close();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final Rectangle rect = new Rectangle(0, 0, 0, 0); //needs to be final or effectively final for lambda
        WindowUtils.getAllWindows(true).forEach(desktopWindow -> {
            if(!desktopWindow.getTitle().equals(""))
                windowComboBox.getItems().add(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)));
        });

    }
}
