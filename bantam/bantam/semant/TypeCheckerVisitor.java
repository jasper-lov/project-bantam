/*
 * File: TypeCheckerVisitor.java
 * Authors: Dale Skrien, Marc Corliss,
 *          David Furcy and E Christopher Lewis
 *
 *          Modified by Cassidy Correll,
 *          Jasper Loverude,
 *          Dylan Tymkiw
 * Date: 4/2022
 */

package proj10LoverudeTymkiwCorrell.bantam.semant;

import proj10LoverudeTymkiwCorrell.bantam.ast.*;
import proj10LoverudeTymkiwCorrell.bantam.util.ClassTreeNode;
import proj10LoverudeTymkiwCorrell.bantam.util.ErrorHandler;
import proj10LoverudeTymkiwCorrell.bantam.util.Error;
import proj10LoverudeTymkiwCorrell.bantam.util.SymbolTable;
import proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor;
import proj10LoverudeTymkiwCorrell.bantam.ast.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This visitor find the types of all expression nodes and sets the type field
 * of the nodes.  It reports an error for any type incompatibility.
 */
public class TypeCheckerVisitor extends Visitor
{
    /** the ErrorHandler that records the errors */
    private final ErrorHandler errorHandler;
    /** a stack of the current nested for or while statements
     for checking whether a break statement is inside a loop. */
    private final Stack<Stmt> currentNestedLoops;
    /** the current class being visited */
    private ClassTreeNode currentClass;
    /** the current method being visited */
    private Method currentMethod;
    /** the current symbolTable to use for checking types */
    private SymbolTable currentSymbolTable;

    public TypeCheckerVisitor(ErrorHandler errorHandler, ClassTreeNode root) {
        this.errorHandler = errorHandler;
        this.currentClass = root; // the Object class
        this.currentMethod = null;
        this.currentSymbolTable = null;
        this.currentNestedLoops = new Stack<>();
    }

    /*
     * CLASS INVARIANT:  Every visit method for Expr nodes sets the type field
     *                   of the Expr node being visited to a valid type.
     *                   If the node's calculated type is illegal,
     *                   an error was reported and the node's type
     *                   is set to the type it should have been or to
     *                   a generic type like "Object" so that the visits can continue.
     */

    /**
     * returns true if the first type is the same type or a subtype of the second type
     * It assumes t1 and t2 are legal types or null.  For the purpose of this
     * method, we are assuming null is a subtype of all non-primitive types.
     *
     * @param t1 the String name of the first type
     * @param t2 the String name of the second type
     * @return true if t1 is a subtype of t2
     */
    private boolean isSubtype(String t1, String t2) {
        if (t1.equals("null") && !isPrimitiveType(t2)) {
            return true;
        }
        if (t1.equals("int") || t2.equals("int")) {
            return t2.equals(t1);
        }
        if (t1.equals("boolean") || t2.equals("boolean")) {
            return t2.equals(t1);
        }
        // go up the inheritance tree of t1 to see if you
        // encounter t2
        ClassTreeNode t1Node = currentClass.lookupClass(t1);
        ClassTreeNode t2Node = currentClass.lookupClass(t2);
        while (t1Node != null) {
            if (t1Node == t2Node) {
                return true;
            }
            t1Node = t1Node.getParent();
        }
        return false;
    }

