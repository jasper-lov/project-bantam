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
 * The abstract <tt>ConstExpr</tt> class represents constant expressions
 * (int constants, boolean constants, String constants).  It contains a
 * a constant value.  Subclasses of this class can be handled similarly
 * within the compiler, so most functionality can be implemented in the
 * bantam.visitor method for this class.
 *
 * @see ASTNode
 * @see Expr
 */
public abstract class ConstExpr extends Expr {
    /**
     * Constant value
     */
    protected String constant;

    /**
     * ConstExpr constructor
     *
     * @param lineNum  source line number corresponding to this AST node
     * @param constant constant value
     */
    public ConstExpr(int lineNum, String constant) {
        super(lineNum);
        this.constant = constant;
    }

    /**
     * Get the constant value
     *
     * @return constant value
     */
    public String getConstant() {
        return constant;
    }

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see bantam.visitor.Visitor
     */
    abstract public Object accept(Visitor v);
}
