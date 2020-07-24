package com.me.deusexguitester.tester;

import com.me.deusexguitester.controller.MainSceneController;
import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.model.Report;
import com.me.deusexguitester.model.Test;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import javafx.geometry.Bounds;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ersinn on 15.07.2020.
 */
public class Tester {

    private static Tester tester;

    private Robot robot;

    public Report perform(Test test){

        Rectangle windowRect = getRectangleOfWindowByTitle(test.testInfo.testedWindow);

        // if specified window is not found
        if(windowRect == null){
            System.out.println("\tSpecified window is not found");
            return null;
        }

        Report report = new Report(test.testInfo.name);

        ArrayList<Command> commands = test.commands;

        Rectangle finalWindowRect = windowRect;

        commands.forEach(command -> {

            switch (command.action){

                case "mouseClicked":

                    robot.mouseMove(finalWindowRect.x + command.mouseActionX,finalWindowRect.y + command.mouseActionY);
                    robotMouseClick(command.mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK, command.pressedTime);

                    break;

                case "mouseDoubleClicked":

                    robot.mouseMove(finalWindowRect.x + command.mouseActionX,finalWindowRect.y + command.mouseActionY);
                    robotMouseDoubleClick(command.mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);

                    break;

                case "mousePressed":

                    // somehow java robot sees button3 as right click
                    robot.mouseMove(finalWindowRect.x + command.mouseActionX,finalWindowRect.y + command.mouseActionY);
                    robot.mousePress(command.mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);

                    break;

                case "mouseReleased":

                    // somehow java robot sees button3 as right click
                    // move smoothly on release action in case if it is a drag-drop
                    robotMoveMouseSmoothly(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, finalWindowRect.x + command.mouseActionX,finalWindowRect.y + command.mouseActionY);
                    robot.mouseRelease(command.mouseButtonNumber == 1 ? InputEvent.BUTTON1_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);

                    break;

                case "mouseWheelMoved":

                    robot.mouseMove(finalWindowRect.x + command.mouseActionX,finalWindowRect.y + command.mouseActionY);
                    robot.mouseWheel(command.wheelRotation);

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

                    Tester.getRobot().keyPress(KeyEvent.VK_CONTROL);
                    Tester.getRobot().keyPress(KeyEvent.VK_C);
                    Tester.getRobot().keyRelease(KeyEvent.VK_CONTROL);
                    Tester.getRobot().keyRelease(KeyEvent.VK_C);

                    // get the selected text from clipboard
                    String copiedText = getClipboardText();

                    // compare
                    if(command.value.equals(copiedText)){
                        System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                        report.countPassedVerificationPoint++;
                    }
                    else{
                        System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");
                        report.countFailedVerificationPoint++;
                    }

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
                    Tester.getRobot().delay(500);
                    robot.mouseRelease(InputEvent.getMaskForButton(1));
                    //Tester.getRobot().setAutoDelay(2000);

                    // get the screenshot
                    BufferedImage currentScreenshot = Tester.getRobot().createScreenCapture(finalWindowRect);

                    // load the image which wanted to be tested - saved as bmp -
                    BufferedImage testScreenshot = test.getScreenshotByName(command.screenshotName);

                    // compare
                    if(compareImages(currentScreenshot,testScreenshot)) {
                        System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                        report.countPassedVerificationPoint++;
                    }
                    else{
                        System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");
                        report.countFailedVerificationPoint++;
                    }

                    break;

                case "wait":

                    try {
                        Thread.sleep(command.milisec);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;

                case "verifyPortion":

                    // take the applet window coordinates
                    bounds = MainSceneController.mainPaneInstance.getBoundsInLocal();
                    screenBounds = MainSceneController.mainPaneInstance.localToScreen(bounds);
                    x = (int) screenBounds.getMinX();
                    y = (int) screenBounds.getMinY();

                    // focus to applet
                    robot.mouseMove(x,y);
                    robot.mousePress(InputEvent.getMaskForButton(1));
                    Tester.getRobot().delay(500);
                    robot.mouseRelease(InputEvent.getMaskForButton(1));

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
                    if(compareImages(currentPortion,testPortion)){
                        System.out.println("\tVerify Point " + command.verifyNumber + " is PASSED");
                        report.countPassedVerificationPoint++;
                    }
                    else{
                        System.out.println("\tVerify Point " + command.verifyNumber + " is FAILED");
                        report.countFailedVerificationPoint++;
                    }

                    break;
            }

            robot.delay(1500);

        });

        return report;
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

    public static String getClipboardText(){

        String copiedText = null;

        // throw exception when another process updates clipboard - attempt to get clipboard -
        while (true){

            try {
                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                copiedText = systemClipboard.getData(DataFlavor.stringFlavor).toString();
                return copiedText;
            } catch (UnsupportedFlavorException | IOException e) {
                System.out.println("error in clipboard");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e1) {
                }
            }

        }

    }

    public static void robotMoveMouseSmoothly(int currentX, int currentY, int destinationX, int destinationY){

        double dx = (destinationX - currentX) / (double) Math.abs(destinationX - currentX);
        double dy = (destinationY - currentY) / (double) Math.abs(destinationX - currentX);

        double x = currentX;
        double y = currentY;

        for(int i= 0; i < Math.abs((destinationX - currentX)); i++){

            x = x + dx;
            y = y + dy;
            Tester.getRobot().mouseMove((int) x, (int) y);
            Tester.getRobot().delay(5);

        }

    }

    public static void robotMouseClick(int buttonNumberMask, long pressedTime){

        Tester.getRobot().mousePress(buttonNumberMask);
        Tester.getRobot().delay((int)pressedTime);
        Tester.getRobot().mouseRelease(buttonNumberMask);
    }

    public static void robotMouseDoubleClick(int buttonNumberMask){
        Tester.getRobot().setAutoDelay(100);

        Tester.getRobot().mousePress(buttonNumberMask);
        Tester.getRobot().mouseRelease(buttonNumberMask);

        Tester.getRobot().mousePress(buttonNumberMask);
        Tester.getRobot().mouseRelease(buttonNumberMask);
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
