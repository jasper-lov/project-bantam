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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Class for representing a class symbol table
 */
public class SymbolTable {
    /**
     * Hash table that maps strings to Objects.  The object value corresponds to
     * the type of the variable or method.  For variables it will be a String and
     * for methods it will be an AST node.
     */
    private Hashtable<String, Object> hash;
    /**
     * List that holds each scope
     */
    private Vector<Hashtable<String, Object>> scopes;
    /**
     * Parent class symbol table (may be null)
     * If lookup fails in this symbol table should lookup in parent
     */
    private SymbolTable parent;

    /**
     * SymbolTable constructor
     * create an empty symbol table
     */
    public SymbolTable() {
        hash = null;
        scopes = new Vector<Hashtable<String, Object>>();
        parent = null;
    }

    /**
     * SymbolTable constructor
     * create a symbol table from an existing one
     * (used internally for cloning symbol tables
     *
     * @param scopes the scopes of symbols
     * @param parent parent symbol table
     */
    private SymbolTable(Vector<Hashtable<String, Object>> scopes,
                        SymbolTable parent) {
        this.scopes = scopes;
        if (scopes.size() > 0) {
            this.hash = scopes.elementAt(scopes.size() - 1);
        }
        else {
            this.hash = null;
        }
        this.parent = parent;
    }

    /**
     * Set the parent symbol table
     *
     * @param parent symbol table of the parent class
     */
    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    /**
     * Enter a new scope
     */
    public void enterScope() {
        hash = new Hashtable<String, Object>();
        scopes.add(hash);
    }

    /**
     * Exit a scope
     */
    public void exitScope() {
        if (scopes.size() == 0) {
            throw new RuntimeException("No scope to exit");
        }
        scopes.removeElementAt(scopes.size() - 1);
        if (scopes.size() > 0) {
            hash = scopes.elementAt(scopes.size() - 1);
        }
        else {
            hash = null;
        }
    }

