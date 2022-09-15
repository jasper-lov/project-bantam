/*
 * @(#)SourceFile.java                        2.0 1999/08/11
 *
 * Copyright (C) 1999 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 *
 * Modified by Dale Skrien, Fall 2021
 */

package proj10LoverudeTymkiwCorrell.bantam.lexer;

import proj10LoverudeTymkiwCorrell.bantam.util.CompilationException;

import java.io.*;

/**
 * A class for extracting the characters, one at a time, from a text file or a Reader.
 */
class SourceFile
{
    public static final char EOL = '\n';         // end of line character
    public static final char CR = '\r';  // carriage return character
    public static final char EOF = '\u0000';     // end of file character

    private Reader sourceReader;   // the reader of the data
    private int currentLineNumber; // for bantam error messages
    private int prevChar;          // the previous character read
    private String filename;       // the file currently being scanned.

    /**
     * creates a new SourceFile object for the file with the given name
     *
     * @param filename the name of the file to be read.
     * @throws CompilationException if the file is not found
     */
    SourceFile(String filename) {
        try {
            sourceReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            throw new CompilationException("File " + filename + "not found.", e);
        }
        currentLineNumber = 1;
        prevChar = -1;
        this.filename = filename;
    }

    /**
     * creates a new SourceFile object for the given Reader
     *
     * @param in the Reader that provides the characters to be processes
     */
    SourceFile(Reader in) {
        sourceReader = in;
        currentLineNumber = 1;
        prevChar = -1;
    }

    int getCurrentLineNumber() {
        return currentLineNumber;
    }

    String getFilename() { return filename;}

    /**
     * Finds and returns the next character in the source file.
     * The current line number is incremented if the end of a line is reached
     *
     * @return the next character in the source file
     */
    char getNextChar() throws IOException {
        int c = sourceReader.read();

        if (c == -1) {
            c = EOF;
        }
        else if (c == CR || (c == EOL && prevChar != CR)) {
            currentLineNumber++;
        }
        prevChar = c;
        return (char) c;
    }
}

