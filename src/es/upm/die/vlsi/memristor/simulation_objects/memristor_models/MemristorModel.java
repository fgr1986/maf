package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import java.util.ArrayList;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;

import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 *
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando García Redondo, fgarcia@die.upm.es
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorModel {

    protected MemristorLibrary.Memristor_Model memristorType;
    protected ArrayList<Magnitude> magnitudes;
    protected ArrayList<Magnitude> electricalMagnitudes;
    protected ArrayList<Magnitude> stateVariables;
    // auxiliar variables to speed up saving
    protected ArrayList<Magnitude> savedElectricalMagnitudes;
    protected ArrayList<Magnitude> savedStateVariables;
    protected int memristorParameterCount;
    protected int memristorPresetsCount;
    protected MemristorParameter[] parameters;
    protected String[] parameterPresetNames;
    protected MemristorParameter[][] parameterPresets;
    // Magnitudes
    public final int TIMEINDEX = 0;
    public final int VINDEX = 1;
    public final int IINDEX = 2;
    public final int RINDEX = 3;

    protected double normalizedControlRatioDown = 10;
    protected double normalizedControlRatioUp = 0.1;

    protected double derivedStateVariableControlRatioDown = 10;
    protected double derivedStateVariableControlRatioUp = 0.2;
    protected double derivedStateVariableAlmostZero = 1e-5;
    private double maxTStepNeeded = -666;
    protected boolean needDecrease;
    protected boolean acceptIncrease;
    protected int amplitudeSignGoingON = 1;
    protected int magnitudeValuesSize;
    protected boolean limitCurrent;
    protected double negativeComplianceCurrent;
    protected double positiveComplianceCurrent;

    // iterationIndexes for Corner Analysis
    private int[] iterationIndexes;

    // Magnitude Variables
    protected Magnitude time;
    protected Magnitude voltage;
    protected Magnitude current;
    protected Magnitude memristance;
    //
    protected Magnitude controlMagnitude;

    /**
     * Default constructor
     *
     * @param memristorType
     */
    public MemristorModel(MemristorLibrary.Memristor_Model memristorType) {
        this.memristorType = memristorType;
        electricalMagnitudes = new ArrayList<>();
        stateVariables = new ArrayList<>();
        savedElectricalMagnitudes = new ArrayList<>();
        savedStateVariables = new ArrayList<>();
        // common magnitudes to all the models
        time = new Magnitude("time", true, TIMEINDEX, 1, "s");
        voltage = new Magnitude("v", true, VINDEX, 1, "V");
        current = new Magnitude("i", true, IINDEX, 1, "A");
        memristance = new Magnitude("Rm", true, RINDEX, 1, ResourcesMAF.OHM1);
        electricalMagnitudes.add(time);
        electricalMagnitudes.add(voltage);
        electricalMagnitudes.add(current);
        electricalMagnitudes.add(memristance);
        limitCurrent = false;
        negativeComplianceCurrent = 1;
        positiveComplianceCurrent = 1;
    }

    // overriden
    protected MemristorModel getEmptyMemristor() {
        return null;
    }

    public MemristorModel getRandomMemristor() {
        MemristorModel m = getEmptyMemristor();
        // compliance current
        m.setLimitCurrent(limitCurrent);
        m.setNegativeComplianceCurrent(negativeComplianceCurrent);
        m.setPositiveComplianceCurrent(positiveComplianceCurrent);
        // Parameters
        for (MemristorParameter p : parameters) {
            if (p.isAllowVariations()) {
                m.getParameter(p.getId()).setValue(p.getRandomValue());
            } else {
                m.getParameter(p.getId()).setValue(p.getValue());
            }
        }
        return m;
    }

    // overriden
    public MemristorModel getCopy() {
        return null;
    }

    public void checkLimitingCurrent() {
        if (limitCurrent) {
            if (current.getCurrentValue() > 0
                    && current.getCurrentValue() > positiveComplianceCurrent) {
                current.setCurrentValue(positiveComplianceCurrent);
            } else if (current.getCurrentValue() < 0
                    && current.getCurrentValue() < negativeComplianceCurrent) {
                current.setCurrentValue(negativeComplianceCurrent);
            }
        }
    }
    
    public void checkLimitingInternalCurrent( Magnitude internalCurrent ) {
        if (limitCurrent) {
            if (internalCurrent.getCurrentValue() > 0
                    && internalCurrent.getCurrentValue() > positiveComplianceCurrent) {
                internalCurrent.setCurrentValue( (positiveComplianceCurrent+internalCurrent.getBackTempValue(1))/2);
//                internalCurrent.setCurrentValue( 2*internalCurrent.getBackTempValue(1)-internalCurrent.getBackTempValue(2) );
            } else if (internalCurrent.getCurrentValue() < 0
                    && internalCurrent.getCurrentValue() < negativeComplianceCurrent) {
//                internalCurrent.setCurrentValue((negativeComplianceCurrent+internalCurrent.getBackTempValue(1))/2);
                internalCurrent.setCurrentValue( -2*Math.abs(internalCurrent.getBackTempValue(1))-Math.abs(internalCurrent.getBackTempValue(2)) );
            }
        }
    }

    public int prepareValueIterationCombinations() {
        int totalCombinations = 1;
        for (MemristorParameter p : parameters) {
            if (p.isStandard()) {
                totalCombinations = totalCombinations * p.getValueList().length;
            }
        }
        iterationIndexes = new int[parameters.length];
        return totalCombinations;
    }

    public MemristorModel getNextValueCombination() {
        // update memristor parameter values
        int paramCount = 0;
        for (MemristorParameter p : parameters) {
            p.setValue(p.getValueList()[iterationIndexes[paramCount++]]);
        }
        // add 1 to the first index
        iterationIndexes[0]++;
        // update indexes
        for (int pCount = 0; pCount < parameters.length - 1; pCount++) {
            if (null == parameters[pCount].getValueList()) {
                System.out.println(parameters[pCount].getTag());
            }
            if (iterationIndexes[pCount] == parameters[pCount].getValueList().length) {
                iterationIndexes[pCount + 1]++;
                iterationIndexes[pCount] = 0;
            }
        }
        // Create scenario
        return getCopy();
    }

    public void setParameterPreset(int presetIndex) {
        parameters = new MemristorParameter[memristorParameterCount];
//        for(int i=0; i< memristorParameterCount;i++){
//            parameters[i] = parameterPresets[presetIndex][i];
//        }        
        System.arraycopy(parameterPresets[presetIndex], 0, parameters, 0, memristorParameterCount);
    }

    public void recoverTempState(){
        electricalMagnitudes.stream().forEach((m) -> {
            m.recoverTempState();
        });
        stateVariables.stream().forEach((m) -> {
            m.recoverTempState();
        });
    }

    public void updateMagnitudesTempState() {
        electricalMagnitudes.stream().forEach((m) -> {
            m.updateTempState();
        });
        stateVariables.stream().forEach((m) -> {
            m.updateTempState();
        });
    }

    public void updateSaveState(int i) {
        electricalMagnitudes.stream().forEach((m) -> {
            m.updateState(i);
        });
        stateVariables.stream().forEach((m) -> {
            m.updateState(i);
        });
    }

    public String getTitlesMagnitudesInARow() {
        String row = "";
        row = electricalMagnitudes.stream().filter((m) -> (m.isSaved())).map((m) -> "%" + m.getTag() + "\t").reduce(row, String::concat);
        row = stateVariables.stream().filter((m) -> (m.isSaved())).map((m) -> "%" + m.getTag() + "\t").reduce(row, String::concat);
        row += "\n";
        return row;
    }

    protected void updateMagnitudes() {
        magnitudes = new ArrayList<>();
        electricalMagnitudes.stream().forEach((m) -> {
            if (m.isSaved()) {
                savedElectricalMagnitudes.add(m);
            }
            magnitudes.add(m);
        });
        stateVariables.stream().forEach((m) -> {
            if (m.isSaved()) {
                savedStateVariables.add(m);
            }
            magnitudes.add(m);
        });

    }

    public String getMagnitudesInARow(int index) {
        String row = "";
        row = savedElectricalMagnitudes.stream().map((m) -> m.getValueInPosition(index) + "\t").reduce(row, String::concat);
        row = savedStateVariables.stream().map((m) -> m.getValueInPosition(index) + "\t").reduce(row, String::concat);
        row += "\n";
        return row;
    }

    public String getMagnitudesInARow() {
        String row = "";
        row = savedElectricalMagnitudes.stream().map((m) -> m.getCurrentValue() + "\t").reduce(row, String::concat);
        row = savedStateVariables.stream().map((m) -> m.getCurrentValue() + "\t").reduce(row, String::concat);
        row += "\n";
        return row;
    }

    protected SimulationResult checkMagnitudes(int stepCounter) {
        SimulationResult result = new SimulationResult(SimulationResult.states.CORRECT, stepCounter, "Correct Magnitudes.");
        boolean partialCheck = false;
        for (Magnitude m : electricalMagnitudes) {
            if (Double.isNaN(m.getCurrentValue()) || Double.isInfinite(m.getCurrentValue())) {
                System.out.println( m.getTag() + ": " + m.getCurrentValue() );
                partialCheck = true;
            }
        }
        for (Magnitude m : stateVariables) {
            if (Double.isNaN(m.getCurrentValue()) || Double.isInfinite(m.getCurrentValue())) {
                System.out.println( m.getTag() + ": " + m.getCurrentValue() );
                partialCheck = true;
            }
        }
        if (partialCheck) {
            result = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, stepCounter, getCheckProblem());
            // Debug
            electricalMagnitudes.stream().forEach((m) -> {
                System.out.println( m.getTag() + ": " + m.getCurrentValue() );
            });
            stateVariables.stream().forEach((m) -> {
                System.out.println( m.getTag() + ": " + m.getCurrentValue() );
            });
        }
        return result;
    }

    private String getCheckProblem() {
        String auxString1 = "Electrical magnitudes: ";
        String auxString2 = "\n\nState variables: ";
        auxString1 = electricalMagnitudes.stream().map((m) -> "\n" + m.getRecentHistory()).reduce(auxString1, String::concat);
        auxString2 = stateVariables.stream().map((m) -> "\n" + m.getRecentHistory()).reduce(auxString2, String::concat);
        return auxString1 + auxString2;
    }

    public boolean acceptStep() {
        
        needDecrease = controlMagnitude.getBackTempValue(1) == 0
                ? (Math.abs(controlMagnitude.getCurrentValue()) > normalizedControlRatioDown)
                : Math.abs(controlMagnitude.getCurrentValue() / controlMagnitude.getBackTempValue(1)) > normalizedControlRatioDown;
        if( controlMagnitude.getBackTempValue(1)>0 && controlMagnitude.getCurrentValue()<0
                || controlMagnitude.getBackTempValue(1)<0 && controlMagnitude.getCurrentValue()>0){
            needDecrease = true;
        }
        acceptIncrease = needDecrease ? false : controlMagnitude.getBackTempValue(1) == 0
                ? (Math.abs(controlMagnitude.getCurrentValue()) > normalizedControlRatioUp)
                : Math.abs(controlMagnitude.getCurrentValue() / controlMagnitude.getBackTempValue(1)) > normalizedControlRatioUp;
        return !needDecrease;
    }

    // overriden
    public void setInstance() {
        int a = 0;
    }

    public void setValueInParameter(int parameterIndex, double value) {
        if (parameterIndex == 9) {
            System.out.println(parameters[parameterIndex].getValue());
        }
        parameters[parameterIndex].setValue(value);
        if (parameterIndex == 9) {
            System.out.println(parameters[parameterIndex].getValue());
        }
    }

    public ArrayList<Magnitude> getStateVariables2Plot() {
        ArrayList<Magnitude> sv2p = new ArrayList<>();
        savedStateVariables.stream().forEach((sv) -> {
            sv2p.add(sv);
        });
        return sv2p;
    }

    public void setMagnitudeValuesSize(int magnitudeValuesSize) {
        this.magnitudeValuesSize = magnitudeValuesSize;
        savedElectricalMagnitudes.stream().forEach((m) -> {
            m.setValuesSize(magnitudeValuesSize);
        });
        savedStateVariables.stream().forEach((m) -> {
            m.setValuesSize(magnitudeValuesSize);
        });
    }

    // overriden
    public boolean reachedON(int memristanceLevels) {
        return true;
    }

    // overriden
    public boolean reachedOFF(int memristanceLevels) {
        return true;
    }

    public boolean isLimitCurrent() {
        return limitCurrent;
    }

    public void setLimitCurrent(boolean limitCurrent) {
        this.limitCurrent = limitCurrent;
    }

    public double getNegativeComplianceCurrent() {
        return negativeComplianceCurrent;
    }

    public void setNegativeComplianceCurrent(double negativeComplianceCurrent) {
        this.negativeComplianceCurrent = negativeComplianceCurrent;
    }

    public double getPositiveComplianceCurrent() {
        return positiveComplianceCurrent;
    }

    public void setPositiveComplianceCurrent(double positiveComplianceCurrent) {
        this.positiveComplianceCurrent = positiveComplianceCurrent;
    }

    public String[] getParameterPresetNames() {
        return parameterPresetNames;
    }

    public void setParameterPresetNames(String[] parameterPresetNames) {
        this.parameterPresetNames = parameterPresetNames;
    }

    public int getNormalizedStateIndex() {
        return 0;
    }

    public int getMagnitudeValuesSize() {
        return magnitudeValuesSize;
    }

    public Magnitude getElectricalMagnitude(int index) {
        return electricalMagnitudes.get(index);
    }

    public Magnitude getStateVariable(int index) {
        return stateVariables.get(index);
    }

    public MemristorParameter getParameter(int index) {
        return parameters[index];
    }

    public String getMagnitudeName(int index) {
        return magnitudes.get(index).getTag();
    }

    public String getMagnitudeUnits(int index) {
        return magnitudes.get(index).getPrefixUnit();
    }

    public int getExportedMagnitudesNumber() {
        return magnitudes.size();
    }

    public ArrayList<Magnitude> getExportedMagnitudes() {
        return magnitudes;
    }


    public MemristorParameter[] getParameters() {
        return parameters;
    }

    public ArrayList<Magnitude> getElectricalMagnitudes() {
        return electricalMagnitudes;
    }

    public void setElectricalMagnitudes(
            ArrayList<Magnitude> electricalMagnitudes) {
        this.electricalMagnitudes = electricalMagnitudes;
    }

    public ArrayList<Magnitude> getStateVariables() {
        return stateVariables;
    }

    public void setStateVariables(ArrayList<Magnitude> stateVariables) {
        this.stateVariables = stateVariables;
    }

    public MemristorLibrary.Memristor_Model getMemristorType() {
        return memristorType;
    }

    public SimulationResult resolveStep(Timing timing, InputVoltage voltage) {
        return null;
    }

    public void setMemristorType(MemristorLibrary.Memristor_Model memristorType) {
        this.memristorType = memristorType;
    }

    public double getDerivedStateVariableControlRatioDown() {
        return derivedStateVariableControlRatioDown;
    }

    public void setDerivedStateVariableControlRatioDown(
            double derivedStateVariableControlRatioDown) {
        this.derivedStateVariableControlRatioDown = derivedStateVariableControlRatioDown;
    }

    public double getDerivedStateVariableControlRatioUp() {
        return derivedStateVariableControlRatioUp;
    }

    public void setDerivedStateVariableControlRatioUp(
            double derivedStateVariableControlRatioUp) {
        this.derivedStateVariableControlRatioUp = derivedStateVariableControlRatioUp;
    }

    public double getDerivedStateVariableAlmostZero() {
        return derivedStateVariableAlmostZero;
    }

    public void setDerivedStateVariableAlmostZero(
            double derivedStateVariableAlmostZero) {
        this.derivedStateVariableAlmostZero = derivedStateVariableAlmostZero;
    }

    public void setInstanceGoingON(int memristanceLevels) {

    }

    public void setInstanceGoingOFF(int memristanceLevels) {

    }

    public boolean reachedState(int stateCount, int memristanceLevels,
            boolean goingON) {
        return true;
    }

    public boolean isNeedDecrease() {
        return needDecrease;
    }

    public void setNeedDecrease(boolean needDecrease) {
        this.needDecrease = needDecrease;
    }

    public boolean isAcceptIncrease() {
        return acceptIncrease;
    }

    public void setAcceptIncrease(boolean acceptIncrease) {
        this.acceptIncrease = acceptIncrease;
    }

    public double getMaxTStepNeeded() {
        return maxTStepNeeded;
    }

    public void setMaxTStepNeeded(double maxTStepNeeded) {
        this.maxTStepNeeded = maxTStepNeeded;
    }

    public int getAmplitudeSignGoingON() {
        return amplitudeSignGoingON;
    }

    public void setAmplitudeSignGoingON(int amplitudeSignGoingON) {
        this.amplitudeSignGoingON = amplitudeSignGoingON;
    }

}
