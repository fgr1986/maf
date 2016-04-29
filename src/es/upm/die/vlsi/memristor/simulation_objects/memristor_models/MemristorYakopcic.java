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
public class MemristorYakopcic extends MemristorModel {

    private final String[] presets = {"D-SiCCu-80um", "Air Force Research Laboratory - Boise State University Chalcogenide memristor, SMACD version",
        "HP TiO2 memristor", "Air Force Research Laboratory - Boise State University Chalcogenide memristor",
        "University of Michigan memristor"};
    private final double[] defaultA1 = {0.097, 0.097, 2.3e-4, 0.097, 3.7e-7};
    private final double[] defaultA2 = {0.05, 0.097, 3.8e-4, 0.097, 4.35e-7};
    private final double[] defaultB1 = {0.005, 0.05, 1, 0.05, 0.7};
    private final double[] defaultB2 = {0.005, 0.05, 1, 0.05, 0.7};
    private final double[] defaultAlphaP = {1, 1, 4, 1, 1.2};
    private final double[] defaultAlphaN = {25, 0.5, 24, 5, 3};
    private final double[] defaultAP = {4e3, 1e6, 5, 4000, 0.005};
    private final double[] defaultAN = {4e3, 1e6, 30, 4000, 0.08};
    private final double[] defaultVP = {0.16, 0.5, 1.2, 0.16, 1.5};
    private final double[] defaultVN = {0.16, 0.5, 0.6, 0.15, 0.5};
    private final double[] defaultXP = {0.3, 0.9, 0.7, 0.3, 0.2};
    private final double[] defaultXN = {0.5, 0.1, 0.8, 0.5, 0.5};
    private final double[] defaultXInit = {0.001, 0.001, 0.02, 0.001, 0.1};
    private final double[] defaultRInit = {2e2, 215, 215, 215, 215};
    private final double[] defaultEta = {1, 1, 1, 1, 1};

    private final double[] defaultA1Sigma = {1e-2, 1e-2, 1e-5, 1e-2, 1e-9};
    private final double[] defaultA2Sigma = {1e-2, 1e-2, 1e-5, 1e-2, 1e-9};
    private final double[] defaultB1Sigma = {1e-3, 1e-3, 1e-1, 1e-3, 1e-2};
    private final double[] defaultB2Sigma = {1e-3, 1e-3, 1e-1, 1e-3, 1e-2};
    private final double[] defaultAlphaPSigma = {1e-2, 1e-2, 1, 1e-2, 1e-2};
    private final double[] defaultAlphaNSigma = {1e-2, 1e-2, 1, 1e-2, 1e-2};
    private final double[] defaultAPSigma = {100, 100, 1, 10, 1e-4};
    private final double[] defaultANSigma = {100, 100, 2, 10, 1e-4};
    private final double[] defaultVPSigma = {1e-2, 1e-2, 1e-1, 1e-2, 1e-1};
    private final double[] defaultVNSigma = {1e-2, 1e-2, 1e-1, 1e-2, 1e-1};
    private final double[] defaultXPSigma = {1e-2, 1e-2, 0.5e-1, 1e-2, 1e-2};
    private final double[] defaultXNSigma = {1e-2, 1e-2, 0.5e-1, 1e-2, 1e-2};
    private final double[] defaultXInitSigma = {1e-2, 1e-2, 1e-2, 1e-2, 1e-2};
    private final double[] defaultRInitSigma = {10, 10, 10, 10, 10};
    private final int A1INDEX = 0;
    private final int A2INDEX = 1;
    private final int ALPHAPINDEX = 2;
    private final int ALPHANINDEX = 3;
    private final int B1INDEX = 4;
    private final int B2INDEX = 5;
    private final int APINDEX = 6;
    private final int ANINDEX = 7;
    private final int VPINDEX = 8;
    private final int VNINDEX = 9;
    private final int XPINDEX = 10;
    private final int XNINDEX = 11;
    private final int XINITINDEX = 12;
    private final int RINITINDEX = 13;
    private final int ETAINDEX = 14;

    private double a1;
    private double a2;
    private double b1;
    private double b2;
    private double alphaP;
    private double alphaN;
    private double aP;
    private double aN;
    private double vP;
    private double vN;
    private double xP;
    private double xN;
    private double xInit;
    private double rInit;
    private double eta;

    private double expVp;
    private double expVn;
    private final double constMinI = 1e-9;
    private final double constMinV = 1e-6;

