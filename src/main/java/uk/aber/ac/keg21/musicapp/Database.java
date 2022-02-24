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

    String album = "Create table if not exists album(albumID integer primary key NOT NULL, name string NOT NULL, produced DATE, artist integer, foreign key (artist) REFERENCES artist (artistID));";

    String song = "Create table if not exists songs(songID integer primary key NOT NULL, name string NOT NULL, album Integer,artist INTEGER, duration string, Foreign key (album) references album (albumID), FOREIGN KEY (artist) references artist (artistID))";

    String drop = "DROP TABLE songs";
    
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

        String album = "INSERT INTO album(albumID, name, produced, artist) VALUES(?,?,?,?)";
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
                System.out.println("ArtistID = " + rs.getInt("artist"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertSong(int albumID, int songID, String songName, int artistID, String duration) {

        String song = "INSERT INTO songs(songID, name, album, artist, duration) VALUES(?,?,?,?,?)";
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
                System.out.println("AlbumID = " + rs.getInt("album"));
                System.out.println("ArtistID = " + rs.getInt("artist"));
                System.out.println("Duration = " + rs.getInt("duration"));
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
     * Scans file recursivley and populates SQLite DB will each files tag data
     * @param file
     * @throws CannotReadException
     * @throws TagException
     * @throws InvalidAudioFrameException
     * @throws ReadOnlyFileException
     * @throws IOException
     */
    public void scanAndPopulate(File file) throws IOException {
        
            if (file.isFile()) {
//                System.out.println(Files.probeContentType(file.toPath()));
                AudioFile audioFile = null;
                MP3File f = null;
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
                
                Tag tag = f.getTag();
                MP3AudioHeader audioHeader = f.getMP3AudioHeader();
                AbstractID3v2Tag v2tag  = f.getID3v2Tag();
                
                System.out.println(audioHeader.getTrackLengthAsString());
                String artistTag = v2tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST);
                String parts[] = artistTag.split(" Feat. ");
                
                String artist = parts[0];
                artist.replace(',', ' ');
                String album = v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM);
                String song = v2tag.getFirst(ID3v24Frames.FRAME_ID_TITLE);
                
                
                if(checkExists(artist, "artist", "artistID") && artistID != previousID) {
                    artistID = getID(artist, "artist", "artistID");
                    System.out.println(artistID);
                } else {
                    insertArtist(artistID, artist);
                }
                
                if(checkExists(album, "album", "albumID")) {
                    albumID = getID(album, "album", "albumID");
                    System.out.println(albumID);
                } else {
                    insertAlbum(artistID, albumID, album, Integer.parseInt(v2tag.getFirst(FieldKey.YEAR)));
                }
                
                insertSong(albumID, songID, song, artistID, String.valueOf(audioHeader.getTrackLength()));
                
                
//                
                
                numFiles++;
                previousID = albumID;
                albumID++;
                artistID++;
                songID++;
//                if(Objects.equals(previousArtist, parts[0]) && previousID != id) {
////                    
//                } else if(previousArtist != parts[0]){
//                    insertArtist(id, parts[0]);
//                }
//                
//                if(Objects.equals(previousAlbum, v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM)) && previousID != id) {
//
//                } else if(previousAlbum != v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM)) {
//                    insertAlbum(id, id, v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM), Integer.parseInt(v2tag.getFirst(FieldKey.YEAR)));
//                }
//
//                if(Objects.equals(previousAlbum, v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM)) && previousID !=id) {
//                    id--;
//                    insertSong(id, songID , v2tag.getFirst(ID3v24Frames.FRAME_ID_TITLE), id, String.valueOf(audioHeader.getTrackLength()));
//                } else if(!Objects.equals(previousAlbum, v2tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM))) {
//                    insertSong(id, songID, v2tag.getFirst(ID3v24Frames.FRAME_ID_TITLE), id, String.valueOf(audioHeader.getTrackLength()));
//                }           id++;
                
                } else {
                numDirectories++;
                File[] files = file.listFiles();
                for (File otherFile : files) {
                    scanAndPopulate(otherFile);
                }
            }
        }

    public static Database getInstance() {
        if (music == null)
            music = new Database();
        return music;
    }
}
