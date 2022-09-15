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
 * The abstract <tt>ASTNode</tt> class represents a generic AST node.
 * It contains a line number (<tt>lineNumber</tt>).
 * <p/>
 * <p/>
 * <p/>
 * <br /><br />
 * <h3><center>AST Summary</center></h3>
 * <p/>
 * <tt>ASTNode</tt><br />An <tt>ASTNode</tt> is an abstract class that represents a generic node
 * in the AST.  It contains a line number indicating where the original source code (represented by
 * the node) was scanned.  All nodes in the AST extend <tt>ASTNode</tt>.
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>Program</tt><br />Each (correct) Bantam program contains one top-level <tt>Program</tt> node.
 * A <tt>Program</tt> node extends <tt>ASTNode</tt> and contains a list of classes (<tt>ClassList</tt>).
 * <p/>
 * <li> <tt>ClassList</tt><br />A <tt>ClassList</tt> node extends <tt>ListNode</tt> and contains a list
 * of <tt>Class_</tt> nodes.
 * <p/>
 * <li> <tt>Class_</tt><br />A <tt>Class_</tt> node represents a class from the Bantam source program.
 * It extends <tt>ASTNode</tt> and contains a filename (<tt>String</tt>), a class name (<tt>String</tt>),
 * a parent name (<tt>String</tt>), and a list of class members (<tt>MemberList</tt>).
 * <p/>
 * <li> <tt>Member</tt><br />A <tt>Member</tt> node is an abstract class that represents a class member
 * in a Bantam source program.  It is extended by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>Method</tt><br />A <tt>Method</tt> node represents a method from the Bantam source
 * program.  It extends <tt>Member</tt> and contains a return type (<tt>String</tt>), a
 * name (<tt>String</tt>), a list of parameters (<tt>ExprList</tt>), a list of statements
 * (<tt>StmtList</tt>), and a return statement (<tt>RetnStmt</tt>).
 * <p/>
 * <li> <tt>Field</tt><br />A <tt>Field</tt> node represents a field from the Bantam source program.
 * It extends <tt>Member</tt> and contains a declaration type (<tt>String</tt>), a name
 * (<tt>String</tt>), and an (optional) initialization expression (<tt>Expr</tt>).  For fields
 * without initialization expressions, the initialization expression is <tt>null</tt>.
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>Stmt</tt><br />A <tt>Stmt</tt> node is an abstract class that represents a statement in a
 * Bantam source program.  It is extended by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>ExprStmt</tt><br />An <tt>ExprStmt</tt> represents an expression statement from the Bantam
 * source program.  It extends <tt>Stmt</tt> and contains an expression (<tt>Expr</tt>).
 * <p/>
 * <li> <tt>DeclStmt</tt><br />A <tt>DeclStmt</tt> represents a declaration statement from the Bantam
 * source program.  It extends <tt>Stmt</tt> and contains a declaration type (<tt>String</tt>),
 * a name (<tt>String</tt>), and a (non-optional) initialization expression (<tt>Expr</tt>).
 * <p/>
 * <li> <tt>IfStmt</tt><br />An <tt>IfStmt</tt> represents an if statement from the Bantam source
 * program.  It extends <tt>Stmt</tt> and contains a predicate expression (<tt>Expr</tt>), a
 * then statement (<tt>Stmt</tt>), and a else statement (<tt>Stmt</tt>).
 * <p/>
 * <li> <tt>WhileStmt</tt><br />A <tt>WhileStmt</tt> represents a while statement from the
 * Bantam source program.  It extends <tt>Stmt</tt> and contains a continuation expression
 * (<tt>Expr</tt>) and a body statement (<tt>Stmt</tt>).
 * <p/>
 * <li> <tt>BlockStmt</tt><br />A <tt>BlockStmt</tt> represents a block statement from the
 * Bantam source program.  It contains a list of expressions (<tt>ExprList</tt>).
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>Expr</tt><br />An <tt>Expr</tt> node is an abstract class that represents an expression in a
 * Bantam source program.  It is extended by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>AssignExpr</tt><br />An <tt>AssignExpr</tt> represents an assignment expression from the
 * Bantam source program.  It extends <tt>Expr</tt> and contains an optional reference object
 * (<tt>String</tt>, which can be <tt>null</tt>), a lefthand variable name (<tt>String</tt>), and
 * a righthand expression (<tt>Expr</tt>).
 * <p/>
 * <li> <tt>DispatchExpr</tt><br />A <tt>DispatchExpr</tt> represents a dynamic dispatch
 * expression from the Bantam source program.  It extends <tt>Expr</tt> and contains an
 * optional reference object (<tt>Expr</tt>, which can be <tt>null</tt>), a method name
 * (<tt>String</tt>), and a list of arguments (<tt>ExprList</tt>).
 * <p/>
 * <li> <tt>CastExpr</tt><br />A <tt>CastExpr</tt> represents an explicit cast expression from
 * the Bantam source program.  It extends <tt>Expr</tt> and contains a target object type
 * (<tt>String</tt>), an expression to cast (<tt>Expr</tt>), and a flag (<tt>boolean</tt>)
 * indicating whether it's an upcast or a downcast.
 * <p/>
 * <li> <tt>InstanceofExpr</tt><br />An <tt>InstanceofExpr</tt> represents an <tt>instanceof</tt>
 * operation in a Bantam source program.   It extends <tt>Expr</tt> and contains a
 * expression to test (<tt>Expr</tt>) and an object type (<tt>String</tt>).
 * <p/>
 * <li> <tt>BinaryExpr</tt><br />A <tt>BinaryExpr</tt> is an abstract class that represents
 * all binary expressions where the left and right operands are both expressions (<i>i.e.</i>,
 * not casts or <tt>instanceof</tt>).  It extends <tt>Expr</tt> and contains a lefthand expression
 * (<tt>Expr</tt>) and a righthand expression (<tt>Expr</tt>).  It is extended by the
 * following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>BinaryArithExpr</tt><br />A <tt>BinaryArithExpr</tt> is an abstract class
 * that represents binary arithmetic expressions.  It extends <tt>BinaryExpr</tt>.
 * It is subclassed by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>BinaryArithPlusExpr</tt><br />A <tt>BinaryArithPlusExpr</tt>
 * represents a addition (`<tt>+</tt>') expression.
 * <p/>
 * <li> <tt>BinaryArithMinusExpr</tt><br />A <tt>BinaryArithMinusExpr</tt>
 * represents a subtraction (`<tt>-</tt>') expression.
 * <p/>
 * <li> <tt>BinaryArithTimesExpr</tt><br />A <tt>BinaryArithTimesExpr</tt>
 * represents a multiplication (`<tt>*</tt>') expression.
 * <p/>
 * <li> <tt>BinaryArithDivideExpr</tt><br />A <tt>BinaryArithDivideExpr</tt>
 * represents a division (`<tt>/</tt>') expression.
 * <p/>
 * <li> <tt>BinaryArithModulusExpr</tt><br />A <tt>BinaryArithModulusExpr</tt>
 * represents a modulus (`<tt>%</tt>') expression.
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>BinaryCompExpr</tt><br />A <tt>BinaryCompExpr</tt> is an abstract class
 * that represents binary comparison expressions.  It extends <tt>BinaryExpr</tt>.
 * It is subclassed by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>BinaryCompEqExpr</tt><br />A <tt>BinaryCompEqExpr</tt>
 * represents an equivalence (`<tt>==</tt>') expression.
 * <p/>
 * <li> <tt>BinaryCompNeExpr</tt><br />A <tt>BinaryCompNeExpr</tt>
 * represents a not equals (`<tt>!=</tt>') expression.
 * <p/>
 * <li> <tt>BinaryCompLtExpr</tt><br />A <tt>BinaryCompLtExpr</tt>
 * represents a less than (`<tt><</tt>') expression.
 * <p/>
 * <li> <tt>BinaryCompLeqExpr</tt><br />A <tt>BinaryCompLeqExpr</tt>
 * represents a less than or equal to (`<tt><=</tt>') expression.
 * <p/>
 * <li> <tt>BinaryCompGtExpr</tt><br />A <tt>BinaryCompGtExpr</tt>
 * represents a greater than (`<tt>></tt>') expression.
 * <p/>
 * <li> <tt>BinaryCompGeqExpr</tt><br />A <tt>BinaryCompGeqExpr</tt>
 * represents a greater than or equal to (`<tt>>=</tt>') expression.
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>BinaryLogicExpr</tt><br />A <tt>BinaryLogicExpr</tt> is an abstract class
 * that represents binary boolean logic expressions.  It extends <tt>BinaryExpr</tt>.
 * It is subclassed by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>BinaryLogicAndExpr</tt><br />A <tt>BinaryLogicAndExpr</tt>
 * represents a logical AND (`<tt>&&</tt>') expression.
 * <p/>
 * <li> <tt>BinaryLogicOrExpr</tt><br />A <tt>BinaryLogicOrExpr</tt>
 * represents a logical OR (`<tt>||</tt>') expression.
 * <p/>
 * </ul>
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>UnaryExpr</tt><br />A <tt>UnaryExpr</tt> is an abstract class that represents
 * all unary expressions where the operand is an expression (<i>i.e.</i>,
 * not <tt>new</tt>).  It extends <tt>Expr</tt> and contains an expression operand
 * (<tt>Expr</tt>).  It is extended by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>UnaryNegExpr</tt><br />A <tt>UnaryNegExpr</tt>
 * represents a unary integer negation (`<tt>-</tt>') expression.
 * <p/>
 * <li> <tt>UnaryNotExpr</tt><br />A <tt>UnaryNotExpr</tt>
 * represents a unary boolean complement (`<tt>!</tt>') expression.
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>ConstExpr</tt><br />A <tt>ConstExpr</tt> is an abstract class that represents
 * all constant expressions.  It extends <tt>Expr</tt> and contains a constant value
 * (<tt>String</tt>).  It is extended by the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>ConstIntExpr</tt><br />A <tt>ConstIntExpr</tt>
 * represents an integer constant (<i>e.g.</i>, <tt>12</tt>) expression.
 * <p/>
 * <li> <tt>ConstBooleanExpr</tt><br />A <tt>ConstBooleanExpr</tt>
 * represent a boolean constant (<i>e.g.</i>, <tt>true</tt>, <tt>false</tt>) expression.
 * <p/>
 * <li> <tt>ConstStringExpr</tt><br />A <tt>ConstStringExpr</tt>
 * represent a string constant (<i>e.g.</i>, <tt>``abc''</tt>) expression.
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>VarExpr</tt><br />A <tt>VarExpr</tt> represents a variable reference
 * expression.  It extends <tt>Expr</tt> and contains an optional reference
 * object name (<tt>String</tt>, which might be <tt>null</tt>) and a variable
 * name (<tt>String</tt>).
 * <p/>
 * </ul>
 * <p/>
 * <li> <tt>ListNode</tt><br />A <tt>ListNode</tt> is an abstract class that represents a generic list of
 * nodes in the AST.  All lists of nodes must extend <tt>ListNode</tt>.  These include
 * the following:
 * <p/>
 * <ul>
 * <p/>
 * <li> <tt>MemberList</tt><br />A <tt>MemberList</tt> node extends <tt>ListNode</tt> and contains
 * a list of <tt>Member</tt> nodes.
 * <p/>
 * <li> <tt>StmtList</tt><br />A <tt>StmtList</tt> node extends <tt>ListNode</tt> and contains a
 * list of <tt>Stmt</tt> nodes.
 * <p/>
 * <li> <tt>ExprList</tt><br />An <tt>ExprList</tt> node extends <tt>ListNode</tt> and contains a
 * list of <tt>Expr</tt> nodes.
 * <p/>
 * </ul>
 * <p/>
 * </ul>
 */
public abstract class ASTNode {
    /**
     * The source line number corresponding to this AST node
     */
    protected int lineNum;

    /**
     * ASTNode constructor
     *
     * @param lineNum source line number corresponding to this AST node
     */
    protected ASTNode(int lineNum) {
        this.lineNum = lineNum;
    }

    /**
     * Get the line number corresponding to this AST node
     *
     * @return line number
     */
    public int getLineNum() {
        return lineNum;
    }

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor
     */
    abstract public Object accept(Visitor v);
}
