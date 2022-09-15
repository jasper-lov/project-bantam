/*
    File: Controller.java
    Names: Jasper Loverude, Dylan Tymkiw, Cassidy Correll
    Class: CS 361
    Project 10
    Date: May 6
*/

package proj10LoverudeTymkiwCorrell;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.jfoenix.controls.JFXDrawer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import proj10LoverudeTymkiwCorrell.bantam.ast.Program;
import proj10LoverudeTymkiwCorrell.bantam.parser.Parser;
import proj10LoverudeTymkiwCorrell.bantam.semant.SemanticAnalyzer;
import proj10LoverudeTymkiwCorrell.bantam.util.CompilationException;
import proj10LoverudeTymkiwCorrell.bantam.util.Error;
import proj10LoverudeTymkiwCorrell.bantam.util.ErrorHandler;
import proj10LoverudeTymkiwCorrell.bantam.transpiler.Transpiler;


/**
 * Controller class contains handler methods for buttons and menu items.
 */
public class Controller {

    @FXML private StyleClassedTextArea ideConsole;
    @FXML private Button transpileButton, transpileRunButton, stopButton, checkButton;
    @FXML private TabPane tabPane;
    @FXML private MenuItem undoMI, redoMI;
    @FXML private MenuItem selectAllMI, cutMI, copyMI, pasteMI;
    @FXML private MenuItem saveMI, saveAsMI, closeMI;
    @FXML private MenuItem findReplaceMI;
    @FXML private Button findButton, replaceButton, replaceAllButton;
    @FXML private TitledPane consolePane;
    @FXML private JFXDrawer drawer;
    @FXML private TextField findTextField, replaceTextField;
    @FXML private ToolBar findReplaceBar;
    // list of saved tabs and their content
    private final HashMap<Tab,String> tabToContentsMap = new HashMap<>();
    // list of saved tabs and their saving path
    private final HashMap<Tab,String> tabToFilePathMap = new HashMap<>();
    //list of filepath and the corresponding tab
    private final HashMap<String,Tab> filePathToTabMap = new HashMap<>();

    // Class DialogHelper handling all dialog instantiation
    private final DialogHelper dialogHelper = new DialogHelper();
    // Class FindReplaceHelper handling find and replace operations
    private FindReplaceHelper findReplaceHelper;

    //Stores the process thread
    private Thread processThread = null;

    //BooleanProperty to determine if the thread is active
    private SimpleBooleanProperty isThreadActive = new SimpleBooleanProperty(false);

    //Boolean to keep track of whether we are on light or dark mode
    private Boolean isDarkMode = false;

    //Controller for our drawer
    private FolderDrawerController treeController;

    //Map holding characters to autocomplete
    private final HashMap<String,String> autoCompleteCharacterMap = new HashMap<>() {
        {
            put("(",")");
            put("{","}");
            put("[","]");
        }
    };

    //Keeps track of the current file
    public static FileTracker fileTracker = new FileTracker();

    // Updated by handleCheck, tells you if the last checked file is legal or not
    private boolean isLegalBantam;

    // Creates Transpiler Object
    private Transpiler transpiler = new Transpiler();

    /**
     * Sets up listeners to disable/enable menu items +
     * connects existing close boxes to the created close MenuItems
     */
    @FXML
    private void initialize() {

        handleNew();

        StyleClassedTextArea consoleStyleClassTxtArea = new StyleClassedTextArea();
        consoleStyleClassTxtArea.setId("console");
        this.consolePane.setContent(new VirtualizedScrollPane<>(consoleStyleClassTxtArea));

        this.ideConsole = consoleStyleClassTxtArea;

        this.findReplaceHelper = new FindReplaceHelper(findTextField, replaceTextField);


        // disable appropriate menu items when no tabs are open
        closeMI.disableProperty().bind(noTabs());
        saveMI.disableProperty().bind(noTabs());
        saveAsMI.disableProperty().bind(noTabs());
        undoMI.disableProperty().bind(noTabs());
        redoMI.disableProperty().bind(noTabs());
        selectAllMI.disableProperty().bind(noTabs());
        cutMI.disableProperty().bind(noTabs());
        copyMI.disableProperty().bind(noTabs());
        pasteMI.disableProperty().bind(noTabs());
        findReplaceMI.disableProperty().bind(noTabs());

        findButton.disableProperty().bind(noTabs());
        replaceButton.disableProperty().bind(noTabs());
        replaceAllButton.disableProperty().bind(noTabs());

        // Bind compile buttons so that they are disabled when a process is running
        transpileButton.disableProperty().bind(Bindings.or(isThreadActive, noTabs()));
        transpileRunButton.disableProperty().bind(Bindings.or(isThreadActive, noTabs()));
        stopButton.disableProperty().bind(Bindings.or(isThreadActive.not(), noTabs()));
        checkButton.disableProperty().bind(Bindings.or(isThreadActive, noTabs()));

    }

