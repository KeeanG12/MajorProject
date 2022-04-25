package uk.aber.ac.keg21.musicapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
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
    private TableColumn colProduced;

    @FXML
    private Button playButton;

    @FXML
    private Label currentSong;

    @FXML
    private MediaPlayer player1;

    @FXML
    public Slider timeSlider;

    @FXML
    public Label totalDuration;

    @FXML
    public Slider volumeSlider;

    @FXML
    public Button nextButton;

    @FXML
    public Button previousButton;
    
    @FXML
    public Button artistButton;


    @FXML
    public Button settingsButton;

    @FXML
    public Button albumButton;

    @FXML
    public Button shuffleButton;

    @FXML
    public TextField searchField;

    @FXML
    public ImageView albumArt;


    Media sound = new Media(new File("src/main/resources/Tunes/Childish Gambino/Because The Internet/Death By Numbers.mp3").toURI().toString());

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isEnd;
    private String previousSong = "";

    private double volume = 1.0;

    Duration time = new Duration(0.0);

    Database music = Database.getInstance();

    public String currentFile;

    PlayerController playerController = new PlayerController();

    //Creating a selection model for playing songs
    TableView.TableViewSelectionModel<SongDataModel> selectionModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player1 = new MediaPlayer(sound);

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
        
        colProduced.setCellValueFactory(
                new PropertyValueFactory<SongDataModel, Integer>("produced")
        );
        
        playerController.initializeSongs(tableView, searchField);

        //Setting the on action event handler for the Play/Pause Button
        playButton.setOnAction(e -> {

            playButton = (Button) e.getSource();
            if (!isPlaying) {
                try {
                    //Call Play Button method and set the graphic to Pause
                    playButton();
                    playButton.setGraphic(new ImageView(playerController.pauseIcon));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    //Call Pause button method and set the graphic to Play
                    pauseButton();
                    playButton.setGraphic(new ImageView(playerController.playIcon));
                    ;
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }

        });

        player1.setOnEndOfMedia(new MediaHandler());

    }


    @FXML
    public void playButton() throws URISyntaxException {
        SongDataModel selected = getSelected();

        //Finding the directory path from the selected cell data
        String artist = selected.getArtistName();
        currentSong.setText(artist + " - " + selected.getName());


        //Uses JavaFX-Media to play a MP3 file when button is clicked
        String path = selected.getFilepath();

//        if (getAlbumArt(path) != null) {
//            albumArt.setImage((Image) getAlbumArt(path));
//        } else {
//            Image album = new Image("D:\\UniWork\\Third Year\\Major Project\\MajorProject\\src\\main\\resources\\uk\\aber\\ac\\keg21\\musicapp\\Icons\\AlbumCover.png");
//            albumArt.setImage(album);
//        }

        currentFile = path;
        Media sound = new Media(new File(path).toURI().toString());
        player1 = new MediaPlayer(sound);

        if (time == new Duration(0.0)) {
            player1.play();
            isPaused = false;
        } else if (path != previousSong) {
            player1.setStartTime(new Duration(0.0));
            player1.play();
            isPaused = false;
        } else if (isPaused && time != new Duration(0.0)) {
            player1.setStartTime(time);
            player1.play();
            isPaused = false;
        }

        playerController.changeVolume(volumeSlider, player1, volume);
        playerController.songDuration(selected.getDuration(), player1, timeSlider, totalDuration);
        player1.setOnEndOfMedia(new MediaHandler());


        isPlaying = true;
        previousSong = path;

    }

    private class MediaHandler implements Runnable {

        @Override
        public void run() {
            player1.stop();
            
            //Getting the index of current file and selecting that
            int index = playerController.findIndex(currentFile);
            selectionModel.select(index);

            Media nextSong = new Media(new File(playerController.findNext(currentFile, currentSong)).toURI().toString());

            player1 = new MediaPlayer(nextSong);

            playerController.changeVolume(volumeSlider, player1, volume);
            playerController.songDuration(getSelected().getDuration(), player1, timeSlider, totalDuration);

            player1.play();
            player1.setOnEndOfMedia(this);
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
        isPaused = true;
    }

    public void shuffleButton() {
        if (!isPlaying) {
            music.shuffleTable(tableView);
        }

    }

    @FXML
    public void nextButton() {
        player1.stop();

        //Use the findNext method to find the next file and reassign the player to it
        currentFile = playerController.findNext(currentFile, currentSong);
        Media sound = new Media(new File(currentFile).toURI().toString());
        player1 = new MediaPlayer(sound);

        //Getting the index of current file and selecting that
        int index = playerController.findIndex(currentFile);
        selectionModel.select(index);

        
        playerController.songDuration(getSelected().getDuration(), player1, timeSlider, totalDuration);
        playButton.setGraphic(new ImageView(playerController.pauseIcon));

        player1.play();
    }

    @FXML
    public void previousButton() {
        player1.stop();

        //Use the findNext method to find the next file and reassign the player to it
        currentFile = playerController.findPrevious(currentFile, currentSong);
        Media sound = new Media(new File(currentFile).toURI().toString());
        player1 = new MediaPlayer(sound);

        //Getting the index of current file and selecting that
        int index = playerController.findIndex(currentFile);
        selectionModel.select(index);

        playerController.changeVolume(volumeSlider, player1, volume);
        playerController.songDuration(getSelected().getDuration(), player1, timeSlider, totalDuration);


        player1.play();
    }

    public void settingButton(ActionEvent actionEvent) throws IOException {
        playerController.settingButton(stage, player1, settingsButton);

    }

    public void albumButton(ActionEvent actionEvent) throws IOException {
        playerController.albumButton(stage, player1, albumButton);

    }

    public void artistButton(ActionEvent actionEvent) throws IOException {
        playerController.artistButton(stage, player1, artistButton);

    }
}
