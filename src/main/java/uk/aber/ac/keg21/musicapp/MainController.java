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
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ListIterator;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    private Stage stage;

    @FXML
    public TableView tableView;
    
    @FXML
    private TableColumn colSongID;
    
    @FXML
    private TableColumn colTitle;
    
    @FXML
    private TableColumn colArtistName;

    @FXML
    private TableColumn colAlbumName;
    
    @FXML
    private TableColumn colDuration;
    
    @FXML
    private Button playPauseButton;
    
    @FXML
    private Label currentSong;

    @FXML
    private MediaPlayer player1;
    private MediaPlayer player2;
    
    @FXML
    private Label currentTime;
    
    @FXML
    private Label totalDuration;
    
    @FXML
    private Slider timeSlider;
    
    @FXML
    public Button nextButton;

    @FXML
    public Button previousButton;
    
    
    @FXML
    public Button settingsButton;
    
    @FXML
    public Button pauseButton;

    private ImageView play;
    private ImageView pause;
    
    private boolean isPlaying = false;
    private boolean isEnd;
    private String previousSong = "";

    Duration time = new Duration(0.0);

    Database music = Database.getInstance();
    
    private String currentFile;

    //Creating a selection model for playing songs
    TableView.TableViewSelectionModel<SongDataModel> selectionModel;
    
    @FXML
    public void playButton() throws URISyntaxException {
        
        SongDataModel selected = getSelected();

        //Finding the directory path from the selected cell data
        String artist = selected.getArtistName();
        currentSong.setText(artist + " - " + selected.getName());


        //Uses JavaFX-Media to play a MP3 file when button is clicked
        String path = selected.getFilepath();
        currentFile = path;
        Media sound = new Media(new File(path).toURI().toString());
        player1 = new MediaPlayer(sound);
        
        if (!isPlaying) {
            
            if(time != new Duration(0.0)) {
                player1.setStartTime(time);
                player1.play();
                time = new Duration(0.0);
            } else {
                player1.play();
            }
            
            isPlaying = true;
        }
    }
    
    private SongDataModel getSelected() {
        //Getting the selection model to know which cell the user selects
        selectionModel = tableView.getSelectionModel();

        //Setting selection mode to single, so they can only select a single cell
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        SongDataModel selected = selectionModel.getSelectedItem();
        
        return selected;
    }
    
    @FXML
    public void pauseButton() throws URISyntaxException {
        //Set the time to the song time when it was paused
        
        time = player1.getCurrentTime();
        System.out.println(time);
        //Pause player and set playing to false
        player1.pause();
        isPlaying = false;
        
    }
    
    @FXML
    public void nextButton() {
        player1.stop();
        
        //Use the findNext method to find the next file and reassign the player to it
        currentFile = findNext();
        Media sound = new Media(new File(currentFile).toURI().toString());
        player1 = new MediaPlayer(sound);
        
        //Getting the index of current file and selecting that
        int index = findIndex(currentFile);
        selectionModel.select(index);
        
        player1.play();
    }

    @FXML
    public void previousButton() {
        player1.stop();

        //Use the findNext method to find the next file and reassign the player to it
        currentFile = findPrevious();
        Media sound = new Media(new File(currentFile).toURI().toString());
        player1 = new MediaPlayer(sound);

        //Getting the index of current file and selecting that
        int index = findIndex(currentFile);
        selectionModel.select(index);


        player1.play();
    }
    
    public void settingButton(ActionEvent actionEvent) throws IOException {
        if(player1 != null) {
            player1.stop();
        }
        Parent root = FXMLLoader.load(Main.class.getResource("Setting.fxml"));
        stage = (Stage) settingsButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);
        
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
    
    public int findIndex(String currentFile) {
        int index = 0;
        
        
        for (SongDataModel song: music.songList) {
            if (song.getFilepath() == currentFile) {
                index = music.songList.indexOf(song);
            }
        }
        return index;
    }
    
    
    public String findNext() {
        
        
        String nextFilepath = "";
        Boolean found = false;
        SongDataModel nextSong = new SongDataModel();

        for (SongDataModel song: music.songList) {
            if(song.getFilepath() == currentFile) {
               nextSong =  music.songList.get(music.songList.indexOf(song) + 1);
               nextFilepath = nextSong.getFilepath();
               currentSong.setText(nextSong.getArtistName()+ " - "+ nextSong.getName());
                System.out.println(nextFilepath);
            }
        }
        return nextFilepath;
    }

    public String findPrevious() {
        
        String previousFilepath = "";
        Boolean found = false;
        SongDataModel previousSong = new SongDataModel();

        for (SongDataModel song: music.songList) {
            if(song.getFilepath() == currentFile) {
                previousSong =  music.songList.get(music.songList.indexOf(song) - 1);
                previousFilepath = previousSong.getFilepath();
                currentSong.setText(previousSong.getArtistName()+ " - "+ previousSong.getName());
                System.out.println(previousFilepath);
            }
        }
        return previousFilepath;
    }

    public void playOnEnd() {
        
        if(player1 != null) {
            player1.setOnEndOfMedia(() -> {
                Media sound = new Media(new File(findNext()).toURI().toString());
                player1 = new MediaPlayer(sound);

            });
            player1.play();
        }
        
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
