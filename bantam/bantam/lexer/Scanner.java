package proj10LoverudeTymkiwCorrell.bantam.lexer;

import proj10LoverudeTymkiwCorrell.bantam.util.Error;
import proj10LoverudeTymkiwCorrell.bantam.util.ErrorHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * This class reads characters from a file or a Reader
 * and breaks it into Tokens.
 */
public class Scanner
{
    /** the source of the characters to be broken into tokens */
    private SourceFile sourceFile;
    /** collector of all errors that occur */
    private ErrorHandler errorHandler;

    private char currentChar;

    private boolean isChecked;

//    private final List<Integer> specialSymbolsASCII = Arrays.asList(
//            33,38,40,41,42,43,44,45,46,47,58,59,60,61,62,92,123,124,125);

    private final Set<Character> specialSymbolsSet = Set.of(
            '!', '&', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';',
            '<', '=', '>', '\\', '{', '|', '}', '%');

    private final Set<String> legalEscapeCharSet = Set.of(
            "f", "n", "t", "\\", "\"");

    private final Set<Character> whiteSpaceSet = Set.of(
            ' ', '\t', SourceFile.CR, SourceFile.EOL);

    private final Map<String, Token.Kind> stringTokenKindMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("NEW", Token.Kind.NEW),
            new AbstractMap.SimpleEntry<>("cast", Token.Kind.CAST),
            new AbstractMap.SimpleEntry<>("instaceof", Token.Kind.INSTANCEOF),
            new AbstractMap.SimpleEntry<>("true", Token.Kind.BOOLEAN),
            new AbstractMap.SimpleEntry<>("false", Token.Kind.BOOLEAN),
            new AbstractMap.SimpleEntry<>("break", Token.Kind.BREAK),
            new AbstractMap.SimpleEntry<>("Class", Token.Kind.CLASS),
            new AbstractMap.SimpleEntry<>("var", Token.Kind.VAR),
            new AbstractMap.SimpleEntry<>("else", Token.Kind.ELSE),
            new AbstractMap.SimpleEntry<>("extends", Token.Kind.EXTENDS),
            new AbstractMap.SimpleEntry<>("for", Token.Kind.FOR),
            new AbstractMap.SimpleEntry<>("if", Token.Kind.IF),
            new AbstractMap.SimpleEntry<>("return", Token.Kind.RETURN),
            new AbstractMap.SimpleEntry<>("while", Token.Kind.WHILE)
    );


    /**
     * creates a new scanner for the given file
     * @param filename the name of the file to be scanned
     * @param handler the ErrorHandler that collects all the errors found
     */
    public Scanner(String filename, ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        sourceFile = new SourceFile(filename);
        isChecked = true;

    }

    /**
     * creates a new scanner for the given file
     * @param handler the ErrorHandler that collects all the errors found
     */
    public Scanner(Reader reader, ErrorHandler handler) {
        errorHandler = handler;
        sourceFile = new SourceFile(reader);
        isChecked = true;

    }

    public String getSourceFile(){
        return sourceFile.getFilename();
    }


    /**
     * read characters and collect them into a Token.
     * It ignores white space unless it is inside a string or a comment.
     * It returns an EOF Token if all characters from the sourceFile have
     * already been read.
     * @return the Token containing the characters read
     */
    public Token scan() {


        if(isChecked){
            currentChar = scannerGetNextChar();
        }
        else{
            isChecked = true;
        }
        // Ignores whitespace, CR, and EOl
        while (whiteSpaceSet.contains(currentChar))
        {
            currentChar = scannerGetNextChar();
        }
        // If next token is KeyWord
        if (Character.isLetter(currentChar)){

            return scanCharacters(currentChar);

        }
        // If next token is an integer
        else if (Character.isDigit(currentChar)) {

            return scanIntegers(currentChar);

        }
        // If next token is a special symbol
        else if (specialSymbolsSet.contains(currentChar)) {

            return scanSpecialSymbols(currentChar);

        }
        // If next token is a string
        else if (currentChar == '\"') {

            return scanStrings(currentChar);

        }
        // If next token is EOF token
        else if (currentChar == SourceFile.EOF ){

            return new Token(Token.Kind.EOF, ""+ currentChar,
                    sourceFile.getCurrentLineNumber());

        }

        return new Token(Token.Kind.ERROR, "" + currentChar,
                sourceFile.getCurrentLineNumber());

    }

    /**
     * Scans special symbols and returns the appropriate token
     * @param (character) character given from scanner
     * @author Dylan Tymkiw
     * */
    private Token scanSpecialSymbols(char character){
        //Check what kind of especial symbol
        //If +|-|!|=|>|<|/| check if the next line, if not just return the token
        currentChar = character;
        String charArray = "" + character;

        //Left curly bracket
        if (character == '{'){
            return new Token(Token.Kind.LCURLY, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        //Right curly bracket
        else if (character == '}'){
            return new Token(Token.Kind.RCURLY, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        //Left parenthesis
        else if (character == '('){
            return new Token(Token.Kind.LPAREN, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        //Right parenthesis
        else if (character == ')'){
            return new Token(Token.Kind.RPAREN, charArray,
                    sourceFile.getCurrentLineNumber());
        }

        //Semicolon
        else if(character == ';'){
            return new Token(Token.Kind.SEMICOLON, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        //Comma
        else if(character == ','){
            return new Token(Token.Kind.COMMA, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        // Dot
        else if(character == '.'){
            return new Token(Token.Kind.DOT, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        //Colon
        else if(character == ':'){
            return new Token(Token.Kind.COLON, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        // Unary Not
        else if (character == '!'){
            currentChar = scannerGetNextChar();
            //When followed by white space
            if (currentChar != '=') {
                isChecked = false;
                return new Token(Token.Kind.UNARYNOT, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //When followed by equal sign
            else{
                charArray += currentChar;
                return new Token(Token.Kind.COMPARE, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        // Multiplication sign
        else if (character == '*'){
            return new Token(Token.Kind.MULDIV, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        // Modulus Division sign
        else if (character == '%'){
            return new Token(Token.Kind.MULDIV, charArray,
                    sourceFile.getCurrentLineNumber());
        }
        // Plus sign
        else if (character == '+'){
            currentChar = scannerGetNextChar();
            //If followed by white Space
            if (currentChar != '+'){
                isChecked = false;
                return new Token(Token.Kind.PLUSMINUS, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by another plus sign
            else{
                charArray += currentChar;
                return new Token(Token.Kind.UNARYINCR, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        //Minus sign
        else if(character == '-'){
            currentChar = scannerGetNextChar();
            //If followed by whitespace
            if(currentChar != '-'){
                isChecked = false;
                return new Token(Token.Kind.PLUSMINUS, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by another minus sign
            else{
                charArray += currentChar;
                return new Token(Token.Kind.UNARYDECR, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        // Less than sign or greater than sign
        else if(character == '<' || character == '>'){
            currentChar = scannerGetNextChar();
            //If followed by whitespace
            if(currentChar != '='){
                isChecked = false;
                return new Token(Token.Kind.COMPARE, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by equal sign
            else{
                charArray += currentChar;
                return new Token(Token.Kind.COMPARE, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        //Equal sign
        else if(character == '='){
            currentChar = scannerGetNextChar();
            //If followed by whitespace
            if(currentChar != '='){
                isChecked = false;
                return new Token(Token.Kind.ASSIGN, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by another equal sign
            else{
                charArray += currentChar;
                return new Token(Token.Kind.COMPARE, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        //AND operator
        else if(character == '&'){
            currentChar = scannerGetNextChar();
            //If followed by white space, throw an error
            if(currentChar != '&'){
                errorHandler.register(Error.Kind.SEMANT_ERROR, sourceFile.getFilename(),
                        sourceFile.getCurrentLineNumber(), "Invalid Symbol");
                isChecked = false;
                return new Token(Token.Kind.ERROR, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by another &, create the token
            else{
                charArray += currentChar;
                return new Token(Token.Kind.BINARYLOGIC, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        //OR operator
        else if(character == '|'){
            currentChar = scannerGetNextChar();
            //If followed by white space, throw an error
            if(currentChar != '|'){
                errorHandler.register(Error.Kind.SEMANT_ERROR, sourceFile.getFilename(),
                        sourceFile.getCurrentLineNumber(), "Invalid Symbol");
                isChecked = false;
                return new Token(Token.Kind.ERROR, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by another &, create the token
            else{
                charArray += currentChar;
                return new Token(Token.Kind.BINARYLOGIC, charArray,
                        sourceFile.getCurrentLineNumber());
            }
        }
        // Divide token or Comment
        else if(character == '/'){
            currentChar = scannerGetNextChar();
            //If followed by whitespace it is a divide operator
            if(currentChar != '/' && currentChar != '*'){
                return new Token(Token.Kind.MULDIV, charArray,
                        sourceFile.getCurrentLineNumber());
            }
            //If followed by / or * call comment method
            else{
               return scanComments();
            }
        }

        errorHandler.register(Error.Kind.SEMANT_ERROR, sourceFile.getFilename(),
                sourceFile.getCurrentLineNumber(), "Invalid Symbol");
        return new Token(Token.Kind.ERROR, charArray, sourceFile.getCurrentLineNumber());
    }

    /**
     * Scans characters and returns the appropriate token
     *
     * @param character char at beginning of token
     * @return Token object containing token information
     * */
    private Token scanCharacters(char character){
        //Keep going until white space
        //Check if they are keywoards or identifiers or booleans
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(character);
        currentChar = scannerGetNextChar();

        // Word can contain alphabetical charaters, digits, and underscores
        while(Character.isLetterOrDigit(currentChar) || currentChar == '_'){
            stringBuilder.append(currentChar);
            currentChar = scannerGetNextChar();
        }

        if(!whiteSpaceSet.contains(currentChar)){
            isChecked = false;
        }
        if(stringTokenKindMap.containsKey(stringBuilder.toString())){

            Token.Kind tokenType = stringTokenKindMap.get(stringBuilder.toString());

            return new Token(tokenType, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());

        }
        else{
            return new Token(Token.Kind.IDENTIFIER, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());
        }

    }

    /**
     * Scans integers and returns the appropriate token
     *
     * @param character char at beginning of token
     * @return Token object containing token information
     * */
    private Token scanIntegers(char character){
        //Keep going until white space

        StringBuilder stringBuilder = new StringBuilder();

        currentChar = character;

        // Word can contain alphabetical characters, digits, and underscores
        while(Character.isDigit(currentChar)){

            stringBuilder.append(currentChar);

            currentChar = scannerGetNextChar();

        }

        if(!whiteSpaceSet.contains(currentChar)){
            isChecked = false;
        }

        // Converts to Long to check if integer out of bounds
        if(Long.parseLong(stringBuilder.toString()) < 0 ||
                Long.parseLong(stringBuilder.toString()) >= Integer.MAX_VALUE){

            registerError("Var is out of bounds.");

            return new Token(Token.Kind.ERROR, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());

        }

        int currentPosition;
        if(currentChar == SourceFile.EOL){
            currentPosition = sourceFile.getCurrentLineNumber() -  1;
        }
        else{
            currentPosition = sourceFile.getCurrentLineNumber();
        }

        return new Token(Token.Kind.INTCONST, stringBuilder.toString(),
                currentPosition);
    }

    /**
     * Helper method to register errors
     *
     * @param errorMessage char at beginning of token
     * */
    private void registerError(String errorMessage) {

        errorHandler.register(Error.Kind.LEX_ERROR,
                sourceFile.getFilename(),
                sourceFile.getCurrentLineNumber(),
                errorMessage);

    }

    /**
     * Scans strings and returns a token holding the spelling
     *
     * @param firstChar char at beginning of token
     * @return Token object containing token information
     * */
    private Token scanStrings(char firstChar) {
        //Keeps going until closing quotes

        boolean containsIllegalEscape = false;

        StringBuilder stringBuilder = new StringBuilder();

        int startingLineNumber = sourceFile.getCurrentLineNumber();

        stringBuilder.append(firstChar);
        currentChar = scannerGetNextChar();
        char lastChar;

        // While !String.valueOf(currentChar).equals("\""
        while(currentChar != '\"'){

            stringBuilder.append(currentChar);
            lastChar = currentChar;
            currentChar = scannerGetNextChar();


            // If last character was backslash, checks for legal escape character
            if(lastChar == '\\'){

                if(!legalEscapeCharSet.contains(String.valueOf(currentChar))){
                    containsIllegalEscape = true;
                }
                else{
                    stringBuilder.append(currentChar);
                    currentChar = scannerGetNextChar();
                }
            }
        }

        stringBuilder.append(currentChar);

        if(stringBuilder.length() > 5000){

            registerError("String constant exceeds 5000 characters.");

            return new Token(Token.Kind.ERROR, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());
        }
        else if(sourceFile.getCurrentLineNumber() != startingLineNumber){

            registerError("String constant spans multiple lines.");

            return new Token(Token.Kind.ERROR, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());

        }
        else if(containsIllegalEscape){

            registerError("String constant contains unsupported escape character.");

            return new Token(Token.Kind.ERROR, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());

        }
        else {

            return new Token(Token.Kind.STRCONST, stringBuilder.toString(),
                    sourceFile.getCurrentLineNumber());

        }

    }

    /**
     * Scans comments
     *
     * @param commentStart character at beginning of token
     * @return Token object containing token information
     * */
    /**
     * Handles comments
     * */
    private Token scanComments(){
        // If starts with // read the line
        // If Starts with /* read until comment block is closed
        if (currentChar == '/'){
            while(currentChar != SourceFile.EOL){
                currentChar = scannerGetNextChar();
            }
        }
        else if(currentChar == '*'){
            while (true){
                if((currentChar = scannerGetNextChar()) == '*' && (currentChar = scannerGetNextChar()) == '/'){
                    break;
                }
            }
        }
        return scan();
    }
    /**
     * Gets next available char, helper method
     *
     *
     * @return char from SourceFile
     * */
    private char scannerGetNextChar(){
        try{
            currentChar = sourceFile.getNextChar();;
            return currentChar;
        }
        catch(IOException ioException){
            System.out.println("Caught IOException from SourceFile in Scanner");
        }

        return ' ';
    }

    public static void main(String[] args){

        // Creates scanner object and list of tokens
        Scanner fileScanner = new Scanner(args[0], new ErrorHandler());

        List<Token> fileTokenList = new ArrayList<>();

        /*
         To modify commandline: Go to run -> edit conigurations ->
         arguments (has filepaths)
        */
        for(int i = 0; i < args.length; i++){

            // Sets file, clears token list and scans in first token
            fileScanner.sourceFile = new SourceFile(args[i]);

            System.out.println(args[i]);

            fileTokenList.clear();

            Token currentToken = fileScanner.scan();

            // Scans tokens and store them until the EOF token is reached
            while(!currentToken.kind.equals(Token.Kind.EOF))
            {
                fileTokenList.add(currentToken);
                currentToken = fileScanner.scan();
            }
            // Prints Tokens
            for(Token token : fileTokenList){
                System.out.println(token);
            }

            if(fileScanner.errorHandler.getErrorList().size() == 0){
                System.out.println("Scanning was successful!");
            }
            else{
                System.out.println("Found" + " "+ fileScanner.errorHandler.getErrorList().size() + " "
                        + "errors");
            }

        }

    }

}


