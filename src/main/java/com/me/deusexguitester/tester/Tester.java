package com.me.deusexguitester.tester;

import com.me.deusexguitester.controller.MainSceneController;
import com.me.deusexguitester.fileManager.FileManager;
import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.model.Test;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import javafx.geometry.Bounds;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by ersinn on 15.07.2020.
 */
public class Tester {

    private static Tester tester;

    private Robot robot;

    public boolean perform(Test test){

        Rectangle windowRect = getRectangleOfWindowByTitle(test.testInfo.testedWindow);

        // if specified window is not found
        if(windowRect == null){
            System.out.println("Specified window is not found");
            return false;
        }

        ArrayList<Command> commands = test.commands;

        Rectangle finalWindowRect = windowRect;

        commands.forEach(command -> {

            switch (command.action){
                case "mousePressed":

                    robot.mouseMove(finalWindowRect.x + Integer.parseInt(command.x),finalWindowRect.y + Integer.parseInt(command.y));
                    robot.mousePress(InputEvent.getMaskForButton(Integer.parseInt(command.buttonNumber)));

                    break;

                case "mouseReleased":

                    robot.mouseMove(finalWindowRect.x + Integer.parseInt(command.x),finalWindowRect.y + Integer.parseInt(command.y));
                    robot.mouseRelease(InputEvent.getMaskForButton(Integer.parseInt(command.buttonNumber)));

                    break;

                case "keyTyped":

                    // only local chars saved as key-typed - whose place is not found in the keyboard -
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

                    // get the selected text from clipboard
                    String copiedText = getClipboardText();

                    // compare
                    if(command.value.equals(copiedText))
                        System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                    else
                        System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");

                    break;

                case "verifyScreen":

                    // take the applet window coordinates
                    Bounds bounds = MainSceneController.mainPaneInstance.getBoundsInLocal();
                    Bounds screenBounds = MainSceneController.mainPaneInstance.localToScreen(bounds);
                    int x = (int) screenBounds.getMinX();
                    int y = (int) screenBounds.getMinY();

                    // focus to applet
                    robot.mouseMove(x,y);
                    robot.mousePress(InputEvent.getMaskForButton(1));
                    Tester.getRobot().setAutoDelay(500);
                    robot.mouseRelease(InputEvent.getMaskForButton(1));
                    Tester.getRobot().setAutoDelay(2000);

                    // get the screenshot
                    BufferedImage currentScreenshot = Tester.getRobot().createScreenCapture(finalWindowRect);

                    // load the image which wanted to be tested - saved as bmp -
                    BufferedImage testScreenshot = test.getScreenshotByName(command.screenshotName);

                    // compare
                    if(compareImages(currentScreenshot,testScreenshot))
                        System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                    else
                        System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");

                    break;

                case "wait":

                    try {
                        Thread.sleep(command.milisec);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;

                case "verifyPortion":

                    // create rect with two points
                    Rectangle portionRect = new Rectangle(new Point(command.x1,command.y1));
                    portionRect.add(new Point(command.x2,command.y2));

                    // move rect relative into the app window
                    portionRect.setLocation(portionRect.x + finalWindowRect.x, portionRect.y + finalWindowRect.y);

                    // get ss
                    BufferedImage currentPortion = Tester.getRobot().createScreenCapture(portionRect);

                    // load the image which wanted to be tested - saved as bmp -
                    BufferedImage testPortion = test.getScreenshotByName(command.screenshotName);

                    // compare
                    if(compareImages(currentPortion,testPortion))
                        System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                    else
                        System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");

                    break;
            }

            tester.robot.setAutoDelay(1250);

        });

        return true;
    }


    private static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {

        // The images must be the same size.
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width  = imgA.getWidth();
        int height = imgA.getHeight();

        // Loop over every pixel.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Compare the pixels for equality.
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    System.out.println(x + " " + y);
                    System.out.println(imgA.getRGB(x, y));
                    System.out.println(imgB.getRGB(x, y));
                    return false;
                }
            }
        }

        return true;
    }

    public static Rectangle getRectangleOfWindowByTitle(String title){
        Rectangle rect = null;

        for (DesktopWindow desktopWindow : WindowUtils.getAllWindows(true)){
            if(title.equals(desktopWindow.getTitle().substring(0,Math.min(desktopWindow.getTitle().length(),25)))){
                rect = desktopWindow.getLocAndSize();
                break;
            }
        }

        return rect;
    }

    private static String getClipboardText(){

        String copiedText = null;

        try {
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            copiedText = systemClipboard.getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        return copiedText;
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
