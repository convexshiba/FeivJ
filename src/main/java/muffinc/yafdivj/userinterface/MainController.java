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

import com.drew.metadata.Tag;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import muffinc.yafdivj.Main;
import muffinc.yafdivj.datatype.YafImg;
import muffinc.yafdivj.datatype.Human;
import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{

//    @FXML
//    private MenuBar menuBar;
    
    @FXML
    public TableView<PhotoGem> photoTable;
    @FXML
    private TableColumn<PhotoGem, String> photoNameColumn;
    @FXML
    private TableColumn<PhotoGem, Integer> countColumn;
    @FXML
    private TableColumn<PhotoGem, String> photoPeoplesColumn;

    @FXML
    public TableView<PeopleGem> humanTable;
    @FXML
    private TableColumn<PeopleGem, String> humanNameColumn;
    @FXML
    private TableColumn<PeopleGem, Integer> humanPhotoNumberColumn;

    @FXML
    private TableView<PhotoGem> humanPhotoTable;
    @FXML
    private TableColumn<PhotoGem, String> humanPhotoNameColumn;
    @FXML
    private TableColumn<PhotoGem, String> humanPhotoLocationColumn;

    @FXML
    private TableView<Tag> photoTagTable;
    @FXML
    private TableColumn<Tag, String> tagNameColumn;
    @FXML
    private TableColumn<Tag, String> tagContentColumn;


    @FXML
    private ImageView photoImageView;

    @FXML
    private HBox photoImageViewParent;

    @FXML
    private ImageView faceImageView;

    @FXML
    private ImageView humanPhotoImageView;

    @FXML
    private HBox humanPhotoImageViewParent;

    @FXML
    private Button addFileButton;

    @FXML
    private Button deleteSelectedPhotoButton;

    @FXML
    private Button deleteFaceButton;

    @FXML
    private Button scanButton;

    @FXML
    private Button scanAllButton;

    @FXML
    private Button detectAllButton;

    @FXML
    private Button addPeopleButton;

    @FXML
    private Button photoPageNewPeople;
    @FXML
    private Button photoPageSetAs;
    @FXML
    private ComboBox<String> setAsCombo;

    @FXML
    private TextField idTextField;

    @FXML
    private ComboBox<String> facesCombo;

    private Main main;

    private FileChooser fileChooser = new FileChooser();

    public ObservableList<PeopleGem> peopleGemObservableList = FXCollections.observableArrayList();
    private ObservableList<PhotoGem> photoGemObservableList = FXCollections.observableArrayList();

    private ObservableList<PhotoGem> peoplePhotoObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        initHumanTable();

        initPhotoTable();

        initHumanPhotoTable();


        initFaceComboPreview();

    }

    private void initHumanPhotoTable() {
        humanPhotoImageView.fitHeightProperty().bind(humanPhotoImageViewParent.heightProperty());

        humanPhotoTable.setItems(peoplePhotoObservableList);
        addHumanPhotoTableListener();
        humanPhotoNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        humanPhotoLocationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
    }

    private void addHumanPhotoTableListener() {
        humanPhotoTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (humanPhotoTable.getSelectionModel().getSelectedItem() != null) {
                repaintHumanPhotoImageView(humanPhotoTable.getSelectionModel().getSelectedItem(),
                        humanTable.getSelectionModel().getSelectedItem().getHuman());
            }

        });
    }

    private void initPhotoTable() {
        photoTable.setItems(photoGemObservableList);
        addPhotoTableListener();
        countColumn.setCellValueFactory(cellData -> cellData.getValue().photoCountProperty().asObject());
        photoNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        photoPeoplesColumn.setCellValueFactory(cellData -> cellData.getValue().peopleNamesProperty());

    }

    private void initHumanTable() {
        humanTable.setItems(peopleGemObservableList);
        addHumanTableListener();
        humanNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        humanPhotoNumberColumn.setCellValueFactory(cellData -> cellData.getValue().photoNumberProperty().asObject());

        tagNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTagName()));
        tagContentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    }

    private void addHumanTableListener() {
        humanTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (humanTable.getSelectionModel().getSelectedItem() != null) {
                Human human = humanTable.getSelectionModel().getSelectedItem().getHuman();

                peoplePhotoObservableList.clear();
                for (YafImg yafImg : human.frogImgs.keySet()) {
                    peoplePhotoObservableList.add(new PhotoGem(yafImg));
                }
                repaintHumanPhotoImageView(null, null);
            }
            humanPhotoTable.getSelectionModel().selectFirst();
        });
    }


    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }


    public PhotoGem addNewImg(File file) {
        return addNewImg(file, true);
    }

    public PhotoGem addNewImg(File file, boolean doScan) {
        PhotoGem photoGem = new PhotoGem(main.engine.addNewImg(file, doScan));
        photoGemObservableList.add(photoGem);
        return photoGem;
    }

    public void deleteImg(PhotoGem photoGem) {
        photoGem.getYafImg().delete();
        photoGemObservableList.remove(photoGem);
    }

    public void handleAddFileButton() {
        String[] extensions = {"*.jpeg", "*.jpg", "*.pgm", "*.tif", "*.gif"};
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", extensions);
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> files = fileChooser.showOpenMultipleDialog(addFileButton.getScene().getWindow());
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getAbsolutePath() + " was chosen");
                addNewImg(file);
            }
        }

        updateHumanObservableList();
    }

    //TODO wrong update method
    public void updateHumanObservableList() {
        peopleGemObservableList.clear();

        for (Human human : main.engine.humanFactory.nameTable.values()) {
            peopleGemObservableList.add(new PeopleGem(human));
        }
    }

    //TODO does this work?
    public void handleDeleteSelectedPhotoButton() {
        if (photoTable.getSelectionModel().getSelectedItem() != null) {
            PhotoGem photoGem = photoTable.getSelectionModel().getSelectedItem();
            deleteImg(photoGem);
            photoTable.setItems(photoGemObservableList);
        }
    }

    private void initFaceComboPreview() {
        facesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (photoTable.getSelectionModel().getSelectedItem().getYafImg().isDetected() && newValue != null) {
                int i = parseSelectedFaceIndex(newValue);

                if (i >= 0) {
//                    System.out.println("selected face: " + newValue.substring(i));
                    PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
                    opencv_core.CvRect cvRect = selected.getYafImg().getCvRects().get(i);
                    Rectangle2D rectangle2D = new Rectangle2D(cvRect.x(), cvRect.y(), cvRect.width(), cvRect.height());
                    faceImageView.setImage(selected.getYafImg().getOriginalImage());
                    faceImageView.setViewport(rectangle2D);
                }

//                repaintIdText(newValue);

                repaintHumanText(newValue);

            }
        });
    }


    //Parse integer from newValue
    private int parseSelectedFaceIndex(String newValue) {
        int i;

        if (newValue != null) {
            for (i = newValue.length() - 1; i >= 0; i--) {
                if (newValue.charAt(i) < '0' || newValue.charAt(i) > '9') {
                    break;
                }
            }

            if (newValue.substring(++i).length() > 0) {
                return Integer.parseInt(newValue.substring(i)) - 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private void addPhotoTableListener() {
        photoImageView.fitHeightProperty().bind(photoImageViewParent.heightProperty());
//        photoImageView.fitWidthProperty().bind(photoImageViewParent.widthProperty());
        photoTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            faceImageView.setImage(null);
            if (photoTable.getSelectionModel().getSelectedItem() != null) {

                PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
                repaintPhotoImageView(selected);
                repaintFacesCombo(selected);
                repaintHumanText(null);

                photoTagTable.setItems(selected.getYafImg().getTagsObservableList());

            }

            facesCombo.getSelectionModel().selectFirst();

        });

    }

    public void handleReScanButton() {
        YafImg yafImg = photoTable.getSelectionModel().getSelectedItem().getYafImg();
        opencv_core.CvRect cvRect = yafImg.getCvRects().get(parseSelectedFaceIndex(facesCombo.getValue()));
        yafImg.redoCvRect(cvRect);
        repaintHumanText(facesCombo.getValue());
    }


    public void handleScanButton() {
        if (photoTable.getSelectionModel().getSelectedItem() != null) {
            PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
            selected.getYafImg().detectAndID();
            repaintPhotoImageView(selected);
            repaintFacesCombo(selected);
        }
    }

    public void handleScanAllButton() {
        for (PhotoGem photoGem : photoTable.getItems()) {
            photoGem.getYafImg().detectAndID();
            repaintPhotoImageView(photoGem);
//            repaintFacesCombo(photoGem);
        }
    }

    public void handleDeleteFaceButton() {
        if (photoTable.getSelectionModel().getSelectedItem() != null) {
            PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();

            int i = parseSelectedFaceIndex(facesCombo.getValue());

            if (i >= 0) {
                YafImg yafImg = photoTable.getSelectionModel().getSelectedItem().getYafImg();
                yafImg.removeCvRect(i);
                repaintPhotoImageView(selected);
                repaintFacesCombo(selected);
            }
            resetFaceImageView();
            repaintHumanText(null);
            facesCombo.getSelectionModel().selectFirst();
        }
    }

    private void resetFaceImageView() {
        faceImageView.setImage(null);
    }


    private void repaintHumanPhotoImageView(PhotoGem selected, Human human) {
        if (selected != null & human != null) {
            humanPhotoImageView.setImage(selected.getYafImg().getThisHumanImage(human));
        } else {
            humanPhotoImageView.setImage(null);
        }
    }

    private void repaintPhotoImageView(PhotoGem selected) {
        photoImageView.setImage(selected.getYafImg().getCurrentImage());
    }

    private void repaintFacesCombo(PhotoGem selected) {
        ObservableList<String> faces = FXCollections.observableArrayList();
        if (selected.getYafImg().isDetected()) {
            if (selected.getYafImg().faceNumber() >= 1) {
                for (int i = 0; i < selected.getYafImg().faceNumber();) {
                    faces.add("Face " + ++i);
                }
                facesCombo.setValue("Please Select Face:");
            } else  {
                facesCombo.setValue("Face Not Found");
            }
        } else {
            facesCombo.setValue("Please Scan This Photo First!");
        }
        facesCombo.setItems(faces);
    }

    private void repaintHumanText(int i) {
        YafImg yafImg = photoTable.getSelectionModel().getSelectedItem().getYafImg();
        opencv_core.CvRect cvRect = yafImg.getCvRects().get(i);

        String thisIs = yafImg.whoIsThisCvRect(cvRect);
        idTextField.setText(thisIs);
        repaintSetAsCombo();
    }

    private void repaintHumanText(String newValue) {

        if (newValue != null) {
            int i = parseSelectedFaceIndex(newValue);

            if (i >= 0) {
                YafImg yafImg = photoTable.getSelectionModel().getSelectedItem().getYafImg();
                opencv_core.CvRect cvRect = yafImg.getCvRects().get(i);

                String thisIs = yafImg.whoIsThisCvRect(cvRect);
                idTextField.setText(thisIs);
                repaintSetAsCombo();

//                if (thisIs.equals("新人?")) {
//                    newPeopleGroup.setVisible(true);
//                    repaintSetAsCombo();
//                } else {
//                    newPeopleGroup.setVisible(false);
//                }
            }
        } else {
            idTextField.clear();
        }

    }

    private void repaintSetAsCombo() {
        ObservableList<String> observableList = FXCollections.observableArrayList();

        for (Human human : main.engine.humanFactory.nameTable.values()) {
            observableList.add(human.name);
        }

        setAsCombo.setValue("请选择人物:");
        setAsCombo.setItems(observableList);
    }

    //TODO seems working
    public void handlePhotoPageNewPeople() {
        int i = parseSelectedFaceIndex(facesCombo.getSelectionModel().getSelectedItem());

        if (i >= 0) {
            YafImg yafImg = photoTable.getSelectionModel().getSelectedItem().getYafImg();

            Human human = main.showAddPeopleDialogue();
            human.linkWithImgCvRect(yafImg, yafImg.getCvRects().get(i));
            repaintHumanText(i);
        }

    }

    // TODO seems working
    public void handlePhotoPageSetAs() {

        int i = parseSelectedFaceIndex(facesCombo.getSelectionModel().getSelectedItem());

        if (setAsCombo.getSelectionModel().getSelectedItem() != null && i >= 0) {
            Human human = main.engine.humanFactory.nameTable.get(setAsCombo.getSelectionModel().getSelectedItem());

            YafImg yafImg = photoTable.getSelectionModel().getSelectedItem().getYafImg();

            human.linkWithImgCvRect(yafImg, yafImg.getCvRects().get(i));
            repaintHumanText(i);
        }
        repaintSetAsCombo();
    }


    public void handleAddPeople() {
        main.showAddPeopleDialogue();
    }


    //TODO add detect all
    public void handleDetectAllButton() {

    }

}
