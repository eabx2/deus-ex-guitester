package com.me.deusexguitester.listener;

import com.me.deusexguitester.model.Command;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.ArrayList;

/**
 * Created by ersinn on 13.07.2020.
 */
public class KeyActivityListener implements NativeKeyListener{

    public ArrayList<Command> commands;

    public KeyActivityListener(ArrayList<Command> commands){
        this.commands = commands;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        Command command = new Command();
        command.action = "keyPressed";
        command.keyCode = String.valueOf(NativeKeyEvent.getKeyText(e.getKeyCode()));

        commands.add(command);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        Command command = new Command();
        command.action = "keyReleased";
        command.keyCode = String.valueOf(NativeKeyEvent.getKeyText(e.getKeyCode()));

        commands.add(command);
    }
}
