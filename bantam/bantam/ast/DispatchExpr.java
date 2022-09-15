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
 * The <tt>DispatchExpr</tt> class represents a dispatch expression.
 * It contains a reference expression (<tt>refExpr</tt>), a method
 * name (<tt>methodName</tt>, and a list of actual parameter expressions
 * (<tt>actualList</tt>).
 *
 * @see ASTNode
 * @see Expr
 */
public class DispatchExpr extends Expr {
    /**
     * The reference expression (produces the object to dispatch on)
     */
    protected Expr refExpr;

    /**
     * The name of the method
     */
    protected String methodName;

    /**
     * The list of actual parameter expressions
     */
    protected ExprList actualList;

    /**
     * DispatchExpr constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     * @param refExpr    reference expression (produces the object to dispatch on)
     * @param methodName the name of the method
     * @param actualList list of actual parameter expressions
     */
    public DispatchExpr(int lineNum, Expr refExpr,
                        String methodName, ExprList actualList) {
        super(lineNum);
        this.refExpr = refExpr;
        this.methodName = methodName;
        this.actualList = actualList;
    }

    /**
     * Get the reference expression
     *
     * @return reference expression
     */
    public Expr getRefExpr() {
        return refExpr;
    }

    /**
     * Get the method name
     *
     * @return method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Get the list of actual parameter expressions
     *
     * @return actual parameter list
     */
    public ExprList getActualList() {
        return actualList;
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
