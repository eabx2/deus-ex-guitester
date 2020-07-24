package com.me.deusexguitester.model;

/**
 * Created by ersinn on 13.07.2020.
 */
public class Command {

    public String action;

    // mouse actions
    public int mouseButtonNumber;
    public int mouseActionX;
    public int mouseActionY;
    public int wheelRotation;

    public long pressedTime;

    // key actions;
    public int rawCode;

    // verify screen
    public String screenshotName;

    // verify value
    public String value;

    // verify portion
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    // verify common properties
    public int verifyNumber;

    // wait value
    public long milisec;

}
