package muffinc.frog.test.userinterface;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import muffinc.frog.test.object.FrogImg;

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
    private TableColumn<PhotoGem, PhotoGem> photoColumn;

    @FXML
    private TableColumn<PhotoGem, String> photoNameColumn;
    
    @FXML
    private TableColumn<PhotoGem, Integer> countColumn;

    @FXML
    private Button addFileButton;

    private FileChooser fileChooser = new FileChooser();

    @FXML
    private Button deleteSelectedButton;

    @FXML
    private Button scanNDetectButton;

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

        photoNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        countColumn.setCellValueFactory(cellData -> cellData.getValue().photoCountProperty().asObject());
        photoColumn.setCellFactory(param -> new PhotoTableCell());


    }

    class PhotoTableCell extends TableCell<PhotoGem, PhotoGem> {
        VBox vb;
        Label photoName;
        ImageView imageView;

        public PhotoTableCell() {
            vb = new VBox();
            vb.setAlignment(Pos.CENTER);
            photoName = new Label();
            imageView = new ImageView();

            vb.getChildren().addAll(imageView, photoName);
            setGraphic(vb);
        }

        @Override
        protected void updateItem(PhotoGem item, boolean empty) {
            if (item != null) {
                photoName.setText(item.getFileName());
                imageView.setImage(item.getImg());
            }
        }
    }

    public void setScanNDetectButton() {
        photoTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (photoTable.getSelectionModel().getSelectedItem() != null) {
                System.out.println();
                PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
                FrogImg frogImg = selected.getFrogImg();
                frogImg.detectFace();
            }
        });
    }

}
