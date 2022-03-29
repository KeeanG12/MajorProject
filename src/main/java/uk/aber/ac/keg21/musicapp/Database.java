package uk.aber.ac.keg21.musicapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
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

    String ARTIST = "Create table if not exists artist(artistID integer primary key NOT NULL, name string NOT NULL)";

    String ALBUM = "Create table if not exists album(albumID integer primary key NOT NULL, name string NOT NULL, produced DATE, artistID integer, foreign key (artistID) REFERENCES artist (artistID));";

    String SONG = "Create table if not exists songs(songID integer primary key NOT NULL, name string NOT NULL, albumID Integer,artistID INTEGER, duration string, filepath string, Foreign key (albumID) references album (albumID), FOREIGN KEY (artistID) references artist (artistID))";

    String dropSongs = "DROP TABLE songs";
    
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
            statement.executeUpdate(ARTIST);
            statement.executeUpdate(ALBUM);
            statement.executeUpdate(SONG);

        } catch ( SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    

    public void insertArtist(String artistName) {
        String artist = "INSERT INTO artist(name) VALUES(?)";
        String sql = "DELETE FROM artist";
        
        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             PreparedStatement statement = conn.prepareStatement(artist)) {
            Statement statement1 = conn.createStatement();
            
            
            
            statement.setString(1, artistName);
            statement.executeUpdate();
            ResultSet rs = statement1.executeQuery("select * from artist");
//            while (rs.next()) {
//                // read the result set for debugging
//                System.out.println("ArtistID = " + rs.getInt("artistID"));
//                System.out.println("Name = " + rs.getString("name"));
//            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertAlbum (int artistID, String albumName, int produced) {

        String album = "INSERT INTO album(name, produced, artistID) VALUES(?,?,?)";
        String sql = "DELETE FROM album";

        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             PreparedStatement statement = conn.prepareStatement(album)) {
            Statement statement1 = conn.createStatement();
            
            statement.setString(1, albumName);
            statement.setInt(2, produced);
            statement.setInt(3, artistID);
            statement.executeUpdate();
            ResultSet rs = statement1.executeQuery("select * from album");
//            while (rs.next()) {
//                // read the result set for debugging
//                System.out.println("AlbumID = " + rs.getInt("albumID"));
//                System.out.println("Name = " + rs.getString("name"));
//                System.out.println("Produced = " + rs.getInt("produced"));
//                System.out.println("ArtistID = " + rs.getInt("artistID"));
//            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertSong(int albumID, String songName, int artistID, String duration, String filepath) {

        String song = "INSERT INTO songs(name, albumID, artistID, duration, filepath) VALUES(?,?,?,?,?)";
        String sql = "DELETE FROM songs";

        try (Connection conn = this.connect();
             //Uses prepared statement to pass input parameters
             PreparedStatement statement = conn.prepareStatement(song)) {
            Statement statement1 = conn.createStatement();
            
            statement.setString(1, songName);
            statement.setInt(2, albumID);
            statement.setInt(3, artistID);
            statement.setString(4, duration);
            statement.setString(5, filepath);
            statement.executeUpdate();
            ResultSet rs = statement1.executeQuery("select * from songs");
//            while (rs.next()) {
//                // read the result set for debugging
//                System.out.println("SongID = " + rs.getInt("songID"));
//                System.out.println("Name = " + rs.getString("name"));
//                System.out.println("AlbumID = " + rs.getInt("albumID"));
//                System.out.println("ArtistID = " + rs.getInt("artistID"));
//                System.out.println("Duration = " + rs.getString("duration"));
//                System.out.println("File Path = " + rs.getString("filepath"));
//            }
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

    public ObservableList<SongDataModel> songList;

    public SongDataModel song = new SongDataModel();
    
    public void fillTable(TableView tableView) {
        songList = FXCollections.observableArrayList();
        
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
    
    public void shuffleTable(TableView tableView) {
        FXCollections.shuffle(songList);
        tableView.setItems(songList);
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
    
    
    
    private int getID(String name, String table, String idType) {
        int id = 0;
        //Select statement to find the ID of on album, artist or song based on arguments
        String sql = "SELECT * from '"+table+"' WHERE name = '"+name+"'";

        try(Connection conn = this.connect();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            //Set return ID as ID in result set
            while (resultSet.next()) {
                id = resultSet.getInt( idType);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    

    //Variables for Number of files, Number of directories and track number
    private int numFiles;
    private int numDirectories;

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
                String artistTag = v2tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST);
                String parts[] = artistTag.split(" Feat. ");
                
                //Declaring current artist, album and song
                String artist = parts[0];
                String album = v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM);
                String song = v2tag.getFirst(ID3v24Frames.FRAME_ID_TITLE);
                
                int artistID = getID(artist, "artist", "artistID");
                int albumID = getID(album, "album", "artistID");
                
                
                
                //Checks if current artist already exists in database and gets the ID if true
                if(checkExists(artist, "artist", "artistID")) {
                    artistID = getID(artist, "artist", "artistID");
                //Adds the current artist if they don't exist in the database    
                } else {
                    insertArtist(artist);
                    artistID = getID(artist, "artist", "artistID");
                }
                //Checks if current album already exists in database and gets the ID if true
                if(checkExists(album, "album", "albumID")) {
                    albumID = getID(album, "album", "albumID");
                    System.out.println(albumID);
                //Adds current album if it doesn't exist in the database    
                } else {
                    insertAlbum(artistID, album, Integer.parseInt(v2tag.getFirst(FieldKey.YEAR)));
                    albumID = getID(album, "album", "albumID");
                }
                //Adds current song
                String filepath = file.getAbsolutePath();
                insertSong(albumID, song, artistID, (audioHeader.getTrackLengthAsString()), filepath);
                
                //Adding 1 to each ID
                numFiles++;
                
                
                } else {
                numDirectories++;
                //List the files in the directory
                File[] files = file.listFiles();
                
                //For each file call rescan on that file
                for (File otherFile : files) {
                    rescan(otherFile);
                }
            }
        }
        
        
        //Used to getInstance of Database class as it is a singleton
    public static Database getInstance() {
        if (music == null)
            music = new Database();
        return music;
    }
}
