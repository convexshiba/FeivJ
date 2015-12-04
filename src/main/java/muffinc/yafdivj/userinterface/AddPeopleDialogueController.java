/*
 * Copyright (c) 2015.  Jun Zhou
 *
 * YaF-DIVJ, Yet another Face Detection Image Viewer in Java
 * <p/>
 * This file is part of YaF-DIVJ.
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

package muffinc.yafdivj.userinterface;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddPeopleDialogueController implements Initializable{

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    @FXML
    private TextField nameField;

    private Stage dialogStage;
//    private boolean okClicked;
    public String name = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // TODO empty string error
    public void handleOK() {
        name = nameField.getText();

        if (name.equals("")) {
            return;
        }

        dialogStage.close();
    }

    public void handleCancel() {
        dialogStage.close();
    }

//    public boolean isOkClicked() {
//        return okClicked;
//    }
}
