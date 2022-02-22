package uk.aber.ac.keg21.musicapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("KG Music Player");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        //launch();
        
        Database music = Database.getInstance();
//        music.startUp();
        music.scanAndPopulate(new File("D:/UniWork/Third Year/Major Project/MajorProject/src/main/resources/uk/aber/ac/keg21/musicapp/Tunes"));
        music.toString();
//        
//        music.delete();
        
    }
}
