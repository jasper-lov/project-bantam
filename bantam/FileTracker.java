/*
 * File: Controller.java
 * Names: Cassidy Correll, Dylan Tymkiw, Jasper Loverude
 * Class: CS 361
 * Project 6
 * Date: March 18
 */


package proj10LoverudeTymkiwCorrell;

import java.io.File;

/**
 * Class to keep track of the current directory
 * @author Dylan Tymkiw
 * */
public class FileTracker {

    private File currentDirectory;

    public FileTracker(){}

    /**
     * Method to set the current directory
     * */
    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    /**
     * Method to get the current directory
     *
     * @return currentDirectory the current directory as a file
     * */
    public File getCurrentDirectory() {
        return currentDirectory;
    }
}