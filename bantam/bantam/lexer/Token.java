 /*
  * @(#)Token.java                        2.0 1999/08/11
  *
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

 import java.util.Set;

 /**
  * This class represents a meaningful sequence of characters in the
  * source code.
  */
 public class Token
 {
     /** the kind of token this is */
     public Kind kind;
     /** the string of characters making up the token */
     public String spelling;
     /** the line number where the token was found in the source code */
     public int position;

     public String getSpelling() {
         return spelling;
     }


     /**
      * constructor
      * @param kind the Kind of token to be created
      * @param spelling the characters making up the token
      * @param position the line number in the source file containing the token
      */
     Token(Kind kind, String spelling, int position) {
         this.spelling = spelling;
         this.position = position;

         // patch the kind field in the cases of boolean constants and keywords,
         // which are not of kind IDENTIFIER
         if (kind == Kind.IDENTIFIER && (spelling.equals("true") || spelling.equals("false"))) {
             this.kind = Kind.BOOLEAN;
         }
         else if (kind == Kind.IDENTIFIER && reservedWords.contains(spelling)) {
             this.kind = Enum.valueOf(Kind.class, spelling.toUpperCase());
         }
         else {
             this.kind = kind;
         }
     }

     /**
      * @return  information about this token
      */
     public String toString() {
         return "Token: Kind=" + kind.name() + ", spelling=" + spelling + ", " +
                 "position=" + position;
     }

     /**
      * This class represents the different kinds of tokens that can be created
      */
     public enum Kind
     {
         // literals, identifiers...
         INTCONST, STRCONST, BOOLEAN, IDENTIFIER,

         // operators...
         BINARYLOGIC, PLUSMINUS, MULDIV, COMPARE, UNARYINCR, UNARYDECR, ASSIGN,
         UNARYNOT,

         // punctuation...
         DOT, COLON, SEMICOLON, COMMA,

         // brackets...
         LPAREN, RPAREN, LCURLY, RCURLY, //Added brackets

         // special tokens...
         COMMENT, ERROR, EOF, //end of file token

         // reserved words
         BREAK, CAST, CLASS, VAR, ELSE, EXTENDS, FOR, IF, INSTANCEOF, NEW,
         RETURN, WHILE
     }

     /** a set of all the reserved words of Bantam Java */
     private static Set<String> reservedWords = Set.of("break", "cast", "class", "var",
             "else", "extends", "for", "if", "instanceof", "new", "return", "while");

 }

	
	
	
	
	
	