    private final int XINDEX = 0;

    // state variables
    private final Magnitude x;

    /**
     * Default constructor
     */
    public MemristorYakopcic() {
        super(MemristorLibrary.Memristor_Model.YAKOPCICEXPONENTIALDRIFTMODEL);
        double[] xBoundaries = {0, 1};
        x = new Magnitude("x", true, XINDEX, 1, "a.u.", true, xBoundaries);
        stateVariables.add(x);
        // control magnitude
        controlMagnitude = x;
        parameterPresetNames = presets;
        memristorPresetsCount = 5;
        memristorParameterCount = 15;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        for (int i = 0; i < memristorPresetsCount; i++) {
            parameterPresets[i][A1INDEX] = new MemristorParameter(A1INDEX, "a1", "a.u.", defaultA1[i], true, defaultA1Sigma[i], true);
            parameterPresets[i][A2INDEX] = new MemristorParameter(A2INDEX, "a2", "a.u.", defaultA2[i], true, defaultA2Sigma[i], true);
            parameterPresets[i][ALPHAPINDEX] = new MemristorParameter(ALPHAPINDEX, "alphaP", "a.u.", defaultAlphaP[i], true, defaultAlphaPSigma[i], true);
            parameterPresets[i][ALPHANINDEX] = new MemristorParameter(ALPHANINDEX, "alphaN", "a.u.", defaultAlphaN[i], true, defaultAlphaNSigma[i], true);
            parameterPresets[i][B1INDEX] = new MemristorParameter(B1INDEX, "b1", "a.u.", defaultB1[i], true, defaultB1Sigma[i], true);
            parameterPresets[i][B2INDEX] = new MemristorParameter(B2INDEX, "b2", "a.u.", defaultB2[i], true, defaultB2Sigma[i], true);
            parameterPresets[i][APINDEX] = new MemristorParameter(APINDEX, "Ap", "a.u.", defaultAP[i], true, defaultAPSigma[i], true);
            parameterPresets[i][ANINDEX] = new MemristorParameter(ANINDEX, "An", "a.u.", defaultAN[i], true, defaultANSigma[i], true);
            parameterPresets[i][VPINDEX] = new MemristorParameter(VPINDEX, "Vp", "a.u.", defaultVP[i], true, defaultVPSigma[i], true);
            parameterPresets[i][VNINDEX] = new MemristorParameter(VNINDEX, "Vn", "a.u.", defaultVN[i], true, defaultVNSigma[i], true);
            parameterPresets[i][XPINDEX] = new MemristorParameter(XPINDEX, "Xp", "a.u.", defaultXP[i], true, defaultXPSigma[i], true);
            parameterPresets[i][XNINDEX] = new MemristorParameter(XNINDEX, "Xn", "a.u.", defaultXN[i], true, defaultXNSigma[i], true);
            parameterPresets[i][XINITINDEX] = new MemristorParameter(XINITINDEX, "x_init", "a.u.", defaultXInit[i], true, defaultXInitSigma[i], true);
            parameterPresets[i][RINITINDEX] = new MemristorParameter(RINITINDEX, "R_init", "a.u.", defaultRInit[i], true, defaultRInitSigma[i], true);
            parameterPresets[i][ETAINDEX] = new MemristorParameter(ETAINDEX, "eta", "", defaultEta[i]);
        }
        // set preset
        setParameterPreset(0);
        // Update magnitude list
        updateMagnitudes();
    }

