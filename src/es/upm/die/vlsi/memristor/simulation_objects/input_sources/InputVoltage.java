package es.upm.die.vlsi.memristor.simulation_objects.input_sources;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author fernando garcía fgarcia@die.upm.es
 */
public class InputVoltage {

    // protected InputVoltageParameter voltage;
//    protected int voltageType; // 0 sinusoidal 1 pulse 2 triangle 3 const, 4
    // VCONSTANTRANGE
    protected Timing timing; // timing control
    protected InputVoltageParameter[] parameters;
    // temp values
    protected final double tempVoltageValues[];
    protected final double tempPartialTimeValues[];
    protected final int tempStateValues[];
    protected int parametersNumber;

    protected String name;

    public InputVoltage() {
        tempVoltageValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
        tempPartialTimeValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
        tempStateValues = new int[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
        name = "Undefined";
    }

    // overriden
    public InputVoltage getRandomInputVoltage(Timing t) {
        return null;
    }

    // overriden

    public InputVoltage getCopy(Timing t) {
        return null;
    }

    public void updateTempState() {
        for (int i = 1; i < tempVoltageValues.length; i++) {
            tempVoltageValues[tempVoltageValues.length - i]
                    = tempVoltageValues[tempVoltageValues.length - i - 1];
            tempPartialTimeValues[tempPartialTimeValues.length - i]
                    = tempPartialTimeValues[tempPartialTimeValues.length - i - 1];
            tempStateValues[tempStateValues.length - i]
                    = tempStateValues[tempStateValues.length - i - 1];
        }
    }

    /*
     * Recover the oldest value
     */
    public void recoverTempState() {
        for (int i = 0; i < tempVoltageValues.length - 1; i++) {
            tempVoltageValues[i] = tempVoltageValues[tempVoltageValues.length - 1];
            tempPartialTimeValues[i] = tempPartialTimeValues[tempPartialTimeValues.length - 1];
            tempStateValues[i] = tempStateValues[tempStateValues.length - 1];
        }
    }

    public InputVoltage[] getValueIterationCombinations() {
        int totalCombinations = 1;
        for (InputVoltageParameter p : parameters) {
            totalCombinations = totalCombinations * p.getValueList().length;
        }
        InputVoltage[] iva = new InputVoltage[totalCombinations];
        int[] iterationIndexes = new int[parameters.length];
        for (int i = 0; i < totalCombinations; i++) {
            // update indexes
            for (int pCount = 0; pCount < parameters.length - 1; pCount++) {
                if (iterationIndexes[pCount] == parameters[pCount].getValueList().length) {
                    iterationIndexes[pCount + 1]++;
                    iterationIndexes[pCount] = 0;
                }
            }
            // update values
            int paramCount = 0;
            for (InputVoltageParameter p : parameters) {
                p.setValue(p.getValueList()[iterationIndexes[paramCount++]]);
            }
            // Create scenario
//            Timing t = timingPilot.GetCopy();
            InputVoltage iv = getCopy(null);
            iva[i] = iv;
            // add 1 to the first index
            iterationIndexes[0]++;

            String pList = "";
            for (InputVoltageParameter p : parameters) {
                pList += " " + p.getTag() + ":" + p.getValue();
            }
            System.out.println("returned params: " + pList);
        }
        return iva;
    }

    public double getCurrentVoltage() {
        return tempVoltageValues[0];
    }

    // overriden
    public void updateState() {
        // update voltage
        updateVoltage();
        // update recovery values
        for (int i = 1; i < tempVoltageValues.length; i++) {
            tempVoltageValues[i] = tempVoltageValues[i - 1];
            tempPartialTimeValues[i] = tempPartialTimeValues[i - 1];
            tempStateValues[i] = tempStateValues[i - 1];
        }
    }

    protected void initBackValues() {
        for (int i = 1; i < tempVoltageValues.length; i++) {
            tempVoltageValues[i] = tempVoltageValues[i - 1];
            tempPartialTimeValues[i] = tempPartialTimeValues[i - 1];
            tempStateValues[i] = tempStateValues[i - 1];
        }
    }

    protected void updateVoltage() {

    }

    // overriden
    public double getVoltageEstimation(double timeInc) {
        return 0;
    }

    public InputVoltageParameter getParameter(int index) {
        return parameters[index];
    }

    public InputVoltageParameter[] getInputVoltageParameters() {
        return parameters;
    }

    public Timing getTiming() {
        return timing;
    }

    public void setTiming(Timing timing) {
        this.timing = timing;
    }

    public InputVoltageParameter[] getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
