/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.ui;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.math.FittingCurvesResult;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorLibrary;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.simulations.DCCornersManager;
import es.upm.die.vlsi.memristor.simulations.DCMonteCarloManager;
import es.upm.die.vlsi.memristor.simulations.DCThreadManager;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 *
 * @author fgarcia
 */
public class DCSimulationUI extends StackPane {

    private final MemristorLibrary.Memristor_Model memristorType;
    private final TextField tfSimulationTitle;
    private final TextField tfMonteCarloThreads;
    private final TextField tfMinLogIPlotted;
    private final TextField tfUseMemoryBuffer;
    private final TextField tfDataDecimation;
    private final CheckBox cbPlotOnlyFittingCurves;
    private final CheckBox cbLogI;
    private final CheckBox cbDataDecimation;
    private final ToggleButton tbUseMemoryBuffer;
//    private final ToggleButton tbStoreInDisk;

    private final ToggleButton tbMonteCarlo;
    private final ToggleButton tbCorner;

    private final CheckBox cbComplianceCurrentSimulations;
    private final TextField tfPositiveComplianceCurrentSimulations;
    private final TextField tfNegativeComplianceCurrentSimulations;
    private final ToggleButton tbImportCSV;
    private final CheckBox cbFitCurves;
    private final Label lbImportCSV;
    private final CheckBox cbMaxParalellThreads;
    private final TextField tfMaxParalellThreads;
    private boolean importCSV;
    private String[] csvFiles;

    private final Button bCharacterizePlotJFreeChart;
    private final Button bCharacterizePlotGnuPlot;
    private final Button bCharacterize;
    private final Button bCancel;
    private final Button bSubcircuit;

    private boolean plotAfterSimulate;
    private boolean plotGnuPlot;
    private DCThreadManager tm;
    private final MemristorParametersUI memristorUI;
    private final TimingParametersUI timingUI;
    private final VoltageParametersUI voltageUI;
    private MemristorModel memristorPilot;
    private final DCUI dcUI;
    private int simulationType;

    private final DialogsManager dm;