    /**
     * Copy constructor
     *
     * @param y
     */
    public MemristorYakopcic(MemristorYakopcic y) {
        super(MemristorLibrary.Memristor_Model.YAKOPCICEXPONENTIALDRIFTMODEL);
        double[] xBoundaries = {0, 1};
        x = new Magnitude("x", true, XINDEX, 1, "a.u.", true, xBoundaries);
        stateVariables.add(x);
        // control magnitude
        controlMagnitude = x;
        // complainCurrent        
        negativeComplianceCurrent = y.getNegativeComplianceCurrent();
        positiveComplianceCurrent = y.getPositiveComplianceCurrent();
        limitCurrent = y.isLimitCurrent();
        // parameter presets
        parameterPresetNames = presets;
        memristorPresetsCount = 5;
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
    public MemristorModel getCopy() {
        return new MemristorYakopcic(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorYakopcic();
    }

    @Override
    public void setInstance() {
        // parameters

        a1 = parameters[A1INDEX].getValue();
        a2 = parameters[A2INDEX].getValue();
        b1 = parameters[B1INDEX].getValue();
        b2 = parameters[B2INDEX].getValue();
        alphaP = parameters[ALPHAPINDEX].getValue();
        alphaN = parameters[ALPHANINDEX].getValue();
        aP = parameters[APINDEX].getValue();
        aN = parameters[ANINDEX].getValue();
        vP = parameters[VPINDEX].getValue();
        vN = parameters[VNINDEX].getValue();
        xP = parameters[XPINDEX].getValue();
        xN = parameters[XNINDEX].getValue();
        xInit = parameters[XINITINDEX].getValue();
        rInit = parameters[RINITINDEX].getValue();
        eta = parameters[ETAINDEX].getValue();
        // util constants
        expVp = Math.exp(vP);
        expVn = Math.exp(vN);
        // Please, remark
        x.setCurrentValue(xInit);
        
        // wboundaries
        double[] xBoundaries = {0, 1};
        x.setHasBoundaries(true);
        x.setBoundaries(xBoundaries);
        x.setHasHardBoundaries(false);
        
        memristance.setCurrentValue(rInit);
        updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        acceptIncrease = false;
        needDecrease = false;
        try {
            // compute stateVariable integration
            x.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, x, timing.gettStep()));
            x.checkBoundaries();
            if (inputVoltage.getCurrentVoltage() > 0) {
                current.setCurrentValue(a1 * x.getCurrentValue() * Math.sinh(b1 * inputVoltage.getCurrentVoltage()));
            } else {
                current.setCurrentValue(a2 * x.getCurrentValue() * Math.sinh(b2 * inputVoltage.getCurrentVoltage()));
            }
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            if (Math.abs(current.getCurrentValue()) > constMinI
                    && Math.abs(inputVoltage.getCurrentVoltage()) > constMinV) {
                memristance.setCurrentValue(Math.abs(inputVoltage.getCurrentVoltage() / current.getCurrentValue()));
            } else {
                memristance.setCurrentValue(memristance.getBackTempValue(1));
            }
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
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
    private double deriv(double tInc,
            double stateVariableEstimation, InputVoltage inputVoltage) {
        double voltageEstimation = inputVoltage.getVoltageEstimation(tInc);
        double g;
        double f;
        // g
        if (voltageEstimation > vP) {
            g = aP * (Math.exp(voltageEstimation) - expVp);
        } else if (voltageEstimation < -vN) {
            g = -aN * (Math.exp(-voltageEstimation) - expVn);
        } else {
            g = 0;
        }
        // f
        if (eta * voltageEstimation > 0) {
            if (stateVariableEstimation >= xP) {
                f = Math.exp(-alphaP * (stateVariableEstimation - xP)) * windowP(stateVariableEstimation);
            } else {
                f = 1;
            }
        } else if (stateVariableEstimation <= (1 - xN)) {
            f = Math.exp(alphaN * (stateVariableEstimation + xN - 1)) * windowN(stateVariableEstimation);
        } else {
            f = 1;
        }
        return g * f;
    }

    private double windowP(double xValue) {
        if (xValue >= xP) {
            return ((xP - xValue) / (1 - xP) + 1);
        } else {
            return 1;
        }
    }

    private double windowN(double xValue) {
        if (xValue <= 1 - xN) {
            return xValue / (1 - xN);
        } else {
            return 1;
        }
    }

    @Override
    public void setInstanceGoingON(int memristanceLevels) {
        // util constants
        expVp = Math.exp(vP);
        expVn = Math.exp(vN);
        x.setCurrentValue(0.5 / memristanceLevels);
        memristance.setCurrentValue(rInit);
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public void setInstanceGoingOFF(int memristanceLevels) {
        // util constants
        expVp = Math.exp(vP);
        expVn = Math.exp(vN);
        x.setCurrentValue((memristanceLevels - 0.5) / memristanceLevels);
        memristance.setCurrentValue(rInit);
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        updateMagnitudesTempState();
    }

    @Override
    public boolean reachedON(int memristanceLevels) {
        return x.getCurrentValue() >= (memristanceLevels - 0.5) / memristanceLevels;
    }

    @Override
    public boolean reachedOFF(int memristanceLevels) {
        return x.getCurrentValue() <= 0.5 / memristanceLevels;
    }

    // @Override
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
