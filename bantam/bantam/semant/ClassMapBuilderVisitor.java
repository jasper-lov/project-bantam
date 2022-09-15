/**
 * File: ClassMapBuilderVisitor
 * Authors: Dale Skrien, Marc Corliss,
 *          David Furcy and E Christopher Lewis
 * Date: 4/12/2022
 */

package proj10LoverudeTymkiwCorrell.bantam.semant;

import proj10LoverudeTymkiwCorrell.bantam.ast.Class_;
import proj10LoverudeTymkiwCorrell.bantam.util.ClassTreeNode;
import proj10LoverudeTymkiwCorrell.bantam.util.ErrorHandler;
import proj10LoverudeTymkiwCorrell.bantam.util.Error;
import proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor;

import java.util.Hashtable;

/**
 * This class visits the AST to find all class declarations and add an entry
 * for each of those classes in the classMap.
 */
public class ClassMapBuilderVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    private ErrorHandler errorHandler;

    ClassMapBuilderVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler
            errorHandler) {
        this.classMap = classMap;
        this.errorHandler = errorHandler;
    }

    /**
     * adds a new ClassTreeNode for this node to the classMap.
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node) {
        if(classMap.containsKey(node.getName()))
            errorHandler.register(Error.Kind.SEMANT_ERROR,node.getFilename(),
                    node.getLineNum(),"Two classes declared with the same name; " +
                            node.getName());
        else if(SemanticAnalyzer.reservedIdentifiers.contains(node.getName()))
            errorHandler.register(Error.Kind.SEMANT_ERROR,node.getFilename(),
                    node.getLineNum(),"A class cannot be named 'this', 'super'," +
                            "'void', 'int', 'boolean', or 'null'; " +
                            node.getName());
        else {
            ClassTreeNode treeNode = new ClassTreeNode(node, false, true, classMap);
            classMap.put(node.getName(), treeNode);
        }
        return null;
    }
}
