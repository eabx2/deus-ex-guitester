package com.me.deusexguitester.model;

import java.util.ArrayList;

/**
 * Created by ersinn on 13.07.2020.
 */
public class Command {

    public String action;

    // mouse actions
    public String buttonNumber;
    public String x;
    public String y;

    // key actions;
    public int rawCode;

    // verify screen
    public String screenshot;

    // verify value
    public String value;

    // verify common properties
    public int verifyNumber;

}
