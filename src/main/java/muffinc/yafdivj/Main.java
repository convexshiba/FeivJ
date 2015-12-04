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

package muffinc.yafdivj;

import com.thoughtworks.xstream.XStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import muffinc.yafdivj.datatype.YafImg;
import muffinc.yafdivj.datatype.Human;
import muffinc.yafdivj.eigenface.TrainingEngine;
import muffinc.yafdivj.userinterface.AddPeopleDialogueController;
import muffinc.yafdivj.userinterface.MainController;
import org.bytedeco.javacpp.opencv_core;

import java.io.*;

public class Main extends Application {

    XStream xStream = new XStream();

    public TrainingEngine engine = null;

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
        File file = new File(getClass().getClassLoader().getResource(TrainingEngine.HUMAN_DIRECTORY).getPath());

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

                    YafImg yafImg = mainController.addNewImg(picFile, false).getYafImg();
                    yafImg.detect();


                    for (opencv_core.CvRect cvRect : yafImg.getCvRects()) {

                        human.linkWithImgCvRect(yafImg, cvRect);

                    }

                }
            }
        }
        mainController.updateHumanObservableList();
    }
}
