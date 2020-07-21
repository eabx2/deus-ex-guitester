package com.me.deusexguitester.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ersinn on 21.07.2020.
 */
public class SelectPortionController implements Initializable {

    public static Rectangle selectedAreaRectangle;

    @FXML
    Pane drawPane;

    @FXML
    Rectangle selectAreaRectangle;

    ContextMenu contextMenu;

    public void onDrawPaneMouseDragDetected(MouseEvent event) {

        // first reset the rectangle
        selectAreaRectangle.setWidth(0);
        selectAreaRectangle.setHeight(0);

        // place the rectangle
        selectAreaRectangle.setX(event.getX());
        selectAreaRectangle.setY(event.getY());

    }

    public void onDrawPaneMouseDragged(MouseEvent event) {

        selectAreaRectangle.setWidth(event.getX() - selectAreaRectangle.getX());
        selectAreaRectangle.setHeight(event.getY() - selectAreaRectangle.getY());

    }

    public void onDrawPaneContextMenuRequested(ContextMenuEvent contextMenuEvent) {

        contextMenu.show(drawPane,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // reset every time this controller called
        selectedAreaRectangle = null;

        contextMenu = new ContextMenu();

        MenuItem done = new MenuItem("Done");
        MenuItem cancel = new MenuItem("Cancel");

        done.setOnAction(event -> {
            selectedAreaRectangle = selectAreaRectangle;
            Stage stage = (Stage) drawPane.getScene().getWindow();
            stage.close();
        });

        cancel.setOnAction(event -> {
            Stage stage = (Stage) drawPane.getScene().getWindow();
            stage.close();
        });

        contextMenu.getItems().addAll(done,cancel);

    }

}
