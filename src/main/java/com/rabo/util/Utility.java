package com.rabo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Utility {

    public static Properties readPropertiesFile(String filePath) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(new File(filePath));
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }
}
