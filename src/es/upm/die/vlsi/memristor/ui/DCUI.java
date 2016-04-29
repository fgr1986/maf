/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.ui;

import es.upm.die.vlsi.memristor.main.MAF;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorLibrary;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author fgarcia
 */
public class DCUI {

    private final MemristorLibrary.Memristor_Model memristorType;
    private TimingParametersUI timingParametersUI;
    private VoltageParametersUI voltageParametersUI;
    private MemristorParametersUI memristorParametersUI;
    private DCSimulationUI dcSimulationUI;
    // stage
    private final Stage stage;
    private final Scene scene;
    private final TabPane tabPane;

    private final MAF parentMAF;

    public DCUI( MemristorLibrary.Memristor_Model  memristorType, MAF parentMAF) {
        this.parentMAF = parentMAF;
        this.memristorType = memristorType;
        this.stage = new Stage();
        this.tabPane = new TabPane();
        startDCUI();
        stage.setTitle("Device Characterization: " + memristorType.title());
        //create scene with set width, height and color
        scene = new Scene(tabPane, 720, 500, Color.WHITESMOKE);
        //set scene to stage
        stage.setScene(scene);
        //center stage on screen
        stage.centerOnScreen();
        //show the stage
        stage.show();
    }

    private void startDCUI() {
        // tab pane
        tabPane.setSide(Side.TOP);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        final Tab tabTiming = new Tab();
        tabTiming.setText("Timing Parameters");
        timingParametersUI = new TimingParametersUI();
        tabTiming.setContent(timingParametersUI);

        final Tab tabVoltage = new Tab();
        tabVoltage.setText("Feeding Parameters");
        voltageParametersUI = new VoltageParametersUI();
        tabVoltage.setContent(voltageParametersUI);

        final Tab tabMemristor = new Tab();
        tabMemristor.setText("Memristor Parameters");
        memristorParametersUI = new MemristorParametersUI(memristorType);
        tabMemristor.setContent(memristorParametersUI);

        final Tab tabSimulation = new Tab();
        tabSimulation.setText("Simulation");
        tabPane.getTabs().addAll(tabTiming, tabVoltage, tabMemristor, tabSimulation);
        dcSimulationUI = new DCSimulationUI(memristorType, this,
                timingParametersUI, voltageParametersUI, memristorParametersUI);
        tabSimulation.setContent(dcSimulationUI);

    }

    public void changeCursor(Cursor cursor) {
        scene.setCursor(cursor);
    }

    public void RefreshLog(String log) {
        if (parentMAF != null) {
            parentMAF.RefreshLog(log);
        }
    }

}
