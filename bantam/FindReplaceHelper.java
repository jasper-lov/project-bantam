package proj10LoverudeTymkiwCorrell;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.CodeArea;

public class FindReplaceHelper {
    private int lastFound;
    @FXML private final TextField findTextField;
    @FXML private final TextField replaceTextField;
    @FXML private CodeArea codeArea;

    /**
     * Handles the stop button. Forcefully stops the thread and resets the processThread
     * to null.
     * @param ftf, rft, Textfields that represent the text to be replaced.
     */
    public FindReplaceHelper(TextField ftf, TextField rtf){
        findTextField = ftf;
        replaceTextField = rtf;
        lastFound = 0;
    }


    /**
     * Finds the string stored in FindString in the give CodeArea
     *
     * @param ca a CodeArea
     * @author Cassidy Correl, Dylan Tymkiw
     * */
    public void handleFind(CodeArea ca){

        String findString = findTextField.getText();


        //If tab is changed start find from top
        if(codeArea != ca){
            codeArea = ca;
            lastFound = 0;
        }

        //Starts at begining of string if reaches end
        // Could add as an || to above conditional
        if(lastFound >= codeArea.getText().length()){
            lastFound = 0;
        }

        lastFound = codeArea.getText().indexOf(findString, lastFound) + 1;

        if(lastFound != 0){
            lastFound = lastFound -1;
        }
        ca.moveTo(lastFound);
        ca.selectWord();
    }

    /**
     * Replaces the string containing the string in the find box with the string in the replace box.
     *
     * @param ca a CodeArea
     * @author Cassidy Correl, Dylan Tymkiw
     * */
    public void handleReplace(CodeArea ca){
        String replaceString = replaceTextField.getText();
        handleFind(ca);
        ca.replaceSelection(replaceString);
    }

    /**
     * Finds the string stored in FindString in the given CodeArea and replaces it with the string in the replace box
     *
     * @param ca a CodeArea
     * @author Cassidy Correl, Dylan Tymkiw
     * */
    public void handleReplaceAll(CodeArea ca){
        String replacedText = ca.getText().replaceAll(
                findTextField.getText(), replaceTextField.getText());

        handleFind(ca);
        ca.replaceText(replacedText);
    }



}
