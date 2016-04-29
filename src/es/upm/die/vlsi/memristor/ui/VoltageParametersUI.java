/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.ui;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltageParameter;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.PulseRampVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.PulseVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.SineVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.TriangleVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author fgarcia
 */
public class VoltageParametersUI extends StackPane {

    SineVoltage sinePilot;
    PulseVoltage pulsePilot;
    TriangleVoltage trianglePilot;
    PulseRampVoltage pulseRampPilot;
    private final ArrayList<InputVoltage> voltageTypes;
    private final ArrayList<ToggleButton> voltageTypeButtons;
    private final ArrayList<ArrayList<TextField>> paramValuesArrays;
    private final ArrayList<ArrayList<TextField>> paramVariationsArrays;
    private final ArrayList<ArrayList<CheckBox>> allowParamVariationsArrays;
    private ToggleGroup toogleGroup;
    private int selectedVoltageIndex;
    private int simulationType;

    public VoltageParametersUI() {
        selectedVoltageIndex = 0;
        this.simulationType = ResourcesMAF.MONTECARLO_SIMULATION;
        // pilot instances to build up the interface
        sinePilot = new SineVoltage();
        pulsePilot = new PulseVoltage();
        trianglePilot = new TriangleVoltage();
        pulseRampPilot = new PulseRampVoltage();
        voltageTypes = new ArrayList<>();
        voltageTypeButtons = new ArrayList<>();
        // array list containing another arrayList of textFields
        paramValuesArrays = new ArrayList<>();
        paramVariationsArrays = new ArrayList<>();
        allowParamVariationsArrays = new ArrayList<>();

        voltageTypes.add(sinePilot);
        voltageTypes.add(pulsePilot);
        voltageTypes.add(trianglePilot);
        voltageTypes.add(pulseRampPilot);
        // main gridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15, 15, 15, 15));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.CENTER);

        toogleGroup = new ToggleGroup();
        toogleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle selectedToggle) -> {
            if (selectedToggle == null) {
                oldValue.setSelected(true);
                return;
            }
            SelectWave(toogleGroup.getToggles().indexOf(selectedToggle));
        });

        GridPane gpVoltageValues = new GridPane();
        gpVoltageValues.setPadding(new Insets(10, 10, 10, 10));
        gpVoltageValues.setHgap(5);
        gpVoltageValues.setVgap(5);
        gpVoltageValues.setAlignment(Pos.TOP_LEFT);
        GridPane gpVoltageVariations = new GridPane();
        gpVoltageVariations.setPadding(new Insets(10, 10, 10, 10));
        gpVoltageVariations.setHgap(5);
        gpVoltageVariations.setVgap(5);
        gpVoltageVariations.setAlignment(Pos.TOP_LEFT);
        int rowOffset = 0;
        for (InputVoltage iv : voltageTypes) {
            ToggleButton tb = new ToggleButton(iv.getName());
            Label liv = new Label(iv.getName());
            tb.setToggleGroup(toogleGroup);
            voltageTypeButtons.add(tb);
            // add button            
            gpVoltageValues.add(tb, 0, rowOffset);
            gpVoltageVariations.add(liv, 0, rowOffset++);
            // Create arrays
            ArrayList<TextField> paramValues = new ArrayList<>();
            ArrayList<TextField> paramVariations = new ArrayList<>();
            ArrayList<CheckBox> allowParamVariations = new ArrayList<>();
            paramValuesArrays.add(paramValues);
            paramVariationsArrays.add(paramVariations);
            allowParamVariationsArrays.add(allowParamVariations);
            // Parameters
            int column = 0;
            int MaxColumns = 6;
            for (InputVoltageParameter p : iv.getParameters()) {
                Label lValue = new Label(p.getTag() + "[" + p.getUnits() + "]");
                ResizeLabel(lValue);
                TextField tfValue = new TextField("" + p.getValue());
                ResizeTextField(tfValue);
                CheckBox cbAllowVariations = new CheckBox(p.getTag() + "[" + p.getUnits() + "]");
                ResizeCheckBox(cbAllowVariations);
                TextField tfVariations = new TextField("" + p.getSigma());
                ResizeTextField(tfVariations);
                if (p.isAllowVariations()) {

                }
                paramValues.add(tfValue);
                paramVariations.add(tfVariations);
                allowParamVariations.add(cbAllowVariations);
                // add to interface
                if (column >= MaxColumns) {
                    column = 0;
                    rowOffset++;
                }
                gpVoltageValues.add(lValue, column, rowOffset);
                gpVoltageVariations.add(cbAllowVariations, column++, rowOffset);
                gpVoltageValues.add(tfValue, column, rowOffset);
                gpVoltageVariations.add(tfVariations, column++, rowOffset);
            }
            rowOffset++;
        }

        Label lbWave = new Label("Select feeding wave:");
        lbWave.setFont(ResourcesMAF.TITLEFONT);
        Label lbWaveVariations = new Label("Select feeding wave parameter variations:");
        lbWaveVariations.setFont(ResourcesMAF.TITLEFONT);
        gridPane.add(lbWave, 0, 0);
        gridPane.add(gpVoltageValues, 0, 1);
        gridPane.add(lbWaveVariations, 0, 2);
        gridPane.add(gpVoltageVariations, 0, 3);
        SelectWave(0);
        voltageTypeButtons.get(2).setSelected(true);
        ScrollPane sp = new ScrollPane();
        sp.setContent(gridPane);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        this.getChildren().add(sp);
    }

    private void RefreshUI() {
        for (int vTypeCount = 0; vTypeCount < paramValuesArrays.size(); vTypeCount++) {
            int parametersNumber = voltageTypes.get(vTypeCount).getParameters().length;
            boolean disableControls = (vTypeCount != selectedVoltageIndex);
            boolean disableVariabilityControls = (simulationType == ResourcesMAF.CORNERS_SIMULATION) || disableControls;
            for (int paramCount = 0; paramCount < parametersNumber; paramCount++) {
                paramValuesArrays.get(vTypeCount).get(paramCount).setDisable(disableControls);
                // variability control
                boolean allowVariations = voltageTypes.get(vTypeCount).getParameter(paramCount).isAllowVariations();
                CheckBox cb = allowParamVariationsArrays.get(vTypeCount).get(paramCount);
                TextField tfv = paramVariationsArrays.get(vTypeCount).get(paramCount);
                tfv.setDisable(disableVariabilityControls || !allowVariations);
                cb.setDisable(disableVariabilityControls || !allowVariations);
                cb.setSelected(allowVariations);
                cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                    tfv.setDisable(!new_val);
                });
            }
        }
    }

    private void ResizeLabel(Label lb) {
        lb.setMinWidth(120.0);
    }

    private void ResizeCheckBox(CheckBox cb) {
        cb.setMinWidth(120.0);
    }

    private void ResizeTextField(TextField tf) {
        tf.setMinWidth(80.0);
        tf.setPrefWidth(80.0);
        tf.setMaxWidth(80.0);
    }

    private void SelectWave(int sel) {
        selectedVoltageIndex = sel;
        RefreshUI();
    }

    public void SelectSimulationType(int sel) {
        simulationType = sel;
        RefreshUI();
    }

    public InputVoltage GetInputVoltage(Timing timing) {
        InputVoltage iv;
        try {
            iv = voltageTypes.get(selectedVoltageIndex);
            int paramCount = 0;
            for (InputVoltageParameter p : voltageTypes.get(selectedVoltageIndex).getInputVoltageParameters()) {
                if (simulationType == ResourcesMAF.MONTECARLO_SIMULATION) {
                    p.setValue(Double.parseDouble(
                            paramValuesArrays.get(selectedVoltageIndex).get(paramCount).getText()));
                    if (p.isAllowVariations()
                            && allowParamVariationsArrays.get(selectedVoltageIndex).get(paramCount).isSelected()) {
                        p.setAllowVariations(true);
                        p.setSigma(Double.parseDouble(
                                paramVariationsArrays.get(selectedVoltageIndex).get(paramCount).getText()));
                    } else {
                        p.setAllowVariations(false);
                    }
                } else {
                    String[] sValueList = paramValuesArrays.get(selectedVoltageIndex).get(paramCount).getText().split(ResourcesMAF.VALUESPLITTER);
                    double[] valueList = new double[sValueList.length];
                    for (int i = 0; i < valueList.length; i++) {
                        valueList[i] = Double.parseDouble(sValueList[i]);
                    }
                    p.setValueList(valueList);
                }
                paramCount++;
            }

        } catch (Exception exc) {
            ErrorDialog("InputVoltageParameters error " + exc.getLocalizedMessage());
            return null;
        }
        return iv;
    }

    private void ErrorDialog(String message) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(new Group(new Text(25, 25, message)), 500, 100, Color.WHITESMOKE);
        dialog.setScene(scene);
        dialog.show();
    }
}
