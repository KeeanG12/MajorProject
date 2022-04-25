package uk.aber.ac.keg21.musicapp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;


public class PlayerController {

    Database music = Database.getInstance();

    Double volume = 1.0;

    Image pauseIcon = new Image("D:\\UniWork\\Third Year\\Major Project\\MajorProject\\src\\main\\resources\\uk\\aber\\ac\\keg21\\musicapp\\Icons\\Pause.png");
    Image playIcon = new Image("D:\\UniWork\\Third Year\\Major Project\\MajorProject\\src\\main\\resources\\uk\\aber\\ac\\keg21\\musicapp\\Icons\\Play.png");


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


    public void changeVolume(Slider volumeSlider, MediaPlayer player1, Double volume) {
        //Setting slider value to current player volume * 100 as the volume range is 0.0 - 1.0
        volumeSlider.setValue(player1.getVolume() * 100);

        //Setting the volume to slider value / 100 if the slider value changes
        volumeSlider.valueProperty().addListener(observable -> player1.setVolume(volumeSlider.getValue() / 100));

        volume = volumeSlider.getValue() / 100;

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

    public void songButton(Stage stage, MediaPlayer player1, Button songsButton) throws IOException {
        if (player1 != null) {
            player1.stop();
        }
        Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"));
        stage = (Stage) songsButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);

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

    public void artistButton(Stage stage, MediaPlayer player1, Button artistButton) throws IOException {
        if (player1 != null) {
            player1.stop();
        }
        Parent root = FXMLLoader.load(Main.class.getResource("Artist.fxml"));
        stage = (Stage) artistButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);

    }

    public int findIndex(String currentFile) {
        int index = 0;


        if (music.currentList.isEmpty()) {
            for (SongDataModel song : music.songList) {
                if (song.getFilepath() == currentFile) {
                    index = music.songList.indexOf(song);
                }
            }
        } else {
            for (SongDataModel song : music.currentList) {
                if (song.getFilepath() == currentFile) {
                    index = music.currentList.indexOf(song);
                }
            }
        }
        return index;
    }


    public String findNext(String currentFile, Label currentSong) {


        String nextFilepath = "";
        Boolean found = false;
        SongDataModel nextSong = new SongDataModel();

        if (music.currentList.isEmpty()) {
            for (SongDataModel song : music.songList) {
                if (song.getFilepath() == currentFile) {
                    if (music.songList.indexOf(song) + 1 != music.songList.size()) {
                        nextSong = music.songList.get(music.songList.indexOf(song) + 1);
                        nextFilepath = nextSong.getFilepath();
                        currentSong.setText(nextSong.getArtistName() + " - " + nextSong.getName());
                        System.out.println(nextFilepath);
                    } else {
                        nextSong = music.songList.get(0);
                        nextFilepath = nextSong.getFilepath();
                        currentSong.setText(nextSong.getArtistName() + "-" + nextSong.getName());
                    }
                }
            }
        } else {
            for (SongDataModel song : music.currentList) {
                if (song.getFilepath() == currentFile) {
                    if (music.currentList.indexOf(song) + 1 != music.currentList.size()) {
                        nextSong = music.currentList.get(music.currentList.indexOf(song) + 1);
                        nextFilepath = nextSong.getFilepath();
                        currentSong.setText(nextSong.getArtistName() + " - " + nextSong.getName());
                        System.out.println(nextFilepath);
                    } else {
                        nextSong = music.currentList.get(0);
                        nextFilepath = nextSong.getFilepath();
                        currentSong.setText(nextSong.getArtistName() + "-" + nextSong.getName());
                    }
                }
            }
        }
        return nextFilepath;
    }

    public String findPrevious(String currentFile, Label currentSong) {

        String previousFilepath = "";
        Boolean found = false;
        SongDataModel previousSong = new SongDataModel();

        if (music.currentList.isEmpty()) {
            for (SongDataModel song : music.songList) {
                if (song.getFilepath() == currentFile) {
                    if (song != music.songList.get(0)) {
                        previousSong = music.songList.get(music.songList.indexOf(song) - 1);
                        previousFilepath = previousSong.getFilepath();
                        currentSong.setText(previousSong.getArtistName() + " - " + previousSong.getName());
                        System.out.println(previousFilepath);
                    } else {
                        previousSong = music.songList.get(music.songList.size() - 1);
                        previousFilepath = previousSong.getFilepath();
                        currentSong.setText(previousSong.getArtistName() + "-" + previousSong.getName());
                    }
                }
            }
        } else {
            for (SongDataModel song : music.currentList) {
                if (song.getFilepath() == currentFile) {
                    if (song != music.currentList.get(0)) {
                        previousSong = music.currentList.get(music.currentList.indexOf(song) - 1);
                        previousFilepath = previousSong.getFilepath();
                        currentSong.setText(previousSong.getArtistName() + " - " + previousSong.getName());
                        System.out.println(previousFilepath);
                    } else {
                        previousSong = music.currentList.get(music.currentList.size() - 1);
                        previousFilepath = previousSong.getFilepath();
                        currentSong.setText(previousSong.getArtistName() + "-" + previousSong.getName());
                    }
                }
            }
        }
        return previousFilepath;
    }

    public void initializeSongs(TableView tableView, TextField searchField) {

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
    }

    public void initializeAlbums(ChoiceBox choiceBox, String previous, TableView tableView, String selected) {
        for (int i = 0; i < music.songList.size() - 1; i++) {
            String current = music.songList.get(i).getAlbumName();

            if (choiceBox.getItems().contains(previous)) {
//                System.out.println("Already Exists");
            } else {
                choiceBox.getItems().add(current);
            }
            previous = music.songList.get(i).getAlbumName();
        }

        music.fillTable(tableView);

        selected = (String) choiceBox.getSelectionModel().getSelectedItem();
        
    }

    public void initializeArtist(ChoiceBox choiceBox, String previous, TableView tableView, String selected) {
        for (int i = 0; i < music.songList.size() - 1; i++) {
            String current = music.songList.get(i).getArtistName();

            if (choiceBox.getItems().contains(previous)) {
//                System.out.println("Already Exists");
            } else {
                choiceBox.getItems().add(current);
            }
            previous = music.songList.get(i).getArtistName();
        }

        music.fillTable(tableView);

        selected = (String) choiceBox.getSelectionModel().getSelectedItem();

    }
}
