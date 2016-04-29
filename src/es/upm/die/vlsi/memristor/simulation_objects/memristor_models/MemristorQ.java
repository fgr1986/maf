package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import es.upm.die.vlsi.memristor.math.RungeKutta;
import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 *
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor connected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorQ extends MemristorModel {

    private final int AINDEX = 0;
    private final int BINDEX = 1;
    private final int CINDEX = 2;
    private final int DINDEX = 3;

    private final int QINDEX = 4;
    
    private final Magnitude q;
    
    private double a;
    private double b;
    private double c;
    private double d;

    /**
     * Default constructor
     */
    public MemristorQ() {
        super(MemristorLibrary.Memristor_Model.QCONTROLLEDMODEL);
        derivedStateVariableControlRatioDown = 2;
        derivedStateVariableControlRatioUp = 0.1;
        q = new Magnitude("q", false, QINDEX, 1, "C");
        electricalMagnitudes.add(q);
        // control magnitude
        controlMagnitude = q;
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 4;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][AINDEX] = new MemristorParameter(AINDEX, "a", "a.u.", 10, true, 1, false);
        parameterPresets[0][BINDEX] = new MemristorParameter(BINDEX, "b", "a.u.", 0, true, 1, false);
        parameterPresets[0][CINDEX] = new MemristorParameter(CINDEX, "c", "a.u.", 1000, true, 10, false);
        parameterPresets[0][DINDEX] = new MemristorParameter(DINDEX, "d", "a.u.", 1, true, 1, false);
        setParameterPreset(0);
        updateMagnitudes();
    }

    public MemristorQ(MemristorQ y) {
        super(MemristorLibrary.Memristor_Model.QCONTROLLEDMODEL);
        derivedStateVariableControlRatioDown = 2;
        derivedStateVariableControlRatioUp = 0.1;
        q = new Magnitude("q", true, QINDEX, 1, "C");
        electricalMagnitudes.add(q);
        // control magnitude
        controlMagnitude = q;
        // complainCurrent        
        negativeComplianceCurrent = y.getNegativeComplianceCurrent();
        positiveComplianceCurrent = y.getPositiveComplianceCurrent();
        limitCurrent = y.isLimitCurrent();
        limitCurrent = y.isLimitCurrent();
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 4;
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
        return new MemristorQ(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorQ();
    }

    @Override
    public void setInstance() {
        a = parameters[AINDEX].getValue();
        b = parameters[BINDEX].getValue();
        c = parameters[CINDEX].getValue();
        d = parameters[DINDEX].getValue();
        q.setCurrentValue(0);
        memristance.setCurrentValue(d);
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        // update back values
        updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        // calculate new values
        acceptIncrease = false;
        needDecrease = false;
        try {
            memristance.setCurrentValue(a * Math.pow(q.getCurrentValue(), 3)
                    +b * Math.pow(electricalMagnitudes.get(QINDEX).getCurrentValue(), 2)
                    +c * electricalMagnitudes.get(QINDEX).getCurrentValue() +d);
            current.setCurrentValue(inputVoltage.getCurrentVoltage() / memristance.getCurrentValue());
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            // compute stateVariable integration
            q.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, q, timing.gettStep()));
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
            time.setCurrentValue(time.getCurrentValue() + timing.gettStep());

            // check magnitudes
            return checkMagnitudes( timing.getStepCounter() );
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
        double voltageEstimation = inputVoltage.getVoltageEstimation(tInc);
        return voltageEstimation/memristance.getCurrentValue();
    }
}
