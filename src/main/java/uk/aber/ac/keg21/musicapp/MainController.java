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
    public Button settingsButton;

    @FXML
    public Button albumButton;

    @FXML
    public Button shuffleButton;

    @FXML
    public TextField searchField;

    @FXML
    public ImageView albumArt;


    Image pause = new Image("D:\\UniWork\\Third Year\\Major Project\\MajorProject\\src\\main\\resources\\uk\\aber\\ac\\keg21\\musicapp\\Icons\\Pause.png");
    Image play = new Image("D:\\UniWork\\Third Year\\Major Project\\MajorProject\\src\\main\\resources\\uk\\aber\\ac\\keg21\\musicapp\\Icons\\Play.png");


    Media sound = new Media(new File("src/main/resources/Tunes/Childish Gambino/Because The Internet/Death By Numbers.mp3").toURI().toString());

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isEnd;
    private String previousSong = "";

    private double volume = 1.0;

    Duration time = new Duration(0.0);

    Database music = Database.getInstance();

    private String currentFile;

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
        tableView.setItems(filteredList);

        //Setting the on action event handler for the Play/Pause Button
        playButton.setOnAction(e -> {

            playButton = (Button) e.getSource();
            if (!isPlaying) {
                try {
                    //Call Play Button method and set the graphic to Pause
                    playButton();
                    playButton.setGraphic(new ImageView(pause));
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    //Call Pause button method and set the graphic to Play
                    pauseButton();
                    playButton.setGraphic(new ImageView(play));
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

        playerController.changeVolume(volumeSlider, player1);
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
            int index = findIndex(findNext());
            selectionModel.select(index);

            Media nextSong = new Media(new File(findNext()).toURI().toString());

            player1 = new MediaPlayer(nextSong);

            player1.setOnEndOfMedia(this);

            changeVolume();
            songDuration(getSelected().getDuration());

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
        currentFile = findNext();
        Media sound = new Media(new File(currentFile).toURI().toString());
        player1 = new MediaPlayer(sound);

        //Getting the index of current file and selecting that
        int index = playerController.findIndex(currentFile);
        selectionModel.select(index);

        
        songDuration(getSelected().getDuration());
        playButton.setGraphic(new ImageView(pause));

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
        int index = playerController.findIndex(currentFile);
        selectionModel.select(index);

        changeVolume();
        songDuration(getSelected().getDuration());


        player1.play();
    }


    public String getTimeFormatted(Duration currentTime) {

        //Casting currentTime Duration to integer for easier formatting
        int seconds = (int) currentTime.toSeconds();
        int minutes = (int) currentTime.toMinutes();

        //If seconds or minutes are greater than 59 then wrap using modulo
        if (seconds > 59) {
            seconds = seconds % 60;
        }

        if (minutes > 59) {
            minutes = minutes % 60;
        }

        //Formatting the string correctly using .format and % to represent integers
        String time = String.format("%02d:%02d", minutes, seconds);

        return time;
    }


    private void changeVolume() {
        //Setting slider value to current player volume * 100 as the volume range is 0.0 - 1.0
        volumeSlider.setValue(player1.getVolume() * 100);

        //Setting the volume to slider value / 100 if the slider value changes
        volumeSlider.valueProperty().addListener(observable -> player1.setVolume(volumeSlider.getValue() / 100));

    }

    private void songDuration(String duration) {

        //Adding a Change Listener to the total duration property so if the song changes the slider's max changes
        player1.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration previousDuration, Duration newDuration) {
                timeSlider.setMax(newDuration.toSeconds());
                totalDuration.setText(getTimeFormatted(newDuration));
                timeSlider.setValue(0.0);
            }
        });

        //When the sliders value changes, if the old val difference is more than 0.5s then use seek() to play song from newVal
        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number previousVal, Number newVal) {
                double currentTime = player1.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newVal.doubleValue()) > 0.5) {
                    player1.seek(Duration.seconds(newVal.doubleValue()));
                }
            }
        });

        //While the current time changes set the labels text to current time
        player1.currentTimeProperty().addListener(observable -> {

            totalDuration.setText(getTimeFormatted(player1.getCurrentTime()) + " / " + duration);
        });


    }

    public void settingButton(ActionEvent actionEvent) throws IOException {
//        if (player1 != null) {
//            player1.stop();
//        }
//        Parent root = FXMLLoader.load(Main.class.getResource("Setting.fxml"));
//        stage = (Stage) settingsButton.getScene().getWindow();
//        stage.setScene(new Scene(root));
//        stage.setFullScreen(true);
        playerController.settingButton(stage, player1, settingsButton);

    }

    public void albumButton(ActionEvent actionEvent) throws IOException {
//        if (player1 != null) {
//            player1.stop();
//        }
//        Parent root = FXMLLoader.load(Main.class.getResource("Albums.fxml"));
//        stage = (Stage) albumButton.getScene().getWindow();
//        stage.setScene(new Scene(root));
//        stage.setFullScreen(true);
        playerController.albumButton(stage, player1, albumButton);

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


        for (SongDataModel song : music.songList) {
            if (song.getFilepath() == currentFile) {
                index = music.songList.indexOf(song);
            }
        }
        return index;
    }

    public Artwork getAlbumArt(String currentFile) {
        AudioFile audioFile = null;

        try {
            audioFile = AudioFileIO.read(new File(currentFile));
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

        Tag tag = audioFile.getTag();

        return tag.getFirstArtwork();
    }


    public String findNext() {


        String nextFilepath = "";
        Boolean found = false;
        SongDataModel nextSong = new SongDataModel();

        for (SongDataModel song : music.songList) {
            if (song.getFilepath() == currentFile) {
                nextSong = music.songList.get(music.songList.indexOf(song) + 1);
                nextFilepath = nextSong.getFilepath();
                currentSong.setText(nextSong.getArtistName() + " - " + nextSong.getName());
                System.out.println(nextFilepath);
            }
        }
        return nextFilepath;
    }

    public String findPrevious() {

        String previousFilepath = "";
        Boolean found = false;
        SongDataModel previousSong = new SongDataModel();

        for (SongDataModel song : music.songList) {
            if (song.getFilepath() == currentFile) {
                previousSong = music.songList.get(music.songList.indexOf(song) - 1);
                previousFilepath = previousSong.getFilepath();
                currentSong.setText(previousSong.getArtistName() + " - " + previousSong.getName());
                System.out.println(previousFilepath);
            }
        }
        return previousFilepath;
    }
}
