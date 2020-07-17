package com.me.deusexguitester.fileManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.deusexguitester.model.Command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by ersinn on 13.07.2020.
 */
public class FileManager {

    private static FileManager fileManager;

    File workspace = new File("./workspace");

    private FileManager(){

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

            File commandsFile = new File(testDir.getPath() + "\\" + "commands.json");
            commandsFile.createNewFile();

            mapper.writerWithDefaultPrettyPrinter().writeValue(commandsFile,commands);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File[] getTests(){
        return workspace.listFiles();
    }

    public ArrayList<Command> getTestCommandsByName(String testName){

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Command> commands = null;

        // tell mapper to read values into ArrayList<Command>
        JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class,Command.class);

        try {
            commands = mapper.readValue(new File(workspace.getPath() + "\\" + testName + "\\" + "commands.json"),type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commands;
    }

    public static FileManager getFileManager(){

        if(fileManager == null)
            fileManager = new FileManager();

        return fileManager;
    }

}
