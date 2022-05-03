package uk.aber.ac.keg21.musicapp;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class SongController implements Initializable {

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

        music.fillTable(tableView);

        //Creating a Filtered List with the SongDataModel
        FilteredList<SongDataModel> filteredList = new FilteredList<>(music.songList, b -> true);

        //Adding listener to text field to check for user input
        searchField.textProperty().addListener((observable, oldVal, newVal) -> {
            //If text is entered set the FilteredList Predicate
            filteredList.setPredicate(songDataModel -> {

                //If there is no changes then display all cells
                if (newVal == null || newVal.isBlank() || newVal.isEmpty()) {
                    return true;
                }

                //Ensuring text user has input is all lower case
                String userInput = newVal.toLowerCase();

                //Checking if any of the Songs in the list match the predicate
                if (songDataModel.getName().toLowerCase().contains(userInput)) {
                    return true;

                } else if (songDataModel.getArtistName().toLowerCase().contains(userInput)) {
                    return true;

                } else if (songDataModel.getAlbumName().toLowerCase().contains(userInput)) {
                    return true;

                } else if (String.valueOf(songDataModel.getSongID()).contains(userInput)) {
                    return true;

                } else {
                    return false;
                }
            });
        });

        //Setting displayed items as the sorted list
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

        //Checking if the start of the song
        if (time == new Duration(0.0)) {
            player1.play();
            isPaused = false;
        } else if (path != previousSong) {
            player1.setStartTime(new Duration(0.0));
            player1.play();
            isPaused = false;
        //If not start of song, play song from time it was paused    
        } else if (isPaused && time != new Duration(0.0)) {
            player1.setStartTime(time);
            player1.play();
            isPaused = false;
        }

        playerController.changeVolume(volumeSlider, player1);
        playerController.songDuration(selected.getDuration(), player1, timeSlider, totalDuration);


        isPlaying = true;
        previousSong = path;

    }

    private class MediaHandler implements Runnable {

        @Override
        public void run() {

            System.out.println("2nd on end");
            //Getting the index of current file and selecting that
            

            Media nextSong = new Media(new File(playerController.findNext(currentFile, currentSong)).toURI().toString());

            int index = playerController.findIndex(playerController.findNext(currentFile, currentSong));
            selectionModel.select(index);

            player1 = new MediaPlayer(nextSong);

            playerController.changeVolume(volumeSlider, player1);
            playerController.songDuration(getSelected().getDuration(), player1, timeSlider, totalDuration);

            player1.setOnEndOfMedia(this);
            player1.play();
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
        //Pause player and set playing to false
        player1.pause();
        isPlaying = false;
        isPaused = true;
    }

    public void shuffleButton() {
        if (!isPlaying) {
            FXCollections.shuffle(music.songList);
            tableView.setItems(music.songList);
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

        playerController.changeVolume(volumeSlider, player1);
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
