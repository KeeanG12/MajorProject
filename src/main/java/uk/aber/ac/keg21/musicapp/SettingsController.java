package uk.aber.ac.keg21.musicapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private Stage stage;
    
    public File library;
    
    @FXML
    public Button songsButton;
    
    @FXML
    public Label pathLabel;
    
    @FXML 
    public Button browseButton;
    
    @FXML
    public Button scanButton;
    
    public void songButton(ActionEvent actionEvent) throws IOException {
        
        Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"));
        stage = (Stage) songsButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);
        
    }
    
    public void browseButton(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(stage);
        
        pathLabel.setText(file.getAbsolutePath());
        
        library = file;
        
    }
    
    public void scanButton(ActionEvent actionEvent) throws IOException {
        Database music = Database.getInstance();
        music.delete();
        music.rescan(library);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }
}
