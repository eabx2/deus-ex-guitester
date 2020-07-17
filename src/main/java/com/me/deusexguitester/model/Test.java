package com.me.deusexguitester.model;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ersinn on 17.07.2020.
 */
public class Test {

    public String name;

    public ArrayList<Command> commands;

    public ArrayList<BufferedImage> screenshots;

    public Test(){
        commands = new ArrayList<>();
        screenshots = new ArrayList<>();
    }

    public void rollName(){
        name = "test_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date().getTime());
    }

    public void clear(){
        commands.clear();
        screenshots.clear();
    }

}