    /**
     * Adds a symbol to the symbol table if one does not already exist
     * Sets the value of the symbol to the specified parameter
     *
     * @param s     symbol name (i.e., name of variable or method)
     * @param value value of symbol (i.e., type)
     */
    public void add(String s, Object value) {
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before adding to table");
        }
        hash.put(s, value);
    }

    /**
     * Looks up a symbol in any scope in the symbol table
     *
     * @param s string of symbol to lookup
     * @return value of symbol (i.e., type), null if not found
     */
    public Object lookup(String s) {
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before looking up in table");
        }

        for (int i = scopes.size() - 1; i >= 0; i--) {
            Hashtable<String, Object> h = scopes.elementAt(i);
            Object value = h.get(s);
            if (value != null) {
                return value;
            }
        }

        if (parent != null) {
            return parent.lookup(s);
        }
        return null;
    }

    /**
     * Looks up a symbol in a particular scope in the symbol table as well
     * as any outer scopes from that specified scope
     * Note: an exception is thrown if the specified level is <0 or >= largest
     * scope level
     *
     * @param s     string of symbol to lookup
     * @param level scope level to lookup string (outermost scope is at level 0)
     * @return value of symbol (i.e., type), null if not found in particular scope level
     */
    public Object lookup(String s, int level) {
        int lastLevel = getCurrScopeLevel();

        // some error checking
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before looking up in table");
        }
        else if (level < 0 || level >= lastLevel) {
            throw new IllegalArgumentException("SymbolTable.lookup(sym,level) called" +
                    " with level (" + level + ") that is not" +
                    " between 0 and current level (" +
                    lastLevel + ") minus one");
        }

        // get an ordered list of ancestor symbol tables
        // (starts from Object's table and ends with current class's table)
        Vector<SymbolTable> tableList = new Vector<SymbolTable>();
        for (SymbolTable st = this; st != null; st = st.parent)
            tableList.add(0, st);

        // find the right scope
        for (int i = 0; i < tableList.size(); i++) {
            SymbolTable st = tableList.elementAt(i);

            // if level within the next table then do lookup
            if (level < st.scopes.size()) {
                // lookup in this scope and earlier scopes until we find
                // symbol or run out of scopes
                for (int l = level; l >= 0; l--) {
                    Hashtable<String, Object> h = st.scopes.elementAt(l);
                    if (h.get(s) != null) {
                        return h.get(s);
                    }
                }
                // if we make it here then we try looking up in parent table
                // (if one exists)
                if (st.parent != null) {
                    return st.parent.lookup(s);
                }
                // otherwise we return null (not found)
                return null;
            }

            // otherwise decrement level and proceed to next table
            level = level - st.scopes.size();
        }

        // we should never get here
        return null;
    }

    /**
     * Looks up a symbol in the current scope in the table
     *
     * @param s string of symbol to lookup
     * @return value of symbol (i.e., type), null if not found
     */
    public Object peek(String s) {
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before peeking in table");
        }
        return hash.get(s);
    }

    /**
     * Looks up a symbol in a particular scope in the symbol table
     * Note: an exception is thrown if the specified level is <0 or >= largest
     * scope level
     *
     * @param s     string of symbol to lookup
     * @param level scope level to lookup string (outermost scope is at level 0)
     * @return value of symbol (i.e., type), null if not found in particular scope level
     */
    public Object peek(String s, int level) {
        int lastLevel = getCurrScopeLevel();

        // some error checking
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before peeking in table");
        }
        else if (level < 0 || level >= lastLevel) {
            throw new IllegalArgumentException("SymbolTable.peek(sym,level) called" +
                    " with level (" + level + ") that is not" +
                    " between 0 and current level (" +
                    lastLevel + ") minus one");
        }

        // get an ordered list of ancestor symbol tables
        // (starts from Object's table and ends with current class's table)
        Vector<SymbolTable> tableList = new Vector<SymbolTable>();
        for (SymbolTable st = this; st != null; st = st.parent)
            tableList.add(0, st);

        // find the right scope
        for (int i = 0; i < tableList.size(); i++) {
            SymbolTable st = tableList.elementAt(i);

            // if level within the next table then do lookup
            if (level < st.scopes.size()) {
                Hashtable<String, Object> h = st.scopes.elementAt(level);
                return h.get(s);
            }

            // otherwise decrement level and proceed to next table
            level = level - st.scopes.size();
        }

        // we should never get here
        return null;
    }

    /**
     * Sets the value of an existing symbol in the innermost scope of the symbol table
     *
     * @param s   string of symbol to set
     * @param val value to set the symbol to
     */
    public void set(String s, Object val) {
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before setting in table");
        }

        for (SymbolTable st = this; st != null; st = st.parent) {
            for (int i = st.scopes.size() - 1; i >= 0; i--) {
                Hashtable<String, Object> h = st.scopes.elementAt(i);
                if (h.get(s) != null) {
                    h.remove(s);
                    h.put(s, val);
                    return;
                }
            }
        }

        throw new RuntimeException("Set symbol '" + s +
                "' is not in the symbol table");
    }

    /**
     * Sets the value of an existing symbol in a particular scope of the
     * symbol table (or an outer scope containing the specified scope)
     *
     * @param s     string of symbol to set
     * @param val   value to set the symbol to
     * @param level scope level containing symbol to be set
     */
    public void set(String s, Object val, int level) {
        int lastLevel = getCurrScopeLevel();

        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before setting in table");
        }
        else if (level < 0 || level >= lastLevel) {
            throw new IllegalArgumentException("SymbolTable.set(sym,val,level) called" +
                    " with level (" + level + ") that is not" +
                    " between 0 and current level (" +
                    lastLevel + ") minus one");
        }

        // get an ordered list of ancestor symbol tables
        // (starts from Object's table and ends with current class's table)
        Vector<SymbolTable> tableList = new Vector<SymbolTable>();
        for (SymbolTable st = this; st != null; st = st.parent)
            tableList.add(0, st);

        // find the right scope
        for (int i = 0; i < tableList.size(); i++) {
            SymbolTable st = tableList.elementAt(i);

            // if level within the next table then do lookup
            if (level < st.scopes.size()) {
                // look in this scope and earlier scopes until we find
                // symbol or run out of scopes
                for (int l = level; l >= 0; l--) {
                    Hashtable<String, Object> h = st.scopes.elementAt(l);
                    if (h.get(s) != null) {
                        h.remove(s);
                        h.put(s, val);
                        return;
                    }
                }
                // if we make it here then we try setting in parent table
                // (if one exists)
                if (st.parent != null) {
                    st.parent.set(s, val);
                }
                return;
            }

            // otherwise decrement level and proceed to next table
            level = level - st.scopes.size();
        }
    }

    /**
     * Gets scope level of a symbol in the table
     * (<0 means symbol not in table)
     *
     * @param s string of symbol to lookup
     * @return scope level
     */
    public int getScopeLevel(String s) {
        if (scopes.size() == 0) {
            throw new RuntimeException("Must enter a scope before looking up in table");
        }

        for (int i = scopes.size() - 1; i >= 0; i--) {
            Hashtable<String, Object> h = scopes.elementAt(i);
            if (h.get(s) != null) {
                if (parent == null) {
                    return (i + 1);
                }
                else {
                    return (i + 1) + parent.getCurrScopeLevel();
                }
            }
        }

        if (parent != null) {
            return parent.getScopeLevel(s);
        }

        return -1;
    }

    /**
     * Gets the number of entries in all scopes of the symbol table
     * Note: includes inherited scopes
     *
     * @return size of current scope
     */
    public int getSize() {
        int size = 0;

        for (int i = 0; i < scopes.size(); i++)
            size += scopes.elementAt(i).size();

        if (parent != null) {
            return parent.getSize() + size;
        }
        return size;
    }

    /**
     * Gets the number of entries in the current scope of the symbol table
     *
     * @return size of current scope
     */
    public int getCurrScopeSize() {
        if (hash != null) {
            return hash.size();
        }
        else {
            return 0;
        }
    }

    /**
     * Gets the current scope level of the symbol table
     * (first scope starts at 1)
     *
     * @return current scope level
     */
    public int getCurrScopeLevel() {
        if (parent != null) {
            return scopes.size() + parent.getCurrScopeLevel();
        }
        return scopes.size();
    }

    /**
     * clone this symbol table as well as all parent symbol tables
     * Note: does a shallow clone, does not copy keys or values
     * Note also: this is an expensive operation
     *
     * @return cloned symbol table
     */
    public SymbolTable clone() {
        // clone parent symbol table (as well as all other ancestors)
        SymbolTable newParent = null;
        if (parent != null) {
            newParent = parent.clone();
        }

        // create new set of clone scopes
        Vector<Hashtable<String, Object>> newScopes
                = new Vector<Hashtable<String, Object>>();
        for (int i = 0; i < scopes.size(); i++)
            newScopes.add((Hashtable<String, Object>) scopes.elementAt(i).clone());

        // use these to create a new symbol table
        SymbolTable st = new SymbolTable(newScopes, newParent);

        // return cloned symbol table
        return st;
    }

    /**
     * For debugging -- dumps out entire symbol table
     * Starts from highest scope level (Object class) and works
     * towards the lowest scope level (current level)
     */
    public void dump() {
        if (parent != null) {
            parent.dump();
        }

        Enumeration e = scopes.elements();
        while (e.hasMoreElements())
            System.out.println(e.nextElement());
    }
}
