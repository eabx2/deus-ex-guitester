package com.me.deusexguitester.fileManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.deusexguitester.model.Command;
import com.me.deusexguitester.model.Test;
import com.me.deusexguitester.model.TestInfo;
import com.me.deusexguitester.model.TestInfoProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public static File workspace = new File("./workspace");

    private FileManager(){
        // create workspace
        workspace.mkdir();
    }

    public ObservableList<TestInfoProperty> getTestInfoPropertyList(){

        ObservableList<TestInfoProperty> testInfoPropertyList = FXCollections.observableArrayList();

        File[] files = workspace.listFiles();

        ObjectMapper mapper = new ObjectMapper();
        TestInfo testInfo;

        for (int i = 0; i < files.length; i++) {
            try {
                testInfo = mapper.readValue(new File(workspace.getPath() + "\\" + files[i].getName() + "\\" + "testInfo.json"),TestInfo.class);
                testInfoPropertyList.add(new TestInfoProperty(testInfo));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return testInfoPropertyList;

    }

    public static FileManager getFileManager(){

        if(fileManager == null)
            fileManager = new FileManager();

        return fileManager;
    }

}
