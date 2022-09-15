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
 * The abstract <tt>Stmt</tt> class represents a statement that is
 * is contained in a method body.  A statement can be either a
 * declaration statement, an expression statement, an if statement, a
 * while statement, a for statement, a break statement, a return statement
 * or a block statement.
 *
 * @see ASTNode
 * @see DeclStmt
 * @see ExprStmt
 * @see IfStmt
 * @see WhileStmt
 * @see ForStmt
 * @see BreakStmt
 * @see ReturnStmt
 * @see BlockStmt
 */
public abstract class Stmt extends ASTNode {
    /**
     * Stmt constructor
     *
     * @param lineNum source line number corresponding to this AST node
     */
    protected Stmt(int lineNum) {
        super(lineNum);
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
