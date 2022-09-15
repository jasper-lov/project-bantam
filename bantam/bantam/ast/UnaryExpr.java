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
 * The abstract <tt>UnaryExpr</tt> class represents unary expressions
 * whose operand is an expression (i.e., this does not include new constructions).
 * It can be either a negation expression ('-'), a complement expression ('!'),
 * an increment expression ('++'), or a decrement expression ('--').
 * It contains an expression.  Subclasses of this class can be handled similarly
 * within the compiler, so most functionality can be implemented in the
 * bantam.visitor method for this class.
 *
 * @see ASTNode
 * @see Expr
 */
public abstract class UnaryExpr extends Expr {
    /**
     * The expression
     */
    protected Expr expr;

    /**
     * UnaryExpr constructor
     *
     * @param lineNum source line number corresponding to this AST node
     * @param expr    expression
     */
    public UnaryExpr(int lineNum, Expr expr) {
        super(lineNum);
        this.expr = expr;
    }

    /**
     * Get the expression
     *
     * @return expression
     */
    public Expr getExpr() {
        return expr;
    }

    /**
     * Get the operation name (e.g., "!")
     * (must be defined by each subclass)
     *
     * @return op name
     */
    abstract public String getOpName();

    /**
     * Get the operation type (e.g., "boolean")
     * (must be defined by each subclass)
     *
     * @return op type
     */
    abstract public String getOpType();

    /**
     * Get the operand type (e.g., "boolean")
     * (must be defined by each subclass)
     *
     * @return operand type
     */
    abstract public String getOperandType();

    /**
     * Is this a postfix operator (as opposed to prefix)?
     *
     * @return boolean indicating whether postfix operator
     */
    abstract public boolean isPostfix();

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see bantam.visitor.Visitor
     */
    abstract public Object accept(Visitor v);
}
