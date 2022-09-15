/*
    File: Transpiler
    Names: Jasper Loverude, Dylan Tymkiw, Cassidy Correll
    Class: CS 361
    Project 10
    Date: May 6
*/

package proj10LoverudeTymkiwCorrell.bantam.transpiler;

import proj10LoverudeTymkiwCorrell.bantam.ast.Program;
import proj10LoverudeTymkiwCorrell.bantam.parser.Parser;
import proj10LoverudeTymkiwCorrell.bantam.util.ErrorHandler;
import proj10LoverudeTymkiwCorrell.bantam.util.FileExtensionChanger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Transpiler {

    private final TranspilerVisitor transpilerVisitor;

    public Transpiler(){
        transpilerVisitor = new TranspilerVisitor();
    }


    /**
     * Writes the program to a given file
     *
     * @param fileName the name of the file to be written
     * @throws IOException
     * */
    private void writeToFile(String fileName) throws IOException {

        String programStringBuilder = transpilerVisitor.getProgramStringBuilder().toString();
        File currFile = new File(fileName);
        if(currFile.exists()){
            currFile.delete();
        }
        PrintWriter writer = new PrintWriter(fileName);
        writer.print(programStringBuilder);
        writer.close();

    }

    /**
     * Visits the program and transpiles it
     *
     * @param filePath the name of the file to compile
     * @return fileToCompile the name of file that needs to be compiled
     * */
    public String transpile(String filePath) {

        // Creates annd
        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        Program program = parser.parse(filePath);
        String fileToCompile = null;
        transpilerVisitor.visit(program);

        try {
            fileToCompile = FileExtensionChanger.fileWithChangedExtension(filePath);
            this.writeToFile(fileToCompile);
        }catch (IOException ex){
            System.out.println("Failed to rename and create java file");//todo: change this for something that can be seen by the user
        }
        // Clears stringBuilder for reuse
        transpilerVisitor.clearProgramStringBuilder();
        return fileToCompile;
    }

    /**
     * Main method for testing, takes command line argument of btm file filepath
     * and transpiles to a .java file
     *
     * @param args Array, first index is the filepath that is transpile
     * */

    public static void main(String[] args){

        Transpiler transpilerWriter = new Transpiler();
        transpilerWriter.transpile(args[0]);
    }

}
