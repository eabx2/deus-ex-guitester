package com.me.deusexguitester.model;

/**
 * Created by ersinn on 21.07.2020.
 */
public class Report {

    public int countPassedVerificationPoint;
    public int countFailedVerificationPoint;

    public void printConsole(){
        System.out.println("\t" + countPassedVerificationPoint + " Verification Points PASSED");
        System.out.println("\t" + countFailedVerificationPoint + " Verification Points FAILED");
    }

}
