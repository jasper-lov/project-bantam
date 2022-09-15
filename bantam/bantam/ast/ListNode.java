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

   Modified by Dale Skrien
   Jan 2014
   --added get(int index) method
   --changed getIterator to iterator and added "implements Iterable<ListNode>"
     so that foreach loops can be used
*/

package proj10LoverudeTymkiwCorrell.bantam.ast;

import proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor;

import java.util.Iterator;
import java.util.Vector;

/**
 * The abstract <tt>ListNode</tt> class represents a generic list of AST
 * nodes.  It contains a line number (<tt>lineNum</tt>) and a list
 * (<tt>listElements</tt>).
 *
 * @see ASTNode
 */
public abstract class ListNode extends ASTNode
    implements Iterable<ASTNode> {
    /**
     * List of ASTNode elements
     */
    private Vector<ASTNode> listElements;

    /**
     * ListNode constructor
     *
     * @param lineNum source line number corresponding to this AST node
     */
    protected ListNode(int lineNum) {
        super(lineNum);
        listElements = new Vector<ASTNode>();
    }

    /**
     * Add an element to this list node
     *
     * @param node element to add
     * @return the updated list
     */
    public ListNode addElement(ASTNode node) {
        listElements.add(node);
        return this;
    }

    /**
     * Returns an iterator for this list
     *
     * @return iterator for this list
     */
    public Iterator<ASTNode> iterator() {
        return listElements.iterator();
    }

    /**
     * Get the list size
     *
     * @return list size
     */
    public int getSize() {
        return listElements.size();
    }

    /**
     * get the index-th element
     *
     * @return index-th element
     * @throws java.lang.ArrayIndexOutOfBoundsException if the index is
     *         too large or too small
     */
    public ASTNode get(int index) {
        return listElements.get(index);
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
