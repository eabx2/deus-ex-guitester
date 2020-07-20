package com.me.deusexguitester.listener;

import com.me.deusexguitester.controller.MainSceneController;
import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.model.Test;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by ersinn on 13.07.2020.
 */
public class MouseActivityListener implements NativeMouseInputListener{

    public static long lastMousePressedTime;

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {

        Rectangle rect = null;

        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
            if(MainSceneController.newTest.testInfo.testedWindow.equals(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)))){
                rect = desktopWindow.getLocAndSize();
                break;
            }
        }

        // if specified window is not found
        if(rect == null)
            return;

        // if mouse event is outside of the window
        if(!((e.getX() >= rect.x && e.getX() <= rect.x + rect.width) && (e.getY() >= rect.y && e.getY() <= rect.y + rect.height)))
            return;

        lastMousePressedTime = e.getWhen();

        Command command = new Command();
        command.action = "mousePressed";
        command.buttonNumber = String.valueOf(e.getButton());
        command.x = String.valueOf((int)(e.getX() - rect.getX())); // relative coordinates
        command.y = String.valueOf((int)(e.getY() - rect.getY()));

        MainSceneController.newTest.commands.add(command);

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {

        Rectangle rect = null;

        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
            if(MainSceneController.newTest == null) return;
            if(MainSceneController.newTest.testInfo.testedWindow.equals(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)))){
                rect = desktopWindow.getLocAndSize();
                break;
            }
        }

        // if specified window is not found
        if(rect == null)
            return;

        // if mouse event is outside of the window
        if(!((e.getX() >= rect.x && e.getX() <= rect.x + rect.width) && (e.getY() >= rect.y && e.getY() <= rect.y + rect.height)))
            return;

        Command command = new Command();
        command.action = "mouseReleased";
        command.buttonNumber = String.valueOf(e.getButton());
        command.x = String.valueOf((int)(e.getX() - rect.getX())); // relative coordinates
        command.y = String.valueOf((int)(e.getY() - rect.getY()));

        MainSceneController.newTest.commands.add(command);

    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {

    }
}
