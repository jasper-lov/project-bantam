/*
 * File: Controller.java
 * Names: Cassidy Corral, Dylan Tymkiw, Jasper Loverude
 * Class: CS 361
 * Project 5
 * Date: March 7
 */

package proj10LoverudeTymkiwCorrell;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * Main class that sets up the stage.
 */
public class Main extends Application {

    /**
     * Main method of the program that calls {@code launch} inherited from the
     * Application class
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the contents of the starting window.
     *
     * @param primaryStage A Stage object that is created by the {@code launch}
     *                     method inherited from the Application class.
     */
    @Override
    public void start(Stage primaryStage){
        try {
            // Load fxml file

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            Controller controller = fxmlLoader.getController();
            controller.setWindowFeatures();

            // Trigger close menu item handler when tab is closed, consumes event
            primaryStage.setOnCloseRequest((WindowEvent we) -> {
                we.consume();
                controller.handleExit(new ActionEvent());
            });


            // Load css files
            ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.add(getClass().getResource("Main.css").toExternalForm());
            stylesheets.add(getClass().getResource("java-keywords.css").toExternalForm());
            primaryStage.setScene(scene);


            // Set the minimum height and width of the main stage
            primaryStage.setMinHeight(600);
            primaryStage.setMinWidth(800);
            primaryStage.setTitle("Project 10: A Bantam Editor and Transpiler");


            // Show the stage
            primaryStage.show();
        }
        catch (NullPointerException | IOException e){
            DialogHelper tempDialogHelper = new DialogHelper();
            tempDialogHelper.getAlert("Application Start Error", e.getMessage()).show();
        }
    }
}
