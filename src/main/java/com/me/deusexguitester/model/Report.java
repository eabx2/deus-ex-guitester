package com.me.deusexguitester.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by ersinn on 21.07.2020.
 */
public class Report {

    public String testName;
    public LocalDateTime time;
    public int countPassedVerificationPoint;
    public int countFailedVerificationPoint;

    public Report(String testName){
        this.testName = testName;
        this.time = LocalDateTime.now();
    }

    public void printConsole(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        System.out.println("\nTest: " + testName + " - " + dtf.format(time));
        System.out.println(countPassedVerificationPoint + " Verification Points PASSED");
        System.out.println(countFailedVerificationPoint + " Verification Points FAILED\n");
    }

}
