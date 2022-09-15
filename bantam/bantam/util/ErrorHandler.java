/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE.

   Modified by Dale Skrien, Fall 2021
*/

package proj10LoverudeTymkiwCorrell.bantam.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ErrorHandler class performs error handling. It keeps track
 * of a list of errors that the compiler finds.
 */
public class ErrorHandler {
    /**
     * The list of errors
     */
    private List<Error> errorList = new ArrayList<>();

    /**
     * Register an error - auxiliarly method used by the other (public) register methods
     *
     * @param error the Error object to be registered
     */
    private void register(Error error) {
        // insert a new error into the error list
        // but keep at most 100 errors.
        if (errorList.size() < 100) {
            insert(error);
        }
    }

    /**
     * Register an error
     *
     * @param kind the type (lex, parse, semantic) of error
     * @param filename the name of the filename where the error occurred
     * @param lineNum the starting line number in the source file where the error occurred
     * @param errorMessage the error message
     */
    public void register(Error.Kind kind, String filename, int lineNum, String errorMessage) {
        // create error and register it
        register((new Error(kind, filename, lineNum, errorMessage)));
    }

    /**
     * Register an error
     *
     * @param kind         the type (lex, parse, semantic) of error
     * @param errorMessage the error message
     */
    public void register(Error.Kind kind, String errorMessage) {
        // create error and register it
        // note: filename, line number, line, and charNum not specified
        // so using null, 0, null, and 0, respectively
        register((new Error(kind, null, -1, errorMessage)));
    }

    /**
     * return true if any errors were reported.
     */
    public boolean errorsFound() {
        return errorList.size() > 0;
    }

    /**
     * Insert an error onto the error list
     *
     * @param e error object to insert
     */
    private void insert(Error e) {
        // the algorithm below will insert errors in order by filename first and
        // then line number.  filenames are kept in the order that they are seen
        // (i.e., an error is registered with that filename).  Line numbers are
        // ordered numerically.

        int i = 0;

        if (e.getFilename() != null) {
            // find the section of the list with the same filename (goes to the end
            // if a section is not found)
            for (i = 0; i < errorList.size(); i++) {
                if (errorList.get(i).getFilename() != null) {
                    if (e.getFilename().equals(errorList.get(i).getFilename())) {
                        break;
                    }
                }
            }

            // find the correct spot in the section for this error's line number
            for (; i < errorList.size(); i++) {
                if (!e.getFilename().equals(errorList.get(i).getFilename()) ||
                        e.getLineNum() < (errorList.get(i)).getLineNum()) {
                    break;
                }
            }
        }

        // add the error to the list
        errorList.add(i, e);
    }

    /**
     * @return an unmodifiable copy of the list of registered errors
     */
    public List<Error> getErrorList() {
        return Collections.unmodifiableList(errorList);
    }

    public void clear() {
        errorList.clear();
    }

}
