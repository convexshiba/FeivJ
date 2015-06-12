package muffinc.frog.test.userinterface;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
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
import org.apache.commons.io.IOUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Set;

public class Main extends Application {

    XStream xStream = new XStream();

    TrainingEngine engine = null;

    MainController mainController = null;

    private ObservableList<PhotoGem> photoGemObservableList = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/uixml/main.fxml"));

        BorderPane root = loader.load();

        mainController = loader.getController();
        mainController.setMain(this);

        primaryStage.setTitle("FROG");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        engine = new TrainingEngine();

    }

    //TODO add files preloading
    public void preload() {
        try {

            FileInputStream fileInputStream = new FileInputStream("/Users/Meth/Documents/FROG/src/test/resources/appxml/" + "filesXML.xml");

            try {
                String wholeFile = IOUtils.toString(fileInputStream);

                File[] files = (File[]) xStream.fromXML(wholeFile);

                for (File file : files) {
                    addNewImg(file);
                }

                mainController.photoTable.setItems(getPhotoGemObservableList());
            } finally {
                fileInputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {

        //TODO Store loaded files in xml
        File[] files = engine.humanFactory.frogImgTable.keySet().toArray(new File[1]);

        String filesxml = xStream.toXML(files);


        File filesXML = new File("/Users/Meth/Documents/FROG/src/test/resources/appxml/" + "filesXML.xml");
        if (!filesXML.exists()) {
            filesXML.createNewFile();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filesXML.getAbsoluteFile()));

        bufferedWriter.write(filesxml);
        bufferedWriter.close();

//        FileWriter fileWriter = new FileWriter(engineXML.getAbsoluteFile());
//        fileWriter.write("adsfasdfasfd\nadsfasdfas");
//        fileWriter.close();
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

    // TODO delete not yet finished
    public void deleteImg(PhotoGem photoGem) {
        FrogImg frogImg = photoGem.getFrogImg();
        photoGemObservableList.remove(photoGem);

    }
}
