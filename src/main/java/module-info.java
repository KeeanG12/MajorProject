module uk.aber.ac.keg21.musicapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires jaudiotagger;
    requires java.sql;
    requires javafx.media;


    opens uk.aber.ac.keg21.musicapp to javafx.fxml;
    exports uk.aber.ac.keg21.musicapp;
    exports uk.aber.ac.keg21.musicapp.UI;
    opens uk.aber.ac.keg21.musicapp.UI to javafx.fxml;
}