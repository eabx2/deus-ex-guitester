package com.me.deusexguitester.model;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.me.deusexguitester.fileManager.FileManager.workspace;

/**
 * Created by ersinn on 17.07.2020.
 */
public class Test {

    public TestInfo testInfo;

    public ArrayList<Command> commands;

    private Test(TestInfo testInfo){
        this.testInfo = testInfo;
        commands = new ArrayList<>();
    }

    public BufferedImage getScreenshotByName(String screenshotName) {

        File imageFile = new File(workspace.getPath() + "\\" + testInfo.name + "\\" + "screenshots" + "\\" + screenshotName + ".bmp");
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void addScreenshot(BufferedImage screenshot, String screenshotName) {

        String screenshotsDir = workspace.getPath() + "\\" + testInfo.name + "\\" + "screenshots";

        // write screenshot
        try {
            ImageIO.write(screenshot,"bmp",new File(screenshotsDir + "\\" + screenshotName + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void deleteDir(){
        try {
            FileUtils.deleteDirectory(new File(workspace.getPath() + "\\" + testInfo.name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTest(){

        String testDir = workspace.getPath() + "\\" + testInfo.name;

        ObjectMapper mapper = new ObjectMapper();

        try {
            // write commands
            File commandsFile = new File(testDir + "\\" + "commands.json");
            commandsFile.createNewFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(commandsFile,commands);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Test createTest(TestInfo testInfo){

        // create testDir
        File testDir = new File(workspace.getPath() + "\\" + testInfo.name);
        testDir.mkdir();

        try {
            ObjectMapper mapper = new ObjectMapper();

            // create testDir\screenshots\
            File screenshotsDir = new File(testDir.getPath() + "\\" + "screenshots");
            screenshotsDir.mkdir();

            // write testInfo
            File testInfoFile = new File(testDir.getPath() + "\\" + "testInfo.json");
            testInfoFile.createNewFile();
            mapper.writerWithDefaultPrettyPrinter().writeValue(testInfoFile,testInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Test(testInfo);

    }

    public static Test loadTest(String testName){

        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Command> commands = null;
        TestInfo testInfo = null;

        // tell mapper to read values into ArrayList<Command>
        JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class,Command.class);

        try {
            commands = mapper.readValue(new File(workspace.getPath() + "\\" + testName + "\\" + "commands.json"),type);
            testInfo = mapper.readValue(new File(workspace.getPath() + "\\" + testName + "\\" + "testInfo.json"),TestInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Test test = new Test(testInfo);
        test.commands = commands;

        return test;
    }

}
