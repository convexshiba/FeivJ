package muffinc.frog.test.userinterface;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import muffinc.frog.test.object.FrogImg;
import sun.awt.SunGraphicsCallback;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    private Main main;

    @FXML
    private MenuBar menuBar;
    
    @FXML
    private TableView<PhotoGem> photoTable;
    
    @FXML
    private TableColumn<PhotoGem, String> photoNameColumn;
    
    @FXML
    private TableColumn<PhotoGem, Integer> countColumn;

    //TODO center and auto resize ImageView
    @FXML
    private ImageView photoImageView;

    @FXML
    private StackPane photoImageViewParent;

    @FXML
    private Button addFileButton;

    private FileChooser fileChooser = new FileChooser();

    @FXML
    private Button deleteSelectedButton;

    @FXML
    private Button scanNDetectButton;

    @FXML
    private ComboBox<String> facesCombo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setAddFileButton() {
        String[] extensions = {"*.jpeg", "*.jpg", "*.pgm"};
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", extensions);
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> files = fileChooser.showOpenMultipleDialog(addFileButton.getScene().getWindow());
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getAbsolutePath() + " was chosen");
                main.addNewImg(file);
            }
        }


        photoTable.setItems(main.getPhotoGemObservableList());

        addPhotoPreviewListener();

        countColumn.setCellValueFactory(cellData -> cellData.getValue().photoCountProperty().asObject());

        photoNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());

        //TODO add file edit and rename
//        photoNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        photoNameColumn.setOnEditCommit(
//                event -> (event.getTableView().getItems()
//                        .get(event.getTablePosition().getRow()))
//                        .setFileName(event.getNewValue())
//        );

    }

    private void addPhotoPreviewListener() {
        photoImageView.fitHeightProperty().bind(photoImageViewParent.heightProperty());
//        photoImageView.fitWidthProperty().bind(photoImageViewParent.widthProperty());
        photoTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (photoTable.getSelectionModel().getSelectedItem() != null) {

                PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
                photoImageView.setImage(selected.getFrogImg().getCurrentImage());

                ObservableList<String> faces = FXCollections.observableArrayList();

                if (selected.getFrogImg().isDetected()) {
                    if (selected.getFrogImg().faceNumber() > 1) {
                        for (int i = 0; i < selected.getFrogImg().faceNumber();) {
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
        });
    }


    public void setScanNDetectButton() {

        if (photoTable.getSelectionModel().getSelectedItem() != null) {
            System.out.println();
            PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
            FrogImg frogImg = selected.getFrogImg();
            frogImg.detectFace();
            photoImageView.setImage(frogImg.getCurrentImage());
        }
    }

}
