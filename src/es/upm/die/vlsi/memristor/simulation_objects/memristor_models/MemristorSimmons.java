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
public class MemristorSimmons extends MemristorModel {

    // 1 eV = 1 V * e
    // Constants
    // private final double K = 1.38e-23; // Boltzmann's constant
    private final double constLambda = 1e9 * ResourcesMAF.e * Math.log(2)
            / (8 * Math.PI * ResourcesMAF.e0);
    private double constLambdaFinal;
    // private final double constB = 1e-9 * 4 * Math.PI
    // * Math.sqrt(2 * Resources.m * Resources.e) / Resources.PLANK;
    private final double constB = 10.246;
    private double constPhi1Final;
    private double j0;
    private double w1;
    private final double constIntegration = 1e9;
    // Current parameters
    // private Parameter T; // Temperature in Kelvins
    private final double defaultRs = 215; // Internal Resistance in ohms
    private final double defaultPhi0 = 0.95; // Electron barrier height in eV
    private final double defaultArea = 1e4; // area in nm²
    private final double defaultK = 5; // dielectric
    private final double defaultI_off = 115e-6;// collected in A
    private final double defaultI_on = 8.9e-6;// collected in A
    private final double defaultF_off = 3.5e-6;// collected in s
    private final double defaultF_on = 40e-6;// collected in s
    private final double defaultA_off = 1.2;// collected in nm
    private final double defaultA_on = 1.8;// collected in nm
    private final double defaultB = 500e-6;// collected in A
    private final double defaultW_c = 107e-3;// collected in nm
    private final double defaultW_init = 1.21;// collected in nm
    private final double defaultRsSigma = 10; // Internal Resistance in ohms
    private final double defaultPhi0Sigma = 0.1; // Electron barrier height in eV
    private final double defaultAreaSigma = 10; // area in nm²
    private final double defaultKSigma = 1; // dielectric
    private final double defaultI_offSigma = 1e-7;// collected in A
    private final double defaultI_onSigma = 1e-7;// collected in A
    private final double defaultF_offSigma = 1e-7;// collected in s
    private final double defaultF_onSigma = 1e-7;// collected in s
    private final double defaultA_offSigma = 1e-2;// collected in nm
    private final double defaultA_onSigma = 1e-2;// collected in nm
    private final double defaultBSigma = 1e-6;// collected in A
    private final double defaultW_cSigma = 5e-3;// collected in nm
    private final double defaultW_initSigma = 1e-2;// collected in nm

    public final int RSINDEX = 0; // Internal Resistance in ohms
    public final int PHI0INDEX = 1; // Electron barrier height in eV
    public final int AREAINDEX = 2; // area in nm²
    public final int KINDEX = 3; // dielectric
    // tunnel barrier parametres
    public final int IOFFINDEX = 4; // collected in A
    public final int IONINDEX = 5;// collected in A
    public final int FOFFINDEX = 6;// collected in s
    public final int FONINDEX = 7;// collected in s
    public final int AOFFINDEX = 8;// collected in nm
    public final int AONINDEX = 9;// collected in nm
    public final int BINDEX = 10;// collected in A
    public final int WCINDEX = 11;// collected in nm
    public final int WINITINDEX = 12;// collected in nm
    
    // Magnitudes w, w_i in nm
    private final int XINDEX = 0;
    private final int WINDEX = 1;

    private final Magnitude x;
    private final Magnitude w;

    // auxiliar variables
    private double rs;
    private double phi0;
    private double area;
    private double k;
    private double iOff;
    private double iOn;
    private double fOff;
    private double fOn;
    private double aOff;
    private double aOn;
    private double b;
    private double wc;
    private double winit;