    public DCSimulationUI(MemristorLibrary.Memristor_Model memristorType,
            DCUI dcUI,
            TimingParametersUI timingUI,
            VoltageParametersUI voltageUI,
            MemristorParametersUI memristorUI) {
        dm = new DialogsManager();
        this.memristorType = memristorType;
        this.timingUI = timingUI;
        this.voltageUI = voltageUI;
        this.memristorUI = memristorUI;
        this.dcUI = dcUI;
        this.simulationType = ResourcesMAF.MONTECARLO_SIMULATION;
        csvFiles = new String[0];

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15, 15, 15, 15));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);

        Label lbParameters = new Label("Simulation Parameters");
        lbParameters.setFont(ResourcesMAF.TITLEFONT);
        gridPane.add(lbParameters, 0, 0);

        ToggleGroup groupSimulationType = new ToggleGroup();
        groupSimulationType.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle selectedToggle) -> {
            if (selectedToggle == null) {
                oldValue.setSelected(true);
                return;
            }
            SelectSimulationType(groupSimulationType.getToggles().indexOf(selectedToggle));
        });

        GridPane gpSimulationParameters = new GridPane();
        gpSimulationParameters.setPadding(new Insets(5, 5, 5, 5));
        gpSimulationParameters.setHgap(5);
        gpSimulationParameters.setVgap(5);
        gpSimulationParameters.setAlignment(Pos.TOP_LEFT);
        int simulationParamRowCount = 0;
        // Simulation Title
        Label lbSimulationTitle = new Label("Simulation title:");
        resizeLabel(lbSimulationTitle, false);
        tfSimulationTitle = new TextField((new Date().toString() + "_" + memristorType.title()).replace(" ", "_").replace(":", "."));
        resizeTextField(tfSimulationTitle);
        gpSimulationParameters.add(lbSimulationTitle, 0, simulationParamRowCount);
        gpSimulationParameters.add(tfSimulationTitle, 1, simulationParamRowCount++);
        // Parallel threads
        cbMaxParalellThreads = new CheckBox("Max concurrent threads:");
        resizeCheckBox(cbMaxParalellThreads, true);
        tfMaxParalellThreads = new TextField("" + ResourcesMAF.MAXFD);
        resizeTextField(tfMaxParalellThreads);
        tfMaxParalellThreads.setDisable(true);
        cbMaxParalellThreads.setOnAction((ActionEvent e) -> {
            if (cbMaxParalellThreads.isSelected()) {
                tfMaxParalellThreads.setDisable(false);
            } else {
                tfMaxParalellThreads.setDisable(true);
            }
        });
        gpSimulationParameters.add(cbMaxParalellThreads, 0, simulationParamRowCount);
        gpSimulationParameters.add(tfMaxParalellThreads, 1, simulationParamRowCount++);
        // Montecarlo
        tbMonteCarlo = new ToggleButton("Monte Carlo Analysis");
        resizeToggleButton(tbMonteCarlo);
        tbMonteCarlo.setToggleGroup(groupSimulationType);
        tfMonteCarloThreads = new TextField("100");
        resizeTextField(tfMonteCarloThreads);
        // Corner
        tbCorner = new ToggleButton("Corner Analysis");
        tbCorner.setToggleGroup(groupSimulationType);
        resizeToggleButton(tbCorner);
        gpSimulationParameters.add(tbCorner, 0, simulationParamRowCount);
        gpSimulationParameters.add(tbMonteCarlo, 1, simulationParamRowCount);
        gpSimulationParameters.add(tfMonteCarloThreads, 2, simulationParamRowCount++);
        // complain current during simulation
        cbComplianceCurrentSimulations = new CheckBox("Compliance current \nduring simulations:");
        resizeCheckBox(cbComplianceCurrentSimulations, true);
        tfPositiveComplianceCurrentSimulations = new TextField("1e-4");
        resizeTextField(tfPositiveComplianceCurrentSimulations);
        tfNegativeComplianceCurrentSimulations = new TextField("-1e-2");
        resizeTextField(tfNegativeComplianceCurrentSimulations);
        cbComplianceCurrentSimulations.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            tfPositiveComplianceCurrentSimulations.setDisable(!new_val);
            tfNegativeComplianceCurrentSimulations.setDisable(!new_val);
        });
        gpSimulationParameters.add(cbComplianceCurrentSimulations, 0, simulationParamRowCount);
        gpSimulationParameters.add(tfPositiveComplianceCurrentSimulations, 1, simulationParamRowCount);
        gpSimulationParameters.add(tfNegativeComplianceCurrentSimulations, 2, simulationParamRowCount++);
        cbComplianceCurrentSimulations.setSelected(true);
        tfPositiveComplianceCurrentSimulations.setDisable(true);
        tfNegativeComplianceCurrentSimulations.setDisable(true);
        // Log Current
        cbLogI = new CheckBox("Plot logarithmic current:");
        resizeCheckBox(cbLogI, false);
        tfMinLogIPlotted = new TextField("1e-9");
        resizeTextField(tfMinLogIPlotted);
        cbLogI.setSelected(true);
        cbLogI.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            tfMinLogIPlotted.setDisable(!new_val);
        });
        // Import CSV
        tbImportCSV = new ToggleButton("Import V-I CSV Measures (linear)");
        resizeToggleButton(tbImportCSV);
        gpSimulationParameters.add(tbImportCSV, 0, simulationParamRowCount);
        lbImportCSV = new Label("");
        resizeLabel(lbImportCSV, false);
        gpSimulationParameters.add(lbImportCSV, 1, simulationParamRowCount);
        // Fitting parameters and complain current
        cbFitCurves = new CheckBox("Try to fit parameters");
        cbFitCurves.setDisable(simulationType != ResourcesMAF.CORNERS_SIMULATION);
        resizeCheckBox(cbFitCurves, false);
        tbImportCSV.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CSV Meassure Files");
            List<File> list = fileChooser.showOpenMultipleDialog(this.getScene().getWindow());
            if (list != null) {
                csvFiles = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    csvFiles[i] = list.get(i).getAbsolutePath();
                }
                importCSV = true;
                lbImportCSV.setText(list.size() + " files.");
                cbFitCurves.setDisable(simulationType != ResourcesMAF.CORNERS_SIMULATION);
            } else {
                tbImportCSV.setSelected(false);
                importCSV = false;
                lbImportCSV.setText("");
                cbFitCurves.setDisable(true);
                cbFitCurves.setSelected(false);
            }
        });
        //
        gpSimulationParameters.add(cbFitCurves, 2, simulationParamRowCount++);
        // plots
        gpSimulationParameters.add(cbLogI, 0, simulationParamRowCount);
        gpSimulationParameters.add(tfMinLogIPlotted, 1, simulationParamRowCount);
        // Plot only fitting curves
        cbPlotOnlyFittingCurves = new CheckBox("Plot only fitting curves");
        resizeCheckBox(cbPlotOnlyFittingCurves, false);
        cbPlotOnlyFittingCurves.setDisable(true);
        // when is enabled?
        cbFitCurves.setOnAction((ActionEvent e) -> {
            dm.infoDialog("Remember to set-up the timing and voltage parameters\nto meet the imported cycles.");
            cbPlotOnlyFittingCurves.setDisable(!cbFitCurves.isSelected());
        });
        gpSimulationParameters.add(cbPlotOnlyFittingCurves, 2, simulationParamRowCount++);
        // Add simulation Parameters
        gridPane.add(gpSimulationParameters, 0, 1);

        // Data Management
        ToggleGroup groupDataManagement = new ToggleGroup();
        groupDataManagement.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle selectedToggle) -> {
            SelectDataManagement(groupDataManagement.getToggles().indexOf(selectedToggle));
        });
        Label lbDataManagement = new Label("Data Management");
        lbDataManagement.setFont(ResourcesMAF.TITLEFONT);
        gridPane.add(lbDataManagement, 0, 2);
        GridPane gpDataManagement = new GridPane();
        gpDataManagement.setPadding(new Insets(5, 5, 5, 5));
        gpDataManagement.setHgap(5);
        gpDataManagement.setVgap(5);
        gpDataManagement.setAlignment(Pos.TOP_LEFT);
        // BufferSize
        tbUseMemoryBuffer = new ToggleButton("Magnitude buffer [kB]:");
        tbUseMemoryBuffer.setToggleGroup(groupDataManagement);
        resizeToggleButton(tbUseMemoryBuffer);
        tfUseMemoryBuffer = new TextField("1");
        resizeTextField(tfUseMemoryBuffer);
        gpDataManagement.add(tbUseMemoryBuffer, 0, 0);
        gpDataManagement.add(tfUseMemoryBuffer, 1, 0);
