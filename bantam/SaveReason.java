/*
    File: SaveReason.java
    Names: Jasper Loverude, Dylan Tymkiw, Cassidy Correll
    Class: CS 361
    Project 6
    Date: March 18
*/


package proj10LoverudeTymkiwCorrell;

/**
 * SaveReasons classes for representing the 3 situations when a file must be saved,
 * used by dialoghelper class as a parameter to generate the proper popup dialog.
 */
public enum SaveReason {
    /*
    * CLOSING - closing a tab
    */
    CLOSING,

    /*
    * EXITING - exiting the application
     */
    EXITING,

    /*
    * COMPILING - transpiling a file or transpiling and running a file
     */
    COMPILING
}
