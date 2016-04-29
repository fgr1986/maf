package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import es.upm.die.vlsi.memristor.math.RungeKutta;
import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/**
 * Memristor simulator. This program is included in the PFC 'Dinamica DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando Garcia Redondo, ETSIT UPM
 * studengetParameter(TINDEX).
 *
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor connected to a sinusoidal input or use the model with an
 * iterative external simulator.
 *
 * @author Fernando Garcia Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorASiC extends MemristorModel {

    // constants
    private final double pi = 3.1415926;
    private final double Kb = 1.38e-23;		// unit: [J/K]
    private final double q = 1.6e-19; 		//[C]
    private final double eps_0 = 8.85e-12;	// [F/m]

    // parameters
    // CF Geometry params
    private double Ea;		// [eV]
    private double alpha;	// adimensional voltage lowering factor
    private double beta;	// adimensional voltage lowering factor
    private double A1;			// [m/s] growing speed
    private double A2;
    private double B1;			// [m/s] growing speed
    private double B2;
    // rThreshold
    private double rThreshold;
    // other sizes
    private double area;		// m^2
    private double d;	// unit: m       d is defined as the initial fixed length of the RRAM switching layer
    // init and boundaries
    private double g0;		// unit: m x0 is defined as the initial length of gap region during both SET/RESET (for SET: x0=G0)
    private double r0;		// unit: m initial CF width
    private double gMax;	// unit: m fixed width of the RRAM switching layer
    private double rMax;	// unit: m fixed width of the RRAM switching layer
    private double rMin;	// unit: m effective CF extending width
    // Conductivity params
    private double Ub;		// [eV]
    private double AA;		// [A/(m^2 K^2)]
    private double eps_r;	// Relative permitivity
    private double scl;		// schottky leackage parameter
    private double eps_i;	// [F/m]
    private double rou; 	// unit: ohm*m From CuSiCTiN-80nm/D-SiCCu-TiN-12-01-03-02-80um-02.csv
//	private double R_cf0;
    // Thermal parameters
    private double Rth; // unit: K/r     effective thermal resistance
    private double T0; 	// unit: K ambient temperature


    // Default parameter values
    // CF Geometry evolution params [8]
    private final double default_Ea = 0.7;
    private final double default_alpha = 0.3;
    private final double default_beta = 0.3;
    private final double default_A1 = 0.1;
    private final double default_A2 = 0.1;
    private final double default_B1 = 0.1;
    private final double default_B2 = 0.1;
    private final double default_rThreshold = 0.8e-9;
    // init and CF geometry boundaries [5]
    private final double default_gMax = 40e-9;
    private final double default_rMax = 5e-9;
    private final double default_rMin = 1e-9;
    private final double default_g0 = default_gMax;
    private final double default_r0 = default_rMin;
    // other sizes
    private final double default_area = 80e-6 * 80e-6;
    private final double default_d = 40e-9;
    // Conductivity params [5] + 2
    private final double default_Ub = 0.9;
    private final double default_AA = 7.2e5;
    private final double default_eps_r = 7;
    private final double default_scl = 6;
    private final double default_rou = (1 / 1.83e-4) * (4 * pi * default_rMax * default_rMax) / default_gMax;
    // Thermal parameters [2]
    private final double default_Rth = 490;
    private final double default_T0 = 300;

    // indexes
    private final int totalParameters = 22;
    // CF Geometry params [7]
    private final int index_Ea = 0;
    private final int index_alpha = 1;
    private final int index_beta = 2;
    private final int index_A1 = 3;
    private final int index_A2 = 4;
    private final int index_B1 = 5;
    private final int index_B2 = 6;
    private final int index_rThreshold = 7;
    // init and boundaries [5]
    private final int index_gMax = 8;
    private final int index_rMax = 9;
    private final int index_rMin = 10;
    private final int index_g0 = 11;
    private final int index_r0 = 12;
    // other sizes
    private final int index_area = 13;
    private final int index_d = 14;
    // Conductivity params [5] + 2
    private final int index_Ub = 15;
    private final int index_AA = 16;
    private final int index_eps_r = 17;
    private final int index_scl = 18;
    private final int index_rou = 19;
    // Thermal parameters [2]
    private final int index_Rth = 20;
    private final int index_T0 = 21;

    // state variables
    private final int temp_index = 0;
    private final int g_index = 1;
    private final int r_index = 2;
    private final int R_cf_index = 3;
    private final int Vg_index = 4;
    private final int I_cf_index = 5;
    private final int I_ext_index = 6;
    private final int g_speed_index = 7;
    private final int r_speed_index = 8;

    private final Magnitude temp;
    private final Magnitude g;
    private final Magnitude r;
    private final Magnitude R_cf;
    private final Magnitude Vg;
    private final Magnitude I_cf;
    private final Magnitude I_ext;
    // control variable
    private final Magnitude g_speed;
    private final Magnitude r_speed;
    //
    private final double maxDeriv = 1e3;
    private final double minGap = 0;

    private double gResetThreshold;
    private boolean waitGThreshold;
    private boolean gResetRecovered;

    /**
     * Default constructor
     */
    public MemristorASiC() {
        super(MemristorLibrary.Memristor_Model.ASICMODEL);
        temp = new Magnitude("temp", true, temp_index, 1, "º");
        g = new Magnitude("g", true, g_index, 1, "m");
        r = new Magnitude("r", true, r_index, 1, "m");
        R_cf = new Magnitude("R_cf", true, R_cf_index, 1, "m");
        Vg = new Magnitude("Vg", true, Vg_index, 1, "V");
        I_cf = new Magnitude("I_cf", true, I_cf_index, 1, "A");
        I_ext = new Magnitude("I_ext", true, I_ext_index, 1, "A");
        g_speed = new Magnitude("g_speed", true, g_speed_index, 1, "m/s");
        r_speed = new Magnitude("r_speed", true, r_speed_index, 1, "m/s");
        stateVariables.add(temp);
        stateVariables.add(g);
        stateVariables.add(r);
        stateVariables.add(R_cf);
        stateVariables.add(Vg);
        stateVariables.add(I_cf);
        stateVariables.add(I_ext);
        stateVariables.add(g_speed);
        stateVariables.add(r_speed);
        // control magnitude
        controlMagnitude = g_speed;
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = totalParameters;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];

        parameterPresets[0][index_Ea] = new MemristorParameter(index_Ea, "Ea", "eV", default_Ea, true, default_Ea / 50, true);
        parameterPresets[0][index_alpha] = new MemristorParameter(index_alpha, "alpha", "a.u.", default_alpha, true, default_alpha / 50, true);
        parameterPresets[0][index_beta] = new MemristorParameter(index_beta, "beta", "a.u.", default_beta, true, default_beta / 50, true);
        parameterPresets[0][index_A1] = new MemristorParameter(index_A1, "A1", "m/s", default_A1, true, default_A1 / 50, true);
        parameterPresets[0][index_A2] = new MemristorParameter(index_A2, "A2", "m/s", default_A2, true, default_A2 / 50, true);
        parameterPresets[0][index_B1] = new MemristorParameter(index_B1, "B1", "m/s", default_B1, true, default_B1 / 50, true);
        parameterPresets[0][index_B2] = new MemristorParameter(index_B2, "B2", "m/s", default_B2, true, default_B2 / 50, true);
        parameterPresets[0][index_rThreshold] = new MemristorParameter(index_rThreshold, "r_t", "m", default_rThreshold, true, default_rThreshold / 50, true);

        parameterPresets[0][index_gMax] = new MemristorParameter(index_gMax, "gMax", "m", default_gMax, true, default_gMax / 50, true);
        parameterPresets[0][index_rMax] = new MemristorParameter(index_rMax, "rMax", "m", default_rMax, true, default_rMax / 50, true);
        parameterPresets[0][index_rMin] = new MemristorParameter(index_rMin, "rMin", "m", default_rMin, true, default_rMin / 50, true);
        parameterPresets[0][index_g0] = new MemristorParameter(index_g0, "g0", "m", default_g0, true, default_g0 / 50, true);
        parameterPresets[0][index_r0] = new MemristorParameter(index_r0, "r0", "m", default_r0, true, default_r0 / 50, true);

        parameterPresets[0][index_area] = new MemristorParameter(index_area, "area", "m^2", default_area, true, default_area / 50, true);
        parameterPresets[0][index_d] = new MemristorParameter(index_d, "d", "m", default_d, true, default_d / 50, true);

        parameterPresets[0][index_Ub] = new MemristorParameter(index_Ub, "Ub", "eV", default_Ub, true, default_Ub / 50, true);
        parameterPresets[0][index_AA] = new MemristorParameter(index_AA, "AA", "A/(m^2 K^2)", default_AA, true, default_AA / 50, true);
        parameterPresets[0][index_eps_r] = new MemristorParameter(index_eps_r, "eps_r", "a.u.", default_eps_r, true, default_eps_r / 50, true);
        parameterPresets[0][index_scl] = new MemristorParameter(index_scl, "scl", "1/V", default_scl, true, default_scl / 50, true);
        parameterPresets[0][index_rou] = new MemristorParameter(index_rou, "rou", "ohms", default_rou, true, default_rou / 50, true);
        parameterPresets[0][index_Rth] = new MemristorParameter(index_Rth, "Rth", "K/W", default_Rth, true, default_Rth / 50, true);
        parameterPresets[0][index_T0] = new MemristorParameter(index_T0, "T0", "K", default_T0, true, default_T0 / 50, true);

        // set preset
        setParameterPreset(0);
        // Set up magnitude list
        updateMagnitudes();
    }

    public MemristorASiC(MemristorASiC y) {
        super(MemristorLibrary.Memristor_Model.ASICMODEL);
        temp = new Magnitude("temp", true, temp_index, 1, "º");
        g = new Magnitude("g", true, g_index, 1, "m");
        r = new Magnitude("r", true, r_index, 1, "m");
        R_cf = new Magnitude("R_cf", true, R_cf_index, 1, "m");
        Vg = new Magnitude("Vg", true, Vg_index, 1, "V");
        I_cf = new Magnitude("I_cf", true, I_cf_index, 1, "A");
        I_ext = new Magnitude("I_ext", true, I_ext_index, 1, "A");
        g_speed = new Magnitude("g_speed", true, g_speed_index, 1, "");
        r_speed = new Magnitude("r_speed", true, r_speed_index, 1, "");
        stateVariables.add(temp);
        stateVariables.add(g);
        stateVariables.add(r);
        stateVariables.add(R_cf);
        stateVariables.add(Vg);
        stateVariables.add(I_cf);
        stateVariables.add(I_ext);
        stateVariables.add(g_speed);
        stateVariables.add(r_speed);
        // control magnitude
        controlMagnitude = g_speed;
        // complainCurrent
        negativeComplianceCurrent = y.getNegativeComplianceCurrent();
        positiveComplianceCurrent = y.getPositiveComplianceCurrent();
        limitCurrent = y.isLimitCurrent();
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = totalParameters;
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
        return new MemristorASiC(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorASiC();
    }

    @Override
    public void setInstance() {
        Ea = parameterPresets[0][index_Ea].getValue();
        alpha = parameterPresets[0][index_alpha].getValue();
        beta = parameterPresets[0][index_beta].getValue();
        A1 = parameterPresets[0][index_A1].getValue();
        A2 = parameterPresets[0][index_A2].getValue();
        B1 = parameterPresets[0][index_B1].getValue();
        B2 = parameterPresets[0][index_B2].getValue();
        rThreshold = parameterPresets[0][index_rThreshold].getValue();

        gMax = parameterPresets[0][index_gMax].getValue();
        rMax = parameterPresets[0][index_rMax].getValue();
        rMin = parameterPresets[0][index_rMin].getValue();
        g0 = parameterPresets[0][index_g0].getValue();
        r0 = parameterPresets[0][index_r0].getValue();

        area = parameterPresets[0][index_area].getValue();
        d = parameterPresets[0][index_d].getValue();

        Ub = parameterPresets[0][index_Ub].getValue();
        AA = parameterPresets[0][index_AA].getValue();
        eps_r = parameterPresets[0][index_eps_r].getValue();
        scl = parameterPresets[0][index_scl].getValue();
        rou = parameterPresets[0][index_rou].getValue();
        Rth = parameterPresets[0][index_Rth].getValue();
        T0 = parameterPresets[0][index_T0].getValue();

        // parameters
        eps_i = eps_0 * eps_r;

        gResetThreshold = gMax/30;
        waitGThreshold = false;
        gResetRecovered = false;

        // init the memristor magnitudes and state variable
        // state variables
        g.setCurrentValue(g0);
        r.setCurrentValue(r0);
        temp.setCurrentValue(T0);
        // gboundaries
        double[] gBoundaries = {0, gMax};
        g.setHasBoundaries(true);
        g.setHasHardBoundaries(false);
        g.setBoundaries(gBoundaries);
        // gboundaries
        double[] rBoundaries = {rMin, rMax};
        r.setHasBoundaries(true);
        r.setHasHardBoundaries(false);
        r.setBoundaries(rBoundaries);
        g.checkBoundaries();
        r.checkBoundaries();
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
            voltage.setCurrentValue(inputVoltage.getCurrentVoltage());

            // temperature
            temp.setCurrentValue(T0 + Math.abs(voltage.getCurrentValue() * current.getCurrentValue()) * Rth);
            // Kinnetics
            // compute stateVariable integration
            g_speed.setCurrentValue(derivG(g.getCurrentValue()));
            r_speed.setCurrentValue(derivR(r.getCurrentValue()));

            r.setCurrentValue(RungeKutta.integrate(this::derivR, r, timing.gettStep()));
            r.checkBoundaries();
            g.setCurrentValue(RungeKutta.integrate(this::derivG, g, timing.gettStep()));
            g.checkBoundaries();
            if( waitGThreshold && r.getCurrentValue()<= rThreshold ){
                gResetRecovered = true;
                g.setCurrentValue( gResetThreshold );
            }

            Vg.setCurrentValue(Math.signum(voltage.getCurrentValue())
                    * Math.max(Math.abs(voltage.getCurrentValue()),
                            Math.abs(voltage.getCurrentValue()) - Math.abs(current.getCurrentValue() - I_ext.getCurrentValue()) * R_cf.getCurrentValue()));

            R_cf.setCurrentValue(rou * (gMax - g.getCurrentValue()) / (Math.PI * r.getCurrentValue() * r.getCurrentValue() / 4));
            if (g.getCurrentValue() > minGap) {
                I_cf.setCurrentValue(Math.signum(voltage.getCurrentValue())
                        * (pi * r.getCurrentValue() * r.getCurrentValue() / 4)
                        * AA * temp.getCurrentValue() * temp.getCurrentValue()
                        * Math.exp(-q * Ub / (Kb * temp.getCurrentValue()))
                        * Math.exp(
                                Math.sqrt(Math.abs(Vg.getCurrentValue())) * (scl + q / (Kb * temp.getCurrentValue()) * (Math.sqrt(q / (g.getCurrentValue() * 4 * pi * eps_i))))));
            } else {
                I_cf.setCurrentValue(voltage.getCurrentValue() / R_cf.getCurrentValue());
            }
            I_ext.setCurrentValue(Math.signum(voltage.getCurrentValue())
                    * (area - pi * r.getCurrentValue() * r.getCurrentValue() / 4)
                    * AA * temp.getCurrentValue() * temp.getCurrentValue()
                    * Math.exp(-q * Ub / (Kb * temp.getCurrentValue()))
                    * Math.exp( Math.sqrt(Math.abs(voltage.getCurrentValue())) * (scl + q / (Kb * temp.getCurrentValue()) * (Math.sqrt(q / (d * 4 * pi * eps_i))))));

            checkLimitingInternalCurrent( I_cf );
            checkLimitingInternalCurrent( I_ext );

            current.setCurrentValue(I_cf.getCurrentValue() + I_ext.getCurrentValue());
            // check complain current
            checkLimitingCurrent();
            // other magnitudes
            if (current.getCurrentValue() != 0) {
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

    private double derivG(double stateVariableEstimation) {
        if ((voltage.getCurrentValue() < 0 && g.getCurrentValue() >= gMax)
                || (voltage.getCurrentValue() > 0 && g.getCurrentValue() <= 0)) {
            return 0;
        }
        if (voltage.getCurrentValue() > 0) {
            return checkDeriv(-A1 * Math.exp(
                    -q * (Ea - Math.abs(voltage.getCurrentValue()) * alpha) / (Kb * temp.getCurrentValue())));
        }
        // Only for pristine state?
        if (r.getCurrentValue() >= rThreshold) {
            return 0;
        }
        return checkDeriv(B1 * Math.exp(
                -q * (Ea - Math.abs(voltage.getCurrentValue()) * beta) / (Kb * temp.getCurrentValue())));
    }

    private double derivR(double stateVariableEstimation) {
        waitGThreshold = voltage.getCurrentValue() < 0 && !gResetRecovered && r.getCurrentValue()<=rThreshold;
        if ((voltage.getCurrentValue() > 0 && r.getCurrentValue() >= rMax && g.getCurrentValue() > 0)
                || (voltage.getCurrentValue() < 0 && r.getCurrentValue() <= rMin && g.getCurrentValue() < gMax)) {
            return 0;
        }
        if ( voltage.getCurrentValue() > 0 ) {
            return checkDeriv(A2 * Math.exp(
                    -q * (Ea - Math.abs(voltage.getCurrentValue()) * alpha) / (Kb * temp.getCurrentValue())));
        }
        return checkDeriv(-B2 * Math.exp(
                -q * (Ea - Math.abs(voltage.getCurrentValue()) * beta) / (Kb * temp.getCurrentValue())));
    }

    private double checkDeriv(double derivValue) {
        if (derivValue > 0) {
            return Math.min(derivValue, maxDeriv);
        } else if (derivValue < 0) {
            return Math.max(derivValue, -maxDeriv);
        }
        return 0;
    }
}
