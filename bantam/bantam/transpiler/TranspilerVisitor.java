/*
    File: TranspilerVisitor
    Names: Jasper Loverude, Dylan Tymkiw, Cassidy Correll
    Class: CS 361
    Project 10
    Date: May 2nd
*/

package proj10LoverudeTymkiwCorrell.bantam.transpiler;

import proj10LoverudeTymkiwCorrell.bantam.ast.*;
import proj10LoverudeTymkiwCorrell.bantam.visitor.Visitor;

import java.util.Iterator;

public class TranspilerVisitor extends Visitor {

    // StringBuilder that appends legal java code to the builder.
    private StringBuilder programStringBuilder;

    // StringBuilder that keeps track of currentIdentation
    private StringBuilder indentationStringBuilder;

    public TranspilerVisitor(){

        super();

        programStringBuilder = new StringBuilder();
        indentationStringBuilder = new StringBuilder();

    }

    /**
     * Gets the StringBuilder for the current indentation
     *
     * @return indentationStringBuilder, that contains current indentation
     */
    private StringBuilder getIndentationStringBuilder(){

        return indentationStringBuilder;

    }

    /**
     * Visit a list node of classes
     *
     * @param node the class list node
     * @return result of the visit
     */
    public Object visit(ClassList node) {
        for (ASTNode aNode : node) {
            aNode.accept(this);
            // Places 2 lines between each java class
            programStringBuilder.append("\n\n\n");
        }
        return null;
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    @Override
    public Object visit(Class_ node) {

        programStringBuilder.append("class ").append(node.getName());

        // adds "extends (parentName)" if class has parent other than Object
        if(!node.getParent().equals("Object")){
            programStringBuilder.append(" extends ").append(node.getParent());
        }

        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a MemberList node
     *
     * @param node the MemberList node
     * @return result of the visit
     */
    @Override
    public Object visit(MemberList node) {

        // Conditional ensures that a class with no members has { } on same line
        boolean listHasMembers = (node.getSize() > 0);
        programStringBuilder.append("{ ");

        if(listHasMembers){
            programStringBuilder.append("\n");
        }
        // Increased indentation for writing members of the class
        indentationStringBuilder.append("    ");
        for(ASTNode child : node){
            child.accept(this);
        }
        indentationStringBuilder.delete(0, 4);

        if(listHasMembers){
            programStringBuilder.append("\n").append(getIndentationStringBuilder());
        }
        programStringBuilder.append("}");

        return null;

    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    @Override
    public Object visit(Field node) {
        programStringBuilder.append("\n").append(getIndentationStringBuilder());
        // adds "protected" modifier to all fields
        programStringBuilder.append("protected ");
        programStringBuilder.append(node.getType()).append(" ").append(node.getName());

        if (node.getInit() != null) {
            programStringBuilder.append(" = ");
            node.getInit().accept(this);
        }
        programStringBuilder.append(";");
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    @Override
    public Object visit(Method node) {

        // Puts one whitespace line between all methods
        programStringBuilder.append("\n\n").append(getIndentationStringBuilder());

        // Considered handling all methods the same way, and appending static and args
        // only for main methods, however this made the code much less readable than
        // this approach, so we just handled special case here
        if (node.getName().equals("main")){
            programStringBuilder.append("public static void main(String[] args)");
        }
        else {
            programStringBuilder.append("public ")
                    .append(node.getReturnType())
                    .append(" ")
                    .append(node.getName());
            programStringBuilder.append("(");
            node.getFormalList().accept(this);
            programStringBuilder.append(")");
        }
        node.getStmtList().accept(this);
        return null;
    }

    /**
     * Visit a list node of statements
     *
     * @param node the statement list node
     * @return result of the visit
     */
    @Override
    public Object visit(StmtList node) {

        // We implemented { } appending in this visit, rather than BlockStmt
        // (which this visitor does not visit), because all BlockStmts
        // contain StmtLists, but thing like classes (with { }) do not contain BlockStmts
        programStringBuilder.append("{");
        // Increase indentation for all contents of StmtList.
        getIndentationStringBuilder().append("    ");

        for (Iterator it = node.iterator(); it.hasNext(); )
            ((Stmt) it.next()).accept(this);

        getIndentationStringBuilder().delete(0, 4);
        programStringBuilder.append("\n").
                append(getIndentationStringBuilder());
        programStringBuilder.append("}");
        return null;
    }

    /**
     * Visit a list node of Formals
     *
     * @param node the statement list node
     * @return result of the visit
     */
    @Override
    public Object visit(FormalList node) {

        for (Iterator it = node.iterator(); it.hasNext(); ) {
            Formal formal = (Formal) it.next();
            formal.accept(this);
            if(it.hasNext()){
                programStringBuilder.append(", ");
            }
        }
        return null;
    }

    /**
     * Visit a formal node
     *
     * @param node the formal node
     * @return result of the visit
     */
    @Override
    public Object visit(Formal node) {

        programStringBuilder.append(node.getType())
                .append(" ")
                .append(node.getName());
        return null;
    }


    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    @Override
    public Object visit(DeclStmt node) {

        programStringBuilder.append("\n")
                .append(getIndentationStringBuilder())
                .append("var ")
                .append(node.getName())
                .append(" = ");
        node.getInit().accept(this);
        programStringBuilder.append(";");
        return null;
    }

    /**
     * Visit an expression statement node
     *
     * @param node the expression statement node
     * @return result of the visit
     */
    @Override
    public Object visit(ExprStmt node) {

        programStringBuilder.append("\n").append(getIndentationStringBuilder());
        node.getExpr().accept(this);
        programStringBuilder.append(";");
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    @Override
    public Object visit(IfStmt node) {

         programStringBuilder.append("\n")
                .append(getIndentationStringBuilder())
                .append("if(");
         node.getPredExpr().accept(this);
         programStringBuilder.append(")");
         node.getThenStmt().accept(this);
         if (node.getElseStmt() != null) {
             programStringBuilder.append("else");
             node.getElseStmt().accept(this);
         }
         return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    @Override
    public Object visit(WhileStmt node) {

        programStringBuilder.append("\n")
                .append(getIndentationStringBuilder())
                .append("while(");
        node.getPredExpr().accept(this);
        programStringBuilder.append(")");
        node.getBodyStmt().accept(this);
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    @Override
    public Object visit(ForStmt node) {
        programStringBuilder.append("\n")
                .append(getIndentationStringBuilder())
                .append("for(");
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        programStringBuilder.append("; ");
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        }
        programStringBuilder.append("; ");
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        programStringBuilder.append(")");
        node.getBodyStmt().accept(this);
        return null;
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    @Override
    public Object visit(BreakStmt node) {

        programStringBuilder.append("\n")
                .append(getIndentationStringBuilder())
                .append("break;");

        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    @Override
    public Object visit(ReturnStmt node) {

        programStringBuilder.append("\n")
                .append(getIndentationStringBuilder())
                .append("return");
        // Visits the expr, if this returnStmt has one.
        if(node.getExpr() != null){
            programStringBuilder.append(" ");
            node.getExpr().accept(this);
        }
        programStringBuilder.append(";");
        return null;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return result of the visit
     */
    @Override
    public Object visit(DispatchExpr node) {

        if(node.getRefExpr() != null){
            node.getRefExpr().accept(this);
            programStringBuilder.append(".");
        }
        /* INELEGANCY ALERT: our semantic analyzer does not properly handle
         (Object).method() calls. Therefor, we made a reserved print()
         and translated it in this way. Not pretty- in order to get
         this to work I added a print(String string); method to Object,
         allowing print() to be an inherited legal method of all classes,
         and therefor always semantically legal. */
        if(node.getMethodName().equals("print")){
            programStringBuilder.append("System.out.println");
        }
        else{
            programStringBuilder.append(node.getMethodName());
        }
        programStringBuilder.append("(");
        node.getActualList().accept(this);
        programStringBuilder.append(")");
        return null;
    }

    /**
     * Visit a list node of Exprs
     *
     * @param node the expression list node
     * @return result of the visit
     */
    @Override
    public Object visit(ExprList node) {
        for (Iterator it = node.iterator(); it.hasNext(); ) {
            ((Expr) it.next()).accept(this);
            if(it.hasNext()){
                programStringBuilder.append(", ");
            }
        }
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    @Override
    public Object visit(NewExpr node) {

        // Our implementation does not allow the user to define a constructor for objects
        // hence the lack of parameter checking.
        programStringBuilder.append("new ")
                .append(node.getType())
                .append("()");
        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return result of the visit
     */
    @Override
    public Object visit(InstanceofExpr node) {

        node.getExpr().accept(this);
        programStringBuilder.append(" instanceof ")
                .append(node.getType());
        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    @Override
    public Object visit(CastExpr node) {

        programStringBuilder.append("(")
                .append(node.getType())
                .append(") ");
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    @Override
    public Object visit(AssignExpr node) {

        if(node.getRefName() != null){
            programStringBuilder.append(node.getRefName())
                    .append(".");
        }
        programStringBuilder.append(node.getName())
                .append(" = ");
        node.getExpr().accept(this);
        return null;
    }


    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryCompEqExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" == ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryCompNeExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" != ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryCompLtExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" < ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryCompLeqExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" <= ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryCompGtExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" > ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryCompGeqExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" >= ");
        node.getRightExpr().accept(this);
        return null;
    }


    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryArithPlusExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" + ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryArithMinusExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" - ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryArithTimesExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" * ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryArithDivideExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" / ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryArithModulusExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" % ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryLogicAndExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" && ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    @Override
    public Object visit(BinaryLogicOrExpr node) {

        node.getLeftExpr().accept(this);
        programStringBuilder.append(" || ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    @Override
    public Object visit(UnaryNegExpr node) {

        programStringBuilder.append("-");
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    @Override
    public Object visit(UnaryNotExpr node) {

        programStringBuilder.append("!");
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    @Override
    public Object visit(UnaryIncrExpr node) {

        if (!node.isPostfix()){
            programStringBuilder.append("++");
            node.getExpr().accept(this);
        }
        else{
            node.getExpr().accept(this);
            programStringBuilder.append("++");
        }

        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    @Override
    public Object visit(UnaryDecrExpr node) {

        if (!node.isPostfix()){
            programStringBuilder.append("--");
            node.getExpr().accept(this);
        }
        else{
            node.getExpr().accept(this);
            programStringBuilder.append("--");
        }
        return null;
    }

    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return result of the visit
     */
    @Override
    public Object visit(VarExpr node) {

        if (node.getRef() != null) {
            node.getRef().accept(this);
            programStringBuilder.append(".");
        }
        programStringBuilder.append(node.getName());
        return null;
    }


    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    @Override
    public Object visit(ConstIntExpr node) {

        programStringBuilder.append(node.getIntConstant());
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    @Override
    public Object visit(ConstBooleanExpr node) {

        programStringBuilder.append(node.getConstant());
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return result of the visit
     */
    @Override
    public Object visit(ConstStringExpr node) {

        programStringBuilder.append(node.getConstant());
        return null;
    }

    public StringBuilder getProgramStringBuilder(){
        return programStringBuilder;
    }

    public void clearProgramStringBuilder(){ programStringBuilder = new StringBuilder(); }
}
