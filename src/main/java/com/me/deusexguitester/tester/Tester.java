package com.me.deusexguitester.tester;

import com.me.deusexguitester.fileManager.FileManager;
import com.me.deusexguitester.model.Command;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ersinn on 15.07.2020.
 */
public class Tester {

    private static Tester tester;

    private Robot robot;

    public boolean perform(String testName){

        System.out.println("Testing Started " + testName);

        tester.robot.setAutoDelay(250);

        ArrayList<Command> commands = FileManager.getFileManager().getTestCommandsByName(testName);

        commands.forEach(command -> {

            switch (command.action){
                case "mousePressed":

                    robot.mouseMove(Integer.parseInt(command.x),Integer.parseInt(command.y));
                    robot.mousePress(InputEvent.getMaskForButton(Integer.parseInt(command.buttonNumber)));

                    break;

                case "mouseReleased":

                    robot.mouseMove(Integer.parseInt(command.x),Integer.parseInt(command.y));
                    robot.mouseRelease(InputEvent.getMaskForButton(Integer.parseInt(command.buttonNumber)));

                    break;

                case "keyTyped":

                    // only local chars saved as key-typed
                    if(LocalKeyBoardCheck.isTurkishKeyStroke(command.rawCode))
                        LocalKeyBoardCheck.handleTurkishKeyStroke(command.rawCode);

                    break;

                case "keyPressed":

                    robot.keyPress(command.rawCode);

                    break;

                case "keyReleased":

                    robot.keyRelease(command.rawCode);

                    break;

                case "verifyValue":

                    // copy selected text
                    robot.setAutoDelay(50);
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyPress(KeyEvent.VK_C);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_C);
                    robot.setAutoDelay(250);

                    // get the selected text from clipboard
                    String copiedText = null;
                    try {
                        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        copiedText = systemClipboard.getData(DataFlavor.stringFlavor).toString();
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }

                    // compare
                    if(command.value.equals(copiedText)) System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                    else System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");

                    break;
            }

        });

        System.out.println("Testing Ended " + testName);

        return true;
    }

    public static Robot getRobot(){
        return getTester().robot;
    }

    public static Tester getTester(){
        if(tester == null){
            tester = new Tester();
            try {
                tester.robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        return tester;
    }

}
