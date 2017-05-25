package com.github.chen0040.som.utils;

import java.io.InputStream;


/**
 * Created by memeanalytics on 12/8/15.
 */
public class FileUtils {
    public static InputStream getResource(String fileName) {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);

    }
}
