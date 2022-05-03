module uk.aber.ac.keg.musicapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires jaudiotagger;
    requires sqlite.jdbc;
    requires javafx.media;
    requires java.sql;


    opens uk.aber.ac.keg21.musicapp to javafx.fxml;
    exports uk.aber.ac.keg21.musicapp;
}