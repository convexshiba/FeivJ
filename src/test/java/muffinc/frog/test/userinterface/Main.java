package muffinc.frog.test.userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import muffinc.frog.test.eigenface.TrainingEngine;

public class Main extends Application {
    TrainingEngine engine = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/uixml/main.fxml"));
        primaryStage.setTitle("FROG");
        primaryStage.setScene(new Scene(root, 1000, 650));
        primaryStage.setResizable(false);
        primaryStage.show();

        engine = new TrainingEngine();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
