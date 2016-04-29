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
public class MemristorOxideBased extends MemristorModel {

    // parameters
    private double a; // unit: m distance between adjacent oxygen vacancy 
    private double f; // unit: HZ vibration frequency of oxygen atom in VO
    private double E_a; // unit: ev average active energy of VO
    private double E_h; // unit: ev hopping barrier of oxygen ion (O2-) 
    private double E_i; // unit: ev energy barrier between the electrode and oxide
    private double gamma; // enhancement factor of external voltage
    private double alpha_h; // unit: m enhancement factor in lower E_a & E_h
    private double alpha_a; // unit: m enhancement factor in lower E_a & E_h
    private double Z;
    private double XT; // unit: m 
    private double VT; // unit: V
    private double G0; // unit: m G0 is defined as the initial fixed length of the RRAM switching layer
    private double x0; // unit: m x0 is defined as the initial length of gap region during both SET/RESET (for SET: x0=G0)
    private double w0;	 // unit: m initial CF width
    private double R0; // unit: m fixed width of the RRAM switching layer
    private double weff; // unit: m effective CF extending width
    private double I0; // unit: A/m^2 hopping current density in the gap region
    private double rou; // unit: ohm*m resistivity of the formed conductive filament (CF)
    private double Rth; // unit: K/W effective thermal resistance
    private double Kb; // unit: ev/K
    private double T0; // unit: K ambient temperature
    private double maxDeriv; // unit: K ambient temperature

    private double R0_quad_4;
    private double exp_G0_XT;

    // default values
    private final double default_a = 0.25e-9; // unit: m distance between adjacent oxygen vacancy 
    private final double default_f = 1e13; // unit: HZ vibration frequency of oxygen atom in VO
    private final double default_Ea = 0.7; // unit: ev average active energy of VO
    private final double default_Eh = 1.12; // unit: ev hopping barrier of oxygen ion (O2-) 
    private final double default_Ei = 0.82; // unit: ev energy barrier between the electrode and oxide
    private final double default_r = 1.5; // enhancement factor of external voltage
    private final double default_alphah = 0.75e-9; // unit: m enhancement factor in lower E_a & E_h
    private final double default_alpha = 0.75e-9; // unit: m enhancement factor in lower E_a & E_h
    private final double default_Z = 1;
    private final double default_XT = 0.4e-9; // unit: m 
    private final double default_VT = 0.4; // unit: V
    private final double default_G0 = 3e-9; // unit: m G0 is defined as the initial fixed length of the RRAM switching layer
    private final double default_x0 = 3e-9; // unit: m x0 is defined as the initial length of gap region during both SET/RESET (for SET: x0=G0)
    private final double default_w0 = 0.5e-9;	 // unit: m initial CF width
    private final double default_R0 = 5e-9; // unit: m fixed width of the RRAM switching layer
    private final double default_weff = 0.5e-9; // unit: m effective CF extending width
    private final double default_I0 = 1e13; // unit: A/m^2 hopping current density in the gap region
    private final double default_rou = 1.9635e-5; // unit: ohm*m resistivity of the formed conductive filament (CF)
    private final double default_Rth = 5e5; // unit: K/W effective thermal resistance
    private final double default_Kb = 8.61733e-5; // unit: ev/K
    private final double default_T0 = 300; // unit: K ambient temperature
    private final double default_maxDeriv = 0.5e2; // max dx / dw

    // indexes
    private final int totalParameters = 22;
    private final int AINDEX = 0;
    private final int FINDEX = 1;
    private final int EAINDEX = 2;
    private final int EHINDEX = 3;
    private final int EIINDEX = 4;
    private final int LOCALRINDEX = 5;
    private final int ALPHAHINDEX = 6;
    private final int ALPHAINDEX = 7;
    private final int ZINDEX = 8;
    private final int XTINDEX = 9;
    private final int VTINDEX = 10;
    private final int G0INDEX = 11;
    private final int X0INDEX = 12;
    private final int W0INDEX = 13;
    private final int R0INDEX = 14;
    private final int WEFFINDEX = 15;
    private final int I0INDEX = 16;
    private final int ROUINDEX = 17;
    private final int RTHINDEX = 18;
    private final int KBINDEX = 19;
    private final int T0INDEX = 20;
    private final int MAXDERIVINDEX = 21;

    // state variables
    private final int TEMPINDEX = 0;
    private final int GINDEX = 1;
    private final int WINDEX = 2;
    private final int RCFINDEX = 3;
    private final int VGINDEX = 4;
    private final int I1INDEX = 5;
    private final int ICFINDEX = 6;
    private final int mDerivGINDEX = 7;
    private final Magnitude temp;
    private final Magnitude g;
    private final Magnitude w;
    private final Magnitude RCF;
    private final Magnitude Vg;
    private final Magnitude I1;
    private final Magnitude ICF;
    
