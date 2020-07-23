package com.me.deusexguitester.listener;

import com.me.deusexguitester.controller.MainSceneController;
import com.me.deusexguitester.model.Command;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Win32Exception;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.awt.*;

/**
 * Created by ersinn on 13.07.2020.
 */
public class MouseActivityListener implements NativeMouseInputListener{

    public static long lastMousePressedTime;

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {

        Rectangle rect = null;

        try {
            for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
                if(MainSceneController.newTest == null) return;
                if(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)).equals(MainSceneController.newTest.testInfo.testedWindow)){
                    rect = desktopWindow.getLocAndSize();
                    break;
                }
            }
        }catch (Win32Exception win32e){

        }

        // if specified window is not found
        if(rect == null)
            return;

        // if mouse event is outside of the window
        if(!((e.getX() >= rect.x && e.getX() <= rect.x + rect.width) && (e.getY() >= rect.y && e.getY() <= rect.y + rect.height)))
            return;

        // delete last press-release pair - which are encountered as a click action -
        MainSceneController.newTest.commands.remove(MainSceneController.newTest.commands.size()-1);
        MainSceneController.newTest.commands.remove(MainSceneController.newTest.commands.size()-1);

        // if it is clickCount is 2 then clear the last click event - union it and this click and save as a double click -
        if(e.getClickCount() % 2 == 0)
            MainSceneController.newTest.commands.remove(MainSceneController.newTest.commands.size()-1);

        Command command = new Command();
        command.action = e.getClickCount() % 2 == 0 ? "mouseDoubleClicked" : "mouseClicked";
        command.mouseButtonNumber = e.getButton();
        command.mouseActionX = (int)(e.getX() - rect.getX()); // relative coordinates
        command.mouseActionY = (int)(e.getY() - rect.getY());

        MainSceneController.newTest.commands.add(command);

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {

        Rectangle rect = null;

        try {
            for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
                if(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)).equals(MainSceneController.newTest.testInfo.testedWindow)){
                    rect = desktopWindow.getLocAndSize();
                    break;
                }
            }
        } catch (Win32Exception win32e){
            return;
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
        command.mouseButtonNumber = e.getButton();
        command.mouseActionX = (int)(e.getX() - rect.getX()); // relative coordinates
        command.mouseActionY = (int)(e.getY() - rect.getY());

        MainSceneController.newTest.commands.add(command);

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {

        Rectangle rect = null;

        try {
            for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
                if(MainSceneController.newTest == null) return;
                if(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)).equals(MainSceneController.newTest.testInfo.testedWindow)){
                    rect = desktopWindow.getLocAndSize();
                    break;
                }
            }
        }catch (Win32Exception win32e){

        }

        // if specified window is not found
        if(rect == null)
            return;

        // if mouse event is outside of the window
        if(!((e.getX() >= rect.x && e.getX() <= rect.x + rect.width) && (e.getY() >= rect.y && e.getY() <= rect.y + rect.height)))
            return;

        Command command = new Command();
        command.action = "mouseReleased";
        command.mouseButtonNumber = e.getButton();
        command.mouseActionX = (int)(e.getX() - rect.getX()); // relative coordinates
        command.mouseActionY = (int)(e.getY() - rect.getY());

        MainSceneController.newTest.commands.add(command);

    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {

    }

}
