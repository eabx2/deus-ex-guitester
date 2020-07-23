package com.me.deusexguitester.listener;

import com.me.deusexguitester.controller.MainSceneController;
import com.me.deusexguitester.model.Command;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Win32Exception;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.awt.*;

/**
 * Created by ersinn on 23.07.2020.
 */
public class MouseWheelActivityListener implements NativeMouseWheelListener{

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {

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

        Command command = new Command();
        command.action = "mouseWheelMoved";
        command.wheelRotation = e.getWheelRotation();
        command.mouseActionX = (int)(e.getX() - rect.getX()); // relative coordinates
        command.mouseActionY = (int)(e.getY() - rect.getY());

        MainSceneController.newTest.commands.add(command);

    }

}
