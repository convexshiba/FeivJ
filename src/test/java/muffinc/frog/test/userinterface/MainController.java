package muffinc.frog.test.userinterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import muffinc.frog.test.object.FrogImg;
import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML
    private MenuBar menuBar;
    
    @FXML
    public TableView<PhotoGem> photoTable;
    
    @FXML
    private TableColumn<PhotoGem, String> photoNameColumn;
    
    @FXML
    private TableColumn<PhotoGem, Integer> countColumn;

    //TODO center and auto resize ImageView
    @FXML
    private ImageView photoImageView;

    @FXML
    private ImageView faceImageView;

    @FXML
    private StackPane photoImageViewParent;

    @FXML
    private Button addFileButton;

    @FXML
    private Button deleteSelectedPhotoButton;

    @FXML
    private Button deleteFaceButton;

    @FXML
    private Button scanNDetectButton;

    @FXML
    private Button idButton;

    @FXML
    private Button idAllButton;

    @FXML
    private TextArea idText;

    @FXML
    private ComboBox<String> facesCombo;

    private Main main;

    private FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //TODO add file edit and rename
//        photoNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        photoNameColumn.setOnEditCommit(
//                event -> (event.getTableView().getItems()
//                        .get(event.getTablePosition().getRow()))
//                        .setFileName(event.getNewValue())
//        );


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

        addFaceImagePreviewListener();
    }

    public void setDeleteSelectedPhotoButton() {
        if (photoTable.getSelectionModel().getSelectedItem() != null) {
            PhotoGem photoGem = photoTable.getSelectionModel().getSelectedItem();
            main.deleteImg(photoGem);
            photoTable.setItems(main.getPhotoGemObservableList());
        }
    }

    private void addFaceImagePreviewListener() {
        facesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (photoTable.getSelectionModel().getSelectedItem().getFrogImg().isDetected() && newValue != null) {
                int i = parseSelectedFaceIndex(newValue);

                if (i >= 0) {
//                    System.out.println("selected face: " + newValue.substring(i));
                    PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
                    opencv_core.CvRect cvRect = selected.getFrogImg().getCvRects().get(i);
                    Rectangle2D rectangle2D = new Rectangle2D(cvRect.x(), cvRect.y(), cvRect.width(), cvRect.height());
                    faceImageView.setImage(photoImageView.getImage());
                    faceImageView.setViewport(rectangle2D);
                }

                repaintIdText(newValue);
            }
        });
    }


    //Parse integer from newValue
    private int parseSelectedFaceIndex(String newValue) {
        int i;

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
    }

    private void addPhotoPreviewListener() {
        photoImageView.fitHeightProperty().bind(photoImageViewParent.heightProperty());
//        photoImageView.fitWidthProperty().bind(photoImageViewParent.widthProperty());
        photoTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            faceImageView.setImage(null);
            if (photoTable.getSelectionModel().getSelectedItem() != null) {

                PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
                repaintPhotoImageView(selected);


                repaintFacesCombo(selected);

            }
        });
    }



    public void setScanNDetectButton() {

        if (photoTable.getSelectionModel().getSelectedItem() != null) {
            System.out.println();
            PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();
            selected.getFrogImg().detectFace();
            repaintPhotoImageView(selected);
            repaintFacesCombo(selected);
        }
    }

    public void setDeleteFaceButton() {
        if (photoTable.getSelectionModel().getSelectedItem() != null) {

            PhotoGem selected = photoTable.getSelectionModel().getSelectedItem();

            int i = parseSelectedFaceIndex(facesCombo.getValue());

            if (i >= 0) {
                FrogImg frogImg = photoTable.getSelectionModel().getSelectedItem().getFrogImg();
                frogImg.removeCvRect(i);
                repaintPhotoImageView(selected);
                repaintFacesCombo(selected);
            }
            resetFaceImageView();
        }
    }

    private void resetFaceImageView() {
        faceImageView.setImage(null);
    }


    private void repaintPhotoImageView(PhotoGem selected) {
        photoImageView.setImage(selected.getFrogImg().getCurrentImage());
    }

    private void repaintFacesCombo(PhotoGem selected) {
        ObservableList<String> faces = FXCollections.observableArrayList();
        if (selected.getFrogImg().isDetected()) {
            if (selected.getFrogImg().faceNumber() >= 1) {
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

    private void repaintIdText(String newValue) {
        int i = parseSelectedFaceIndex(newValue);

        //TODO repaint Not Yet Finished.
        if (i >= 0) {
            FrogImg frogImg = photoTable.getSelectionModel().getSelectedItem().getFrogImg();
            opencv_core.CvRect cvRect = frogImg.getCvRects().get(i);
            if (frogImg.idMatrices.containsKey(cvRect)) {
                StringWriter sw = new StringWriter();

                PrintWriter pw = new PrintWriter(sw);

                frogImg.idMatrices.get(cvRect).print(pw, 6, 2);

                idText.setText(sw.toString());
            } else {
                idText.setText("Please ID this face first.");
            }
        } else {
            idText.setText("Please Select Face.");
        }
    }

    public void setIdButton() {

        int i = parseSelectedFaceIndex(facesCombo.getValue());

        if (i >= 0) {
            FrogImg frogImg = photoTable.getSelectionModel().getSelectedItem().getFrogImg();
            opencv_core.CvRect cvRect = frogImg.getCvRects().get(i);

            main.engine.getIdentify(frogImg, cvRect);

            repaintIdText(facesCombo.getValue());
        }
    }

    public void setIdAllButton() {

    }



}
