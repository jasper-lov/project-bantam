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
 * The <tt>WhileStmt</tt> class represents a while (loop) statement
 * appearing in a method declaration.  It contains a predicate expression
 * (<tt>predExpr</tt>) and body statement (<tt>bodyStmt</tt>).
 *
 * @see ASTNode
 * @see Stmt
 */
public class WhileStmt extends Stmt {
    /**
     * The predicate expression
     */
    protected Expr predExpr;

    /**
     * The body statement
     */
    protected Stmt bodyStmt;

    /**
     * WhileStmt constructor
     *
     * @param lineNum  source line number corresponding to this AST node
     * @param predExpr the predicate expression
     * @param bodyStmt the then statement
     */
    public WhileStmt(int lineNum, Expr predExpr, Stmt bodyStmt) {
        super(lineNum);
        this.predExpr = predExpr;
        this.bodyStmt = bodyStmt;
    }

    /**
     * Get the predicate expression
     *
     * @return predicate expression
     */
    public Expr getPredExpr() {
        return predExpr;
    }

    /**
     * Get the body statement
     *
     * @return body statement
     */
    public Stmt getBodyStmt() {
        return bodyStmt;
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