    private final Magnitude mDerivG;

    /**
     * Default constructor
     */
    public MemristorOxideBased() {
        super(MemristorLibrary.Memristor_Model.OXIDEBASEDMODEL);
        temp = new Magnitude("temp", true, TEMPINDEX, 1, "Âº");
        g = new Magnitude("g", true, GINDEX, 1, "m");
        w = new Magnitude("w", true, WINDEX, 1, "m");
        RCF = new Magnitude("RCF", true, RCFINDEX, 1, "m");
        Vg = new Magnitude("Vg", true, VGINDEX, 1, "V");
        I1 = new Magnitude("I1", true, I1INDEX, 1, "A");
        ICF = new Magnitude("ICF", true, ICFINDEX, 1, "A");
        mDerivG = new Magnitude("mDerivG", true, mDerivGINDEX, 1, "");
        stateVariables.add(temp);
        stateVariables.add(g);
        stateVariables.add(w);
        stateVariables.add(RCF);
        stateVariables.add(Vg);
        stateVariables.add(I1);
        stateVariables.add(ICF);
        stateVariables.add(mDerivG);
        // control magnitude
        controlMagnitude = mDerivG;
        // Parameter Presets
        parameterPresetNames = new String[1];
        parameterPresetNames[0] = "default";
        memristorPresetsCount = 1;
        memristorParameterCount = totalParameters;
        parameterPresets = new MemristorParameter[memristorPresetsCount][memristorParameterCount];
        parameters = new MemristorParameter[memristorParameterCount];
        parameterPresets[0][AINDEX] = new MemristorParameter(AINDEX, "a", "m", default_a, true, default_a / 50, true);
        parameterPresets[0][FINDEX] = new MemristorParameter(FINDEX, "f", "Hz", default_f, true, default_f / 50, true);
        parameterPresets[0][EAINDEX] = new MemristorParameter(EAINDEX, "E_a", "eV", default_Ea, true, default_Ea / 50, true);
        parameterPresets[0][EHINDEX] = new MemristorParameter(EHINDEX, "E_h", "eV", default_Eh, true, default_Eh / 50, true);
        parameterPresets[0][EIINDEX] = new MemristorParameter(EIINDEX, "E_i", "eV", default_Ei, true, default_Ei / 50, true);
        parameterPresets[0][LOCALRINDEX] = new MemristorParameter(LOCALRINDEX, "gamma", "a.u.", default_r, true, default_r / 50, true);
        parameterPresets[0][ALPHAHINDEX] = new MemristorParameter(ALPHAHINDEX, "alphaH", "m", default_alphah, true, default_alphah / 50, true);
        parameterPresets[0][ALPHAINDEX] = new MemristorParameter(ALPHAINDEX, "alpha_a", "m", default_alpha, true, default_alpha / 50, true);
        parameterPresets[0][ZINDEX] = new MemristorParameter(ZINDEX, "z", "a.u.", default_Z, false, default_Z / 50, true);
        parameterPresets[0][XTINDEX] = new MemristorParameter(XTINDEX, "XT", "m", default_XT, true, default_XT / 50, true);
        parameterPresets[0][VTINDEX] = new MemristorParameter(VTINDEX, "VT", "V", default_VT, true, default_VT / 50, true);
        parameterPresets[0][G0INDEX] = new MemristorParameter(G0INDEX, "G0", "m", default_G0, true, default_G0 / 50, true);
        parameterPresets[0][X0INDEX] = new MemristorParameter(X0INDEX, "x0", "m", default_x0, true, default_x0 / 50, true);
        parameterPresets[0][W0INDEX] = new MemristorParameter(W0INDEX, "w0", "m", default_w0, true, default_w0 / 50, true);
        parameterPresets[0][R0INDEX] = new MemristorParameter(R0INDEX, "wCF", "m", default_R0, true, default_R0 / 50, true);
        parameterPresets[0][WEFFINDEX] = new MemristorParameter(WEFFINDEX, "wEff", "m", default_weff, true, default_weff / 50, true);
        parameterPresets[0][I0INDEX] = new MemristorParameter(I0INDEX, "I0", "A/m^2", default_I0, true, default_I0 / 50, true);
        parameterPresets[0][ROUINDEX] = new MemristorParameter(ROUINDEX, "rou", "ohm*m", default_rou, true, default_rou / 50, true);
        parameterPresets[0][RTHINDEX] = new MemristorParameter(RTHINDEX, "Rth", "K/W ", default_Rth, true, default_Rth / 50, true);
        parameterPresets[0][KBINDEX] = new MemristorParameter(KBINDEX, "Kb", "ev/K ", default_Kb, true, default_Kb / 50, true);
        parameterPresets[0][T0INDEX] = new MemristorParameter(T0INDEX, "T0", "k", default_T0, true, default_T0 / 50, true);
        parameterPresets[0][MAXDERIVINDEX] = new MemristorParameter(MAXDERIVINDEX, "max d/dt", "a.u.", default_maxDeriv, false, default_maxDeriv / 50, true);
//        parameterPresets[0][THICKNESSINDEX] = new MemristorParameter(THICKNESSINDEX, "thickness", "m", default_thickness, false, default_thickness / 50, true);

        // set preset
        setParameterPreset(0);
        // Set up magnitude list
        updateMagnitudes();
    }

