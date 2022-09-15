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
 * The <tt>AssignExpr</tt> class represents assignment expressions.
 * It contains an optional reference name (potentially needed if the
 * variable is a field), a variable name (variable being assigned to) and
 * an expression (for assigning to the variable).
 *
 * @see ASTNode
 * @see Expr
 */
public class AssignExpr extends Expr {
    /**
     * The optional reference object used to access the lefthand variable
     * (only applicable if the variable is a field)
     */
    protected String refName;

    /**
     * The name of the lefthand variable
     */
    protected String name;

    /**
     * The righthand expression for assigning to the lefthand variable
     */
    protected Expr expr;

    /**
     * AssignExpr constructor
     *
     * @param lineNum source line number corresponding to this AST node
     * @param refName the optional reference object used to access the lefthand variable
     * @param name    the name of the lefthand variable
     * @param expr    righthand expression for assigning to the lefthand variable
     */
    public AssignExpr(int lineNum, String refName, String name, Expr expr) {
        super(lineNum);
        this.refName = refName;
        this.name = name;
        this.expr = expr;
    }

    /**
     * Get the optional reference name
     *
     * @return reference name
     */
    public String getRefName() {
        return refName;
    }

    /**
     * Get the lefthand variable name
     *
     * @return lefthand variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the righthand expression of the assignment
     *
     * @return righthand expression
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
