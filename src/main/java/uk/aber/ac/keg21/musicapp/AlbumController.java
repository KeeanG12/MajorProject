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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
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

    String previous;

    Database music = Database.getInstance();

    String selected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (int i = 0; i < music.songList.size() - 1; i++) {
            String current = music.songList.get(i).getAlbumName();

            if (choiceBox.getItems().contains(previous)) {
//                System.out.println("Already Exists");
            } else {
                choiceBox.getItems().add(current);
            }
            previous = music.songList.get(i).getAlbumName();

        }

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

        selected = (String) choiceBox.getSelectionModel().getSelectedItem();

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
        tableView.setItems(filteredList);


    }


    public void songButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"));
        stage = (Stage) songsButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setFullScreen(true);

    }
}
