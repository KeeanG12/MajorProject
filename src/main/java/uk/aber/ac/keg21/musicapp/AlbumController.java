package uk.aber.ac.keg21.musicapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AlbumController implements Initializable {

    @FXML
    public ChoiceBox choiceBox;

    @FXML
    private Stage stage;

    @FXML
    public Button songsButton;
    
    @FXML
    public Button settingsButton;
    
    @FXML
    public Button playButton;
    
    @FXML
    public Button pauseButton;

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
    private MediaPlayer player1;
    
    @FXML
    public Label currentSong;
    
    @FXML
    public Slider volumeSlider;
    
    @FXML
    public Label totalDuration;
    
    @FXML
    public Slider timeSlider;

    public String currentFile;
    
    public Duration time = new Duration(0.0);

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private String previousSong = "";

    private double volume = 1.0;
    
    TableView.TableViewSelectionModel<SongDataModel> selectionModel;

    String previous;

    Database music = Database.getInstance();

    String selected;
    
    PlayerController playerController = new PlayerController();


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

        playerController.initializeAlbums(choiceBox, previous, tableView, selected);
        
        changeTable();
    }

    public void changeTable() {
        //Creating a Filtered List with the SongDataModel
        FilteredList<SongDataModel> filteredList = new FilteredList<>(music.songList, b -> true);


        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {

                filteredList.setPredicate(songDataModel -> {

                    //If there is no changes then display all cells
                    if (newVal == null || newVal.isBlank() || newVal.isEmpty()) {
                        return true;
                    }

                    String userSelection = newVal.toLowerCase();
                     
                    if (songDataModel.getAlbumName().toLowerCase().contains(userSelection)) {
                        return true;
                    }


                    return false;

                });
            }
        });
        music.currentList = filteredList;
        tableView.setItems(music.currentList);

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

    }

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
//        player1.setOnEndOfMedia(new MainController.MediaHandler());


        isPlaying = true;
        previousSong = path;

    }

    public void pauseButton() throws URISyntaxException {
        //Set the time to the song time when it was paused

        time = player1.getCurrentTime();
        System.out.println(time);
        //Pause player and set playing to false
        player1.pause();
        isPlaying = false;
        isPaused = true;
    }

    private SongDataModel getSelected() {
        //Getting the selection model to know which cell the user selects
        selectionModel = tableView.getSelectionModel();

        //Setting selection mode to single, so they can only select a single cell
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        SongDataModel selected = selectionModel.getSelectedItem();

        return selected;
    }


    public void songButton(ActionEvent actionEvent) throws IOException {
        playerController.songButton(stage, player1, songsButton);

    }
    
    public void settingsButton(ActionEvent actionEvent) throws IOException {
        playerController.settingButton(stage, player1, settingsButton);
    }
}
