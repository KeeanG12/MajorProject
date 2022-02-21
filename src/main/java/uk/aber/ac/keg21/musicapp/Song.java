package uk.aber.ac.keg21.musicapp;

public class Song {
    private int songID;
    private String name;
    private int albumID;

    public Song(int songID, String name, int albumID) {
        this.songID = songID;
        this.name = name;
        this.albumID = albumID;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }
}
