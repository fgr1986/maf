package es.upm.die.vlsi.memristor.simulation_objects.input_sources;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author fernando garcía fgarcia@die.upm.es
 */
public class PulseRampVoltage extends InputVoltage {

    public static final int P_DELAY_INDEX = 0;
    public static final int P_SLOPE_LENGTH_INDEX = 1;
    public static final int P_V_LENGTH_INDEX = 2;
    public static final int P_PERIOD_INDEX = 3;
    public static final int P_VAMP_INC_INDEX = 4;
    public static final int P_VSTART_INDEX = 5;
    public static final int P_VINIT_INDEX = 6;
    
    private double slopeVPerSec;
    private double waitLength;
    // first slope
    private double firstSlopeVPerSec;
    private double firstSlopeLength;
    // states
    private static final int ST_IDLE = 0;
    private static final int ST_FIRST_SLOPE = 1;
    private static final int ST_START_V = 2;
    private static final int ST_SLOPE_UP = 3;
    private static final int ST_V = 4;
    private static final int ST_SLOPE_DOWN = 5;

    // auxiliar variables
    private double delay;
    private double slopeLength;
    private double vLength;
    private double period;
    private double ampInc;
    private double startVoltage;
    private double vinit;
    
    //only for this
    private double[] tempVoltageLevelValues;
    
    public PulseRampVoltage() {
        super();
        this.name = "Pulse ramp wave";
        this.timing = null;
        parametersNumber = 7;
        parameters = new InputVoltageParameter[parametersNumber];
        parameters[0] = new InputVoltageParameter(P_DELAY_INDEX, "delay", "s", 0.1, false, 0.1, true);
        parameters[1] = new InputVoltageParameter(P_SLOPE_LENGTH_INDEX, "slope length", "s", 0.02, true, 0.005, true);
        parameters[2] = new InputVoltageParameter(P_V_LENGTH_INDEX, "v length", "s", 0.2, true, 0.01, true);
        parameters[3] = new InputVoltageParameter(P_PERIOD_INDEX, "period", "s", 0.4, true, 0.01, true);
        parameters[4] = new InputVoltageParameter(P_VAMP_INC_INDEX, "amp inc ", "V", 0.02, true, 0.01, false);
        parameters[5] = new InputVoltageParameter(P_VSTART_INDEX, "v start", "V", 0, true, 0.1, false);
        parameters[6] = new InputVoltageParameter(P_VINIT_INDEX, "v_init", "V", 0, true, 0.1, false);
        
        // recreate internal parameters
        recreateVoltageInternalParameters();
    }

    public PulseRampVoltage(InputVoltage voltagePilot, Timing timing) {
        super();
        this.name = "Pulse wave";
        this.timing = timing;
        parametersNumber = 7;
        parameters = new InputVoltageParameter[parametersNumber];
        int paramCounter = 0;
        for (InputVoltageParameter ivpor : voltagePilot.getParameters()) {
            parameters[paramCounter++] = new InputVoltageParameter(ivpor);
        }
        // recreate parameters
        recreateVoltageInternalParameters();
    }

    private void recreateVoltageInternalParameters() {
        
        //fgarcia
        tempVoltageLevelValues = new double[ ResourcesMAF.MAGNITUDERECOVERYSTEPS];
        //params
        delay = getParameter(P_DELAY_INDEX).getValue();
        slopeLength = getParameter(P_SLOPE_LENGTH_INDEX).getValue();
        vLength = getParameter(P_V_LENGTH_INDEX).getValue();
        period = getParameter(P_PERIOD_INDEX).getValue();
        ampInc = getParameter(P_VAMP_INC_INDEX).getValue();
        startVoltage = getParameter(P_VSTART_INDEX).getValue();
        vinit = getParameter(P_VINIT_INDEX).getValue();
        waitLength = period - vLength;
        slopeVPerSec = 0;
        firstSlopeVPerSec = 0;
        firstSlopeLength = 0;
        tempVoltageLevelValues[0] = startVoltage;
        if (slopeLength > 0) {
            slopeVPerSec = (startVoltage - vinit) / (slopeLength);
            firstSlopeLength = slopeLength;
            firstSlopeVPerSec = (startVoltage - vinit) / firstSlopeLength;
        }
        tempStateValues[0] = ST_IDLE;
        // temp values
        tempVoltageValues[0] = vinit;
        tempPartialTimeValues[0] = 0;
        initBackValues();
    }

    // overriden
    @Override
    public InputVoltage getCopy(Timing t) {
        return new PulseRampVoltage(this, t);
    }

