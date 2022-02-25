package uk.aber.ac.keg21.musicapp;

public class Song {
    private int songID;
    private String name;
    private int albumID;
    private int artistID;
    private String duration;
    

    public Song(int songID, String name, int albumID, int artistID, String duration) {
        this.songID = songID;
        this.name = name;
        this.albumID = albumID;
        this.artistID = artistID;
        this.duration = duration;
        
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

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
