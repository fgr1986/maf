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
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorTEAM extends MemristorModel {

    private final int TEAMWINDOW1 = 0;
    private final int TEAMWINDOW2 = 1;
    private final int GARCIAWINDOW = 2;
    private final String[] windowNames = {"TEAM window 1", "TEAM window 2", "Biolek like window", "No window"};

    private final double defaultR_off = 2e5;
    private final double defaultR_on = 300;
    private final double defaultI_off = 115e-6;
    private final double defaultI_on = -8.9e-6;
    private final double defaultK_off = 3.5e-6;//8e-13;//3.5e-6;
    private final double defaultK_on = -40e-6; //-8e-13;//-40e-6;
    private final double defaultAlpha_off = 3;// 10;
    private final double defaultAlpha_on = 3;// 10;
    private final double defaultA_off = 1.2e-9;
    private final double defaultA_on = 2e-9;
    private final double defaultX_off = 3e-9;
    private final double defaultX_on = 0;
    private final double defaultW_c = 107e-12;
    private final double defaultW_init = 1.21e-9;

    private final double defaultR_offSigma = 1e2;
    private final double defaultR_onSigma = 5;
    private final double defaultI_offSigma = 1e-8;
    private final double defaultI_onSigma = 1e-8;
    private final double defaultK_offSigma = 1e-11;
    private final double defaultK_onSigma = 1e-11;
    private final double defaultAlpha_offSigma = 1;
    private final double defaultAlpha_onSigma = 1;
    private final double defaultA_offSigma = 1e-11;
    private final double defaultA_onSigma = 1e-11;
    private final double defaultX_offSigma = 1e-11;
    private final double defaultX_onSigma = 1e-11;
    private final double defaultW_cSigma = 5e-13;
    private final double defaultW_initSigma = 1e-11;

    private double lambda;

    // parameter indexes
    public final int ROFFINDEX = 0;
    public final int RONINDEX = 1;
    public final int KOFFINDEX = 2;
    public final int KONINDEX = 3;
    public final int IOFFINDEX = 4;
    public final int IONINDEX = 5;
    public final int AOFFINDEX = 6;
    public final int AONINDEX = 7;
    public final int ALPHAOFFINDEX = 8;
    public final int ALPHAONINDEX = 9;
    public final int XOFFINDEX = 10;
    public final int XONINDEX = 11;
    public final int WCINDEX = 12;
    public final int XINITINDEX = 13;
    public final int WINDOWINDEX = 14;

    // magnitude indexes
    private final int XINDEX = 0;
    private final int WINDEX = 1;

    // private boolean forceDecreaseStep;
    private final Magnitude x;
    private final Magnitude w;

    // auxiliar variables
    private double rOff;
    private double rOn;
    private double kOff;
    private double kOn;
    private double iOff;
    private double iOn;
    private double aOff;
    private double aOn;
    private double xOff;
    private double xOn;
    private double alphaOff;
    private double alphaOn;
    private double wc;
    private double xinit;
    private int window;

    /**
     * Default constructor
     */
    public MemristorTEAM() {
        super(MemristorLibrary.Memristor_Model.TEAMMODEL);
        x = new Magnitude("x", true, XINDEX, 1e9, "nm");
        w = new Magnitude("normaliced", true, WINDEX, 1, "");
        stateVariables.add(x);
        stateVariables.add(w);
        // control magnitude
        controlMagnitude = x;

        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 15;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][ROFFINDEX] = new MemristorParameter(ROFFINDEX, "Roff", "", defaultR_off, true, defaultR_offSigma, true);
        parameterPresets[0][RONINDEX] = new MemristorParameter(RONINDEX, "Ron", "", defaultR_on, true, defaultR_onSigma, true);
        parameterPresets[0][KOFFINDEX] = new MemristorParameter(KOFFINDEX, "k_off", "", defaultK_off, true, defaultK_offSigma, true);
        parameterPresets[0][KONINDEX] = new MemristorParameter(KONINDEX, "k_on", "", defaultK_on, true, defaultK_onSigma, true);
        parameterPresets[0][IOFFINDEX] = new MemristorParameter(IOFFINDEX, "i_off", "", defaultI_off, true, defaultI_offSigma, true);
        parameterPresets[0][IONINDEX] = new MemristorParameter(IONINDEX, "i_on", "", defaultI_on, true, defaultI_onSigma, true);
        parameterPresets[0][AOFFINDEX] = new MemristorParameter(AOFFINDEX, "a_off", "", defaultA_off, true, defaultA_offSigma, true);
        parameterPresets[0][AONINDEX] = new MemristorParameter(AONINDEX, "a_on", "", defaultA_on, true, defaultA_onSigma, true);
        parameterPresets[0][ALPHAOFFINDEX] = new MemristorParameter(ALPHAOFFINDEX, "alpha_off", "", defaultAlpha_off, true, defaultAlpha_offSigma, true);
        parameterPresets[0][ALPHAONINDEX] = new MemristorParameter(ALPHAONINDEX, "alpha_on", "", defaultAlpha_on, true, defaultAlpha_onSigma, true);
        parameterPresets[0][XOFFINDEX] = new MemristorParameter(XOFFINDEX, "x_off", "", defaultX_off, true, defaultX_offSigma, true);
        parameterPresets[0][XONINDEX] = new MemristorParameter(XONINDEX, "x_on", "", defaultX_on, true, defaultX_onSigma, true);
        parameterPresets[0][WCINDEX] = new MemristorParameter(WCINDEX, "w_c", "", defaultW_c, true, defaultW_cSigma, true);
        parameterPresets[0][XINITINDEX] = new MemristorParameter(XINITINDEX, "x_init", "", defaultW_init, true, defaultW_initSigma, true);
        parameterPresets[0][WINDOWINDEX] = new MemristorParameter(WINDOWINDEX, "window", "", 0, false, windowNames);
        setParameterPreset(0);

        derivedStateVariableControlRatioDown = 2;
        derivedStateVariableControlRatioUp = 0.1;

        updateMagnitudes();
        System.out.println("Selecting the model of memristor: TEAM.");
    }

    /**
     * Copy constructor
     *
     * @param y
     */
    public MemristorTEAM(MemristorTEAM y) {
        super(MemristorLibrary.Memristor_Model.TEAMMODEL);
        x = new Magnitude("x", true, XINDEX, 1e9, "nm");
        w = new Magnitude("normaliced", true, WINDEX, 1, "");
        stateVariables.add(x);
        stateVariables.add(w);
        // control magnitude
        controlMagnitude = w;
        
        // wboundaries
        double[] wBoundaries = {0, 1};
        w.setHasBoundaries(true);
        w.setBoundaries(wBoundaries);
        w.setHasHardBoundaries(false);
        // complainCurrent        
        negativeComplianceCurrent = y.getNegativeComplianceCurrent();
        positiveComplianceCurrent = y.getPositiveComplianceCurrent();
        limitCurrent = y.isLimitCurrent();
        // parameters
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 15;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        int paramCount = 0;
        for (MemristorParameter mp : y.getParameters()) {
            parameterPresets[0][paramCount++] = new MemristorParameter(mp);
        }
        derivedStateVariableControlRatioDown = 2;
        derivedStateVariableControlRatioUp = 0.1;
        // set preset
        setParameterPreset(0);
        // Update magnitude list
        updateMagnitudes();
    }

    @Override
    public MemristorModel getCopy() {
        return new MemristorTEAM(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorTEAM();
    }

    @Override
    public void setInstance() {

        rOff = parameters[ROFFINDEX].getValue();
        rOn = parameters[RONINDEX].getValue();
        kOff = parameters[KOFFINDEX].getValue();
        kOn = parameters[KONINDEX].getValue();
        iOff = parameters[IOFFINDEX].getValue();
        iOn = parameters[IONINDEX].getValue();
        aOff = parameters[AOFFINDEX].getValue();
        aOn = parameters[AONINDEX].getValue();
        xOff = parameters[XOFFINDEX].getValue();
        xOn = parameters[XONINDEX].getValue();
        alphaOff = parameters[ALPHAOFFINDEX].getValue();
        alphaOn = parameters[ALPHAONINDEX].getValue();
        wc = parameters[WCINDEX].getValue();
        xinit = parameters[XINITINDEX].getValue();
        window = (int) parameters[WINDOWINDEX].getValue();

        // xboundaries
        double[] xBoundaries = {xOn, xOff};
        x.setHasBoundaries(true);
        x.setBoundaries(xBoundaries);

        lambda = Math.log(rOff / rOn);
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        x.setCurrentValue(xinit);
        w.setCurrentValue((x.getCurrentValue() - xOn) / (xOff - xOn));
        memristance.setCurrentValue((rOn * Math.exp(lambda * w.getCurrentValue())));
        this.updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        acceptIncrease = false;
        needDecrease = false;

        try {
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
            // compute stateVariable integration
            x.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, x, timing.gettStep()));
            w.setCurrentValue((x.getCurrentValue() - xOn) / (xOff - xOn));
            w.checkBoundaries();
            memristance.setCurrentValue((rOn * Math.exp(lambda * w.getCurrentValue())));
            // v=r*i
            current.setCurrentValue(voltage.getCurrentValue() / memristance.getCurrentValue());
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            time.setCurrentValue(time.getCurrentValue() + timing.gettStep());

            return checkMagnitudes(timing.getStepCounter());
        } catch (Exception exc) {
            return new SimulationResult(SimulationResult.states.SIMULATION_ERROR, timing.getStepCounter(), exc.getLocalizedMessage());
        }
    }

    private double deriv(double tInc,
            double stateVariableEstimation, InputVoltage inputVoltage) {
        double voltageEstimation = inputVoltage.getVoltageEstimation(tInc);
        double currentEstimation = voltageEstimation / memristance.getCurrentValue();
        double derivValue = f(currentEstimation, stateVariableEstimation) * ion_flow(currentEstimation);
        return derivValue;
    }

    private double ion_flow(double currentEstimation) {
        if (currentEstimation >= iOff) {
            return kOff * Math.pow(Math.abs(currentEstimation / iOff) - 1, alphaOff);
        } else if (currentEstimation <= iOn) {
            return kOn * Math.pow(Math.abs(currentEstimation / iOn) - 1, alphaOn);
        } else {
            return 0;
        }
    }

    private double f(double currentEstimation, double stateVariableEstimation) {
        double f;
        switch (window) {
            case TEAMWINDOW1: {
                if (currentEstimation == 0) {
                    f = 0;
                } else if (currentEstimation > 0) {
                    f = Math.exp(-Math.exp((stateVariableEstimation - aOff) / wc));
                } else {
                    f = Math.exp(-Math.exp((aOn - stateVariableEstimation) / wc));
                }
            }
            break;
            case TEAMWINDOW2:// team 1
            {
                if (currentEstimation == 0) {
                    f = 0;
                } else if (currentEstimation > 0) {
                    f = Math.exp(-Math.exp((stateVariableEstimation - xOff) / wc));
                } else {
                    f = Math.exp(-Math.exp((xOn - stateVariableEstimation) / wc));
                }
            }
            break;
            case GARCIAWINDOW: // biolek like
            {
                if (currentEstimation == 0) {
                    f = 0;
                } else if (currentEstimation > 0) {
                    f = 1 - Math.pow(stateVariableEstimation - aOn + 1, 20);
                } else {
                    f = 1 - Math.pow(stateVariableEstimation - aOff - 1, 20);
                }
            }
            break;
            default: // NOWINDOW
            {
                f = 1;
            }
            break;
        }
        return f;
    }

    @Override
    public void setInstanceGoingON(int memristanceLevels) {
        lambda = Math.log(rOff / rOn);
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        w.setCurrentValue((memristanceLevels - 0.5) / memristanceLevels);

        x.setCurrentValue(xOn + w.getCurrentValue() * (xOff - xOn));
        memristance.setCurrentValue(0);
        this.updateMagnitudesTempState();
        // System.out.println("Going ON: x " +
        // w.getCurrentValue());
    }

    @Override
    public void setInstanceGoingOFF(int memristanceLevels) {
        lambda = Math.log(rOff / rOn);
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        w.setCurrentValue(0.5 / memristanceLevels);
        x.setCurrentValue(xOn + w.getCurrentValue() * (xOff - xOn));
        memristance.setCurrentValue(0);
        this.updateMagnitudesTempState();
        // System.out.println("Going OFF: x " +
        // w.getCurrentValue());
    }

    // The characteristics of the model forces this:
    @Override
    public boolean reachedON(int memristanceLevels) {
        return w.getCurrentValue() <= 0.5 / memristanceLevels;
    }

    @Override
    public boolean reachedOFF(int memristanceLevels) {
        return w.getCurrentValue() >= (memristanceLevels - 0.5) / memristanceLevels;
    }

    @Override
    public int getNormalizedStateIndex() {
        return WINDEX;
    }

    @Override
    public int getAmplitudeSignGoingON() {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public boolean reachedState(int stateCount, int memristanceLevels,
            boolean goingON) {
        // ON-> x = 0; OFF -> x = 1
        if (goingON) {
            return w.getCurrentValue() <= (memristanceLevels - stateCount - 0.5) / memristanceLevels;
        } else {
            return w.getCurrentValue() >= (stateCount + 0.5) / memristanceLevels;
        }
    }

}
