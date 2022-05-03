package uk.aber.ac.keg21.musicapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController{

    private Stage stage;
    
    public File library;
    
    private MediaPlayer player1;
    
    @FXML
    public Button songsButton;
    
    @FXML
    public Label pathLabel;
    
    @FXML 
    public Button browseButton;
    
    @FXML
    public Button albumButton;
    
    @FXML
    public Button scanButton;
    
    @FXML
    public Button artistButton;
    
    PlayerController playerController = new PlayerController();
    
    public void songButton(ActionEvent actionEvent) throws IOException {
        playerController.songButton(stage, player1, songsButton);
    }
    
    public void albumButton(ActionEvent actionEvent) throws IOException {
        playerController.albumButton(stage, player1, albumButton);
    }

    public void artistButton(ActionEvent actionEvent) throws IOException {
        playerController.artistButton(stage, player1, artistButton);
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
}
