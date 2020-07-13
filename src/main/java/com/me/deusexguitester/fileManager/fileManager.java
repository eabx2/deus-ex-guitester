package com.me.deusexguitester.fileManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.deusexguitester.model.Command;

import java.io.File;
import java.io.File.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by ersinn on 13.07.2020.
 */
public class fileManager {

    private static fileManager fileManager;

    File workspace = new File("./workspace");

    private fileManager(){

        // create workspace
        workspace.mkdir();
    }

    public void createTest(ArrayList<Command> commands){

        String testDirName = "test_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date().getTime());

        File testDir = new File(workspace.getPath() + "\\" + testDirName);

        // create test dir
        testDir.mkdir();

        try {
            ObjectMapper mapper = new ObjectMapper();

            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commands);

            File commandsFile = new File(testDir.getPath() + "\\" + "commands.json");
            commandsFile.createNewFile();

            FileWriter writer = new FileWriter(commandsFile);
            writer.write(jsonString);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File[] getTests(){
        return workspace.listFiles();
    }

    public static fileManager getFileManager(){

        if(fileManager == null)
            fileManager = new fileManager();

        return fileManager;
    }

}
