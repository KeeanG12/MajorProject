package uk.aber.ac.keg21.musicapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    @FXML
    public void playButton(ActionEvent actionEvent) throws URISyntaxException {
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
        player.play();
    }
    
    public void settingButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("Setting.fxml"));
        stage = (Stage) settingsButton.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 800));
        
    }
    
    //Creating the list to populate TableView
    private ObservableList <SongDataModel> songList;
    
    SongDataModel songs = new SongDataModel();

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
        songList = FXCollections.observableArrayList();
        
        
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
        
        //Using SQLite Inner Join to Select 3 tables using albumID
        String select = "SELECT " +
                "songID," +
                "songs.name AS songName," +
                "artist.name AS artistName," +
                "album.name AS albumName, " +
                "songs.duration as duration, " +
                "songs.filepath AS filepath " +
                "FROM songs " +
                "INNER JOIN album ON album.albumID = songs.albumID " +
                "INNER JOIN artist ON artist.artistID = album.artistID";
        
        try(Connection conn = this.connect();

            Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(select);
            //Adding new song to list while ResultSet has next
            while (rs.next()) {
                SongDataModel s = new SongDataModel();
                s.setSongID(rs.getInt("songID"));
                s.setName(rs.getString("songName"));
//                s.setArtistID(rs.getInt("artistID"));
                s.setArtistName(rs.getString("artistName"));
//                s.setAlbumID(rs.getInt("albumID"));
                s.setAlbumName(rs.getString("albumName"));
                s.setDuration(rs.getString("duration"));
                s.setFilepath(rs.getString("filepath"));
                songList.add(s);
            }
            
            //Adding list to Table once all songs are in list
            tableView.setItems(songList);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
