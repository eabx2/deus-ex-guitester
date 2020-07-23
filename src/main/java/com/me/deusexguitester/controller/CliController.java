package com.me.deusexguitester.controller;

/**
 * Created by ersinn on 16.07.2020.
 */

public class CliController {

    private static CliController cli;

    private CliController(){
        System.out.println("*** Deus Ex GUI-Tester ***");
    }

    public void listen(){

    }

    public static CliController getCli(){
        if(cli == null) cli = new CliController();
        return cli;
    }

}
