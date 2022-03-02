package uk.aber.ac.keg21.musicapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    private Stage stage;

    @FXML
    public TableView tableView;
    
    @FXML
    public TableColumn colSongID;
    
    @FXML
    public TableColumn colTitle;
    
    @FXML
    public TableColumn colArtistName;

    @FXML
    public TableColumn colAlbumName;
    
    @FXML
    public TableColumn colDuration;
    
    @FXML
    public Button playPauseButton;
    
    @FXML
    public Label currentSong;

    @FXML
    public MediaPlayer player;
    
    @FXML
    public Button settingsButton;
    
    
    private boolean isPaused = true;
    private boolean isPlaying;
    private boolean isEnd;
    

    @FXML
    public void playPauseButton(ActionEvent actionEvent) throws URISyntaxException {
        //Getting the selection model to know which cell the user selects
        TableView.TableViewSelectionModel<SongDataModel> selectionModel = tableView.getSelectionModel();
        
        //Setting selection mode to single, so they can only select a single cell
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        SongDataModel selected = selectionModel.getSelectedItem();
        
        //Finding the directory path from the selected cell data
        String artist = selected.getArtistName();
        currentSong.setText(artist + " - " + selected.getName());
        

        //Uses JavaFX-Media to play a MP3 file when button is clicked
        String path = selected.getFilepath();
        Media sound = new Media(new File(path).toURI().toString());
        player = new MediaPlayer(sound);
        
        if(isPlaying) {
            isPlaying = false;
            isPaused = true;
            player.pause();
        } else if(isPaused) {
            isPaused = false;
            isPlaying = true;
            player.play();
        }
    }
    
    public void settingButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("Setting.fxml"));
        stage = (Stage) settingsButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 800));
        
    }

    private Connection connect() {
        // SQLite connection string
        String db = "jdbc:sqlite:music.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(db);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        //Setting the CellValue with the Song class as a Data Model
        colSongID.setCellValueFactory(
                new PropertyValueFactory<SongDataModel, Integer>("songID")
        );

        colTitle.setCellValueFactory(
                new PropertyValueFactory<SongDataModel, String>("name")
        );

        colArtistName.setCellValueFactory(
                new PropertyValueFactory<SongDataModel, String>("artistName")
        );

        colAlbumName.setCellValueFactory(
                new PropertyValueFactory<SongDataModel, String>("albumName")
        );

        colDuration.setCellValueFactory(
                new PropertyValueFactory<SongDataModel, Integer>("duration")
        );
        
        Database music = Database.getInstance();
        music.fillTable(tableView);


    }
}
