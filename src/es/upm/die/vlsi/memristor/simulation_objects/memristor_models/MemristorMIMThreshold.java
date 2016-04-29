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
 * @author Fernando García Redondo,
 * fernando.garca@gmaigetStateVariable(LINDEX).com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorMIMThreshold extends MemristorModel {

    private final double defaultA1 = 9e-8;
    private final double defaultA2 = 0.01e-8;
    private final double defaultP = 5;
    private final double defaultB1 = 2;
    private final double defaultB2 = 4;
    private final double defaultFon = 40e-3;
    private final double defaultFoff = 40e-3;
    private final double defaultSfo = 4;
    private final double defaultSfm = 20;
    private final double defaultWmin = 0.05;
    private final double defaultWmax = 0.95;
    private final double defaultP0 = 1.2;
    private final double defaultN = 4;
    private final double defaultWInit = 0.5;
    private final double defaultC = 8e-5;

    private final double defaultA1Sigma = 0.1e-3;
    private final double defaultA2Sigma = 0.001e-3;
    private final double defaultPSigma = 1;
    private final double defaultB1Sigma = 0.1;
    private final double defaultB2Sigma = 0.2;
    private final double defaultFonSigma = 4e-4;
    private final double defaultFoffSigma = 4e-4;
    private final double defaultSfoSigma = 0.1;
    private final double defaultSfmSigma = 1;
    private final double defaultWminSigma = 0.001;
    private final double defaultWmaxSigma = 0.001;
//	private double defaultLSigma = 1e-10;
    private final double defaultP0Sigma = 0.01;
    private final double defaultNSigma = 0;
    private final double defaultWInitSigma = 0.05;
    private final double defaultCSigma = 0;
    public final int A1INDEX = 0;
    public final int A2INDEX = 1;
    public final int B1INDEX = 2;
    public final int B2INDEX = 3;
    public final int PINDEX = 4;
    public final int FOFFINDEX = 5;
    public final int FONINDEX = 6;
    public final int SFOINDEX = 7;
    public final int SFMINDEX = 8;
    public final int WMININDEX = 9;
    public final int WMAXINDEX = 10;
    public final int P0INDEX = 11;
    public final int NINDEX = 12;
    public final int WINITINDEX = 13;
    public final int CINDEX = 14;

    private final double constMinI = 1e-12;
    private final double constMinV = 1e-9;

    private final int WINDEX = 0;

    private double p2;
    private double c_1;
    private double wmax_wmin;
    private double[] new_levels;

    // state variables
    private final Magnitude w;

    // aux variables
    private double a1;
    private double a2;
    private double b1;
    private double b2;
    private double p;
    private double fOff;
    private double fOn;
    private double sfo;
    private double sfm;
    private double wmin;
    private double wmax;
    private double p0;
    private double n;
    private double winit;
    private double c;

    /**
     * Default constructor
     */
    public MemristorMIMThreshold() {
        super(MemristorLibrary.Memristor_Model.MIMTHRESHOLDMODEL);
        double[] wBoundaries = {0, 1};
        w = new Magnitude("w", true, WINDEX, 1, "", true, wBoundaries);
        stateVariables.add(w);
        // control magnitude
        controlMagnitude = w;
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 15;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][A1INDEX] = new MemristorParameter(A1INDEX, "a1", "", defaultA1, true, defaultA1Sigma, true);
        parameterPresets[0][A2INDEX] = new MemristorParameter(A2INDEX, "a2", "", defaultA2, true, defaultA2Sigma, true);
        parameterPresets[0][B1INDEX] = new MemristorParameter(B1INDEX, "b1", "", defaultB1, true, defaultB1Sigma, true);
        parameterPresets[0][B2INDEX] = new MemristorParameter(B2INDEX, "b2", "", defaultB2, true, defaultB2Sigma, true);
        parameterPresets[0][PINDEX] = new MemristorParameter(PINDEX, "p", "", defaultP, true, defaultPSigma, true);
        parameterPresets[0][FOFFINDEX] = new MemristorParameter(FOFFINDEX, "f_off", "", defaultFon, true, defaultFonSigma, true);
        parameterPresets[0][FONINDEX] = new MemristorParameter(FONINDEX, "f_on", "", defaultFoff, true, defaultFoffSigma, true);
        parameterPresets[0][SFOINDEX] = new MemristorParameter(SFOINDEX, "sfo", "", defaultSfo, true, defaultSfoSigma, true);
        parameterPresets[0][SFMINDEX] = new MemristorParameter(SFMINDEX, "sfm", "", defaultSfm, true, defaultSfmSigma, true);
        parameterPresets[0][WMININDEX] = new MemristorParameter(WMININDEX, "w_min", "", defaultWmin, true, defaultWminSigma, true);
        parameterPresets[0][WMAXINDEX] = new MemristorParameter(WMAXINDEX, "w_max", "", defaultWmax, true, defaultWmaxSigma, true);
        parameterPresets[0][P0INDEX] = new MemristorParameter(P0INDEX, "p0", "", defaultP0, true, defaultP0Sigma, true);
        parameterPresets[0][NINDEX] = new MemristorParameter(NINDEX, "n", "", defaultN, true, defaultNSigma, true);
        parameterPresets[0][WINITINDEX] = new MemristorParameter(WINITINDEX, "w_init", "", defaultWInit, true, defaultWInitSigma, true);
        parameterPresets[0][CINDEX] = new MemristorParameter(CINDEX, "c (scale f_i)", "", defaultC, true, defaultCSigma, true);
        // set preset
        setParameterPreset(0);
        derivedStateVariableControlRatioDown = 5;
        derivedStateVariableControlRatioUp = 0.1;
        // Set up magnitude list
        updateMagnitudes();
        System.out.println("Selecting the model: MIM Threshold memristor.");
    }

    public MemristorMIMThreshold(MemristorMIMThreshold y) {
        super(MemristorLibrary.Memristor_Model.MIMTHRESHOLDMODEL);
        double[] wBoundaries = {0, 1};
        w = new Magnitude("w", true, WINDEX, 1, "", true, wBoundaries);
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
        memristorParameterCount = 15;
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
    public MemristorMIMThreshold getCopy() {
        return new MemristorMIMThreshold(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorMIMThreshold();
    }

    @Override
    public void setInstance() {
        a1 = parameters[A1INDEX].getValue();
        a2 = parameters[A2INDEX].getValue();
        b1 = parameters[B1INDEX].getValue();
        b2 = parameters[B2INDEX].getValue();
        p = parameters[PINDEX].getValue();
        fOff = parameters[FOFFINDEX].getValue();
        fOn = parameters[FONINDEX].getValue();
        sfo = parameters[SFOINDEX].getValue();
        sfm = parameters[SFMINDEX].getValue();
        wmin = parameters[WMININDEX].getValue();
        wmax = parameters[WMAXINDEX].getValue();
        p0 = parameters[P0INDEX].getValue();
        n = parameters[NINDEX].getValue();
        winit = parameters[WINITINDEX].getValue();
        c = parameters[CINDEX].getValue();
        // wboundaries
        double[] wBoundaries = {wmin, wmax};
        w.setHasBoundaries(true);
        w.setBoundaries(wBoundaries);
        w.setHasHardBoundaries(false);
        // util constants
        wmax_wmin = wmax - wmin;
        c_1 = 1 / c;
        p2 = (int) (2 * p);
        w.setCurrentValue(winit);
        updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        acceptIncrease = false;
        needDecrease = false;
        try {
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
            // compute stateVariable integration
            w.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, w, timing.gettStep()));
            w.checkBoundaries();
            current.setCurrentValue(//getParameter(LINDEX).getValue() *
                    Math.pow(w.getCurrentValue(), n)
                    * a1 * Math.sinh(b1 * voltage.getCurrentValue())
                    + a2 * (Math.exp(b2 * voltage.getCurrentValue()) - 1));
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            if (Math.abs(current.getCurrentValue()) > constMinI && Math.abs(inputVoltage.getCurrentVoltage()) > constMinV) {
                memristance.setCurrentValue(Math.abs(inputVoltage.getCurrentVoltage() / current.getCurrentValue()));
            } else {
                memristance.setCurrentValue(memristance.getBackTempValue(1));
            }
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

        double ro = sf(stateVariableEstimation);
        return g(voltageEstimation, stateVariableEstimation, ro);
    }

    private double g(double voltageEstimation, double stateVariableEstimation, double ro) {
        if (stateVariableEstimation > 0 && stateVariableEstimation <= wmax) {
            double auxV1 = 1 - stateVariableEstimation / (2 * p0);
            return c_1 * fOn * auxV1 * Math.exp(ro * p0 * (1 - Math.sqrt(auxV1)));

        } else if (stateVariableEstimation < 0 && stateVariableEstimation >= wmin) {
            double auxV2 = 1 + stateVariableEstimation / (2 * p0);
            return -c_1 * fOff * auxV2 * Math.exp(ro * p0 * (1 - Math.sqrt(auxV2)));
        } else {
            return 0;
        }
    }

    private double sf(double stateVariableEstimation) {
        return sfo + sfm * (1 - Math.pow((2 * stateVariableEstimation - 1), p2));
    }

    @Override
    public void setInstanceGoingON(int memristanceLevels) {
        //from 0 to 1 (w)
        // ideal 1->0;
        // real 0.8->0.2
        // i = r*wmas_wmin + wmin
        p2 = (int) (2 * p);
        c_1 = 1 / c;
        wmax_wmin = wmax - wmin;
        w.setCurrentValue((0.5 / memristanceLevels) * wmax_wmin + wmin);
        new_levels = new double[memristanceLevels];
        for (int i = 0; i < memristanceLevels; i++) {
            new_levels[i] = ((i + 0.5) / memristanceLevels) * wmax_wmin + wmin;
        }
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public void setInstanceGoingOFF(int memristanceLevels) {
        //
        p2 = (int) (2 * p);
        c_1 = 1 / c;
        wmax_wmin = wmax - wmin;
        w.setCurrentValue(((memristanceLevels - 0.5) / memristanceLevels) * wmax_wmin + wmin);
        new_levels = new double[memristanceLevels];
        for (int i = 0; i < memristanceLevels; i++) {
            new_levels[i] = ((memristanceLevels - i - 0.5) / memristanceLevels) * wmax_wmin + wmin;
        }
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public boolean reachedON(int memristanceLevels) {
        return w.getCurrentValue() >= new_levels[memristanceLevels - 1];
    }

    @Override
    public boolean reachedOFF(int memristanceLevels) {
        return w.getCurrentValue() <= new_levels[memristanceLevels - 1];
    }

    @Override
    public boolean reachedState(int stateCount, int memristanceLevels,
            boolean goingON) {
        // ON-> w = 1; OFF -> w = 0
        if (stateCount == 0) {
            return true;
        }
        if (goingON) {
            return w.getCurrentValue() >= new_levels[stateCount - 1];
        } else {
            return w.getCurrentValue() <= new_levels[stateCount - 1];
        }
    }

    @Override
    public int getNormalizedStateIndex() {
        return WINDEX;
    }

}
