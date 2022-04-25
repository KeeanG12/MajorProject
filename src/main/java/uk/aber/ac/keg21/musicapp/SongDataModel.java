package uk.aber.ac.keg21.musicapp;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SongDataModel {
    public SimpleIntegerProperty songID = new SimpleIntegerProperty();
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleIntegerProperty albumID = new SimpleIntegerProperty();
    public SimpleStringProperty albumName = new SimpleStringProperty();
    public SimpleStringProperty artistName = new SimpleStringProperty();
    public SimpleStringProperty duration = new SimpleStringProperty();
    public SimpleStringProperty filepath = new SimpleStringProperty();
    public SimpleIntegerProperty produced = new SimpleIntegerProperty();


    public int getSongID() {
        return songID.get();
    }

    public SimpleIntegerProperty songIDProperty() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID.set(songID);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDuration() {
        return duration.get();
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getAlbumName() {
        return albumName.get();
    }

    public SimpleStringProperty albumNameProperty() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName.set(albumName);
    }

    public String getArtistName() {
        return artistName.get();
    }

    public int getProduced() {
        return produced.get();
    }

    public SimpleIntegerProperty producedProperty() {
        return produced;
    }

    public void setProduced(int produced) {
        this.produced.set(produced);
    }

    public SimpleStringProperty artistNameProperty() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName.set(artistName);
    }

    public String getFilepath() {
        return filepath.get();
    }

    public SimpleStringProperty filepathProperty() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath.set(filepath);
    }
}
