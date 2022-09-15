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

package proj10LoverudeTymkiwCorrell.bantam.util;

import proj10LoverudeTymkiwCorrell.bantam.ast.Class_;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * The <tt>ClassTreeNode</tt> class represents a node in the class
 * hierarchy tree.  To perform semantic analysis the class hierarchy
 * tree must be constructed and used when building the environment and
 * type checking.  A <tt>ClassTreeNode</tt> represents one class in the
 * class hierarchy tree and contains all the relevant information about
 * that class (AST node, parent, children, symbol table, etc.).
 *
 * @see proj10LoverudeTymkiwCorrell.bantam.semant.SemanticAnalyzer
 */
public class ClassTreeNode {
    /**
     * The AST node for this class
     */
    private Class_ astNode;

    /**
     * A boolean flag indicating whether this class was built-in (true) or user-defined (false)
     */
    private boolean builtin;

    /**
     * A boolean flag indicating whether this class is extendable (true) or not (false)
     */
    private boolean extendable;

    /**
     * Maps class names to ClassTreeNode objects describing the class
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * The ClassTreeNode of the parent class
     */
    private ClassTreeNode parent;

    /**
     * A list of ClassTreeNodes of the subclasses of this class
     */
    private Vector<ClassTreeNode> children = new Vector<ClassTreeNode>();

    /**
     * Total number of descendants (strict subclasses)
     */
    private int numDescendants;

    /**
     * The (scoped) variable symbol table for this class used for type checking this class
     */
    private SymbolTable varSymbolTable = new SymbolTable();

    /**
     * The (scoped) method symbol table for this class used for type checking this class
     */
    private SymbolTable methodSymbolTable = new SymbolTable();

    /**
     * ClassTreeNode constructor
     *
     * @param astNode    the AST node for this class
     * @param builtin    boolean indicating whether this class was built-in
     * @param extendable boolean indicating whether this class is extendable
     * @param classMap   class map for accessing class tree nodes
     */
    public ClassTreeNode(Class_ astNode, boolean builtin, boolean extendable,
                         Hashtable<String, ClassTreeNode> classMap) {
        this.astNode = astNode;
        this.builtin = builtin;
        this.extendable = extendable;
        this.classMap = classMap;
        numDescendants = 0;
    }

    /**
     * Get the name of the class
     *
     * @return the name of this class
     */
    public String getName() {
        return astNode.getName();
    }

    /**
     * Get the AST node for this class
     *
     * @return AST node for this class
     */
    public Class_ getASTNode() {
        return astNode;
    }

    /**
     * Is this class built-in?
     *
     * @return boolean indicating whether this class was built-in
     */
    public boolean isBuiltIn() {
        return builtin;
    }

    /**
     * Is this class extendable?
     *
     * @return boolean indicating whether this class is extendable
     */
    public boolean isExtendable() {
        return extendable;
    }

    /**
     * Get the parent class tree node of the class
     *
     * @return parent class tree node
     */
    public ClassTreeNode getParent() {
        return parent;
    }

    /**
     * Set the parent class tree node of this class
     * Also adds this class to list of parent's children (if not already there)
     * In addition, sets variable and method symbol tables to the parent's symbol tables
     *
     * @param parent the class tree node of the parent class
     */
    public void setParent(ClassTreeNode parent) {
        if (parent == null) {
            throw new RuntimeException("Internal error: null parent in ClassTreeNode.setParent");
        }

        if (this.parent != parent) {
            // set parent of this class
            this.parent = parent;

            // set parent symbol table
            varSymbolTable.setParent(parent.getVarSymbolTable());
            methodSymbolTable.setParent(parent.getMethodSymbolTable());

            // also add this as a child of parent (if not already done)
            parent.addChild(this);

            // must update numDescendants the parent and for all ancestors of this parent
            // WARNING: must be careful -- class hierarchy tree may not be well formed
            // must avoid cycles
            Vector<ClassTreeNode> v = new Vector<ClassTreeNode>();
            for (ClassTreeNode ctn = parent; ctn != null; ctn = ctn.getParent()) {
                // check for a cycle -- break if we find one (program is wrong and
                // error should be detected by semantic analyzer)
                if (v.contains(ctn)) {
                    break;
                }
                // if not cycle then update num descendants by num descendants of
                // this new child node + 1 (new child)
                ctn.numDescendants += numDescendants + 1;
                // add to vector to potentially detect a cycle
                v.add(ctn);
            }
        }
    }

    /**
     * Add an immediate subclass to this class
     * Also makes child's parent this class (if not already)
     *
     * @param child the class tree node of the immediate subclass
     */
    public void addChild(ClassTreeNode child) {
        if (child == null) {
            throw new RuntimeException("Internal error: null child in ClassTreeNode.addChild");
        }

        if (!children.contains(child)) {
            // add child to list of children
            children.add(child);

            // if this class is not the parent of the child then set it to be
            if (child.getParent() != this) {
                child.setParent(this);
            }
        }
    }


    @Override
    public String toString() {
        return astNode.getName()
                + ":" + (parent == null ? null : parent.getASTNode().getName());
    }

    /**
     * removes the child from the list of children for this node.
     * It returns true if the child was removed from the list or false if the child
     * wasn't in this node's list of children
     * @param child the ClassTreeNode to be removed from the list of children
     * @return true if the child was removed from the list else false
     */
    public boolean removeChild(ClassTreeNode child) {
        if ( children.contains(child)) {
            children.remove(child);
            return true;
        }
        return false;
    }

    /**
     * Get an iterator of class tree nodes representing the immediate subclasses of this class
     *
     * @return list of children
     */
    public Iterator<ClassTreeNode> getChildrenList() {
        return children.iterator();
    }

    /**
     * Get the number of children of this class
     *
     * @return number of children
     */
    public int getNumChildren() {
        return children.size();
    }

    /**
     * Get the number of descendants (strict subclasses) of this class
     *
     * @return number of descendants (strict subclasses)
     */
    public int getNumDescendants() {
        return numDescendants;
    }

    /**
     * Get the variable symbol table of the class
     *
     * @return variable symbol table
     */
    public SymbolTable getVarSymbolTable() {
        return varSymbolTable;
    }

    /**
     * Get the method symbol table of the class
     *
     * @return method symbol table
     */
    public SymbolTable getMethodSymbolTable() {
        return methodSymbolTable;
    }

    /**
     * Lookup a class tree node
     *
     * @param className the name of the class to lookup
     * @return corresponding class tree node (null if not found)
     */
    public ClassTreeNode lookupClass(String className) {
        return classMap.get(className);
    }

    /**
     * gets a map of all the classes and their ClassTreeNodes
     *
     * ADDED BY DJS
     * @return Hashtable whose keys are class names and whose
     *         values are ClassTreeNodes
     */
    public Hashtable<String, ClassTreeNode> getClassMap() {
        return classMap;
    }
}
