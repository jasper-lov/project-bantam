/*
    File: FolderDrawerController
    Names: Jasper Loverude, Dylan Tymkiw, Cassidy Correll
    Class: CS 361
    Project 10
    Date: March 18
*/

package proj10LoverudeTymkiwCorrell;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ResourceBundle;




/**
 * Helper class for folder drawer
 * @author Jasper Loverude, Dylan Tymkiw
 * */
public class FolderDrawerController implements Initializable {

    @FXML private TreeView<String> tree;
    private Controller controller;
    private File currentDirectory;

    public void initialize(URL url, ResourceBundle rb){
        // activate mouse click behavior
        handleMouse();
    }


    /**
     * This method sets the directory of the tree
     * @author Dylan Tymkiw
     * */
    public void setDirectory(File file){

        treeCreate(file);

        currentDirectory = Controller.fileTracker.getCurrentDirectory();
    }

    /**
     * Creates a file tree of the given directory
     * @param (dir) the initial directory
     * @author Dylan Tymkiw
     * */
    private void treeCreate(File dir){
        TreeItem<String> root = new TreeItem<String>(dir.getName());
        FilenameFilter javaFileFilter = (dir1, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".btm");

        };

        File[] files = dir.listFiles(javaFileFilter);
        for(int i=0; i<files.length; i++) {

            if (!(files[i] == null) && !files[i].isHidden() && files[i].isFile()) {
                TreeItem<String> child = new TreeItem<>(files[i].getName());
                root.getChildren().add(child);
            }
        }
        tree.setRoot(root);
        root.setExpanded(true);
    }

    @FXML
    /**
     * Sets behavior for when the mouse is clicked
     * @author Dylan Tymkiw
     * */
    private void handleMouse(){
        tree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String currentFileName =
                        tree.getSelectionModel().getSelectedItem().getValue();

                // If user clicks on the directory,
                if(currentFileName.equals(currentDirectory.getName())){
                    return;
                }

                String currentFilePath =
                        Controller.fileTracker.getCurrentDirectory() + "/" + currentFileName;

                // Checks if file is already open
                if(this.controller.getTabToFilePathMap().containsValue(currentFilePath)){
                    this.controller.setSelectedTab(this.controller.getfilePathToTabMap().get(currentFilePath));
                    return;
                }

                File fileToOpen = new File(currentFilePath);

                this.controller.openFile(fileToOpen);
            }
        });

    }
    /**
     * Ensures parent class knows about main controller
     * @param (mainController) the main controller
     * */
    public void setMainController(Controller mainController){

        this.controller = mainController;

    }


}