    /**
     * Default constructor
     */
    public MemristorSimmons() {
        super(MemristorLibrary.Memristor_Model.SIMMONSMODEL);
        x = new Magnitude("x", true, XINDEX, 1, "");
        w = new Magnitude("w", true, WINDEX, 1, "nm");
        stateVariables.add(x);
        stateVariables.add(w);
        // control magnitude
        controlMagnitude = w;

        // Parameter presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 13;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        // params
        parameterPresets[0][RSINDEX] = new MemristorParameter(RSINDEX, "Rs", ResourcesMAF.OHM1, defaultRs, true, defaultRsSigma, true);
        parameterPresets[0][PHI0INDEX] = new MemristorParameter(PHI0INDEX, "phio", "eV", defaultPhi0, true, defaultPhi0Sigma, true);
        parameterPresets[0][AREAINDEX] = new MemristorParameter(AREAINDEX, "area", "nm^2", defaultArea, true, defaultAreaSigma, true);
        parameterPresets[0][KINDEX] = new MemristorParameter(KINDEX, "k", "", defaultK, true, defaultKSigma, true);
        parameterPresets[0][IOFFINDEX] = new MemristorParameter(IOFFINDEX, "i_off", "A", defaultI_off, true, defaultI_offSigma, true);
        parameterPresets[0][IONINDEX] = new MemristorParameter(IONINDEX, "i_on", "A", defaultI_on, true, defaultI_onSigma, true);
        parameterPresets[0][FOFFINDEX] = new MemristorParameter(FOFFINDEX, "f_off", "s", defaultF_off, true, defaultF_offSigma, true);
        parameterPresets[0][FONINDEX] = new MemristorParameter(FONINDEX, "f_on", "s", defaultF_on, true, defaultF_onSigma, true);
        parameterPresets[0][AOFFINDEX] = new MemristorParameter(AOFFINDEX, "a_off", "nm", defaultA_off, true, defaultA_offSigma, true);
        parameterPresets[0][AONINDEX] = new MemristorParameter(AONINDEX, "a_on", "nm", defaultA_on, true, defaultA_onSigma, true);
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

    /**
     * Default constructor
     *
     * @param y
     */
    public MemristorSimmons(MemristorSimmons y) {
        super(MemristorLibrary.Memristor_Model.SIMMONSMODEL);
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

        // Parameter presets
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
        return new MemristorSimmons(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorSimmons();
    }

    @Override
    public void setInstance() {
        rs = getParameter(RSINDEX).getValue();
        phi0 = getParameter(PHI0INDEX).getValue();
        area = getParameter(AREAINDEX).getValue();
        k = getParameter(KINDEX).getValue();
        iOff = getParameter(IOFFINDEX).getValue();
        iOn= getParameter(IONINDEX).getValue();
        fOff= getParameter(FONINDEX).getValue();
        fOn= getParameter(FOFFINDEX).getValue();
        aOff= getParameter(AOFFINDEX).getValue();
        aOn= getParameter(AONINDEX).getValue();
        b = getParameter(BINDEX).getValue();
        wc= getParameter(WCINDEX).getValue();
        winit= getParameter(WINITINDEX).getValue();

        constLambdaFinal = constLambda / k;
        constPhi1Final = 1.15 * constLambdaFinal;
        w1 = constLambdaFinal * 1.2 / phi0;
        j0 = ResourcesMAF.e * ResourcesMAF.e / (2 * Math.PI * ResourcesMAF.PLANK) * area;

        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        w.setCurrentValue(winit);
        x.setCurrentValue((w.getCurrentValue() - aOff) / (aOn - aOff));
        memristance.setCurrentValue(rs);

        this.updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        double sig;
        double w2;
        double R;
        // wDot in meters (should be scaled with constIntegration)
        double dw;
        double Phi1;
        double lambda;
        double vGAP;
        double B;
        acceptIncrease = false;
        needDecrease = false;
        try {
            sig = Math.signum(inputVoltage.getCurrentVoltage());
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());
            // compute stateVariable integration
            w.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, w, timing.gettStep()));
            checkWLimits();
            x.setCurrentValue((w.getCurrentValue() - aOff) / (aOn - aOff));
            lambda = (constLambdaFinal / w.getCurrentValue());
            vGAP = (voltage.getCurrentValue() - current.getCurrentValue() * rs);
            w2 = (w1 + w.getCurrentValue() * (1 - 9.2 * lambda / (3 * phi0 + 4 * lambda - 2 * Math.abs(vGAP))));

            if (w2 <= w1 + ResourcesMAF.constMinW) {
                w2 = (w1 + 2 * ResourcesMAF.constMinW);
                needDecrease = true;
            }
            dw = (w2 - w1);
            B = (constB * dw);
            R = ((w2 / w1) * (w.getCurrentValue() - w1) / (w.getCurrentValue() - w2));
            if (R <= 0) {
                needDecrease = true;
            }
            Phi1 = (phi0 - Math.abs(vGAP)
                    * (w1 + w2)
                    / (2 * w.getCurrentValue()) - (constPhi1Final / dw)
                    * Math.log(R));
            if (Phi1 < 0) {
                needDecrease = true;
                Phi1 = 0;
            }
            current.setCurrentValue(
                    sig * j0 / Math.pow(dw, 2) * (Phi1 * Math.exp(-B * Math.sqrt(Phi1)) - (Phi1 + Math.abs(vGAP))
                    * Math.exp(-B * Math.sqrt(Phi1 + Math.abs(vGAP)))));
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            if (Math.abs(current.getCurrentValue()) > ResourcesMAF.constMinI
                    && Math.abs(voltage.getCurrentValue()) > ResourcesMAF.constMinV) {
                memristance.setCurrentValue(
                        Math.abs(voltage.getCurrentValue() / current.getCurrentValue()));
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

    private void checkWLimits() {
        if (w.getCurrentValue() < aOff) {
            w.setCurrentValue(aOff);
            needDecrease = true;
        }
        if (w.getCurrentValue() > aOn) {
            w.setCurrentValue(aOn);
            needDecrease = true;
        }
    }

    private double deriv(double tInc,
            double stateVariableEstimation, InputVoltage inputVoltage) {
        double voltageEstimation = inputVoltage.getVoltageEstimation(tInc);
        double currentEstimation = voltageEstimation / memristance.getCurrentValue();
        if (voltageEstimation > 0 && w.getCurrentValue() < aOn) {
            return constIntegration * fOff * Math.sinh(Math.abs(currentEstimation) / iOff)
                    * Math.exp(-Math.exp((stateVariableEstimation - aOff) / wc) - stateVariableEstimation / wc);
        } else if (stateVariableEstimation > aOff) {
            return -constIntegration * fOn * Math.sinh(Math.abs(currentEstimation) / iOn)
                    * Math.exp(-Math.exp((aOn - stateVariableEstimation) / wc) - stateVariableEstimation / wc);
        } else {
            return 0;
        }
    }

    @Override
    public void setInstanceGoingON(int memristanceLevels) {
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        x.setCurrentValue(0.5 / memristanceLevels);
        w.setCurrentValue((aOn - aOff) * x.getCurrentValue() + aOff);
        memristance.setCurrentValue(rs);

        this.updateMagnitudesTempState();
    }

    @Override
    public void setInstanceGoingOFF(int memristanceLevels) {
        voltage.setCurrentValue(0);
        current.setCurrentValue(0);
        // State variables w and wDot
        x.setCurrentValue((memristanceLevels - 0.5) / memristanceLevels);
        w.setCurrentValue((aOn - aOff) * x.getCurrentValue() + aOff);
        memristance.setCurrentValue(rs);

        this.updateMagnitudesTempState();
    }

    @Override
    public boolean reachedON(int memristanceLevels) {
        return x.getCurrentValue() >= (memristanceLevels - 0.5)
                / memristanceLevels; // numLevels - 1 + 1/2
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
