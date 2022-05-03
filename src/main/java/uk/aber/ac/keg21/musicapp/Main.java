package uk.aber.ac.keg21.musicapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    
    
    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"));
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    
    public static void main(String[] args) throws IOException {
        launch(args);
        Database music = Database.getInstance();
        
    }
}
