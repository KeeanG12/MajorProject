package uk.aber.ac.keg21.musicapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private Stage stage;
    
    @FXML
    public Button songsButton;
    
    public void songButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"));
        stage = (Stage) songsButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 800));
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }
}
