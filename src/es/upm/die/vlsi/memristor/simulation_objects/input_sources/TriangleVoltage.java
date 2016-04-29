package es.upm.die.vlsi.memristor.simulation_objects.input_sources;

import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author fernando garcía fgarcia@die.upm.es
 */
public class TriangleVoltage extends InputVoltage {

    public static final int P_DELAY_INDEX = 0;
    public static final int P_SLOPE1_LENGTH_INDEX = 1;
    public static final int P_SLOPE2_LENGTH_INDEX = 2;
    public static final int P_AMP_INDEX = 3;
    public static final int P_OFFSET_INDEX = 4;
    public static final int P_VINIT_INDEX = 5;
    
    private double slope1VPerSec;
    private double slope2VPerSec;    
    // first slope
    private double firstSlopeVPerSec;
    private double firstSlopeLength;
    // states    
    private static final int ST_IDLE = 0;
    private static final int ST_FIRST_SLOPE = 1;
    private static final int ST_SLOPE1 = 2;
    private static final int ST_SLOPE2 = 3;
    
    // auxiliar variables
    private double delay;
    private double slope1Length;
    private double slope2Length;
    private double amp;
    private double offset;
    private double vinit;

    public TriangleVoltage() {
        super();
        this.name = "Triangle wave";
        parametersNumber = 6;
        parameters = new InputVoltageParameter[parametersNumber];
        parameters[0] = new InputVoltageParameter(P_DELAY_INDEX, "delay", "s", 0, false, 0.1, true);
        parameters[1] = new InputVoltageParameter(P_SLOPE1_LENGTH_INDEX, "slope up length", "s", 0.5, true, 0.01, true);
        parameters[2] = new InputVoltageParameter(P_SLOPE2_LENGTH_INDEX, "slope down length", "s", 0.5, true, 0.01, true);
        parameters[3] = new InputVoltageParameter(P_AMP_INDEX, "amp", "V", 1, true, 0.01, false);
        parameters[4] = new InputVoltageParameter(P_OFFSET_INDEX, "offset", "V", 0, true, 0.01, false);
        parameters[5] = new InputVoltageParameter(P_VINIT_INDEX, "v_init", "V", 0, true, 0.01, false);

        // recreate internal parameters
        recreateVoltageInternalParameters();
    }

    public TriangleVoltage(InputVoltage voltagePilot, Timing timing) {
        super();
        this.name = "Triangle wave";
        parametersNumber = 6;
        parameters = new InputVoltageParameter[parametersNumber];
        this.timing = timing;
        int paramCounter = 0;
        for (InputVoltageParameter ivpor : voltagePilot.getParameters()) {
            parameters[paramCounter++] = new InputVoltageParameter(ivpor);
        }
        // recreate internal parameters
        recreateVoltageInternalParameters();
    }

    private void recreateVoltageInternalParameters() {  
        
        delay = getParameter(P_DELAY_INDEX).getValue();
        slope1Length = getParameter(P_SLOPE1_LENGTH_INDEX).getValue();
        slope2Length = getParameter(P_SLOPE2_LENGTH_INDEX).getValue();
        amp = getParameter(P_AMP_INDEX).getValue();
        offset = getParameter(P_OFFSET_INDEX).getValue();
        vinit = getParameter(P_VINIT_INDEX).getValue();
      
        slope1VPerSec = 0;
        slope2VPerSec = 0;
        firstSlopeVPerSec = 0;
        firstSlopeLength = 0;
        double amp2 = 2 * amp;
        double v1 = amp + offset;
        if (slope1Length > 0) {
            firstSlopeLength = (v1-vinit)/amp2*slope1Length;
            firstSlopeVPerSec = (v1 - vinit) / firstSlopeLength;
        }
        if (slope1Length > 0) {
            slope1VPerSec = amp2 / (slope1Length);
        }
        if (slope2Length > 0) {
            slope2VPerSec = -amp2 / (slope2Length);
        }
        tempStateValues[0] = ST_IDLE;
        // temp values
        tempVoltageValues[0] = vinit;
        tempPartialTimeValues[0] = 0;
        initBackValues();
    }

