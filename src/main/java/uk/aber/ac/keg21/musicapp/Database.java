package uk.aber.ac.keg21.musicapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    String artist = "Create table if not exists artist(artistID integer primary key NOT NULL, name string NOT NULL)";

    String album = "Create table if not exists album(albumID integer primary key NOT NULL, name string NOT NULL, produced DATE, artist integer, foreign key (artist) REFERENCES artist (artistID));";

    String song = "Create table if not exists songs(songID integer primary key NOT NULL, name string NOT NULL, album Integer,artist INTEGER, Foreign key (album) references album (albumID), FOREIGN KEY (artist) references artist (artistID));";

    private static Database music = null;

    private Database() {

    }

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:music.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void startUp() {

        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(artist);
            statement.executeUpdate(album);
            statement.executeUpdate(song);

        } catch ( SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Database getInstance() {
        if (music == null)
            music = new Database();
        return music;
    }
}
