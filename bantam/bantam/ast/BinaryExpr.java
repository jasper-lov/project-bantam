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
 * The abstract <tt>BinaryExpr</tt> class represents binary expressions
 * whose operands are both expressions (i.e., this does not include assignments,
 * instanceof, etc.).  It can be either a comparison expression ('==', '!=',
 * '<', '>', '<=', '>='), an arithmetic expression ('+', '-', '*', '/', '%'),
 * or a boolean expression ('&&', '||').  It contains a lefthand expression
 * and a righthand expression.  Subclasses of this class can be handled
 * similarly within the compiler, so most functionality can be implemented
 * in the bantam.visitor method for this class.
 *
 * @see ASTNode
 * @see Expr
 */
public abstract class BinaryExpr extends Expr {
    /**
     * The lefthand expression
     */
    protected Expr leftExpr;

    /**
     * The righthand expression
     */
    protected Expr rightExpr;

    /**
     * BinaryExpr constructor
     *
     * @param lineNum   source line number corresponding to this AST node
     * @param leftExpr  left operand expression
     * @param rightExpr right operand expression
     */
    public BinaryExpr(int lineNum, Expr leftExpr, Expr rightExpr) {
        super(lineNum);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    /**
     * Get the lefthand expression
     *
     * @return lefthand expression
     */
    public Expr getLeftExpr() {
        return leftExpr;
    }

    /**
     * Get the righthand expression
     *
     * @return righthand expression
     */
    public Expr getRightExpr() {
        return rightExpr;
    }

    /**
     * Get the operation name (e.g., "+")
     * (must be defined by each subclass)
     *
     * @return op name
     */
    abstract public String getOpName();

    /**
     * Get the operation type (e.g., "int")
     * (must be defined by each subclass)
     *
     * @return op type
     */
    abstract public String getOpType();

    /**
     * Get the operand type (e.g., "int")
     * (must be defined by each subclass)
     *
     * @return operand type
     */
    abstract public String getOperandType();

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see bantam.visitor.Visitor
     */
    abstract public Object accept(Visitor v);
}