    /**
     * returns true if the given type is int or boolean
     */
    private boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("boolean");
    }

    /**
     * returns true if the given type is a primitive type or a declared class
     */
    private boolean typeHasBeenDeclared(String type) {
        return isPrimitiveType(type) || currentClass.lookupClass(type) != null;
    }

    /**
     * register an error with the Errorhandler
     * @param node the ASTNode where the error was found
     * @param message the error message
     */
    private void registerError(ASTNode node, String message) {
        errorHandler.register(Error.Kind.SEMANT_ERROR,
                currentClass.getASTNode().getFilename(), node.getLineNum(), message);
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        // set the currentClass to this class
        currentClass = currentClass.lookupClass(node.getName());
        currentSymbolTable = currentClass.getVarSymbolTable();
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    public Object visit(Field node) {
        //The fields have already been added to the symbol table by the SemanticAnalyzer,
        // so the only thing to check is the compatibility of the init expr's type with
        //the field's type.
        if (!typeHasBeenDeclared(node.getType())) {
            registerError(node,"The declared type " + node.getType() +
                    " of the field " + node.getName() + " is undefined.");
        }
        Expr initExpr = node.getInit();
        if (initExpr != null) {
            initExpr.accept(this);
            if (!isSubtype(initExpr.getExprType(), node.getType())) {
                registerError(node,"The type of the initializer is "
                        + initExpr.getExprType() + " which is not compatible with the "
                        + node.getName() + " field's type " + node.getType());
            }
        }
        //Note: if there is no initial value, then leave it with its default Java value
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        // is the return type a legitimate type
        if (!typeHasBeenDeclared(node.getReturnType()) && !node.getReturnType().equals(
                "void")) {
            registerError(node,"The return type " + node.getReturnType() +
                    " of the method " + node.getName() + " is undefined.");
        }

        //create a new scope for the method
        currentSymbolTable.enterScope();
        currentMethod = node;
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);

        //check that non-void methods end with a return stmt
        if(! node.getReturnType().equals("void")) {
            StmtList sList = node.getStmtList();
            if (sList.getSize() == 0
                    || !(sList.get(sList.getSize() - 1) instanceof ReturnStmt)) {
                registerError(node, "Methods with non-void return type must " +
                        "end with a return statement.");
            }
        }
        currentMethod = null;
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a formal node
     *
     * @param node the formal node
     * @return result of the visit
     */
    public Object visit(Formal node) {
        if (!typeHasBeenDeclared(node.getType())) {
            registerError(node,"The declared type " + node.getType() +
                    " of the formal parameter " + node.getName() + " is undefined.");
        }
        // add it to the current scope if there isn't already a formal of the same name
        if (currentSymbolTable.getScopeLevel(node.getName()) ==
                currentSymbolTable.getCurrScopeLevel()) {
            registerError(node,"The name of the formal parameter "
                    + node.getName() + " is the same as the name of another formal" +
                    " parameter.");
        }
        currentSymbolTable.add(node.getName(), node.getType());
        return null;
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {

        // Check that initExpr type matches node type
        Expr initExpr = node.getInit();
        initExpr.accept(this);
        Object initType = initExpr.getExprType();

        // Gets initType if VarExpr
        if (initExpr instanceof VarExpr){
            initType = currentSymbolTable.lookup(((VarExpr)initExpr).getName());
        }
        // Gets initType if DispatchExpr
        else if(initExpr instanceof DispatchExpr){
            initType = currentClass.getMethodSymbolTable()
                    .lookup(((DispatchExpr)initExpr).getMethodName());

        }

        //
        if(initType instanceof Method){
            String methodName = ((Method)initType).getName();
            initType = ((Method)initType).getReturnType();
            if(initType.equals("void")){
                registerError(node,"Method "
                    + methodName + " has return type void");

            }
        }

        // Add it to the current scope if var has not been already declared
        if (currentSymbolTable.lookup(node.getName(),
                currentSymbolTable.getCurrScopeLevel() - 1) != null) {
            registerError(node,"Variable "
                    + node.getName() + " has already been declared");
            initType = "Object";
        }

        currentSymbolTable.add(node.getName(), initType);

        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        node.getPredExpr().accept(this);
        String predExprType = node.getPredExpr().getExprType();
        if (!"boolean".equals(predExprType)) {
            registerError(node,"The type of the predicate is " +
                    (predExprType != null ? predExprType : "unknown") +
                    ", not boolean.");
        }
        currentSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();
        if (node.getElseStmt() != null) {
            currentSymbolTable.enterScope();
            node.getElseStmt().accept(this);
            currentSymbolTable.exitScope();
        }
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        node.getPredExpr().accept(this);
        if (!isSubtype(node.getPredExpr().getExprType(), "boolean")) {
            registerError(node,"The type of the predicate is " +
                    node.getPredExpr().getExprType() + " which is not boolean.");
        }
        currentSymbolTable.enterScope();
        currentNestedLoops.push(node);
        node.getBodyStmt().accept(this);
        currentNestedLoops.pop();
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {

        // Visits init Expression if not null
        if(node.getInitExpr() != null){
            node.getInitExpr().accept(this);
        }

        // Visits predicate Expression if not null, and checks if boolean
        if(node.getPredExpr() != null){
            node.getPredExpr().accept(this);

            if(!isSubtype(node.getPredExpr().getExprType(), "boolean")){
                registerError(node,"The type of the predicate is " +
                        node.getPredExpr().getExprType() + " which is not boolean.");
            }
        }

        // Visits update Expression if not null
        if(node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }

        // Create new scope and move down tree
        currentSymbolTable.enterScope();
        currentNestedLoops.push(node);
        node.getBodyStmt().accept(this);
        currentNestedLoops.pop();
        currentSymbolTable.exitScope();


        return null;
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {

        if(currentNestedLoops.empty()){
            registerError(node,"Break statement not inside loop");
        }
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        currentSymbolTable.enterScope();
        node.getStmtList().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            if (!isSubtype(node.getExpr().getExprType(), currentMethod.getReturnType())) {
                registerError(node,"The type of the return expr is " +
                        node.getExpr().getExprType() +
                        " which is not compatible with the " +
                        currentMethod.getName() + " method's return type "
                        + currentMethod.getReturnType());
            }
        }
        else if (!currentMethod.getReturnType().equals("void")) {
            registerError(node, "The type of the method " + currentMethod.getName() +
                    " is not void and so return statements in it must return a value.");
        }
        return null;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return the type of the expression
     */
    public Object visit(DispatchExpr node) {


        // Gets reference expression ('this', 'super', or null.)
        Expr refExpr = node.getRefExpr();

        // If reference is not null, checks with reference expression method
        if(refExpr != null){

            String refName = ((VarExpr) refExpr).getName();

            // If refExpr is super, checks parent method symbol table with method
            if(refName.equals("super")) {

                checkArgs(node);
                return currentClass.getParent().getMethodSymbolTable()
                        .lookup(node.getMethodName());

            }
            // If refExpr is this, checks level 0 of methodSymbolTable
            else if(refName.equals("this")) {

                checkArgs(node);
                return currentClass.getMethodSymbolTable()
                        .lookup(node.getMethodName(), 0);

            }
            else{
                System.out.println("doable?");
            }

        }

        // If no refExpr, uses default lookup method
        checkArgs(node);
        return currentClass.getMethodSymbolTable().lookup(node.getMethodName());

    }

    /**
     * Helper method to check if arguments are legal
     *
     * @param node the dispatch expression node
     * */
    private void checkArgs(DispatchExpr node){

        List<String> parametersType = getFormalTypesList((Method)
                currentClass.getMethodSymbolTable().lookup(node.getMethodName()));

        // Checks that length of formalList and actualList match in size
        if(node.getActualList().getSize() != parametersType.size()){
            registerError(node,
                    "Actual arguments did not match size of expected arguments.");
            return;
        }

        // Iterates through actual argument list and compares types to formal list
        int i = 0;

        for(ASTNode argument : node.getActualList()){

            argument.accept(this);

            // If argument types do not match,
            if(!((Expr)argument).getExprType().equals(parametersType.get(i))){
                registerError(node, "Expected type " + parametersType.get(i) +
                        ", got type "
                        + ((Expr)argument).getExprType() + ".");
            }
            i++;
        }

    }

    /**
     * returns a list of the types of the formal parameters
     *
     * @param method the methods whose formal parameter types are desired
     * @return a List of Strings (the types of the formal parameters)
     */
    private List<String> getFormalTypesList(Method method) {
        List<String> result = new ArrayList<>();
        for (ASTNode formal : method.getFormalList())
            result.add(((Formal) formal).getType());
        return result;
    }

    /**
     * Visit a list node of expressions
     *
     * @param node the expression list node
     * @return a List<String> of the types of the expressions
     */
    public Object visit(ExprList node) {
        List<String> typesList = new ArrayList<>();
        for (ASTNode expr : node) {
            expr.accept(this);
            typesList.add(((Expr) expr).getExprType());
        }
        //return a List<String> of the types of the expressions
        return typesList;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return the type of the expression
     */
    public Object visit(NewExpr node) {
        if (currentClass.lookupClass(node.getType()) == null) {
            registerError(node,"The type " + node.getType() + " does not exist.");
            node.setExprType("Object"); // to allow analysis to continue
        }
        else {
            node.setExprType(node.getType());
        }
        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return the type of the expression
     */
    public Object visit(InstanceofExpr node) {
        if (currentClass.lookupClass(node.getType()) == null) {
            registerError(node,"The reference type " + node.getType()
                    + " does not exist.");
        }
        node.getExpr().accept(this);
        if (isSubtype(node.getExpr().getExprType(), node.getType())) {
            node.setUpCheck(true);
        }
        else if (isSubtype(node.getType(), node.getExpr().getExprType())) {
            node.setUpCheck(false);
        }
        else {
            registerError(node,"You can't compare type " +
                    node.getExpr().getExprType() + "to " + "incompatible type "
                    + node.getType() + ".");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return the type of the expression
     */
    public Object visit(CastExpr node) {

        // Checks if cast type is not a defined type
        if(!isPrimitiveType(node.getType()) &&
                currentClass.lookupClass(node.getType()) == null){
            registerError(node,"Cast  " +
                    node.getType() + " not a defined type.");
        }

        node.getExpr().accept(this);
        node.getExpr().setExprType(node.getType());

        return node.getType();
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return the type of the expression
     */
    public Object visit(AssignExpr node) {

        node.getExpr().accept(this);
        Object exprType = node.getExpr().getExprType();

        if(node.getExpr() instanceof VarExpr){
            exprType = currentSymbolTable.lookup(((VarExpr) node.getExpr()).getName());
        }

        // If in symbol table must make sure type matches
        if(currentSymbolTable.lookup(node.getName()) != null){
            if(!currentSymbolTable.lookup(node.getName()).equals(exprType)){
                registerError(node,"expected  " +
                        currentSymbolTable.lookup(node.getName()) + ", got  "
                        + exprType + ".");
            }
        }
        else{
            //adds to symbol table if not already in symbol table
            currentSymbolTable.set((String) exprType, node.getExpr());
        }
        return exprType;
    }


    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return the type of the expression
     */
    public Object visit(VarExpr node) {

        Expr ref = node.getRef();
        // get reference

        if(ref != null){

            String refName = ((VarExpr) ref).getName();

            if(refName.equals("super")) {
                return currentClass.getParent().getVarSymbolTable()
                        .lookup(node.getName());

            }
            else if(refName.equals("this")) {
                return currentClass.getVarSymbolTable().lookup(node.getName());

            }

        }
        //set expression type to type
        node.setExprType((String)currentSymbolTable.lookup(node.getName()));
        return currentSymbolTable.lookup(node.getName());


    }

    /**
     * returns an array of length 2 containing the types of
     * the left and right children of the node.
     * @param node The BinaryExpr whose children are to be typed
     * @return A String[] of length 2 with the types of the 2 children
     */
    private String[] getLeftAndRightTypes(BinaryExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        return new String[]{type1,type2};
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return the type of the expression
     */
    public Object visit(BinaryCompEqExpr node) {

        String[] types = getLeftAndRightTypes(node);
        if (types[0] == null || types[1] == null) {
            return null; //error in one expr, so skip further checking
        }
        if (!(isSubtype(types[0], types[1]) || isSubtype(types[1], types[0]))) {
            registerError(node,"The " + "two values being compared for " +
                    "equality are not compatible types.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return the type of the expression
     */
    public Object visit(BinaryCompNeExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(isSubtype(types[0], types[1]) || isSubtype(types[1], types[0]))) {
            registerError(node,"The two values being compared for equality " +
                    "are not compatible types.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return the type of the expression
     */
    public Object visit(BinaryCompLtExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being compared by \"<\" are " +
                    "not both ints.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return the type of the expression
     */
    public Object visit(BinaryCompLeqExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The  two values being compared by \"<=\" are" +
                    " not both ints.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return the type of the expression
     */
    public Object visit(BinaryCompGtExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being compared by \">\" are" +
                    " not both ints.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return the type of the expression
     */
    public Object visit(BinaryCompGeqExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The  two values being compared by \">=\" are " +
                    "not both ints.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return the type of the expression
     */
    public Object visit(BinaryArithPlusExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being added are not both ints.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return the type of the expression
     */
    public Object visit(BinaryArithMinusExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being subtraced are not both ints.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return the type of the expression
     */
    public Object visit(BinaryArithTimesExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being multiplied are not both ints.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return the type of the expression
     */
    public Object visit(BinaryArithDivideExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being divided are not both ints.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return the type of the expression
     */
    public Object visit(BinaryArithModulusExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("int") && types[1].equals("int"))) {
            registerError(node,"The two values being operated on with % are " +
                    "not both ints.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return the type of the expression
     */
    public Object visit(BinaryLogicAndExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("boolean") && types[1].equals("boolean"))) {
            registerError(node,
                    "The two values being operated on with && are not both booleans"
                            + ".");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return the type of the expression
     */
    public Object visit(BinaryLogicOrExpr node) {
        String[] types = getLeftAndRightTypes(node);
        if (!(types[0].equals("boolean") && types[1].equals("boolean"))) {
            registerError(node,
                    "The two values being operated on with || are not both booleans"
                            + ".");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return the type of the expression
     */
    public Object visit(UnaryNegExpr node) {
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();
        if (!(type.equals("int"))) {
            registerError(node,"The value being negated is of type "
                    + type + ", not int.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return the type of the expression
     */
    public Object visit(UnaryNotExpr node) {
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();
        if (!type.equals("boolean")) {
            registerError(node,"The not (!) operator applies only to boolean " +
                    "expressions, not " + type + " expressions.");
        }
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return the type of the expression
     */
    public Object visit(UnaryIncrExpr node) {
        if (!(node.getExpr() instanceof VarExpr)) {
            registerError(node,"The  expression being incremented can only be " +
                    "a variable name with an optional \"this.\" or \"super.\" prefix.");
        }
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();
        if (!(type.equals("int"))) {
            registerError(node,"The value being incremented is of type "
                    + type + ", not int.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return the type of the expression
     */
    public Object visit(UnaryDecrExpr node) {
        if (!(node.getExpr() instanceof VarExpr)) {
            registerError(node,"The  expression being incremented can only be " +
                    "a variable name with an optional \"this.\" or \"super.\" prefix.");
        }
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();
        if (!(type.equals("int"))) {
            registerError(node,"The value being decremented is of type "
                    + type + ", not int.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return the type of the expression
     */
    public Object visit(ConstIntExpr node) {
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return the type of the expression
     */
    public Object visit(ConstBooleanExpr node) {
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return the type of the expression
     */
    public Object visit(ConstStringExpr node) {
        node.setExprType("String");
        return null;
    }

}
