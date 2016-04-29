package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import es.upm.die.vlsi.memristor.math.RungeKutta;
import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEARES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 *
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorUAB extends MemristorModel {

    public String[] windowPresets = {"Biolek",
        "Prodomakis"};

    public final int RONINDEX = 0;
    public final int ROFFINDEX = 1;
    public final int DINDEX = 2;
    public final int UVINDEX = 3;
    public final int XINITINDEX = 4;
    public final int PINDEX = 5;
    public final int JINDEX = 6;
    public final int WINDOWINDEX = 7;

    private double rOn;
    private double rOff;
    private double d;
    private double uv;
    private double xInit;
    private double p;
    private double j;
    private double window;

    private double k;
    private int p2;

    private final int XINDEX = 0;

    private final Magnitude x;
    
    /**
     * Default constructor
     */
    public MemristorUAB() {
        super(MemristorLibrary.Memristor_Model.NONLINEARDRIFTMODEL);
        x = new Magnitude("x", true, XINDEX, 1, "");
        stateVariables.add(x);
        // control magnitude
        controlMagnitude = x;
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 8;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][RONINDEX] = new MemristorParameter(RONINDEX, "Ron", ResourcesMAF.OHM1, 100, true, 10, true);
        parameterPresets[0][ROFFINDEX] = new MemristorParameter(ROFFINDEX, "Roff", ResourcesMAF.OHM1, 100e3, true, 10, true);
        parameterPresets[0][DINDEX] = new MemristorParameter(DINDEX, "D", "m", 10e-9, true, 10e-11, true);
        parameterPresets[0][UVINDEX] = new MemristorParameter(UVINDEX, "uv", "m^2s^-1V^-1", 1e-14, true, 1e-16, true);
        parameterPresets[0][XINITINDEX] = new MemristorParameter(XINITINDEX, "x_init", "a.u.", 0.1, true, 1e-2, true);
        parameterPresets[0][PINDEX] = new MemristorParameter(PINDEX, "p", "a.u.", 10);
        parameterPresets[0][JINDEX] = new MemristorParameter(JINDEX, "j", "a.u.", 1);
        parameterPresets[0][WINDOWINDEX] = new MemristorParameter(WINDOWINDEX, "window", "", 0, false, windowPresets);
        // set preset
        setParameterPreset(0);
        // Update Magnitudes list
        updateMagnitudes();
    }

    /**
     * Default constructor
     *
     * @param y
     */
    public MemristorUAB(MemristorUAB y) {
        super(MemristorLibrary.Memristor_Model.NONLINEARDRIFTMODEL);
        x = new Magnitude("x", true, XINDEX, 1, "");
        stateVariables.add(x);
        // control magnitude
        controlMagnitude = x;
        // complainCurrent        
        negativeComplianceCurrent = y.getNegativeComplianceCurrent();
        positiveComplianceCurrent = y.getPositiveComplianceCurrent();
        limitCurrent = y.isLimitCurrent();
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 8;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        int paramCount = 0;
        for (MemristorParameter mp : y.getParameters()) {
            parameterPresets[0][paramCount++] = new MemristorParameter(mp);
        }
        // set preset
        setParameterPreset(0);
        // Update magnitude list
        updateMagnitudes();
    }

    @Override
    public MemristorModel getCopy() {
        return new MemristorUAB(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorUAB();
    }

    @Override
    public void setInstance() {

        rOn = parameters[RONINDEX].getValue();
        rOff = parameters[ROFFINDEX].getValue();
        d = parameters[DINDEX].getValue();
        uv = parameters[UVINDEX].getValue();
        xInit = parameters[XINITINDEX].getValue();
        p = parameters[PINDEX].getValue();
        j = parameters[JINDEX].getValue();
        window = parameters[WINDOWINDEX].getValue();

        p2 = (int) p * 2;
        k = uv * rOn / (d * d);
        // wboundaries
        double[] xBoundaries = {0, 1};
        x.setHasBoundaries(true);
        x.setBoundaries(xBoundaries);
        x.setHasHardBoundaries(false);
        
        memristance.setCurrentValue(rOn * x.getCurrentValue()
                + rOff * (1 - x.getCurrentValue()));
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        // calculate new values
        acceptIncrease = false;
        needDecrease = false;
        try {
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
            current.setCurrentValue(voltage.getCurrentValue() / memristance.getCurrentValue());
            // check complain current
            checkLimitingCurrent();
            // compute stateVariable integration
            x.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, x, timing.gettStep()));
            x.checkBoundaries();
            memristance.setCurrentValue(rOn * x.getCurrentValue()  + rOff * (1 - x.getCurrentValue()));
            time.setCurrentValue(timing.getTimeCounter());
            // check magnitudes
            return checkMagnitudes(timing.getStepCounter());
        } catch (Exception exc) {
            return new SimulationResult(SimulationResult.states.SIMULATION_ERROR, timing.getStepCounter(), exc.getLocalizedMessage());
        }
    }

    /**
     *
     * @param tInc
     * @param stateVariableEstimation
     * @param inputVoltage
     * @return
     */
    public double deriv(double tInc,
            double stateVariableEstimation, InputVoltage inputVoltage) {        
        double currentEstimation = inputVoltage.getVoltageEstimation(tInc)/ memristance.getCurrentValue();
        return currentEstimation * k * window( stateVariableEstimation );
    }
    
    /**
     *
     * @param xValue
     * @return
     */
    public double window( double xValue ) {
        double w = 0;
        switch ((int) window) {
            case 0:{
                w = windowBiolek(xValue);
            }
            break;
            case 1:{
                w = windowProdromakis(xValue);
            }
            break;
        }
        return w;
    }

    /*
     * 
     * Fernando window function
     * 
     * @returns func f(x,i,p)={1-(x-stp(-i))^(2*p)} x between [0,1], 0 if other
     */
    public double windowBiolek( double xValue ) {
        return 1 - Math.pow(xValue - stp(-current.getCurrentValue()), p2);
    }

    /*
     * Fernando window function
     * 
     * @returns func f(x,i,p)={1-(x-stp(-i))^(2*p)} x between [0,1], 0 if other
     */
    public double windowProdromakis( double xValue) {
        // j(1-[(w-0.5)2+0.75]p)
        return j * Math.pow(1 - Math.pow( - 0.5, 2) + 0.75,  p);
    }

    /*
     * Auxiliar function
     * 
     * @returns the stp function of a variable
     */
    public double stp(double number) {
        if (number >= 0) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override
    public void setInstanceGoingON(int memristanceLevels) {
        rOn = parameters[RONINDEX].getValue();
        rOff = parameters[ROFFINDEX].getValue();
        d = parameters[DINDEX].getValue();
        uv = parameters[UVINDEX].getValue();
        xInit = parameters[XINITINDEX].getValue();
        p = parameters[PINDEX].getValue();
        j = parameters[JINDEX].getValue();
        window = parameters[WINDOWINDEX].getValue();

        p2 = (int) p * 2;
        k = uv * rOn / (d * d);
        x.setCurrentValue(0.5 / memristanceLevels);
        memristance.setCurrentValue(rOn * x.getCurrentValue()
                + rOff * (1 - x.getCurrentValue()));
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public void setInstanceGoingOFF(int memristanceLevels) {
        rOn = parameters[RONINDEX].getValue();
        rOff = parameters[ROFFINDEX].getValue();
        d = parameters[DINDEX].getValue();
        uv = parameters[UVINDEX].getValue();
        xInit = parameters[XINITINDEX].getValue();
        p = parameters[PINDEX].getValue();
        j = parameters[JINDEX].getValue();
        window = parameters[WINDOWINDEX].getValue();

        p2 = (int) p * 2;
        k = uv * rOn / (d * d);
        x.setCurrentValue((memristanceLevels - 0.5) / memristanceLevels);
        memristance.setCurrentValue(rOn * x.getCurrentValue()
                + rOff * (1 - x.getCurrentValue()));
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public boolean reachedON(int memristanceLevels) {
        // numLevels - 1 + 1/2
        return x.getCurrentValue() >= (memristanceLevels - 0.5) / memristanceLevels;
    }

    @Override
    public boolean reachedOFF(int memristanceLevels) {
        return x.getCurrentValue() <= 0.5 / memristanceLevels;
    }

    @Override
    public int getNormalizedStateIndex() {
        return XINDEX;
    }

    @Override
    public boolean reachedState(int stateCount, int memristanceLevels,
            boolean goingON) {
        // ON-> x = 1; OFF -> x = 0
        if (goingON) {
            return x.getCurrentValue() >= (stateCount + 0.5) / memristanceLevels;
        } else {
            return x.getCurrentValue() <= (memristanceLevels - stateCount - 0.5) / memristanceLevels;
        }
    }
}
