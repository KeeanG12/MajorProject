package uk.aber.ac.keg21.musicapp;

public class Album {

    private int albumID;
    private  String name;
    private int produced;
    private int artistID;

    public Album(int albumID, String name, int produced, int artistID) {
        this.albumID = albumID;
        this.name = name;
        this.produced = produced;
        this.artistID = artistID;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduced() {
        return produced;
    }

    public void setProduced(int produced) {
        this.produced = produced;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }
}
