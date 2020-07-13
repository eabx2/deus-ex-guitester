package com.me.deusexguitester.listener;

import com.me.deusexguitester.model.Command;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.util.ArrayList;

/**
 * Created by ersinn on 13.07.2020.
 */
public class MouseActivityListener implements NativeMouseInputListener{

    public ArrayList<Command> commands;

    public MouseActivityListener(ArrayList<Command> commands){
        this.commands = commands;
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {

        Command command = new Command();
        command.action = "mousePressed";
        command.buttonNumber = String.valueOf(e.getButton());
        command.x = String.valueOf(e.getX());
        command.y = String.valueOf(e.getY());

        commands.add(command);

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        Command command = new Command();
        command.action = "mouseReleased";
        command.buttonNumber = String.valueOf(e.getButton());
        command.x = String.valueOf(e.getX());
        command.y = String.valueOf(e.getY());

        commands.add(command);

    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {

    }
}
