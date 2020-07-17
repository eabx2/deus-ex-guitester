package com.me.deusexguitester.fileManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.model.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;


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

    public void saveTest(Test test){

        // create testDir
        File testDir = new File(workspace.getPath() + "\\" + test.name);
        testDir.mkdir();

        // create testDir\screenshots\
        File screenshotsDir = new File(testDir.getPath() + "\\" + "screenshots");
        screenshotsDir.mkdir();

        try {
            ObjectMapper mapper = new ObjectMapper();

            // write commands
            File commandsFile = new File(testDir.getPath() + "\\" + "commands.json");
            commandsFile.createNewFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(commandsFile,test.commands);

            // write screenshots
            for (int i = 0; i < test.screenshots.size(); i++) {
                ImageIO.write(test.screenshots.get(i),"bmp",new File(screenshotsDir + "\\" + "ss" + i + ".bmp"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File[] getTestFiles(){
        return workspace.listFiles();
    }

    public ArrayList<Command> getCommandsByTestName(String testName){

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

    public BufferedImage getScreenshotByTestName(String testName, String screenshotName){
        File imageFile = new File(workspace.getPath() + "\\" + testName + "\\" + "screenshots" + "\\" + screenshotName + ".bmp");
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FileManager getFileManager(){

        if(fileManager == null)
            fileManager = new FileManager();

        return fileManager;
    }

}