    // overriden
    @Override
    public InputVoltage getRandomInputVoltage(Timing t) {
        PulseRampVoltage randomVoltage = new PulseRampVoltage(this, t);
        for (InputVoltageParameter p : parameters) {
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
    public double getVoltageEstimation(double timeInc) {
        double estimation;
        int estimatedState = tempStateValues[0];
        double estimatedPartialTime = tempPartialTimeValues[0] + timeInc;
        double estimatedVoltageLevel = tempVoltageLevelValues[0];
        double estimatedSlopeVPerSec = slopeVPerSec;
        // estimate state
        switch (estimatedState) {
            case ST_IDLE: {
                if (timing.getTimeCounter() >= delay) {
                    estimatedVoltageLevel = startVoltage;
                    estimatedSlopeVPerSec = (estimatedVoltageLevel - vinit) / slopeLength;
                    estimatedState = ST_FIRST_SLOPE;
                }
            }
            break;
            case ST_FIRST_SLOPE: {
                if (estimatedPartialTime >= firstSlopeLength) {
                    estimatedState = ST_START_V;
                }
            }
            break;
            case ST_START_V: {
                if (estimatedPartialTime >= waitLength) {
                    estimatedVoltageLevel = tempVoltageLevelValues[0] + ampInc;
                    estimatedSlopeVPerSec = (estimatedVoltageLevel - startVoltage) / slopeLength;
                    estimatedState = ST_SLOPE_UP;
                }
            }
            break;
            case ST_SLOPE_UP: {
                if (estimatedPartialTime >= slopeLength) {
                    estimatedState = ST_V;
                }
            }
            break;
            case ST_V: {
                if (estimatedPartialTime >= vLength) {
                    if (waitLength <= 0) {
                        estimatedVoltageLevel = tempVoltageLevelValues[0] + ampInc;
                        estimatedSlopeVPerSec = ampInc / slopeLength;
                        estimatedState = ST_SLOPE_UP;
                    } else {
                        estimatedSlopeVPerSec = (estimatedVoltageLevel - startVoltage) / slopeLength;
                        estimatedState = ST_SLOPE_DOWN;
                    }
                }
            }
            break;
            case ST_SLOPE_DOWN: {
                if (estimatedPartialTime >= slopeLength) {
                    estimatedState = ST_START_V;
                }
            }
            break;
            default: {
                estimatedState = ST_IDLE;
            }
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
            case ST_START_V: {
                estimation = startVoltage;
            }
            break;
            case ST_SLOPE_UP: {
                estimation = tempVoltageValues[0] + timeInc * estimatedSlopeVPerSec;
            }
            break;
            case ST_V: {
                estimation = estimatedVoltageLevel;
            }
            break;
            case ST_SLOPE_DOWN: {
                estimation = tempVoltageValues[0] - timeInc * estimatedSlopeVPerSec;
            }
            break;
            default: {
                estimation = 0;
            }
            break;
        }
        return estimation;
    }

    @Override
    public void updateVoltage() {
        // timing currenttime is already update
        tempPartialTimeValues[0] += timing.gettStep();
        // update state
        switch (tempStateValues[0]) {
            case ST_IDLE: {
                if (timing.getTimeCounter() >= delay) {
                    tempVoltageLevelValues[0] = startVoltage;
                    slopeVPerSec = (tempVoltageLevelValues[0] - vinit) / slopeLength;
                    tempStateValues[0] = ST_FIRST_SLOPE;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_FIRST_SLOPE: {
                if (tempPartialTimeValues[0] >= firstSlopeLength) {
                    tempStateValues[0] = ST_START_V;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_START_V: {
                if (tempPartialTimeValues[0] >= waitLength) {
                    tempVoltageLevelValues[0] = tempVoltageLevelValues[0] + ampInc;
                    slopeVPerSec = (tempVoltageLevelValues[0] - startVoltage) / slopeLength;
                    tempStateValues[0] = ST_SLOPE_UP;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_SLOPE_UP: {
                if (tempPartialTimeValues[0] >= slopeLength) {
                    tempStateValues[0] = ST_V;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_V: {
                if (tempPartialTimeValues[0] >= vLength) {
                    if (waitLength <= 0) {
                        tempVoltageLevelValues[0] = tempVoltageLevelValues[0] + ampInc;
                        slopeVPerSec = ampInc / slopeLength;
                        tempStateValues[0] = ST_SLOPE_UP;
                        tempPartialTimeValues[0] = 0;
                    } else {
                        slopeVPerSec = (tempVoltageLevelValues[0] - startVoltage) / slopeLength;
                        tempStateValues[0] = ST_SLOPE_DOWN;
                        tempPartialTimeValues[0] = 0;
                    }
                }
            }
            break;
            case ST_SLOPE_DOWN: {
                if (tempPartialTimeValues[0] >= slopeLength) {
                    tempStateValues[0] = ST_START_V;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            default: {
            }
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
            case ST_START_V: {
                tempVoltageValues[0] = startVoltage;
            }
            break;
            case ST_SLOPE_UP: {
                tempVoltageValues[0] = tempVoltageValues[0] + timing.gettStep() * slopeVPerSec;
            }
            break;
            case ST_V: {
                tempVoltageValues[0] = tempVoltageLevelValues[0];
            }
            break;
            case ST_SLOPE_DOWN: {
                tempVoltageValues[0] = tempVoltageValues[0] - timing.gettStep() * slopeVPerSec;
            }
            break;
            default: {
                tempVoltageValues[0] = 0;
            }
            break;
        }
        
        // only for thisvoltage
        // update tempVoltageLevelValues
        for (int i = 1; i < tempVoltageLevelValues.length; i++) {
            tempVoltageLevelValues[i] = tempVoltageLevelValues[i - 1];
            tempVoltageLevelValues[i] = tempVoltageLevelValues[i - 1];
            tempVoltageLevelValues[i] = tempVoltageLevelValues[i - 1];        
        }   
    }
}
