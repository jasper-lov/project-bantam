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
*/

package proj10LoverudeTymkiwCorrell.bantam.ast;

import proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor;


/**
 * The <tt>Program</tt> class represents an entire program, which
 * consists of a list of classes (<tt>classList</tt>).
 *
 * @see ASTNode
 * @see ClassList
 */
public class Program extends ASTNode {
    /**
     * List of class declarations that comprise the program
     */
    protected ClassList classList;

    /**
     * Program constructor
     *
     * @param lineNum   source line number corresponding to this AST node
     * @param classList list of class declarations
     */
    public Program(int lineNum, ClassList classList) {
        super(lineNum);
        this.classList = classList;
    }

    /**
     * Get list of classes that comprise the program
     *
     * @return list of class declarations
     */
    public ClassList getClassList() {
        return classList;
    }

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see bantam.visitor.Visitor
     */
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
