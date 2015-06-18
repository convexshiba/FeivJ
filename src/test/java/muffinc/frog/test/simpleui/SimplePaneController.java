package muffinc.frog.test.simpleui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
public class SimplePaneController implements Initializable{

    @FXML
    private TableView<SimplePeople> simplePeopleTableView;

    @FXML
    private TableColumn<SimplePeople, String> c1;

    @FXML
    private TableColumn<SimplePeople, Integer> c2;

    @FXML
    private TableColumn<SimplePeople, String> c3;


    @FXML
    private Button addFileButton;

    @FXML
    private ScrollPane scrollPane;

    private MainApp mainApp;

    private FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Application getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void trainButtonPushed() {
    }

    @FXML
    public void addFilePushed() {
//        String[] extensions = {"jpeg", "jpg", "pgm"};
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", );
//        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(addFileButton.getScene().getWindow());
//        System.out.println(file.getAbsolutePath() + " was chosen");

//        mainApp.trainingEngine.setInImg(file);

        if (mainApp.trainingEngine != null) {
            simplePeopleTableView.setItems(mainApp.getSimplePeopleObservableList());

            c1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            c2.setCellValueFactory(cellData -> cellData.getValue().fileNumsProperty().asObject());
//            c3.setCellValueFactory(cellDate -> cellDate.getValue().fileAddressProperty());
        }
    }
}
