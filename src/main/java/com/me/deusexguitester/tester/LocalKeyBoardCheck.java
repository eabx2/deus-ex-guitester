package com.me.deusexguitester.tester;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Created by ersinn on 16.07.2020.
 */
public class LocalKeyBoardCheck {

    public static boolean isTurkishKeyStroke(int rawCode){

        switch (rawCode){
            case 191: // ö
            case 186: // ş
            case 221: // ü
            case 219: // ğ
            case 222: // i
            case 220: // ç
                return true;
            default:
                return false;
        }

    }

    private static char getTurkishChar(int rawCode){

        switch (rawCode){
            case 191: // ö
                return 'ö';
            case 186: // ş
                return 'ş';
            case 221: // ü
                return 'ü';
            case 219: // ğ
                return 'ğ';
            case 222: // i
                return 'i';
            case 220: // ç
                return 'ç';
            default:
                return ' ';
        }

    }

    public static void handleTurkishKeyStroke(int rawCode){

        char c = getTurkishChar(rawCode);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // set into clipboard
        clipboard.setContents(new StringSelection("" + c),null);

        // type into
        Tester.getRobot().keyPress(KeyEvent.VK_CONTROL);
        Tester.getRobot().keyPress(KeyEvent.VK_V);
        Tester.getRobot().keyRelease(KeyEvent.VK_V);
        Tester.getRobot().keyRelease(KeyEvent.VK_CONTROL);

    }

}
