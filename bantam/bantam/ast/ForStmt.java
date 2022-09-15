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
 * The <tt>ForStmt</tt> class represents a for (loop) statement
 * appearing in a method declaration.  It contains an initialization
 * expression (<tt>initExpr</tt>), a predicate expression
 * (<tt>predExpr</tt>), an update expression (<tt>updateExpr</tt>),
 * and body statement (<tt>bodyStmt</tt>).
 *
 * @see ASTNode
 * @see Stmt
 */
public class ForStmt extends Stmt {
    /**
     * The initialization expression (null for no init expression)
     */
    protected Expr initExpr;

    /**
     * The predicate expression (null for no predicate -- must be boolean expression)
     */
    protected Expr predExpr;

    /**
     * The update expression (null for no update expression)
     */
    protected Expr updateExpr;

    /**
     * The body statement
     */
    protected Stmt bodyStmt;

    /**
     * ForStmt constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     * @param initExpr   the initialization expression (null for no init expression)
     * @param predExpr   the predicate expression (null for no predicate -- must be boolean expression)
     * @param updateExpr the update expression (null for no update expression)
     * @param bodyStmt   the then statement
     */
    public ForStmt(int lineNum, Expr initExpr, Expr predExpr,
                   Expr updateExpr, Stmt bodyStmt) {
        super(lineNum);
        this.initExpr = initExpr;
        this.predExpr = predExpr;
        this.updateExpr = updateExpr;
        this.bodyStmt = bodyStmt;
    }

    /**
     * Get the initialization expression
     * If null, then no initialization expression
     *
     * @return initialization expression
     */
    public Expr getInitExpr() {
        return initExpr;
    }

    /**
     * Get the predicate expression
     * If null, then no predicate expression
     *
     * @return predicate expression
     */
    public Expr getPredExpr() {
        return predExpr;
    }

    /**
     * Get the update expression
     * If null, then no update expression
     *
     * @return update expression
     */
    public Expr getUpdateExpr() {
        return updateExpr;
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
