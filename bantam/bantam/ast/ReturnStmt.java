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
 * The <tt>ReturnStmt</tt> class represents a return statement within
 * the body of a method.  It contains an expression to be returned
 * (<tt>expr</tt>).
 *
 * @see ASTNode
 * @see Stmt
 */
public class ReturnStmt extends Stmt {
    /**
     * An expression to be returned (null for no return expression)
     */
    protected Expr expr;

    /**
     * ReturnStmt constructor
     *
     * @param lineNum source line number corresponding to this AST node
     * @param expr    expression to be returned (null for no return expression)
     */
    public ReturnStmt(int lineNum, Expr expr) {
        super(lineNum);
        this.expr = expr;
    }

    /**
     * Get the return expression
     *
     * @return expression
     */
    public Expr getExpr() {
        return expr;
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
