package com.me.deusexguitester.fileManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.deusexguitester.model.TestInfo;
import com.me.deusexguitester.model.TestInfoProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


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

    public void deleteTestDirectory(String testName){
        try {
            FileUtils.deleteDirectory(new File(workspace.getPath() + "\\" + testName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileManager getFileManager(){

        if(fileManager == null)
            fileManager = new FileManager();

        return fileManager;
    }

}
