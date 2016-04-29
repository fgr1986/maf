/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.ui;

import es.upm.die.vlsi.memristor.resources.FilesFoldersManagement;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorLibrary;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorParameter;
import es.upm.die.vlsi.memristor.simulations.DCMonteCarloManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

/**
 *
 * @author fgarcia
 */
public class MemristorParametersUI extends StackPane {

    private MemristorModel memristor;
    private ArrayList<CheckBox> checkBoxList;
    private ArrayList<ComboBox> comboBoxList;
    private ArrayList<Label> labelList;
    private ArrayList<TextField> textFieldMeanValuesList;
    private ArrayList<TextField> textFieldVariabilityList;
    private ArrayList<ContextMenu> contextMenuList;
    private int selectedPreset;
    private int simulationType;
    private Button bLoadCorners;
    private Button bSaveCorners;
        
    private final MemristorLibrary.Memristor_Model memristorType;

    int savedFileCount;
    
    private final DialogsManager dm;

    public MemristorParametersUI(MemristorLibrary.Memristor_Model memristorType) {
        dm = new DialogsManager();
        savedFileCount = 0;
        this.memristorType = memristorType;
        memristor = MemristorLibrary.GetNewMemristor(memristorType);
        selectedPreset = 0;
        simulationType = ResourcesMAF.MONTECARLO_SIMULATION;
        // Main GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15, 15, 15, 15));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);
        // Create UI
        RecreateParameterAndVariationsUI(gridPane, memristorType.index());
        // Set content in scroll pane
        ScrollPane sp = new ScrollPane();
        sp.setContent(gridPane);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        this.getChildren().add(sp);

    }

