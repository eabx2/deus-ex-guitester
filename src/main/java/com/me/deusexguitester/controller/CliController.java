package com.me.deusexguitester.controller;

import java.util.Scanner;

/**
 * Created by ersinn on 16.07.2020.
 */
public class CliController {

    private static CliController cli;

    private CliController(){
        System.out.println("*** Deus Ex GUI-Tester ***");
    }

    public void listen(){

        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                String input = in.next().trim().toLowerCase();
                System.out.println(input);
            }
        }

    }

    public static CliController getCli(){
        if(cli == null) cli = new CliController();
        return cli;
    }

}
