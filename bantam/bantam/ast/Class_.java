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
 * The <tt>Class_</tt> class represents a class declaration,
 * which consists of a filename (<tt>filename</tt>), a class name
 * (<tt>name</tt>), the name of its parent class (<tt>parent</tt>),
 * and a list of members (<tt>members</tt>) which can be either field
 * declarations or method declarations.
 *
 * @see ASTNode
 */
public class Class_ extends ASTNode {
    /**
     * The filename of the file containing this class
     */
    protected String filename;

    /**
     * The name of this class
     */
    protected String name;

    /**
     * The name of the parent of this class
     */
    protected String parent;

    /**
     * List of the class members
     */
    protected MemberList memberList;

    /**
     * Class_ constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     * @param filename   the filename of the file containing this class
     * @param name       the name of this class
     * @param parent     the name of the parent of this class
     * @param memberList a list of the class members
     */
    public Class_(int lineNum, String filename, String name, String parent, MemberList memberList) {
        super(lineNum);
        this.filename = filename;
        this.name = name;
        this.parent = parent;
        this.memberList = memberList;
    }

    /**
     * Get the filename of the file containing this class
     *
     * @return file name
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the name of this class
     *
     * @return class name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name of the parent of this class
     *
     * @return name of parent class
     */
    public String getParent() {
        return parent;
    }

    /**
     * Get list of members that this class contains
     *
     * @return list of fields
     */
    public MemberList getMemberList() {
        return memberList;
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
