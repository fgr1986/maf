package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import es.upm.die.vlsi.memristor.math.RungeKutta;
import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 *
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorModifiedSimmons extends MemristorModel {

    // 1 eV = 1 V * e
    // Constants
    private double constLambdaFinal;
    private final double constIntegration = 1e9;
    // Current parameters
    // private Parameter T; // Temperature in Kelvins
    private final double defaultRoff = 2e4;
    private double defaultRon = 100;
    private final double defaultI_off = 115e-6;// collected in A
    private final double defaultI_on = 8.9e-6;// collected in A
    private final double defaultF_off = 3.5e-6;// collected in s
    private final double defaultF_on = 40e-6;// collected in s
    private final double defaultA_off = 1.2;// collected in nm
    private final double defaultA_on = 1.8;// collected in nm
    private final double defaultX_off = 3;// collected in nm
    private final double defaultX_on = 0;// collected in nm
    private final double defaultB = 500e-6;// collected in A
    private final double defaultW_c = 107e-3;// collected in nm
    private final double defaultW_init = 1.21;// collected in nm

    private final double defaultI_offSigma = 1e-7;// collected in A
    private final double defaultI_onSigma = 1e-7;// collected in A
    private final double defaultF_offSigma = 1e-7;// collected in s
    private final double defaultF_onSigma = 1e-7;// collected in s
    private final double defaultA_offSigma = 1e-11;// collected in nm
    private final double defaultA_onSigma = 1e-11;// collected in nm
    private final double defaultX_offSigma = 1e-11;// collected in nm
    private final double defaultX_onSigma = 1e-11;// collected in nm
    private final double defaultBSigma = 1e-6;// collected in A
    private final double defaultW_cSigma = 1e-10;// collected in nm
    private final double defaultW_initSigma = 1e-11;// collected in nm
    private final double defaultRoffSigma = 100;
    private final double defaultRonSigma = 10;

    public final int ROFFINDEX = 0;
    public final int RONINDEX = 1;
    // tunnel barrier parametres
    public final int IOFFINDEX = 2; // collected in A
    public final int IONINDEX = 3;// collected in A
    public final int FOFFINDEX = 4;// collected in s
    public final int FONINDEX = 5;// collected in s
    public final int AOFFINDEX = 6;// collected in nm
    public final int AONINDEX = 7;// collected in nm
    public final int XOFFINDEX = 8;// collected in nm
    public final int XONINDEX = 9;// collected in nm
    public final int BINDEX = 10;// collected in A
    public final int WCINDEX = 11;// collected in nm
    public final int WINITINDEX = 12;// collected in nm
    // Magnitudes w, w_i in nm
    private final int XINDEX = 0;
    private final int WINDEX = 1;

    // private boolean forceDecreaseStep;
    private final Magnitude x;
    private final Magnitude w;

    // auxiliar variables
    private double rOff;
    private double rOn;
    private double iOff;
    private double iOn;
    private double fOff;
    private double fOn;
    private double aOff;
    private double aOn;
    private double xOff;
    private double xOn;
    private double b;
    private double wc;
    private double winit;

    /**
     * Default constructor
     */
    public MemristorModifiedSimmons() {
        super(MemristorLibrary.Memristor_Model.MODIFIEDSIMMONSMODEL);
        this.defaultRon = 100;
        x = new Magnitude("x", true, XINDEX, 1, "");
        w = new Magnitude("w", true, WINDEX, 1, "nm");
        stateVariables.add(x);
        stateVariables.add(w);
        // control magnitude
        controlMagnitude = w;
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 13;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][ROFFINDEX] = new MemristorParameter(ROFFINDEX, "Roff", ResourcesMAF.OHM1, defaultRoff, true, defaultRoffSigma, true);
        parameterPresets[0][RONINDEX] = new MemristorParameter(RONINDEX, "Ron", ResourcesMAF.OHM1, defaultRon, true, defaultRonSigma, true);
        parameterPresets[0][IOFFINDEX] = new MemristorParameter(IOFFINDEX, "i_off", "A", defaultI_off, true, defaultI_offSigma, true);
        parameterPresets[0][IONINDEX] = new MemristorParameter(IONINDEX, "i_on", "A", defaultI_on, true, defaultI_onSigma, true);
        parameterPresets[0][FOFFINDEX] = new MemristorParameter(FOFFINDEX, "f_off", "s", defaultF_off, true, defaultF_offSigma, true);
        parameterPresets[0][FONINDEX] = new MemristorParameter(FONINDEX, "f_on", "s", defaultF_on, true, defaultF_onSigma, true);
        parameterPresets[0][AOFFINDEX] = new MemristorParameter(AOFFINDEX, "a_off", "nm", defaultA_off, true, defaultA_offSigma, true);
        parameterPresets[0][AONINDEX] = new MemristorParameter(AONINDEX, "a_on", "nm", defaultA_on, true, defaultA_onSigma, true);
        parameterPresets[0][XOFFINDEX] = new MemristorParameter(XOFFINDEX, "x_off", "nm", defaultX_off, true, defaultX_offSigma, true);
        parameterPresets[0][XONINDEX] = new MemristorParameter(XONINDEX, "x_on", "nm", defaultX_on, true, defaultX_onSigma, true);
        parameterPresets[0][BINDEX] = new MemristorParameter(BINDEX, "b", "A", defaultB, true, defaultBSigma, true);
        parameterPresets[0][WCINDEX] = new MemristorParameter(WCINDEX, "w_c", "nm", defaultW_c, true, defaultW_cSigma, true);
        parameterPresets[0][WINITINDEX] = new MemristorParameter(WINITINDEX, "w_init", "nm", defaultW_init, true, defaultW_initSigma, true);
        // set preset
        setParameterPreset(0);

        derivedStateVariableControlRatioDown = 5;
        derivedStateVariableControlRatioUp = 0.1;
        updateMagnitudes();
        System.out.println("Selecting the model of memristor: HP.");
    }

    public MemristorModifiedSimmons(MemristorModifiedSimmons y) {
        super(MemristorLibrary.Memristor_Model.MODIFIEDSIMMONSMODEL);
        this.defaultRon = 100;
        x = new Magnitude("x", true, XINDEX, 1, "");
        w = new Magnitude("w", true, WINDEX, 1, "nm");
        stateVariables.add(x);
        stateVariables.add(w);
        // control magnitude
        controlMagnitude = w;
        // complainCurrent        
        negativeComplianceCurrent = y.getNegativeComplianceCurrent();
        positiveComplianceCurrent = y.getPositiveComplianceCurrent();
        limitCurrent = y.isLimitCurrent();
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 13;
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
        return new MemristorModifiedSimmons(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorModifiedSimmons();
    }

    @Override
    public void setInstance() {

        rOff = getParameter(ROFFINDEX).getValue();
        rOn = getParameter(RONINDEX).getValue();
        iOff = getParameter(IOFFINDEX).getValue();
        iOn = getParameter(IONINDEX).getValue();
        fOff = getParameter(FONINDEX).getValue();
        fOn = getParameter(FOFFINDEX).getValue();
        aOff = getParameter(AOFFINDEX).getValue();
        aOn = getParameter(AONINDEX).getValue();
        xOff = getParameter(XOFFINDEX).getValue();
        xOn = getParameter(XONINDEX).getValue();
        b = getParameter(BINDEX).getValue();
        wc = getParameter(WCINDEX).getValue();
        winit = getParameter(WINITINDEX).getValue();

        constLambdaFinal = Math.log(rOff / rOn);
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        w.setCurrentValue(winit);
        x.setCurrentValue((w.getCurrentValue() - xOn) / (xOff - xOn));
        memristance.setCurrentValue((rOn * Math.exp(constLambdaFinal * (w.getCurrentValue() - xOn) / (xOff - xOn))));

        this.updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        acceptIncrease = false;
        needDecrease = false;
        try {
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
            // compute stateVariable integration
            w.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, w, timing.gettStep()));
            checkWLimits();
            x.setCurrentValue((w.getCurrentValue() - xOn) / (xOff - xOn));

            memristance.setCurrentValue((rOn * Math.exp(constLambdaFinal * (w.getCurrentValue() - xOff) / (xOn - xOff))));
            // v=r*i
            current.setCurrentValue(voltage.getCurrentValue() / memristance.getCurrentValue());
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            time.setCurrentValue(time.getCurrentValue() + timing.gettStep());
            // check magnitudes
            return checkMagnitudes(timing.getStepCounter());
        } catch (Exception exc) {
            return new SimulationResult(SimulationResult.states.SIMULATION_ERROR, timing.getStepCounter(), exc.getLocalizedMessage());
        }
    }

    private double deriv(double tInc,
            double stateVariableEstimation, InputVoltage inputVoltage) {
        double voltageEstimation = inputVoltage.getVoltageEstimation(tInc);
        double currentEstimation = voltageEstimation / memristance.getCurrentValue();
        if (voltageEstimation > 0) {
            return fOff * Math.sinh(currentEstimation / iOff)
                    * Math.exp(-Math.exp((stateVariableEstimation - aOff) / wc) - stateVariableEstimation / wc);
        } else {
            return fOn * Math.sinh(currentEstimation / iOn)
                    * Math.exp(-Math.exp((aOn - stateVariableEstimation) / wc) - stateVariableEstimation / wc);
        }
    }

    private void checkWLimits() {
        if (w.getCurrentValue() < xOn) {
            w.setCurrentValue(xOff);
            needDecrease = true;
        }
        if (w.getCurrentValue() > xOff) {
            w.setCurrentValue(xOn);
            needDecrease = true;
        }
    }

    @Override
    public void setInstanceGoingON(int memristanceLevels) {
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        x.setCurrentValue(0.5 / memristanceLevels);
        w.setCurrentValue((aOn - aOff) * x.getCurrentValue() + aOff);
        memristance.setCurrentValue(
                (rOn * Math.exp(constLambdaFinal * (w.getCurrentValue() - aOn) / (aOff - aOn))));
        this.updateMagnitudesTempState();
    }

    @Override
    public void setInstanceGoingOFF(int memristanceLevels) {
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        x.setCurrentValue((memristanceLevels - 0.5) / memristanceLevels);
        w.setCurrentValue((aOn - aOff) * x.getCurrentValue() + aOff);
        memristance.setCurrentValue((rOn * Math.exp(constLambdaFinal * (w.getCurrentValue() - aOn) / (aOff - aOn))));

        this.updateMagnitudesTempState();
    }

    @Override
    public boolean reachedON(int memristanceLevels) {
        return x.getCurrentValue() >= (memristanceLevels - 0.5) / memristanceLevels;
// numLevels - 1 + 1/2
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
