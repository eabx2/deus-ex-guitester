package com.me.deusexguitester.listener;

import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.tester.LocalKeyBoardCheck;
import com.me.deusexguitester.tester.Tester;
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

    // this function fix wrong-hooked rawCode for robot
    private int rawCodeMapper(int rawCode){

        switch (rawCode){
            case 162: // ctrl
                return 17;
            case 164: // alt
                return 18;
            case 13: // enter
                return 10;
            default:
                return rawCode;
        }

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

        // only save turkish keys as key-typed
        if(!LocalKeyBoardCheck.isTurkishKeyStroke(e.getRawCode()))
            return;

        Command command = new Command();
        command.action = "keyTyped";
        command.rawCode = e.getRawCode();

        commands.add(command);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        // save turkish keys as key-typed
        if(LocalKeyBoardCheck.isTurkishKeyStroke(e.getRawCode()))
            return;

        Command command = new Command();
        command.action = "keyPressed";
        command.rawCode = rawCodeMapper(e.getRawCode());

        commands.add(command);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {

        // save turkish keys as key-typed
        if(LocalKeyBoardCheck.isTurkishKeyStroke(e.getRawCode()))
            return;

        Command command = new Command();
        command.action = "keyReleased";
        command.rawCode = rawCodeMapper(e.getRawCode());

        commands.add(command);
    }
}