    private void RecreateParameterAndVariationsUI(GridPane gridPane, int memristorType) {
        // Remove previous items
        gridPane.getChildren().removeAll(gridPane.getChildren());
        // Arraylist
        checkBoxList = new ArrayList<>();
        textFieldVariabilityList = new ArrayList<>();
        textFieldMeanValuesList = new ArrayList<>();
        labelList = new ArrayList<>();
        comboBoxList = new ArrayList<>();

        Label lbParam = new Label(MemristorLibrary.GetModelTitle(memristorType) + ". Set memristor parameters:");
        lbParam.setFont(ResourcesMAF.TITLEFONT);
        gridPane.add(lbParam, 0, 0);
        // load corners
        bLoadCorners = new Button("Load corners");
        bLoadCorners.setDisable(true);
        bLoadCorners.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open corners file");
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (file != null) {
                readCorners(file);
            } else {
                dm.errorDialog("No file selected");
            }
        });
        // save corners
        bSaveCorners = new Button("Save corners");
        bSaveCorners.setDisable(true);
        bSaveCorners.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save corners file");
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (file != null) {
                Writer writer = null;
                try {
                    // init
                    writer = new BufferedWriter(new FileWriter(file));
                    writer.write("#################################\n");
                    writer.write("##" + new Date().toString() + "##\n");
                    writer.write("#################################\n");
                    int tfCount = 0;
                    for (TextField tf : textFieldMeanValuesList) {
                        writer.write("## " + labelList.get(tfCount++).getText() + "\n");
                        writer.write(tf.getText() + "\n");
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    FilesFoldersManagement.close(writer);
                }
            } else {
                dm.errorDialog("No file saved");
            }
        });
        // Presets
        Label lbPresets = new Label("Parameter presets:");
        //Non-editable combobox. Created with a builder
        ComboBox comboPresets = new ComboBox(FXCollections.observableArrayList(memristor.getParameterPresetNames()));
        comboPresets.setValue(memristor.getParameterPresetNames()[selectedPreset]);
        comboPresets.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedPreset = comboPresets.getSelectionModel().getSelectedIndex();
                memristor.setParameterPreset(selectedPreset);
                RecreateParameterAndVariationsUI(gridPane, memristorType);
            }
        });
        //resizeLabel(lbPresets);
        resizeComboBox(comboPresets, true);
        GridPane gpPresetBox = new GridPane();
        gpPresetBox.setPadding(new Insets(5, 5, 5, 5));
        gpPresetBox.setHgap(5);
        gpPresetBox.setVgap(5);
        gpPresetBox.setAlignment(Pos.CENTER_LEFT);
        gpPresetBox.add(lbPresets, 0, 0);
        gpPresetBox.add(comboPresets, 1, 0);
        // load/save corners
        gpPresetBox.add(bLoadCorners, 2, 0);
        gpPresetBox.add(bSaveCorners, 3, 0);

        gridPane.add(gpPresetBox, 0, 1);

        // Params
        GridPane gpParams = new GridPane();
        gpParams.setPadding(new Insets(15, 15, 15, 15));
        gpParams.setHgap(5);
        gpParams.setVgap(5);
        gpParams.setAlignment(Pos.CENTER);
        GridPane gpParamVariations = new GridPane();
        gpParamVariations.setPadding(new Insets(15, 15, 15, 15));
        gpParamVariations.setHgap(5);
        gpParamVariations.setVgap(5);
        gpParamVariations.setAlignment(Pos.CENTER);
        int row = 0;
        int column = 0;
        int MaxRows = 6;

        contextMenuList = new ArrayList<>();
        for (MemristorParameter mp : memristor.getParameters()) {
            if (mp.isStandard()) {
                CheckBox cbMPVariation = new CheckBox(mp.getTag() + " [" + mp.getUnits() + "]");
                Label lbMP = new Label(mp.getTag() + " [" + mp.getUnits() + "]");
                TextField tbMP = new TextField("" + mp.getValue());
                valueListMenu2TextField(tbMP, lbMP);
                TextField tbMPVariation = new TextField("" + mp.getSigma());
                resizeLabel(lbMP);
                resizeCheckBox(cbMPVariation);
                resizeTextField(tbMP);
                resizeTextField(tbMPVariation);
                labelList.add(lbMP);
                checkBoxList.add(cbMPVariation);
                textFieldMeanValuesList.add(tbMP);
                textFieldVariabilityList.add(tbMPVariation);
                if (!mp.isAllowVariations() || simulationType == ResourcesMAF.CORNERS_SIMULATION) {
                    tbMPVariation.setDisable(true);
                    cbMPVariation.setDisable(true);
                }

                if (column >= MaxRows) {
                    column = 0;
                    row++;
                }
                gpParams.add(lbMP, column, row);
                gpParamVariations.add(cbMPVariation, column++, row);
                gpParams.add(tbMP, column, row);
                gpParamVariations.add(tbMPVariation, column++, row);
            } else {
                Label lbMP = new Label(mp.getTag());
                //Non-editable combobox. Created with a builder
                ComboBox comboBMP = new ComboBox(FXCollections.observableArrayList(mp.getNonStandardTags()));
                comboBMP.setValue(mp.getNonStandardTags()[0]);
                resizeLabel(lbMP);
                resizeComboBox(comboBMP, false);
                comboBoxList.add(comboBMP);
                if (column >= MaxRows) {
                    column = 0;
                    row++;
                }
                gpParams.add(lbMP, column++, row);
                gpParams.add(comboBMP, column++, row);
            }
        }

        gridPane.add(gpParams, 0, 2);
        Label lbParamVariations = new Label(MemristorLibrary.GetModelTitle(memristorType)
                + ".\nSet memristor parameter " + ResourcesMAF.SIGMA + " variability:");
        lbParamVariations.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        gridPane.add(lbParamVariations, 0, 3);
        gridPane.add(gpParamVariations, 0, 4);

        // Init CheckBoxes
        if (simulationType == ResourcesMAF.MONTECARLO_SIMULATION) {
            enableVariability();
        }
    }

    public MemristorModel GetMemristor() {
        if( SetParameters() ){
        return memristor;
        }else{
            return null;
        }
    }
    
    public void resetMemristor(){        
        memristor = MemristorLibrary.GetNewMemristor(this.memristorType);
    }
    
    private boolean SetParameters() {
        try {
            // clean from previous simulations
            resetMemristor();
            int cbCount = 0;
            int comboBCount = 0;
            for (MemristorParameter mp : memristor.getParameters()) {
                if (mp.isStandard()) {
                    // montecarlo or corners
                    if (simulationType == ResourcesMAF.MONTECARLO_SIMULATION) {
                        mp.setValue(Double.parseDouble(textFieldMeanValuesList.get(cbCount).getText()));
                        if (checkBoxList.get(cbCount).isSelected()) {
                            mp.setAllowVariations(true);
                            mp.setSigma(Double.parseDouble(textFieldVariabilityList.get(cbCount).getText()));
                        } else {
                            mp.setAllowVariations(false);
                        }
                    } else {
                        String[] sValueList = textFieldMeanValuesList.get(cbCount).getText().split(ResourcesMAF.VALUESPLITTER);
                        double[] valueList = new double[sValueList.length];
                        for (int i = 0; i < valueList.length; i++) {
                            valueList[i] = Double.parseDouble(sValueList[i]);
                        }
                        mp.setValueList(valueList);
                    }
                    // update count
                    cbCount++;
                } else {
                    mp.setValue(comboBoxList.get(comboBCount).getSelectionModel().getSelectedIndex());
                    comboBCount++;
                }
            }
        } catch (Exception exc) {
            dm.errorDialog("Model parameters error: " + exc.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private void resizeLabel(Label lb) {
        lb.setMinWidth(120.0);
    }

    private void resizeComboBox(ComboBox cb, boolean limitWidth) {
        cb.setMinWidth(90.0);
        if (limitWidth) {
            cb.setMaxWidth(300.0);
        }
    }

    private void resizeCheckBox(CheckBox cb) {
        cb.setMinWidth(120.0);
    }

    private void resizeTextField(TextField tf) {
        tf.setMinWidth(80.0);
        tf.setPrefWidth(80.0);
        tf.setMaxWidth(80.0);
    }

    public void SelectSimulationType(int sel) {
        simulationType = sel;
        switch (simulationType) {
            case ResourcesMAF.MONTECARLO_SIMULATION: { // Montecarlo
                enableVariability();
            }
            break;
            case ResourcesMAF.CORNERS_SIMULATION: { // Corner
                disableVariability();
            }
            break;
            default: {
            }
            break;
        }
    }

    private void enableVariability() {
        bSaveCorners.setDisable(true);
        bLoadCorners.setDisable(true);
        int cbCount = 0;
        for (MemristorParameter mp : memristor.getParameters()) {
            if (mp.isStandard()) {
                CheckBox cb = checkBoxList.get(cbCount);
                TextField tf = textFieldVariabilityList.get(cbCount);
                if (mp.isAllowVariations()) {
                    cb.setDisable(false);
                    tf.setDisable(false);
                    cb.setSelected(true);
                    cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                        tf.setDisable(!new_val);
                    });

                } else {
                    cb.setDisable(true);
                    tf.setDisable(true);
                }
                cbCount++;
            }
        }
        contextMenuList.stream().forEach((cm) -> {
            cm.getItems().stream().forEach((mi) -> {
                mi.setDisable(true);
            });
        });
    }

    private void disableVariability() {
        bSaveCorners.setDisable(false);
        bLoadCorners.setDisable(false);
        int cbCount = 0;
        for (CheckBox cb : checkBoxList) {
            final int localCbCount = cbCount;
            cb.setDisable(true);
            textFieldVariabilityList.get(localCbCount).setDisable(true);
            cbCount++;
        }
        contextMenuList.stream().forEach((cm) -> {
            cm.getItems().stream().forEach((mi) -> {
                mi.setDisable(false);
            });
        });
    }

    private void valueListMenu2TextField(TextField tf, Label lb) {
        final ContextMenu contextMenu = new ContextMenu();
        contextMenuList.add(contextMenu);
        MenuItem item1 = new MenuItem("Generate list of values for this parameter");
        item1.setOnAction((ActionEvent e) -> {
            openValuesGenerator(tf, lb);
        });
        contextMenu.getItems().addAll(item1);
        tf.setContextMenu(contextMenu);
    }

    private void readCorners(File file) {
        FileInputStream fis = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            //Construct BufferedReader from InputStreamReader
            br = new BufferedReader(new InputStreamReader(fis));
            String line;
            int lineCounter = 0;
            while (lineCounter < textFieldMeanValuesList.size()
                    && (line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("#")) {
                    textFieldMeanValuesList.get(lineCounter++).setText(trimmedLine);
                }
            }
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            FilesFoldersManagement.close(fis);
            FilesFoldersManagement.close(br);
        }
    }

    private void openValuesGenerator(TextField tf, Label lb) {
        final Stage openValuesGeneratorstage = new Stage();
        openValuesGeneratorstage.setTitle("Value list generator for " + lb.getText());
        //create root node of scene, i.e. group
        StackPane stackPaneGenerator = new StackPane();
        //create scene with set width, height and color
        Scene openValuesGeneratorscene = new Scene(stackPaneGenerator, 500, 200, Color.WHITESMOKE);
        //set scene to stage
        openValuesGeneratorstage.setScene(openValuesGeneratorscene);
        //center stage on screen
        openValuesGeneratorstage.centerOnScreen();
        //show the stage
        openValuesGeneratorstage.show();

        GridPane gpValuesGenerator = new GridPane();
        gpValuesGenerator.setPadding(new Insets(5, 5, 5, 5));
        gpValuesGenerator.setHgap(5);
        gpValuesGenerator.setVgap(5);
        gpValuesGenerator.setAlignment(Pos.CENTER);
        Label lt = new Label("From");
        TextField tfInit = new TextField("initial_value");
        resizeLabel(lt);
        resizeTextField(tfInit);
        Label lto = new Label("to");
        TextField tfFinal = new TextField("final_value");
        resizeLabel(lto);
        resizeTextField(tfFinal);
        Label lsteps = new Label("Number of steps");
        TextField tfSteps = new TextField("10");
        resizeLabel(lsteps);
        resizeTextField(tfSteps);
        CheckBox cbLog = new CheckBox("Non-linear");
        resizeCheckBox(cbLog);
        Button bGenerate = new Button("generate");
        TextField tfResults = new TextField("generated_list");
        Button bCopy = new Button("Set values\nto parameter");
        bGenerate.setOnAction((ActionEvent e) -> {
            try {
                double initVal = Double.parseDouble(tfInit.getText());
                double finalVal = Double.parseDouble(tfFinal.getText());
                double steps = Double.parseDouble(tfSteps.getText());
                if (initVal >= finalVal) {
                    throw new Exception("Init value should be larger than final value");
                }
                if (steps < 3) {
                    throw new Exception("We need more steps");
                }
                // first value
                double currentVal = initVal;
                String dataVal = "" + initVal;
                double incVal = 0;
                if (!cbLog.isSelected()) {
                    incVal = (finalVal - initVal) / steps;
                }
                for (int i = 0; i < steps - 2; i++) {
                    if (cbLog.isSelected()) {
                        currentVal = Math.sqrt(currentVal * finalVal);
                    } else {
                        currentVal += incVal;
                    }
                    dataVal += ";" + currentVal;
                }
                // last value
                currentVal = finalVal;
                dataVal += ";" + currentVal;
                tfResults.setText(dataVal);
                bCopy.setDisable(false);
            } catch (Exception excp) {
                dm.errorDialog("Error: " + excp);
            }
        });
        gpValuesGenerator.add(lt, 0, 0);
        gpValuesGenerator.add(tfInit, 1, 0);
        gpValuesGenerator.add(lto, 2, 0);
        gpValuesGenerator.add(tfFinal, 3, 0);
        gpValuesGenerator.add(lsteps, 0, 1);
        gpValuesGenerator.add(tfSteps, 1, 1);
        gpValuesGenerator.add(cbLog, 2, 1);
        gpValuesGenerator.add(bGenerate, 3, 1);
        tfResults.setEditable(false);
        resizeTextField(tfResults);
        bCopy.setOnAction((ActionEvent e) -> {
            tf.setText(tfResults.getText());
            openValuesGeneratorstage.close();
        });
        bCopy.setDisable(true);
        gpValuesGenerator.add(tfResults, 0, 2);
        gpValuesGenerator.add(bCopy, 1, 2);

        ScrollPane sp = new ScrollPane();
        sp.setContent(gpValuesGenerator);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        //add text to the main root group
        stackPaneGenerator.getChildren().add(sp);
    }
}