//        tbStoreInDisk = new ToggleButton("Store directy in disk");
//        tbStoreInDisk.setToggleGroup(groupDataManagement);
//        resizeToggleButton(tbStoreInDisk);
//        gpDataManagement.add(tbStoreInDisk, 0, 1);
        // Data decimation
        cbDataDecimation = new CheckBox("Disk stored data\ndecimation factor:");
        resizeCheckBox(cbDataDecimation, true);
        tfDataDecimation = new TextField("500");
        resizeTextField(tfDataDecimation);
        cbDataDecimation.setSelected(true);
        tfDataDecimation.setDisable(false);
        cbDataDecimation.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            tfDataDecimation.setDisable(!new_val);
        });
        gpDataManagement.add(cbDataDecimation, 0, 2);
        gpDataManagement.add(tfDataDecimation, 1, 2);
        // Add simulation Parameters
        gridPane.add(gpDataManagement, 0, 3);

        // Simulation Control
        Label lbSimulationControl = new Label("Simulation Control");
        lbSimulationControl.setFont(ResourcesMAF.TITLEFONT);
        gridPane.add(lbSimulationControl, 0, 4);
        GridPane gpSimulationControl = new GridPane();
        gpSimulationControl.setPadding(new Insets(5, 5, 5, 5));
        gpSimulationControl.setHgap(5);
        gpSimulationControl.setVgap(5);
        gpSimulationControl.setAlignment(Pos.CENTER);

        bCharacterizePlotGnuPlot = new Button("Characterize and plot\nusing GnuPlot");
        bCharacterizePlotGnuPlot.setGraphic(dm.GetIcon("/es/upm/die/vlsi/memristor/resources/icons/run_plot_icon.png"));
        bCharacterizePlotGnuPlot.setOnAction((ActionEvent e) -> {
            plotAfterSimulate = true;
            plotGnuPlot = true;
            Simulate();
        });
        bCharacterizePlotJFreeChart = new Button("Characterize and plot\nusing embebed graphs");
        bCharacterizePlotJFreeChart.setGraphic(dm.GetIcon("/es/upm/die/vlsi/memristor/resources/icons/run_plot_icon.png"));
        bCharacterizePlotJFreeChart.setOnAction((ActionEvent e) -> {
            plotAfterSimulate = true;
            plotGnuPlot = false;
            Simulate();
        });
        bCharacterize = new Button("Characterize ");
        bCharacterize.setGraphic(dm.GetIcon("/es/upm/die/vlsi/memristor/resources/icons/run_icon.png"));
        bCharacterize.setOnAction((ActionEvent e) -> {
            plotAfterSimulate = false;
            Simulate();
        });
        bSubcircuit = new Button("Subcircuit");
        bSubcircuit.setGraphic(dm.GetIcon("/es/upm/die/vlsi/memristor/resources/icons/subcircuit_icon.png"));
        bSubcircuit.setDisable(true);
        bSubcircuit.setOnAction((ActionEvent e) -> {
        });
        bCancel = new Button("Cancel");
        bCancel.setGraphic(dm.GetIcon("/es/upm/die/vlsi/memristor/resources/icons/cancel_icon.png"));
        bCancel.setDisable(true);
        bCancel.setOnAction((ActionEvent e) -> {
            tm.setInterrupted(true);
            tm.interrupt();
            enableCharacterizationButtons();
        });
        gpSimulationControl.add(bCancel, 0, 0);
        gpSimulationControl.add(bCharacterizePlotGnuPlot, 1, 0);
        gpSimulationControl.add(bCharacterizePlotJFreeChart, 2, 0);
        gpSimulationControl.add(bCharacterize, 1, 1);
        gpSimulationControl.add(bSubcircuit, 2, 1);
        resizeButton(bCancel);
        resizeButton(bCharacterizePlotGnuPlot);
        resizeButton(bCharacterizePlotJFreeChart);
        resizeButton(bCharacterize);
        resizeButton(bSubcircuit);
        // Add Simulation Control
        gridPane.add(gpSimulationControl, 0, 5);

        tbCorner.setSelected(true);
        tbUseMemoryBuffer.setSelected(true);
        ScrollPane sp = new ScrollPane();
        sp.setContent(gridPane);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        this.getChildren().add(sp);

    }

    private void SelectSimulationType(int sel) {
        this.simulationType = sel;
        switch (simulationType) {
            case ResourcesMAF.MONTECARLO_SIMULATION: {
                cbPlotOnlyFittingCurves.setDisable(true);
                cbFitCurves.setDisable(true);
//                tfComplianceCurrentMeassures.setDisable(true);
                tfMonteCarloThreads.setDisable(false);
                memristorUI.SelectSimulationType(ResourcesMAF.MONTECARLO_SIMULATION);
                voltageUI.SelectSimulationType(ResourcesMAF.MONTECARLO_SIMULATION);
                dm.infoDialog("Please, check over memristor/feed parameters.");
            }
            break;
            case ResourcesMAF.CORNERS_SIMULATION: {
                cbFitCurves.setDisable(csvFiles.length <= 0);
                cbPlotOnlyFittingCurves.setDisable(cbFitCurves.isDisable() || !cbFitCurves.isSelected());
//                tfComplianceCurrentMeassures.setDisable(csvFiles.length <= 0);
                tfMonteCarloThreads.setDisable(true);
                memristorUI.SelectSimulationType(ResourcesMAF.CORNERS_SIMULATION);
                voltageUI.SelectSimulationType(ResourcesMAF.CORNERS_SIMULATION);
                dm.infoDialog("Please, check over memristor/feed parameters."
                        + "\nRemember that the the different values \nare separated by '"
                        + ResourcesMAF.VALUESPLITTER + "' char.");
            }
            break;
            default: {
            }
            break;
        }
    }

    private void SelectDataManagement(int sel) {
        switch (sel) {
            case 0: { // buffer
                tfUseMemoryBuffer.setDisable(false);
            }
            break;
            case 1: {
                tfUseMemoryBuffer.setDisable(true);
            }
            break;
            default: {
                tfUseMemoryBuffer.setDisable(false);
            }
            break;
        }
    }

    private void resizeLabel(Label lb, boolean minHeight) {
        lb.setMinWidth(150.0);
        if (minHeight) {
            lb.setMinHeight(2 * USE_PREF_SIZE);
        }
    }

    private void resizeButton(Button bb) {
        bb.setMinWidth(200.0);
        bb.setMinHeight(50);
    }

    private void resizeCheckBox(CheckBox cb, boolean minHeight) {
        cb.setMinWidth(150.0);
        if (minHeight) {
            cb.setMinHeight(2 * USE_PREF_SIZE);
        }
    }

    private void resizeToggleButton(ToggleButton tb) {
        tb.setMinWidth(170.0);
//        lb.setPrefWidth(80.0);
//        lb.setMaxWidth(80.0);
    }

    private void resizeTextField(TextField tf) {
        tf.setMinWidth(180.0);
        tf.setPrefWidth(180.0);
        tf.setMaxWidth(180.0);
    }

    private boolean validateTextFields() {
        try {
            if (cbDataDecimation.isSelected()) {
                int decimation = Integer.parseInt(tfDataDecimation.getText());
                if (decimation < 0) {
                    dm.infoDialog("Invalid decimation value.");
                    return false;
                }
            }
            if (simulationType == ResourcesMAF.MONTECARLO_SIMULATION) {
                int montecarloThreads = Integer.parseInt(tfMonteCarloThreads.getText());
                if (montecarloThreads < 0) {
                    dm.infoDialog("Invalid Monte Carlo threads number value.");
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void Simulate() {
        if (!validateTextFields()) {
            return;
        }
        bCharacterize.setDisable(true);
        bCharacterizePlotJFreeChart.setDisable(true);
        bCancel.setDisable(false);
        bSubcircuit.setDisable(true);

        InputVoltage iv;
        Timing t;
        String error;
        int threadsNumber = 0;
        int magnitudeValuesSize;
        int storeRatio = 1;
        int maxParallelThreads = ResourcesMAF.MAXFD;
        try {
            magnitudeValuesSize = Integer.parseInt(tfUseMemoryBuffer.getText());
            if (magnitudeValuesSize < ResourcesMAF.MINBUFFER || magnitudeValuesSize > ResourcesMAF.MAXBUFFER) {
                error = "The magnitude/state variable buffer size should be between 1 and 100,"
                        + "\n depending on the amount of RAM. ";
                dm.errorDialog("Parameters error" + error);
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
                return;
            }
            // create memristor
            if (simulationType == ResourcesMAF.MONTECARLO_SIMULATION) {
                threadsNumber = Integer.parseInt(tfMonteCarloThreads.getText());
            }
            memristorPilot = memristorUI.GetMemristor();
            if (memristorPilot == null) {
                error = "Memristor parameters were incorrectly provided. ";
                dm.errorDialog("Memristor Parameters error" + error);
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
                enableCharacterizationButtons();
                return;
            }
            // parallel threads
            if (cbMaxParalellThreads.isSelected()) {
                maxParallelThreads = Integer.parseInt(tfMaxParalellThreads.getText());
                if (maxParallelThreads > ResourcesMAF.MAXFD) {
                    error = "The OS can not manage that amount of open files.";
                    dm.errorDialog("Parameters error" + error);
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
                    enableCharacterizationButtons();
                    return;
                }
            }
            // complain current
            if (cbComplianceCurrentSimulations.isSelected()) {
                memristorPilot.setLimitCurrent(true);
                double positiveComplianceCurrent = Double.parseDouble(tfPositiveComplianceCurrentSimulations.getText());
                double negativeComplianceCurrent = Double.parseDouble(tfNegativeComplianceCurrentSimulations.getText());
                if (positiveComplianceCurrent < 0 || negativeComplianceCurrent > 0) {
                    error = "Positive complain current should be positive... and negative complain current should be negative ";
                    dm.errorDialog("Parameters error" + error);
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
                    enableCharacterizationButtons();
                    return;
                }
                memristorPilot.setPositiveComplianceCurrent(positiveComplianceCurrent);
                memristorPilot.setNegativeComplianceCurrent(negativeComplianceCurrent);
            } else {
                memristorPilot.setLimitCurrent(false);
            }
            t = timingUI.getTiming();
            if (t == null) {
                throw new Exception("Error in timing module");
            }
            iv = voltageUI.GetInputVoltage(t);
            if (iv == null) {
                error = "Input Voltage parameters were incorrectly provided. ";
                dm.errorDialog("Input Voltage error" + error);
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
                enableCharacterizationButtons();
                return;
            }
            if( this.cbDataDecimation.isSelected() ){
                storeRatio = Integer.parseInt(tfDataDecimation.getText());
                if (storeRatio <= 1) {
                    error = "If the stored data is reduced,\n"
                            + " the stored data ratio should be an integer greater than one. ";
                    dm.errorDialog("Parameters error" + error);
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
                    enableCharacterizationButtons();
                    return;
                }
            }
        } catch (Exception exc) {
            error = "Memristor creation error: " + exc.getLocalizedMessage();
            dm.errorDialog("Error" + error);
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
            enableCharacterizationButtons();
            return;
        }
        try {
            // create simulation (object + thread)
            if (simulationType == ResourcesMAF.MONTECARLO_SIMULATION) {
                tm = new DCMonteCarloManager(this, threadsNumber, maxParallelThreads,
                        memristorPilot, iv, t, tfSimulationTitle.getText(), 1024 * magnitudeValuesSize,
                        cbDataDecimation.isSelected(), storeRatio, getFolderName());
            } else {
                tm = new DCCornersManager(this, maxParallelThreads,
                        memristorPilot, iv, t, tfSimulationTitle.getText(), 1024 * magnitudeValuesSize,
                        cbDataDecimation.isSelected(), storeRatio, getFolderName());
            }
            // start the thread
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "\n.......................");
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Starting the simulator.");
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Memristor model: {0}", tm.getTitle());
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Initial tStep: {0}", t.gettStep());
            dcUI.changeCursor(Cursor.WAIT);
            tm.start();

        } catch (Exception exc) {
            error = "Simulation error: " + exc.getLocalizedMessage();
            dm.errorDialog("Simulation error: " + exc.getLocalizedMessage());
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, error);
        }
    }

    private void enableCharacterizationButtons() {
        bCharacterize.setDisable(false);
        bCharacterizePlotJFreeChart.setDisable(false);
        bCancel.setDisable(true);
        bSubcircuit.setDisable(true);
        dcUI.changeCursor(Cursor.DEFAULT);
    }

    private String getFolderName() {
        String aux = ResourcesMAF.SIMULATIONSFOLDER + "/";
        aux += ResourcesMAF.DEVICEMONTECARLOOUTPUT;
        aux += "_" + tfSimulationTitle.getText();
        return aux.replace(" ", "_");
    }

    public void reportResults() {
        // 2 different threads, one 2 draw the plots and other for the UI
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Reporting Results");
        Platform.runLater(() -> {
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Starting to report");
            String log;
            dcUI.changeCursor(Cursor.DEFAULT);
            bCharacterizePlotJFreeChart.setDisable(false);
            bCharacterize.setDisable(false);
            bCancel.setDisable(true);
            bSubcircuit.setDisable(false);
            if (!tm.getSimulationResult().allCorrect()) {
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Error in the analysis: {0}", tm.getSimulationResult().getConsoleMessage());
                dm.errorDialog("Error in the analysis: " + tm.getSimulationResult().getCompleteMessage());
                log = "Error in the analysis: " + tm.getSimulationResult().getCompleteMessage()
                        + "\nSimulation time: " + ResourcesMAF.getFormatTime((tm.getEndTime() - tm.getStartTime()) * 1e-9)
                        + "\n---------------------------------------------------------------\n";
            } else {
                if (tm.getSimulationResult().getState() == SimulationResult.states.SIMULATION_WARNING) {
                    dm.infoDialog("Warning: " + tm.getSimulationResult().getCompleteMessage());
                }
                Logger.getLogger(ResourcesMAF.loggerName)
                        .log(Level.INFO, tm.getSimulationResult().getConsoleMessage());
                log = "\n---------------------------------------------------------------\n"
                        + "Device Characterization correctly computed and plotted.";

                log += "\nMagnitudes buffer size: " + tm.getMagnitudeValuesSize();
                log += "\nNumber of threads: " + (tm.getThreadsCorrectlyProcessed() + tm.getThreadsBadlyProcessed())
                        + "\nNumber of threads correctly computed: " + tm.getThreadsCorrectlyProcessed()
                        + "\nNumber of threads badly computed: " + tm.getThreadsBadlyProcessed()
                        + "\nSimulation time: " + ResourcesMAF.getFormatTime((tm.getEndTime() - tm.getStartTime()) * 1e-9);
            }
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Preparing result files");
            // csv
            tm.setImportCSV(importCSV);
            tm.setCsvFiles(csvFiles);
            // load result files
            tm.prepareResults();
            // fit data
            if (cbFitCurves.isSelected()) {
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Fitting curves");
                tm.setFitCurves(true);
                tm.fitCurves();
                log += "\nFitting time: " + ResourcesMAF.getFormatTime((tm.getEndTimeFitting() - tm.getStartTimeFitting()) * 1e-9) + " (s).";
                for (FittingCurvesResult f : tm.getBestFittingScenarios()) {
                    log += "\n\t" + f.getCompleteMessage();
                }
            }
            // plot data
            if (plotAfterSimulate) {
                boolean logI = cbLogI.isSelected();
                double minIPlotted = Double.parseDouble(tfMinLogIPlotted.getText());
                tm.setPlotOnlyFittingCurves(cbPlotOnlyFittingCurves.isSelected());
                tm.setLogI(logI);
                tm.setMinIPlotted(minIPlotted);
                int threadsN = tm.getThreadsNumber();
                if (!plotGnuPlot && threadsN <= ResourcesMAF.MAXJFREECHARTSIMULATIONS) {
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Preparing plots");
                    tm.plotUsingJFreeChart();
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Opening plots");
                    DCPlots dcp = new DCPlots(tm);
                } else {
                    if (!plotGnuPlot && threadsN > ResourcesMAF.MAXJFREECHARTSIMULATIONS) {
                        dm.infoDialog("Too much curves to be shown using JFreeChart. Using GnuPlot instead.");
                    }
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Preparing GnuPlot plots");
                    tm.plotUsingGnuplot();
                }
                log += "\nPlotting time: " + ResourcesMAF.getFormatTime(
                        (tm.getPlotter().getEndTimePlotting() - tm.getPlotter().getStartTimePlotting()) * 1e-9) + " (s).";
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Plots generated");

            }
            // plot gnuplot
            // plot jfreechart
            log += "\n---------------------------------------------------------------\n";
            dcUI.RefreshLog(log);
            dm.infoDialog("Operations finished.");
            // cleaning threads and so oon
            tm = null;
        });
    }

}
