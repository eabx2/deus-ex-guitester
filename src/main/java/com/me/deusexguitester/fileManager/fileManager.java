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

    public static File workspace = new File("./workspace");

    private FileManager(){
        // create workspace
        workspace.mkdir();
    }

    public File[] getTestFiles(){
        return workspace.listFiles();
    }


    public static FileManager getFileManager(){

        if(fileManager == null)
            fileManager = new FileManager();

        return fileManager;
    }

}