    @Override
    public InputVoltage getCopy(Timing t) {
        return new TriangleVoltage(this, t);
    }
    
    @Override
    public InputVoltage getRandomInputVoltage(Timing t) {
        TriangleVoltage randomVoltage = new TriangleVoltage(this, t);
        for( InputVoltageParameter p : parameters ){
            if (p.isAllowVariations()) {
                randomVoltage.getParameter(p.getId()).setValue(p.GetRandomValue());
            } else {
                randomVoltage.getParameter(p.getId()).setValue(p.getValue());
            }
        }
        randomVoltage.recreateVoltageInternalParameters();
        return randomVoltage;
    }
    
    @Override
    public double getVoltageEstimation( double timeInc ) {
        double estimation;
        int estimatedState = tempStateValues[0];
        double estimatedPartialTime = tempPartialTimeValues[0] + timeInc;
        // estimate state
        switch (estimatedState) {
            case ST_IDLE: {
                if (timing.getTimeCounter() >= delay) {
                    estimatedState = ST_FIRST_SLOPE;
                }
            }
            break;
            case ST_FIRST_SLOPE: {
                if (estimatedPartialTime >= firstSlopeLength) {
                    estimatedState = ST_SLOPE2;
                }
            }
            break;
            case ST_SLOPE1: {
                if (estimatedPartialTime >= slope1Length) {
                    estimatedState = ST_SLOPE2;
                }
            }
            break;
            case ST_SLOPE2: {
                if (estimatedPartialTime >= slope2Length) {
                    estimatedState = ST_SLOPE1;
                }
            }
            break;
            default:
                break;
        }
        // estimate voltage
        switch (estimatedState) {
            case ST_IDLE: {
                estimation = vinit;
            }
            break;
            case ST_FIRST_SLOPE: {
                estimation = tempVoltageValues[0] + timeInc * firstSlopeVPerSec;
            }
            break;
            case ST_SLOPE1: {
                estimation = tempVoltageValues[0] + timeInc * slope1VPerSec;
            }
            break;
            case ST_SLOPE2: {
                estimation = tempVoltageValues[0] + timeInc * slope2VPerSec;
            }
            break;
            default:{
                estimation = 0;
            }
            break;
        }
        return estimation;
    }

    @Override
    protected void updateVoltage() {
        // timing currenttime is already update
        tempPartialTimeValues[0] += timing.gettStep();
        // updatestate
        switch (tempStateValues[0]) {
            case ST_IDLE: {
                if (timing.getTimeCounter() >= delay) {
                    tempStateValues[0] = ST_FIRST_SLOPE;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_FIRST_SLOPE: {
                if (tempPartialTimeValues[0] >= firstSlopeLength) {
                    tempStateValues[0] = ST_SLOPE2;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_SLOPE1: {
                if (tempPartialTimeValues[0] >= slope1Length) {
                    tempStateValues[0] = ST_SLOPE2;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_SLOPE2: {
                if (tempPartialTimeValues[0] >= slope2Length) {
                    tempStateValues[0] = ST_SLOPE1;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            default:{}
                break;
        }
        
        // update voltage
        switch (tempStateValues[0]) {
            case ST_IDLE: {
                tempVoltageValues[0] = vinit;
            }
            break;
            case ST_FIRST_SLOPE: {
                tempVoltageValues[0] = tempVoltageValues[0] + timing.gettStep() * firstSlopeVPerSec;
            }
            break;
            case ST_SLOPE1: {
                tempVoltageValues[0] = tempVoltageValues[0] + timing.gettStep() * slope1VPerSec;
            }
            break;
            case ST_SLOPE2: {
                tempVoltageValues[0] = tempVoltageValues[0] + timing.gettStep() * slope2VPerSec;
            }
            break;
            default:{
                tempVoltageValues[0] = 0;
            }
            break;
        }
    }
}
