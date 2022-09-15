/*
 * File: CompilationException.java
 * Author: djskrien
 * Date: 10/3/2021
 */
package proj10LoverudeTymkiwCorrell.bantam.util;

/**
 * This class represents an error in a Bantam Java program.
 * The compiler throws this exception when it finds errors.
 */
public class CompilationException extends RuntimeException
{
    private ErrorHandler errorHandler = null;

    /**
     * creates an exception whose details are in the ErrorHandler
     * @param errorHandler the ErrorHandler with the details of the error.
     */
    public CompilationException(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * creates an exception with a message and a cause
     * @param message The message telling the reason the exception was thrown
     * @param cause The Throwable that caused the exception to occur.
     */
    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorHandler getErrorHandler() { return errorHandler; }
}
