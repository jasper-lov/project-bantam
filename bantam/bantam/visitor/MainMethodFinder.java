/*
* File: MainMethodFinder.java
* Authors: Jasper Loverude,
*  Cassidy Correl,
*  Dylan Tymkiw
*
* Date: April 2022
* */


package proj10LoverudeTymkiwCorrell.bantam.visitor;

import proj10LoverudeTymkiwCorrell.bantam.ast.ASTNode;
import proj10LoverudeTymkiwCorrell.bantam.ast.Class_;
import proj10LoverudeTymkiwCorrell.bantam.ast.Method;

/**
 * File: MainMethodFinder
 * User: djskrien
 * Date: 4/2022
 */

public class MainMethodFinder extends Visitor {

    private boolean hasMainMethodinMainClass;

    /**
     * Checks whether AST has a main class with main method
     *
     * @param rootNode the rootnode of Abstract Syntax Tree
     * @return whether program contains valid main method
     */
    public boolean hasMain(ASTNode rootNode) {
        hasMainMethodinMainClass = false;
        rootNode.accept(this);
        return hasMainMethodinMainClass;
    }

    /**
     * Checks if class node is main, only visits if class is "main"
     *
     * @param node the Class_ node
     * @return result of visiting
     */
    @Override
    public Object visit(Class_ node) {

        // Only continues tree traversal (visits members) IF class has name "main"
        if ("Main".equals(node.getName())) {

            node.getMemberList().accept(this);
        }

        return null;
    }


    /**
     * Checks whether Method node is named main
     * sets boolean if true
     *
     * @param node the Method node
     * @return null
     */
    @Override
    public Object visit(Method node) {

        /* If method title "main", with no parameters, and void return type,
        there is legal main method, as MainMethodFinder only visits
        methods/members of classes with name "main".  */
        if ("main".equals(node.getName()) &&
                "void".equals(node.getReturnType()) &&
                        node.getFormalList().getSize() == 0) {
            hasMainMethodinMainClass = true;
        }
        return null;
    }

}