    public MemristorOxideBased(MemristorOxideBased y) {
        super(MemristorLibrary.Memristor_Model.OXIDEBASEDMODEL);
        temp = new Magnitude("temp", true, TEMPINDEX, 1, "Âº");
        g = new Magnitude("g", true, GINDEX, 1, "m");
        w = new Magnitude("w", true, WINDEX, 1, "m");
        RCF = new Magnitude("RCF", true, RCFINDEX, 1, "m");
        Vg = new Magnitude("Vg", true, VGINDEX, 1, "V");
        I1 = new Magnitude("I1", true, I1INDEX, 1, "A");
        ICF = new Magnitude("ICF", true, ICFINDEX, 1, "A");
        mDerivG = new Magnitude("mDerivG", true, mDerivGINDEX, 1, "");
        stateVariables.add(temp);
        stateVariables.add(g);
        stateVariables.add(w);
        stateVariables.add(RCF);
        stateVariables.add(Vg);
        stateVariables.add(I1);
        stateVariables.add(ICF);
        stateVariables.add(mDerivG);
        // control magnitude
        controlMagnitude = mDerivG;
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
        return new MemristorOxideBased(this);
    }

    @Override
    protected MemristorModel getEmptyMemristor() {
        return new MemristorOxideBased();
    }

    @Override
    public void setInstance() {
        a = parameterPresets[0][AINDEX].getValue();
        f = parameterPresets[0][FINDEX].getValue();
        E_a = parameterPresets[0][EAINDEX].getValue();
        E_h = parameterPresets[0][EHINDEX].getValue();
        E_i = parameterPresets[0][EIINDEX].getValue();
        gamma = parameterPresets[0][RINDEX].getValue();
        alpha_h = parameterPresets[0][ALPHAHINDEX].getValue();
        alpha_a = parameterPresets[0][ALPHAINDEX].getValue();
        Z = parameterPresets[0][ZINDEX].getValue();
        XT = parameterPresets[0][XTINDEX].getValue();
        VT = parameterPresets[0][VTINDEX].getValue();
        G0 = parameterPresets[0][G0INDEX].getValue();
        x0 = parameterPresets[0][X0INDEX].getValue();
        w0 = parameterPresets[0][W0INDEX].getValue();
        R0 = parameterPresets[0][R0INDEX].getValue();
        weff = parameterPresets[0][WEFFINDEX].getValue();
        I0 = parameterPresets[0][I0INDEX].getValue();
        rou = parameterPresets[0][ROUINDEX].getValue();
        Rth = parameterPresets[0][RTHINDEX].getValue();
        Kb = parameterPresets[0][KBINDEX].getValue();
        T0 = parameterPresets[0][T0INDEX].getValue();
        maxDeriv = parameterPresets[0][MAXDERIVINDEX].getValue();
//        thickness = parameterPresets[0][THICKNESSINDEX].getValue();
        // topCfResistance
        R0_quad_4 = R0 * R0 / 4;
        exp_G0_XT = Math.exp(-G0 / XT);
        // init the memristor magnitudes and state variable
        // state variables
        g.setCurrentValue(x0);
        w.setCurrentValue(w0);
        temp.setCurrentValue(T0);
        // gboundaries
        double[] gBoundaries = {0, G0};
        g.setHasBoundaries(true);
        g.setHasHardBoundaries(false);
        g.setBoundaries(gBoundaries);
        // gboundaries
        double[] wBoundaries = {w0, R0};
        w.setHasBoundaries(true);
        w.setHasHardBoundaries(false);
        w.setBoundaries(wBoundaries);
        g.checkBoundaries();
        w.checkBoundaries();
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
            mDerivG.setCurrentValue( derivG(g.getCurrentValue()) );
            g.setCurrentValue(RungeKutta.integrate(this::derivG, g, timing.gettStep()));
            g.checkBoundaries();
            w.setCurrentValue(RungeKutta.integrate(this::derivR, w, timing.gettStep()));
            w.checkBoundaries();
            
            RCF.setCurrentValue( rou*(G0-g.getCurrentValue())/(Math.PI*w.getCurrentValue() * w.getCurrentValue()/4) );           
            I1.setCurrentValue(I0 * Math.PI * (R0_quad_4 - w.getCurrentValue() * w.getCurrentValue() / 4)
                    * exp_G0_XT * Math.sinh(voltage.getCurrentValue() / VT));
            if( g.getCurrentValue()>0 ){
                ICF.setCurrentValue(I0 * Math.PI * (w.getCurrentValue() * w.getCurrentValue() / 4)
                        * Math.exp(-g.getCurrentValue() / XT) * Math.sinh(Vg.getCurrentValue() / VT));
            }else{
                ICF.setCurrentValue( voltage.getCurrentValue()/RCF.getCurrentValue() );
            }
            
            // fgarcia debug
//            System.out.println("\n---------------\n" + "\n"
//                        + "\tVg: " + Vg.getCurrentValue());
            
//            Vg.setCurrentValue(voltage.getCurrentValue() - ICF.getCurrentValue() * RCF.getCurrentValue());
            Vg.setCurrentValue(voltage.getCurrentValue()
                    - (current.getCurrentValue() - I1.getCurrentValue()) * RCF.getCurrentValue());
            checkLimitingInternalCurrent(I1);
            checkLimitingInternalCurrent(ICF);
            // fgarcia debug
//            if(current.getCurrentValue()<0){
//                System.out.println("time: " + new java.text.DecimalFormat("#0.000000000000").format(time.getCurrentValue()) 
//                        + "\n"
//                        + "\tI1: " + I1.getCurrentValue()
//                        + "\tICF: " + ICF.getCurrentValue()
//                        + "\tRCF: " + RCF.getCurrentValue()
//                        + "\tw: " + w.getCurrentValue()
//                        + "\tg: " + g.getCurrentValue());
//            }
            
            current.setCurrentValue(I1.getCurrentValue() + ICF.getCurrentValue());
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

    private double derivG( double stateVariableEstimation) {
        
        double derivValue = 0;
//        if ( voltage.getCurrentValue()>0 && Vg.getCurrentValue()<=0
//                || voltage.getCurrentValue()<0 && Vg.getCurrentValue()>=0){
//            return 0;
//        }
        // SET
        // v inc -> R dec
        // -> G dec
        if ( voltage.getCurrentValue() > 0 && stateVariableEstimation > 0 ) { // set
            // depends on E (V/M)
            derivValue = -a * f * Math.exp(-(E_a - Vg.getCurrentValue()
                    / stateVariableEstimation * alpha_a * Z) / (Kb * temp.getCurrentValue()));
            // RESET
            // v dec -> R inc
            // -> G inc
        } else if (voltage.getCurrentValue() < 0) {
            // depends on E (V/m)
            if (stateVariableEstimation <= 0) {
                derivValue = a * f * Math.exp(-(E_i - gamma * Z * Vg.getCurrentValue()) / (Kb * temp.getCurrentValue()));
            } else if(stateVariableEstimation < G0){
                double dxr1 = a * f * Math.exp(-(E_i - gamma * Z * Vg.getCurrentValue()) / (Kb * temp.getCurrentValue()));
                double dxr2 = a * f * Math.exp(-E_h / (Kb * temp.getCurrentValue()))
                        * Math.sinh( alpha_h * Z *  Vg.getCurrentValue() / (stateVariableEstimation* Kb * temp.getCurrentValue()));
                derivValue = Math.min(Math.abs(dxr1), Math.abs(dxr2));
            }
        }
        if( derivValue>0 ){
            return Math.min( derivValue, maxDeriv );
        }else if(derivValue<0){            
            return Math.max( derivValue, -maxDeriv );
        }
        return 0;
    }

    private double derivR( double stateVariableEstimation ) {
        double derivValue = 0;
        if (voltage.getCurrentValue() > 0 && g.getCurrentValue() <= 0 ) {
            derivValue = 2 *(weff + weff*weff / stateVariableEstimation)
                    * f * Math.exp(-(E_a - Vg.getCurrentValue() / G0 * alpha_a * Z) / (Kb * temp.getCurrentValue()));
        }
        return Math.min(derivValue, maxDeriv);
    }
}
