package uk.aber.ac.keg21.musicapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PlayerController {

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

//    @FXML
//    public Slider timeSlider;
//
//    @FXML
//    public Label totalDuration;
//
//    @FXML
//    public Slider volumeSlider;

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

    //Creating a selection model for playing songs
    TableView.TableViewSelectionModel<SongDataModel> selectionModel;


    public SongDataModel getSelected(TableView tableView) {
        //Getting the selection model to know which cell the user selects
        selectionModel = tableView.getSelectionModel();

        //Setting selection mode to single, so they can only select a single cell
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        SongDataModel selected = selectionModel.getSelectedItem();

        return selected;
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


    public void changeVolume(Slider volumeSlider, MediaPlayer player1) {
        //Setting slider value to current player volume * 100 as the volume range is 0.0 - 1.0
        volumeSlider.setValue(player1.getVolume() * 100);

        //Setting the volume to slider value / 100 if the slider value changes
        volumeSlider.valueProperty().addListener(observable -> player1.setVolume(volumeSlider.getValue() / 100));

    }

    public void songDuration(String duration, MediaPlayer player1, Slider timeSlider, Label totalDuration) {

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

    public void settingButton(Stage stage, MediaPlayer player1, Button settingsButton) throws IOException {
        if (player1 != null) {
            player1.stop();
        }
        Parent root = FXMLLoader.load(Main.class.getResource("Setting.fxml"));
        stage = (Stage) settingsButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);

    }

    public void albumButton(Stage stage, MediaPlayer player1, Button albumButton) throws IOException {
        if (player1 != null) {
            player1.stop();
        }
        Parent root = FXMLLoader.load(Main.class.getResource("Albums.fxml"));
        stage = (Stage) albumButton.getScene().getWindow();
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


    public String findNext(String currentFile) {


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

    public String findPrevious(String currentFile) {

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
