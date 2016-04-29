/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.main;

import es.upm.die.vlsi.memristor.resources.FilesFoldersManagement;
import java.util.Date;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorLibrary;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorLibrary.Memristor_Model;
import es.upm.die.vlsi.memristor.ui.DCUI;
import es.upm.die.vlsi.memristor.ui.DialogsManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javafx.stage.FileChooser;

/**
 *
 * @author fgarcia
 */
public class MAF extends Application {

    private DCUI dc_ui;
    private TextArea mafMonitor;
    private DialogsManager dm;

    @Override
    public void start(Stage primaryStage) {
        // Better rendering?
//        System.setProperty("prism.order", "j2d");
        // Main panel
        BorderPane mafBorderPane = new BorderPane();
        dm = new DialogsManager();
        // Menu        
        MenuBar menuBar = new MenuBar();
        // --- Menu Characterization
        Menu menuDC = new Menu("Device Characterization");
        int modelCount = 0;
        for (Memristor_Model mm : MemristorLibrary.Memristor_Model.values()) {
            String m = mm.title();
            MenuItem memristorItem = new MenuItem(m);
            int localModelCount = modelCount;
            memristorItem.setOnAction((ActionEvent t) -> {
                dc_ui = new DCUI(MemristorLibrary.Memristor_Model.values()[localModelCount], this);
            });
            menuDC.getItems().add(memristorItem);
            modelCount++;
        }
        // --- Menu Multilevel
        Menu menuMLO = new Menu("Multilevel Operation");
        menuMLO.setDisable(true);
        // --- Other Menus
        Menu menuAbout = new Menu("About");
        MenuItem menuItemMaf = new MenuItem("About MAF");
        menuItemMaf.setOnAction((ActionEvent t) -> {
            final Stage stage = new Stage();
            //create root node of scene, i.e. group
            Group rootGroup = new Group();
            //create scene with set width, height and color
            Scene scene = new Scene(rootGroup, 500, 300, Color.WHITESMOKE);
            //set scene to stage
            stage.setScene(scene);
            //center stage on screen
            stage.centerOnScreen();
            //show the stage
            stage.show();
            Label label = new Label("MAF v2.0", dm.GetIcon("/maf/resources/memristor.png"));
            //add text to the main root group
            rootGroup.getChildren().add(label);
        });
        MenuItem menuItemModels = new MenuItem("About the memristor models");
        menuItemModels.setOnAction((ActionEvent t) -> {
            final Stage stage = new Stage();
            //create root node of scene, i.e. group
            Group rootGroup = new Group();
            //create scene with set width, height and color
            Scene scene = new Scene(rootGroup, 500, 300, Color.WHITESMOKE);
            //set scene to stage
            stage.setScene(scene);
            //center stage on screen
            stage.centerOnScreen();
            //show the stage
            stage.show();
            Label label = new Label(ResourcesMAF.APPTITLE
                    + " " + ResourcesMAF.APPVERSION, dm.GetIcon("/maf/resources/memristor.png"));
            //add text to the main root group
            rootGroup.getChildren().add(label);
        });
        menuAbout.getItems().addAll(menuItemMaf, menuItemModels);

        Menu menuExit = new Menu("Exit");
        MenuItem menuItemExit = new MenuItem("Exit");
        menuItemExit.setOnAction((ActionEvent t) -> {
            System.exit(0);
        });
        menuExit.getItems().add(menuItemExit);
        // menu save
        Menu menuSave = new Menu("Save log");
        MenuItem menuItemSave = new MenuItem("Save log to file");
        menuItemSave.setOnAction((ActionEvent t) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save corners file");
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                Writer writer = null;
                try {
                    // init
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.write("#################################\n");
                    writer.write("##" + new Date().toString() + "##\n");
                    writer.write("#################################\n");
                    int tfCount = 0;
                    writer.write(mafMonitor.getText());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    FilesFoldersManagement.close(writer);
                }
            } else {
                dm.errorDialog( "No file saved");
            }
        });
        menuSave.getItems().add(menuItemSave);
        // add menus
        menuBar.getMenus().addAll(menuDC, menuMLO, menuAbout, menuExit, menuSave);

        // ScrollPane
        ScrollPane sp = new ScrollPane();
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        // text area monitor
        mafMonitor = new TextArea();
        mafMonitor.setEditable(false);
        sp.setContent(mafMonitor);

        // add to container
        mafBorderPane.setTop(menuBar);
        mafBorderPane.setCenter(sp);

        StackPane root = new StackPane();
        root.getChildren().add(mafBorderPane);

        Scene scene = new Scene(root, 600, 300);

        RefreshLog(ResourcesMAF.APPTITLE);
        RefreshLog(ResourcesMAF.APPVERSION);
        RefreshLog(ResourcesMAF.WEB);
        RefreshLog("-------------------------------");
        RefreshLog("");
        RefreshLog(new Date().toString());
        primaryStage.setTitle("MAF");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void RefreshLog(final String message) {
        mafMonitor.appendText("\n" + message);
    }
}
