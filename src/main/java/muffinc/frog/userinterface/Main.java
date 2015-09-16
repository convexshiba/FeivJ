package muffinc.frog.userinterface;

import com.thoughtworks.xstream.XStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import muffinc.frog.object.FrogImg;
import muffinc.frog.object.Human;
import muffinc.frog.eigenface.TrainingEngine;
import org.bytedeco.javacpp.opencv_core;

import java.io.*;

public class Main extends Application {

    XStream xStream = new XStream();

    TrainingEngine engine = null;

    MainController mainController = null;

    Stage primaryStage;



    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/uixml/main.fxml"));

        BorderPane root = loader.load();

        mainController = loader.getController();
        mainController.setMain(this);


        primaryStage.setTitle("Welcome to YaF RIVJ!");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        engine = new TrainingEngine();

        loadHumans();

    }

    //TODO Store loaded files in xml
    @Override
    public void stop() throws Exception {

//        File[] files = engine.humanFactory.frogImgTable.keySet().toArray(new File[1]);
//
//        String filesxml = xStream.toXML(files);
//
//
//        File filesXML = new File("/Users/Meth/Documents/FROG/src/main/resources/appxml/" + "filesXML.xml");
//        if (!filesXML.exists()) {
//            filesXML.createNewFile();
//        }
//
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filesXML.getAbsoluteFile()));
//
//        bufferedWriter.write(filesxml);
//        bufferedWriter.close();

//        FileWriter fileWriter = new FileWriter(engineXML.getAbsoluteFile());
//        fileWriter.write("adsfasdfasfd\nadsfasdfas");
//        fileWriter.close();
    }

    public static void main(String[] args) {
        launch(args);
    }


//    public ObservableList<PeopleGem> getPeopleGemObservableList() {
//        return peopleGemObservableList;
//    }



    public Human showAddPeopleDialogue() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/uixml/addPeopleDialogue.fxml"));

            AnchorPane anchorPane = loader.load();

            Stage dialogueStage = new Stage();
            dialogueStage.setTitle("Add People");
            dialogueStage.initModality(Modality.WINDOW_MODAL);
            dialogueStage.initOwner(primaryStage);
            Scene scene = new Scene(anchorPane);
            dialogueStage.setScene(scene);

            AddPeopleDialogueController controller = loader.getController();
            controller.setDialogStage(dialogueStage);

            dialogueStage.showAndWait();

            return newHuman(controller.name);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Human newHuman(String name) {

        Human human = engine.humanFactory.newHuman(name);
        mainController.updateHumanObservableList();

        return human;
    }

    private void loadHumans() {
        File file = new File(TrainingEngine.HUMAN_DIRECTORY);

        // ((FileFilter) new WildcardFileFilter("*"))
        for (File humanFile : file.listFiles()) {
            if (humanFile.isFile() || humanFile.listFiles().length == 0) {
//                System.out.println(humanFile.getName() + " is not a valid Human profile.");
            } else {
                Human human = engine.humanFactory.newHuman(humanFile.getName());

                for (File picFile : humanFile.listFiles()) {

                    if (picFile.getName().charAt(0) == '.') {
                        continue;
                    }

                    FrogImg frogImg = mainController.addNewImg(picFile, false).getFrogImg();
                    frogImg.detect();


                    for (opencv_core.CvRect cvRect : frogImg.getCvRects()) {

                        human.linkWithImgCvRect(frogImg, cvRect);

                    }

                }
            }
        }
        mainController.updateHumanObservableList();
    }
}
