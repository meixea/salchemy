package org.meixea.salchemy.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class AlchemyApplication extends Application {

    static public AppProperties appProperties = new AppProperties();

    static public void main(String[] args) {

        appProperties.restore();

        launch();
    }
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(AlchemyApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Scyrim Alchemy");
        stage.setScene(scene);


        URL icon = AlchemyApplication.class.getResource("icons/app.png");
        stage.getIcons().add(new Image(icon.toString()));

        stage.show();

    }

}