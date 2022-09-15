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
 * The abstract <tt>Expr</tt> class represents an expression that is
 * is contained in either a statement, a field declaration, or another
 * expression.  An expression can be either a dispatch expression, a new
 * expression, an assignment expression, a binary expression (comparison,
 * arithmetic, or boolean logic), a unary expression (negation or
 * complement), a variable expression, or a constant expression (int
 * constant, boolean constant, String constant).  An expression contains
 * an expression type (<tt>exprType</tt>), which is set during semantic
 * analysis.
 *
 * @see ASTNode
 * @see DispatchExpr
 * @see NewExpr
 * @see InstanceofExpr
 * @see CastExpr
 * @see AssignExpr
 * @see BinaryExpr
 * @see UnaryExpr
 * @see VarExpr
 * @see ConstExpr
 */
public abstract class Expr extends ASTNode {
    /**
     * The expression type
     */
    private String exprType = null;

    /**
     * Expr constructor
     *
     * @param lineNum source line number corresponding to this AST node
     */
    public Expr(int lineNum) {
        super(lineNum);
    }

    /**
     * Get the type of the expression
     *
     * @return the expression type
     */
    public String getExprType() {
        return exprType;
    }

    /**
     * Set the type of the expression
     *
     * @param exprType the type of the expression
     */
    public void setExprType(String exprType) {
        this.exprType = exprType;
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
