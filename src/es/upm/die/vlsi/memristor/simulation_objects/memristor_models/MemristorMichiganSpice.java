package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import es.upm.die.vlsi.memristor.math.RungeKutta;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * studengetParameter(TINDEX).
 *
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor connected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorMichiganSpice extends MemristorModel {

    private final double defaultKappa = 1903.84;
    // private double defaultRinit;
    private final double defaultBeta = 10.56;
    private final double defaultArea = 1e-18;
    private final double defaultH = 5e-9;
    private final double defaultPhi0 = 1;
    private final double defaultLinit = 0;
    private final double defaultUa = 0.87;
    private final double defaultT = 300;

    private double defaultKappaSigma;
    // private double defaultRinitSigma;
    private double defaultBetaSigma;
    private double defaultAreaSigma;
    private double defaultHSigma;
    private double defaultPhi0Sigma;
    private double defaultLinitSigma;
    private double defaultUaSigma;
    private double defaultTSigma;

    private double alpha;
    private double F0;
    private final int KAPPAINDEX = 0; // rate dependence on applied voltage
    private final int BETAINDEX = 1; // rate dependence on applied voltage
    private final int AREAINDEX = 2; // device area
    private final int HINDEX = 3; // Overall thickness of the dielectric
    private final int PHI0INDEX = 4; // Electron barrier height
    private final int LINITINDEX = 5;
    private final int UAINDEX = 6; // Activation potential
    private final int TINDEX = 7; // Temperature in Kelvins
    // private final int RINITINDEX = 1; // rate dependence on applied voltage

    private double kT;
    private double exp_UaeKT;
    private final int LINDEX = 0;

    // state variables
    private final Magnitude l;

    // aux variables
    private double kappa;
    private double rinit;
    private double beta;
    private double area;
    private double h;
    private double phi0;
    private double linit;
    private double ua;
    private double T;

    /**
     * Default constructor
     */
    public MemristorMichiganSpice() {
        super(MemristorLibrary.Memristor_Model.MICHIGANSPICEMODEL);
        l = new Magnitude("l", true, LINDEX, 1e6, "um");
        stateVariables.add(l);
        // control magnitude
        controlMagnitude = l;
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = 8;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][KAPPAINDEX] = new MemristorParameter(KAPPAINDEX, "kappa", "a.u.", defaultKappa, true, defaultKappaSigma, true);
        parameterPresets[0][BETAINDEX] = new MemristorParameter(BETAINDEX, "beta", "a.u.", defaultBeta, true, defaultBetaSigma, true);
        parameterPresets[0][AREAINDEX] = new MemristorParameter(AREAINDEX, "area", "m^2", defaultArea, true, defaultAreaSigma, true);
        parameterPresets[0][HINDEX] = new MemristorParameter(HINDEX, "h", "m", defaultH, true, defaultHSigma, true);
        parameterPresets[0][PHI0INDEX] = new MemristorParameter(PHI0INDEX, "Phi0", "eV", defaultPhi0, true, defaultPhi0Sigma, true);
        parameterPresets[0][LINITINDEX] = new MemristorParameter(LINITINDEX, "lInit", "nm", defaultLinit, true, defaultLinitSigma, true);
        parameterPresets[0][UAINDEX] = new MemristorParameter(UAINDEX, "Ua", "eV", defaultUa, true, defaultUaSigma, true);
        parameterPresets[0][TINDEX] = new MemristorParameter(TINDEX, "T", "K", defaultT, true, defaultTSigma, true);
        // parameterPresets[0][RINITINDEX] = new MemristorParameter(RINITINDEX, "Rinit", "", defaultRinit, true, defaultRinitSigma, true);

        // set preset
        setParameterPreset(0);
        // Set up magnitude list
        updateMagnitudes();
        System.out.println("Selecting the model of memristor: Michigan.");
    }

    public MemristorMichiganSpice(MemristorMichiganSpice y) {
        super(MemristorLibrary.Memristor_Model.MICHIGANSPICEMODEL);
        l = new Magnitude("l", true, LINDEX, 1e6, "um");
        stateVariables.add(l);
        // control magnitude
        controlMagnitude = l;
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
        return new MemristorMichiganSpice(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorMichiganSpice();
    }

    @Override
    public void setInstance() {
        kappa = parameters[KAPPAINDEX].getValue();
        // rinit = parameters[RINITINDEX].getValue();
        beta = parameters[BETAINDEX].getValue();
        area = parameters[AREAINDEX].getValue();
        h = parameters[HINDEX].getValue();
        phi0 = parameters[PHI0INDEX].getValue();
        linit = parameters[LINITINDEX].getValue();
        ua = parameters[UAINDEX].getValue();
        T = parameters[TINDEX].getValue();
        // init the memristor parameters
        alpha = 2 * Math.sqrt(2 * ResourcesMAF.effMSi) / ResourcesMAF.reducedPLANK;
        kT = ResourcesMAF.k * T;
        exp_UaeKT = Math.exp(-ua * ResourcesMAF.e / kT);
        F0 = 2 * alpha * Math.sqrt(ResourcesMAF.e * Math.pow(phi0, 3)) / 3;
        // init the memristor magnitudes and state variable
        // state variables
        l.setCurrentValue(linit);
        // magnitudes
        current.setCurrentValue(0);
        voltage.setCurrentValue(0);
        // memristance.setCurrentValue(rinit);
        this.updateMagnitudesTempState();
    }

    @Override
    public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
        acceptIncrease = false;
        needDecrease = false;
        try {
            voltage.setCurrentValue( inputVoltage.getCurrentVoltage());
            // compute stateVariable integration
            l.setCurrentValue(RungeKutta.integrate(this::deriv, inputVoltage, l, timing.gettStep()));
            // Current
            if (Math.abs(voltage.getCurrentValue()) > phi0) {
                current.setCurrentValue(Math.signum(voltage.getCurrentValue()) * area * highBias());
            } else {
                current.setCurrentValue(Math.signum(voltage.getCurrentValue()) * area * lowBias());
            }
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            if (Math.abs(current.getCurrentValue()) > ResourcesMAF.constMinI && Math.abs(voltage.getCurrentValue()) > ResourcesMAF.constMinV) {
                memristance.setCurrentValue(Math.abs(voltage.getCurrentValue() / current.getCurrentValue()));
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
        double derivValue = prev_underflow(stateVariableEstimation, voltageEstimation) * ion_flow(voltageEstimation);
        return derivValue;
    }

    private double ion_flow(double voltageEstimation) {
        return beta * exp_UaeKT * Math.sinh(voltageEstimation * kappa / T);
    }

    private double prev_underflow(double stateVariableEstimation, double voltageEstimation) {
        return Math.max(0, Math.min(1, (1 - Math.pow((stateVariableEstimation / h - heaviside(-ion_flow(voltageEstimation))), 20))));
    }

    // Give a continuous transition between high and low bias
    // private double logistic() {
    // return 1 / (1 + Math.exp(-Math.pow(
    // Math.abs(voltage.getCurrentValue()) - phi0, 3)));
    // }
    private double highBias() {
        return 4 * 3.14 * ResourcesMAF.effMSi * Math.pow(ResourcesMAF.e, 2)
                / (Math.pow(ResourcesMAF.PLANK, 3) * Math.pow(alpha, 2) * phi0) * Math.pow(F(), 2) // F(0)
                * Math.exp(-F0 / F());

    }

    private double F() {
        return Math.abs(voltage.getCurrentValue()) / (h - l.getCurrentValue());
    }

    private double lowBias() {
        return 8
                * Math.PI
                * ResourcesMAF.effMSi
                * ResourcesMAF.e
                * Math.pow(kT, 2)
                / Math.pow(ResourcesMAF.PLANK, 3)
                * Math.PI
                / (c1LowBias() * kT * Math.sin(Math.PI * c1LowBias() * kT))
                * Math.exp(-alpha * (h - l.getCurrentValue())
                        * Math.sqrt(ResourcesMAF.e * phi0))
                * Math.sinh(alpha * (h - l.getCurrentValue())
                        * Math.abs(voltage.getCurrentValue()) / 4
                        * Math.sqrt(ResourcesMAF.e / phi0));
    }

    private double c1LowBias() {
        // Low bias C1
        return alpha * (h - l.getCurrentValue())
                * (2 * Math.sqrt(phi0 * ResourcesMAF.e));
    }

    /*
     * Auxiliar function
     * 
     * @returns the heaviside function of a variable
     */
    public double heaviside(double number) {
        if (number >= 0) {
            return 1;
        } else {
            return 0;
        }
    }

}
