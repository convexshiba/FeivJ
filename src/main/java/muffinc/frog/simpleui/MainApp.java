package muffinc.frog.simpleui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import muffinc.frog.eigenface.TrainingEngine;

/**
 * FROG, a Face Recognition Gallery in Java
 * Copyright (C) 2015 Jun Zhou
 * <p/>
 * This file is part of FROG.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * zj45499 (at) gmail (dot) com
 */
public class MainApp extends Application {

    TrainingEngine trainingEngine = null;

    private ObservableList<SimplePeople> simplePeopleObservableList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception {
        trainingEngine = new TrainingEngine();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/simpleui/SimplePane.fxml"));

        AnchorPane root = loader.load();

        SimplePaneController simplePaneController = loader.getController();
        simplePaneController.setMainApp(this);

        primaryStage.setTitle("this is a test");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<SimplePeople> getSimplePeopleObservableList() {
        return simplePeopleObservableList;
    }
}
