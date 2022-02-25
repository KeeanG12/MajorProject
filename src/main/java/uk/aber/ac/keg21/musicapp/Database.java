package uk.aber.ac.keg21.musicapp;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Database {

    String artist = "Create table if not exists artist(artistID integer primary key NOT NULL, name string NOT NULL)";

    String album = "Create table if not exists album(albumID integer primary key NOT NULL, name string NOT NULL, produced DATE, artistID integer, foreign key (artistID) REFERENCES artist (artistID));";

    String song = "Create table if not exists songs(songID integer primary key NOT NULL, name string NOT NULL, albumID Integer,artistID INTEGER, duration string, Foreign key (albumID) references album (albumID), FOREIGN KEY (artistID) references artist (artistID))";

    String drop = "DROP TABLE song";
    
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
    
    

    public void insertArtist(int artistID, String artistName) {
        String artist = "INSERT INTO artist(artistID, name) VALUES(?,?)";
        String sql = "DELETE FROM artist";
        
        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             PreparedStatement statement = conn.prepareStatement(artist)) {
            Statement statement1 = conn.createStatement();
            
            

            statement.setInt(1, artistID);
            statement.setString(2, artistName);
            statement.executeUpdate();
            ResultSet rs = statement1.executeQuery("select * from artist");
            while (rs.next()) {
                // read the result set
                System.out.println("ArtistID = " + rs.getInt("artistID"));
                System.out.println("Name = " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertAlbum (int artistID, int albumID, String albumName, int produced) {

        String album = "INSERT INTO album(albumID, name, produced, artistID) VALUES(?,?,?,?)";
        String sql = "DELETE FROM album";

        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             PreparedStatement statement = conn.prepareStatement(album)) {
            Statement statement1 = conn.createStatement();

            statement.setInt(1, albumID);
            statement.setString(2, albumName);
            statement.setInt(3, produced);
            statement.setInt(4, artistID);
            statement.executeUpdate();
            ResultSet rs = statement1.executeQuery("select * from album");
            while (rs.next()) {
                // read the result set
                System.out.println("AlbumID = " + rs.getInt("albumID"));
                System.out.println("Name = " + rs.getString("name"));
                System.out.println("Produced = " + rs.getInt("produced"));
                System.out.println("ArtistID = " + rs.getInt("artistID"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertSong(int albumID, int songID, String songName, int artistID, String duration) {

        String song = "INSERT INTO songs(songID, name, albumID, artistID, duration) VALUES(?,?,?,?,?)";
        String sql = "DELETE FROM songs";

        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             PreparedStatement statement = conn.prepareStatement(song)) {
            Statement statement1 = conn.createStatement();

            statement.setInt(1, songID);
            statement.setString(2, songName);
            statement.setInt(3, albumID);
            statement.setInt(4, artistID);
            statement.setString(5, duration);
            statement.executeUpdate();
            ResultSet rs = statement1.executeQuery("select * from songs");
            while (rs.next()) {
                // read the result set
                System.out.println("SongID = " + rs.getInt("songID"));
                System.out.println("Name = " + rs.getString("name"));
                System.out.println("AlbumID = " + rs.getInt("albumID"));
                System.out.println("ArtistID = " + rs.getInt("artistID"));
                System.out.println("Duration = " + rs.getString("duration"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    
    public void delete() {
        String sql = "DELETE FROM songs";
        String sql2 = "DELETE FROM artist";
        String sql3 = "DELETE FROM album";

        try (Connection conn = this.connect()) {
             //Uses prepared statement to pass input parameters
            Statement statement1 = conn.createStatement();
            
            statement1.executeUpdate(sql);
            statement1.executeUpdate(sql2);
            statement1.executeUpdate(sql3);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean checkExists(String name, String table, String idType) {
        
        String sql = "SELECT '"+idType+"', name FROM '"+table+"' WHERE name = '"+name+"'";
        
        
        
        boolean exists = false;
        
        
        try(Connection conn = this.connect();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
    
    int dbID = 1;
    
    
    
    private int getID(String name, String table, String idType) {
        String sql = "SELECT * from '"+table+"' WHERE name = '"+name+"'";

        
        


        try(Connection conn = this.connect();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            dbID = resultSet.getInt( idType);
            while (resultSet.next()) {
                dbID = resultSet.getInt( idType);
                System.out.println(resultSet.getInt(idType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbID;
    }
    
    
    //Scan Directory
    //Check if 

    //Variables for Number of files, Number of directories and track number
    private int numFiles;
    private int numDirectories;

    int id = 1;
    String previousArtist;
    String previousAlbum;
    String previousSong;
    int previousID = 0;
    int artistID = 1;
    int albumID = 1;
    int songID = 1;
    

    @Override
    public String toString() {
        return "Database{" +
                "numFiles=" + numFiles +
                ", numDirectories=" + numDirectories +
                '}';
    }

    /**
     * Scans file recursivley and populates SQLite DB with each files tag data
     * @param file
     * @throws CannotReadException
     * @throws TagException
     * @throws InvalidAudioFrameException
     * @throws ReadOnlyFileException
     * @throws IOException
     */
    public void rescan(File file) throws IOException {
        
            //Checks if the file is a directory or a file
            if (file.isFile()) {
                //Initialising variables in order to get tag data
                AudioFile audioFile = null;
                MP3File f = null;
                //Setting the MP3 file to AudioFile.read of the current file being parsed in
                try {
                    f = (MP3File) AudioFileIO.read(new File(file.getAbsolutePath()));
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
                
                //Creating a tag variable to get the tag data of the MP3 AudioFile
                Tag tag = f.getTag();
                MP3AudioHeader audioHeader = f.getMP3AudioHeader();
                AbstractID3v2Tag v2tag  = f.getID3v2Tag();
                
                
                //Retrieving everything before " Feat." in the artist name using .split
                System.out.println(audioHeader.getTrackLengthAsString());
                String artistTag = v2tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST);
                String parts[] = artistTag.split(" Feat. ");
                
                //Declaring current artist, album and song
                String artist = parts[0];
                String album = v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM);
                String song = v2tag.getFirst(ID3v24Frames.FRAME_ID_TITLE);
                
                
                //Checks if current artist already exists in database and gets the ID if true
                if(checkExists(artist, "artist", "artistID") && artistID != previousID) {
                    artistID = getID(artist, "artist", "artistID");
                    System.out.println(artistID);
                //Adds the current artist if they don't exist in the database    
                } else {
                    insertArtist(artistID, artist);
                }
                //Checks if current album already exists in database and gets the ID if true
                if(checkExists(album, "album", "albumID")) {
                    albumID = getID(album, "album", "albumID");
                    System.out.println(albumID);
                //Adds current album if it doesn't exist in the database    
                } else {
                    insertAlbum(artistID, albumID, album, Integer.parseInt(v2tag.getFirst(FieldKey.YEAR)));
                }
                
                insertSong(albumID, songID, song, artistID, (audioHeader.getTrackLengthAsString()));
                
                numFiles++;
                previousID = albumID;
                albumID++;
                artistID++;
                songID++;
                
                
                } else {
                numDirectories++;
                File[] files = file.listFiles();
                for (File otherFile : files) {
                    rescan(otherFile);
                }
            }
        }

    public static Database getInstance() {
        if (music == null)
            music = new Database();
        return music;
    }
}
