package com.me.deusexguitester.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.Point2D;
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

    private Point2D pressedPoint;

    ContextMenu contextMenu;

    public void onDrawPaneMouseDragDetected(MouseEvent event) {

        // first reset the rectangle
        selectAreaRectangle.setWidth(0);
        selectAreaRectangle.setHeight(0);

        pressedPoint.setLocation(event.getX(), event.getY());

    }

    public void onDrawPaneMouseDragged(MouseEvent event) {

        selectAreaRectangle.setX(pressedPoint.getX() < event.getX() ? pressedPoint.getX() : event.getX());
        selectAreaRectangle.setY(pressedPoint.getY() < event.getY() ? pressedPoint.getY() : event.getY());

        selectAreaRectangle.setWidth(Math.abs(event.getX() - pressedPoint.getX()));
        selectAreaRectangle.setHeight(Math.abs(event.getY() - pressedPoint.getY()));

    }

    public void onDrawPaneContextMenuRequested(ContextMenuEvent contextMenuEvent) {

        contextMenu.show(drawPane,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // reset every time this controller called
        selectedAreaRectangle = null;

        pressedPoint = new Point2D.Double();

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
