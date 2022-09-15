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
 * The <tt>Field</tt> class represents a field (instance variable) declaration
 * appearing in a class declaration.  It contains a variable type (<tt>type</tt>),
 * a name (<tt>name</tt>), and an optional initialization expression (<tt>init</tt>).
 *
 * @see ASTNode
 */
public class Field extends Member {
    /**
     * The type of the field (instance variable)
     */
    protected String type;

    /**
     * The name of the field (instance variable)
     */
    protected String name;

    /**
     * The (optional) initialization expression for the field (instance variable)
     */
    protected Expr init;

    /**
     * Field constructor
     *
     * @param lineNum source line number corresponding to this AST node
     * @param type    the type of the field (instance variable)
     * @param name    the name of the field (instance variable)
     * @param init    the (optional) initialization expression for the field (instance variable)
     */
    public Field(int lineNum, String type, String name, Expr init) {
        super(lineNum);
        this.type = type;
        this.name = name;
        this.init = init;
    }

    /**
     * Get the type of the field
     *
     * @return field type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the name of the field
     *
     * @return field name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the initialization expression of the field
     *
     * @return initialization expression
     */
    public Expr getInit() {
        return init;
    }

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor
     */
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