    /**
     * Helper method that sets window feature logic, like the drawer and find/replace bar
     *
     */
    public void setWindowFeatures(){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FolderDrawerView.fxml"));
            VBox box = loader.load();
            this.treeController = loader.getController();
            this.treeController.setMainController(this);
            drawer.setSidePane(box);
            drawer.setMinWidth(0);
        }
        catch (IOException e){
            dialogHelper.getAlert("Error initializing", e.getMessage()).show();
        }

        findReplaceHelper = new FindReplaceHelper(findTextField, replaceTextField);
    }

    /**
     * Returns a true BooleanBinding if there are no more tabs and a false one if there
     * is at least one tab.
     *
     * @return a BooleanBinding demonstrating if there are no more tabs
     */
    private BooleanBinding noTabs() {
        return Bindings.isEmpty(tabPane.getTabs());
    }


    /**
     * Gets the currently selected tab in tabPane
     *
     * @return the selected tab
     */
    private Tab getSelectedTab () {

        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        return selectedTab;
    }

    public void setSelectedTab (Tab selectedTab) {

        tabPane.getSelectionModel().select(selectedTab);
    }

    /**
     * helper function to get the text box in the selected tab
     *
     * @return TextArea  the text box in the selected tab
     */
    private CodeArea getSelectedTextBox() {
        Tab currentTab = getSelectedTab();
        VirtualizedScrollPane scrollPane;
        scrollPane = (VirtualizedScrollPane) currentTab.getContent();
        return (CodeArea) scrollPane.getContent();
    }


    /**
     * Handles menu bar item About. Shows a dialog that contains program information.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleAbout(ActionEvent event) {
        Dialog<ButtonType> dialog = dialogHelper.getAboutDialog();
        dialog.showAndWait();
    }


    /**
     * Handles menu bar item New. Creates a new tab and adds it to the tabPane.
     */
    @FXML
    public void handleNew() {

        // calls helper method for untitled tabName generation
        String newTabName = this.getNextAvailableUntitled();

        // creates tab and sets close behavior
        Tab newTab = new Tab(newTabName);

        newTab.setOnCloseRequest(closeEvent -> {
            tabPane.getSelectionModel().select(newTab);
            handleClose(new ActionEvent());
            closeEvent.consume();
        });

        // installs toolTip
        Tooltip tabToolTip = new Tooltip(newTab.getText());
        newTab.setTooltip(tabToolTip);

        // create a code area
        HighlightedCodeArea highlightedCodeArea = new HighlightedCodeArea();
        CodeArea codeArea = highlightedCodeArea.getCodeArea();

        codeArea.setOnKeyTyped(KEY_TYPED -> {
            if(this.autoCompleteCharacterMap.containsKey(
                    KEY_TYPED.getCharacter())){
                codeArea.insertText(codeArea.getCaretPosition(),
                        this.autoCompleteCharacterMap.get(KEY_TYPED.getCharacter()));
            }
        });

        //Sets style class so it can be referenced in css
        codeArea.getStyleClass().add("code-area");

        newTab.setContent(new VirtualizedScrollPane<>(codeArea));
        // add new tab to the tabPane and sets as topmost
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().selectLast();
    }

    /**
     * Helper method that generates a string that is the lowest "Untitled-x" available.
     *
     * @return String name for newTab
     * @author Caleb Bitting, Jasper Loverude, Andy Xu
     */
    private String getNextAvailableUntitled() {

        // Stores whether "Untitled" is available in boolean, iterates through all tabs
        boolean defaultUntitledAvailable = true;

        ObservableList<Tab> allTabsList = this.tabPane.getTabs();

        for(Tab currTab : allTabsList){

            // If "Untitled", sets boolean to false,
            String currTabTitle = currTab.getText();
            if(currTabTitle.equals("Untitled")) {
                defaultUntitledAvailable = false;
                break;
            }
        }

        if(defaultUntitledAvailable) return "Untitled";

        // Iterates through every tab in hashSet, until the lowest "Untitled-x" is found
        int untitledNumber = 1;
        String lowestUntitledName = "Untitled-" + untitledNumber;

        for(int i = 0; i < allTabsList.size(); i++){

            String untitledName = allTabsList.get(i).getText();

            if(lowestUntitledName.equals(untitledName)){
                untitledNumber++;
                lowestUntitledName = "Untitled-" + untitledNumber;
                i = 0;
            }
        }
        // Returns "Untitled-x" with lowest x available
        return lowestUntitledName;

    }

    /**
     * Handles menu bar item Open. Shows a dialog and lets the user select a file to be
     * loaded into the text box.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleOpen(ActionEvent event) {
        // create a new file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Java Bantam Files", "*.btm"));
        File selectedFile = fileChooser.showOpenDialog(tabPane.getScene().getWindow());

        // if user selects a file (instead of pressing cancel button
        if (selectedFile != null) {
            openFile(selectedFile);
            if (drawer.isClosed()){
                fileTracker.setCurrentDirectory(selectedFile.getParentFile());
                drawer.open();
                drawer.setPrefWidth(200);
                System.out.println(treeController);
                treeController.setDirectory(fileTracker.getCurrentDirectory());
            }
            else if(fileTracker.getCurrentDirectory() != selectedFile.getParentFile()) {
                fileTracker.setCurrentDirectory(selectedFile.getParentFile());
                treeController.setDirectory(fileTracker.getCurrentDirectory());
            }
        }

    }


    /**
     * Helper method to open a file
     * @param (file) file to open
     * @author Dylan Tymkiw
     * */
    public void openFile(File file){

        try {
            // reads the file content to a String
            String content = new String(Files.readAllBytes(
                    Paths.get(file.getPath())));
            // open a new tab
            this.handleNew();
            // set text/name of the tab to the filename
            this.getSelectedTab().setText(file.getName());
            getSelectedTextBox().replaceText(content);
            // update tabToContentsMap field
            this.tabToContentsMap.put(getSelectedTab(), content);
            this.tabToFilePathMap.put(getSelectedTab(), file.getPath());
            this.filePathToTabMap.put(file.getPath(), getSelectedTab());
            this.getSelectedTab().getTooltip().setText(file.getPath());


        } catch (IOException e) {
            dialogHelper.getAlert("File Opening Error", e.getMessage()).show();
        }
    }


    /**
     * Handles menu bar item Close. Creates a dialog if the selected tab is unsaved and
     * closes the tab.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    public void handleClose(ActionEvent event) {
        closeSelectedTab(event, SaveReason.CLOSING);
    }


    /**
     * Helper method that closes tabs. Prompts the user to save if tab is dirty. If the
     * user chooses to save the changes, the changes are saved and the tab is closed.
     * If the tab is clean or the user chooses to save the dirty tab, the tab is closed.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     *
     * @return Optional the Optional object returned by dialog.showAndWait().
     *                  returns null if tab text is already saved.
     */
    private Optional<ButtonType> closeSelectedTab(ActionEvent event, SaveReason reason) {

        // If selectedTab is unsaved, opens dialog to ask user whether they would like to save
        if(selectedTabIsDirty()) {
            String fileName = getSelectedTab().getText();
            Dialog<ButtonType> saveDialog = dialogHelper.getSavingDialog
                    (fileName, reason);

            Optional<ButtonType> result  = saveDialog.showAndWait();
            // save if user chooses YES
            if (result.isPresent() && result.get() == ButtonType.YES) {
                this.handleSave(event);
                // Keep the tab if the save is unsuccessful (eg. canceled)
                if (selectedTabIsDirty()) {
                    return result;
                }
            }
            // quit the dialog and keep selected tab if user chooses CANCEL
            else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return result;
            }
        }
        // remove tab from tabPane if text is saved or user chooses NO
        this.tabToContentsMap.remove(getSelectedTab());
        this.filePathToTabMap.remove(this.tabToFilePathMap.get(getSelectedTab()));
        this.tabToFilePathMap.remove(getSelectedTab());
        tabPane.getTabs().remove(getSelectedTab());
        return Optional.empty();
    }


    /**
     * Handler method for menu bar item Exit. When exit item of the menu
     * bar is clicked, the application quits if all tabs in the tabPane are
     * closed properly.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    public void handleExit(ActionEvent event) {
        tabPane.getSelectionModel().selectLast();
        while (tabPane.getTabs().size() > 0) {
            // try close the currently selected tab
            Optional<ButtonType> result = closeSelectedTab(event, SaveReason.EXITING);
            // if the user chooses Cancel at any time, then the exiting is canceled,
            // and the application stays running.
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                return;
            }
        }
        // exit if all tabs are closed
        System.exit(0);
    }


    /**
     * Helper method that checks if the text in the selected tab is saved.
     *
     * @return boolean whether the text in the selected tab is dirty (unsaved changes).
     */
    private boolean selectedTabIsDirty() {

        // Gets current contents of tab and its hashed contents (Null if unsaved)
        String currentContents = getSelectedTextBox().getText();
        Tab selectedTab = this.tabPane.getSelectionModel().getSelectedItem();
        String savedContent = this.tabToContentsMap.get(selectedTab);

        // If no saved contents, and tab is empty, contents not dirty
        if(savedContent == null && currentContents.equals("")) {
            return false;
        }
        // If no saved contents, but code area not empty, contents are dirty
        else if(savedContent == null) {
            return true;
        }
        // Otherwise, returns false (not dirty) if contents equal, or true if they aren't
        else return !savedContent.equals(currentContents);

    }

    /**
     * Handler method for menu bar item Save. Behaves like Save as... if the text
     * has never been saved before. Otherwise, save the text to its corresponding
     * text file.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     *
     * @return whether the save was successful
     */
    @FXML
    private boolean handleSave(ActionEvent event) {
        // if the text has been saved before
        if (tabToContentsMap.containsKey(getSelectedTab())) {
            // create a File object for the corresponding text file
            File savedFile = new File(tabToFilePathMap.get(getSelectedTab()));
            try {
                // write the new content to the text file
                FileWriter writer = new FileWriter(savedFile);
                writer.write(getSelectedTextBox().getText());
                writer.close();
                // update tabToContentsMap field
                tabToContentsMap.put(getSelectedTab(), getSelectedTextBox().getText());

                return true;
            } catch (IOException e) {
                dialogHelper.getAlert("File Saving Error", e.getMessage()).show();

                return false;
            }
        }
        // if text in selected tab was not loaded from a file nor ever saved to a file
        else {
            return handleSaveAs(event);
        }
    }

    /**
     * Handles menu bar item Save as....  a dialog appears in which the user is asked for
     * to save a file with four permitted extensions: .java, .txt, .fxml, and .css.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     *
     * @return whether the save was successful
     */
    @FXML
    private boolean handleSaveAs(ActionEvent event) {
        // create a new fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Java Bantam Files",
                    "*.btm"));
        File fileToSave = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
        // if user did not choose CANCEL
        if (fileToSave != null) {
            try {
                // save file
                FileWriter fw = new FileWriter(fileToSave);
                fw.write(this.getSelectedTextBox().getText());
                fw.close();
                // update tabToContentsMap field and tab text
                this.tabToContentsMap.put(getSelectedTab(), getSelectedTextBox()
                        .getText());
                this.tabToFilePathMap.put(getSelectedTab(), fileToSave.getPath());
                this.filePathToTabMap.put(fileToSave.getPath() ,getSelectedTab());
                this.getSelectedTab().setText(fileToSave.getName());
                this.getSelectedTab().getTooltip().setText(fileToSave.getPath());

                return true;
            } catch ( IOException e ) {
                dialogHelper.getAlert("File Saving Error", e.getMessage()).show();

                return false;
            }
        }
        return false;
    }

    /**
     * Handler method for menu bar item Undo.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleUndo(ActionEvent event) {
        getSelectedTextBox().undo();
    }

    /**
     * Handler method for menu bar item Redo.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleRedo(ActionEvent event) {
        getSelectedTextBox().redo();
    }

    /**
     * Handler method for menu bar item Cut.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleCut(ActionEvent event) {
        getSelectedTextBox().cut();
    }

    /**
     * Handler method for menu bar item Copy.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleCopy(ActionEvent event) {
        getSelectedTextBox().copy();
    }

    /**
     * Handler method for menu bar item Paste.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handlePaste(ActionEvent event) {
        getSelectedTextBox().paste();
    }

    /**
     * Handler method for menu bar item Select all.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleSelectAll(ActionEvent event) {
        getSelectedTextBox().selectAll();
    }

    /**
     * Handler method for Find and Replace menu bar item
     */
    @FXML
    private void handleFindReplace(){
        findReplaceBar.setVisible(true);
    }

    /**
     * Toggles between light and dark mode on the GUI
     */
    @FXML
    private void handleDarkMode() {
        Scene scene = tabPane.getScene();
        ObservableList<String> stylesheets = scene.getStylesheets();
        try {
            if (!isDarkMode) {
                stylesheets.add(getClass().getResource("darkmode-view.css")
                        .toExternalForm());
                stylesheets.remove(getClass().getResource("Main.css")
                        .toExternalForm());
                isDarkMode = true;
            } else {
                stylesheets.add(getClass().getResource("Main.css").toExternalForm());
                stylesheets.remove(getClass().getResource("darkmode-view.css").
                        toExternalForm());
                isDarkMode = false;
            }
        } catch (NullPointerException npe) {
            dialogHelper.getAlert("No CSS File Found", npe.getMessage()).show();
        }
    }

    /**
     * Handler method for Compile button.
     * If the tab is dirty, asks user to save. If user chooses to save, the changes are
     * saved and the tab is compiled. If user chooses not to save, the currently saved
     * version of the file is compiled (the unsaved changes are ignored). If the user
     * cancels the dialog, no compilation is performed.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleTranspile(ActionEvent event) throws InterruptedException {
        compileTab(event);
    }

    /**
     * Checks file and saves if dirty.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     * @return a boolean that indicates whether the file was saved.
     */
    private boolean saveIfDirty(ActionEvent event){
        if (selectedTabIsDirty() || (this.tabToFilePathMap.get(getSelectedTab()) == null)){
            // Creates new dialog
            Dialog<ButtonType> saveDialog = dialogHelper
                    .getSavingDialog(getSelectedTab().getText(), SaveReason.COMPILING);
            Optional<ButtonType> result = saveDialog.showAndWait();
            // call handleSave() if user chooses YES
            if (result.isPresent() && result.get() == ButtonType.YES) {
                boolean saved = handleSave(event);
                if (!saved) {
                    String body = "Compilation was canceled because you " +
                            "aborted saving the file";
                    dialogHelper.getAlert("Compilation Canceled", body).show();
                    event.consume();
                    return false;
                }
            }
            // No compilation if user chooses CANCEL
            else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                event.consume();
                return false;
            }
            else if (result.isPresent() && result.get() == ButtonType.NO) {
                // if user chooses NO and the current tab is not saved before
                if (!tabToFilePathMap.containsKey(getSelectedTab())) {
                    // make an alert box
                    String body = "Current tab has not been saved. Pleas save before compiling.";
                    dialogHelper.getAlert("Unable to Compile", body).show();
                    event.consume();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method for handleTranspile().
     *
     * @see #handleTranspile(ActionEvent)
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     * @return a boolean that indicates whether the compilation was successful.
     */
    private boolean compileTab(ActionEvent event) {
        // Saves file if dirty, returns false if file not saved
        if(!saveIfDirty(event)){
            return false;
        }
        handleCheck(event);
        if(!isLegalBantam){
            return false;
        }

        String filepath = this.tabToFilePathMap.get(getSelectedTab());
        String filepathToCompile = transpiler.transpile(filepath);

        // new process builder for compilation
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("javac", filepathToCompile);

        try {
            Process process = processBuilder.start();
            // if an error occurs
            if ( process.getErrorStream().read() != -1 ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (process.getErrorStream()));
                String line = reader.readLine();
                while (line != null) {
                    this.ideConsole.appendText(line + "\n");
                    line = reader.readLine();
                }
            }
            int exitValue = 0;
            try {
                exitValue = process.waitFor();
            } catch (InterruptedException e) {
                dialogHelper.getAlert("Compilation Failed", e.getMessage()).show();
            }
            // if compilation process exits successfully
            if ( exitValue == 0 ) {
                this.ideConsole.appendText("\nCompilation was successful.\n");
            }
        }
        catch (IOException ex) {
            dialogHelper.getAlert("Compilation Failed", ex.getMessage()).show();
        }


        File transpiledJavaFile = new File(filepathToCompile.replace
                (".btm", ".java"));
        openFile(transpiledJavaFile);
        this.tabToFilePathMap.put(getSelectedTab(), filepathToCompile.replace
                (".btm", ".java"));

        return true;
    }

    /**
     * Handler method for Compile & Run button.
     *
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleTranspileRun(ActionEvent event){
        // run the program if compilation was successful
        if (this.compileTab(event)) {

            String fullpath = this.tabToFilePathMap.get(getSelectedTab());
            String classpath = fullpath.substring(0, fullpath.lastIndexOf("/"));
            String classname = "Main";

            // new process builder for running with java interpreter
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("java", "-cp", classpath, classname);

            // prepare running in a new thread
            processThread = new Thread(() -> {
                try {
                    Process process = processBuilder.start();

                    // get outStream and inStream
                    sendInputFromConsoleToStream(this.ideConsole,
                            process.getOutputStream());
                    sendInputFromStreamToConsole(this.ideConsole,
                            process.getInputStream());

                    // if compilation process exits successfully
                    process.waitFor();
                    Platform.runLater(() -> {
                        this.ideConsole.appendText(String.format
                                ("\nProcess finished with exit code %d.\n",
                                        process.exitValue()));
                    });
                }
                catch (InterruptedException | IOException e) {
                    Platform.runLater(() -> {
                        dialogHelper.getAlert("Runtime Error", e.getMessage()).show();
                    });
                }
                // after the thread
                // is done running, it should set the internal field back to null so that
                // the bindings can recognize that there is no process running
                this.processThread = null;
                this.isThreadActive.set(false);
            });
            this.isThreadActive.set(true);
            this.processThread.start();

        }
    }

    /**
     * Handles Find button from find and replace toolbar. in the currently selected tab
     * it selects the next instance of the string in the find TextField if the tab is not
     * empty
     */
    @FXML
    private void handleFindButton() {

        findReplaceHelper.handleFind(getSelectedTextBox());

    }

    /**
     * Handles replace button in the find and replace toolbar. Replaces the next instance
     * of the string in the find TextField with the string in the replace TextField
     */
    @FXML
    private void handleReplace(){

        if(getSelectedTextBox().getText().equals(""))
            return;

        findReplaceHelper.handleReplace(getSelectedTextBox());
    }

    /**
     * Handles the Replace All button in the find and replace box. Replaces all instances
     * of the string from the find TextField with the string from the replace TextField
     */
    @FXML
    private void handleReplaceAll(){

        if(getSelectedTextBox().getText().equals(""))
            return;

        findReplaceHelper.handleReplaceAll(getSelectedTextBox());
    }

    /**
     * Handles the Close button in the find and replace box. Closes the find and replace
     * box.
     */
    @FXML
    private void handleFRClose(){
        findReplaceBar.setVisible(false);
    }

    /**
     * Handles the stop button. Forcefully stops the thread and resets the processThread
     * to null.
     * @param event An ActionEvent object that gives information about the event
     *              and its source.
     */
    @FXML
    private void handleStop(ActionEvent event) {
        if (processThread != null) {
            processThread.interrupt();
            this.isThreadActive.set(false);
            processThread = null;
        }
    }

    /**
     * Handles the check button. Initializes and calls all parts of the bantam
     * checker
     *
     * @param event An ActionEvent object that gives information about the event
     * and its source.
     */
    @FXML
    private void handleCheck(ActionEvent event) {

        // Saves file if dirty, returns false if file not saved
        if(!saveIfDirty(event)){
            return;
        }

        //get file name
        String filepath = this.tabToFilePathMap.get(getSelectedTab());

        //create error handler
        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);

        try {
            Program program = parser.parse(filepath);
            analyzer.analyze(program);
            try{
                //send message to Console
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write("\nChecking was successful".getBytes());
                byte[] bytes = baos.toByteArray();
                InputStream ioStream = new ByteArrayInputStream(bytes);
                sendInputFromStreamToConsole(this.ideConsole, ioStream);
                isLegalBantam = true;
            }
            catch(Exception ioEx){
                dialogHelper.getAlert("Compile Error", ioEx.getMessage()).show();
            }
        } catch (CompilationException ex) {
            //Print errors to console
            isLegalBantam = false;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try{
                baos.write("\nThere were errors:".getBytes());
            }
            catch(Exception eX){
                dialogHelper.getAlert("Compile Error", eX.getMessage()).show();
            }

            List<Error> errors = errorHandler.getErrorList();
            try{
                for (Error error : errors) {
                    baos.write(error.toString().getBytes());
                    byte[] bytes = baos.toByteArray();
                    InputStream ioStream = new ByteArrayInputStream(bytes);
                    sendInputFromStreamToConsole(this.ideConsole, ioStream);
                }
            }
            catch(Exception ioEx){
                dialogHelper.getAlert("Compile Error", ioEx.getMessage()).show();
            }

        }
    }

    /**
     * Getter method thar returns TabToFile HashMap.
     *
     * @return boolean whether the text in the selected tab is dirty (unsaved changes).
     */
    public HashMap<Tab,String> getTabToFilePathMap(){

        return this.tabToFilePathMap;

    }


    /**
     * Getter method thar returns TabToFile HashMap.
     *
     * @return boolean whether the text in the selected tab is dirty (unsaved changes).
     */
    public HashMap<String, Tab> getfilePathToTabMap(){

        return this.filePathToTabMap;

    }

    /**
     * gets input typed into the Console and writes it to the given OutputStream
     * The characters are written to the OutputStream when \r or \n are typed.
     *
     * @param ioConsole    the StyleClassedTextArea whose input is sent to the stream
     * @param outputStream the OutputStream where the Console input is sent
     */
    public void sendInputFromConsoleToStream(StyleClassedTextArea ioConsole,
                                             OutputStream outputStream) {

        ioConsole.setOnKeyTyped(new EventHandler<>()
        {
            String result = ""; // the text to sent to the stream

            @Override
            public void handle(KeyEvent event) {
                String ch = event.getCharacter();
                result += ch;
                if (ch.equals("\r") || ch.equals("\n")) {
                    try {
                        for (char c : result.toCharArray()) {
                            outputStream.write(c);
                        }
                        outputStream.flush();
                        result = "";
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                                "Could not send input to the output stream."));
                    }
                }
            }

        });
    }

    /**
     * gets the input from an InputStream and writes it continuously to the ioConsole.
     *
     * @param ioConsole   the StyleClassedTextArea where the data is written
     * @param inputStream the InputStream providing the data to be written
     */
    public void sendInputFromStreamToConsole(StyleClassedTextArea ioConsole,
                                             InputStream inputStream) throws IOException {
        // for a discussion of how to convert inputStream data to strings, see
        // [stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string]

        // print to the new std.out while the program is running
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            String result = new String(buffer, 0, length);
            Platform.runLater(() -> {
                ioConsole.appendText(result);
                ioConsole.moveTo(ioConsole.getLength()); //move cursor to the end
                ioConsole.requestFollowCaret();
            });
        }
    }
}
