package muffinc.frog.test.userinterface;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import muffinc.frog.test.eigenface.TrainingEngine;
import muffinc.frog.test.object.FrogImg;
import muffinc.frog.test.simpleui.SimplePaneController;

import java.io.File;

public class Main extends Application {

    TrainingEngine engine = null;

    private ObservableList<PhotoGem> photoGemObservableList = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/uixml/main.fxml"));

        BorderPane root = loader.load();

        MainController mainController = loader.getController();
        mainController.setMain(this);

        primaryStage.setTitle("FROG");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        engine = new TrainingEngine();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<PhotoGem> getPhotoGemObservableList() {
        return photoGemObservableList;
    }

    public void addNewImg(File file) {
        FrogImg frogImg = engine.addNewImg(file);
        photoGemObservableList.add(new PhotoGem(frogImg));
    }
}
