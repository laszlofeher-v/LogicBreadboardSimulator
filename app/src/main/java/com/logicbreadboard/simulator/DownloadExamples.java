package com.logicbreadboard.simulator;


import java.io.*;

/**
 * Created on 12/10/15.
 */
public class DownloadExamples {

    static public String findFile(String name, File file) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                if (name.equalsIgnoreCase(fil.getName())) {
                    return fil.getAbsolutePath();
                }
                if (fil.isDirectory()) {
                    String found = findFile(name, fil);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }
}
