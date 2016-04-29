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
public class PulseVoltage extends InputVoltage {

    public static final int P_DELAY_INDEX = 0; // time pulse delay (s)
    public static final int P_SLOPE_LENGTH_INDEX = 1; // time pulse delay (s)
    public static final int P_V1_LENGTH_INDEX = 2; // time V1 (s)
    public static final int P_V2_LENGTH_INDEX = 3; // time V2 (s)
    public static final int P_V1_INDEX = 4;
    public static final int P_V2_INDEX = 5;
    public static final int P_VINIT_INDEX = 6;
    private double slopeVPerSec;
    // first slope
    private double firstSlopeVPerSec;
    private double firstSlopeLength;
    // states
    private static final int ST_IDLE = 0;
    private static final int ST_FIRST_SLOPE = 1;
    private static final int ST_V1 = 2;
    private static final int ST_V2 = 3;
    private static final int ST_V1_2_V2 = 4;
    private static final int ST_V2_2_V1 = 5;

    // auxiliar variables
    private double delay;
    private double slopeLength;
    private double v1;
    private double v2;
    private double v1Length;
    private double v2Length;
    private double vinit;

    public PulseVoltage() {
        super();
        this.name = "Pulse wave";
        this.timing = null;
        parametersNumber = 7;
        parameters = new InputVoltageParameter[parametersNumber];
        parameters[0] = new InputVoltageParameter(P_DELAY_INDEX, "delay", "s", 0, false, 0.1, true);
        parameters[1] = new InputVoltageParameter(P_SLOPE_LENGTH_INDEX, "slope length", "s", 0.02, true, 0.005, true);
        parameters[2] = new InputVoltageParameter(P_V1_LENGTH_INDEX, "v1 length", "s", 0.2, true, 0.01, true);
        parameters[3] = new InputVoltageParameter(P_V2_LENGTH_INDEX, "v2 length", "s", 0.2, true, 0.01, true);
        parameters[4] = new InputVoltageParameter(P_V1_INDEX, "v1 ", "V", 1, true, 0.01, false);
        parameters[5] = new InputVoltageParameter(P_V2_INDEX, "v2 ", "V", -1, true, 0.01, false);
        parameters[6] = new InputVoltageParameter(P_VINIT_INDEX, "v_init", "V", 0, true, 0.1, false);

        // recreate internal parameters
        recreateVoltageInternalParameters();
    }

    public PulseVoltage(InputVoltage voltagePilot, Timing timing) {
        super();
        this.name = "Pulse wave";
        parametersNumber = 7;
        parameters = new InputVoltageParameter[parametersNumber];
        this.timing = timing;
        int paramCounter = 0;
        for (InputVoltageParameter ivpor : voltagePilot.getParameters()) {
            parameters[paramCounter++] = new InputVoltageParameter(ivpor);
        }
        recreateVoltageInternalParameters();
    }

    private void recreateVoltageInternalParameters() {

        delay = getParameter(P_DELAY_INDEX).getValue();
        slopeLength = getParameter(P_SLOPE_LENGTH_INDEX).getValue();
        v1Length = getParameter(P_V1_LENGTH_INDEX).getValue();
        v2Length = getParameter(P_V2_LENGTH_INDEX).getValue();
        v1 = getParameter(P_V1_INDEX).getValue();
        v2 = getParameter(P_V2_INDEX).getValue();
        vinit = getParameter(P_VINIT_INDEX).getValue();

        slopeVPerSec = 0;
        firstSlopeVPerSec = 0;

        if (slopeLength > 0) {
            slopeVPerSec = (v1 - v2)
                    / (slopeLength);
        }
        firstSlopeLength = slopeLength;
        if (slopeLength > 0
                && vinit != v1) {
            firstSlopeLength = (v1 - vinit) / slopeVPerSec;
            firstSlopeVPerSec = (v1 - vinit)
                    / firstSlopeLength;
        }
        tempStateValues[0] = ST_IDLE;
        // temp values
        tempVoltageValues[0] = vinit;
        tempPartialTimeValues[0] = 0;
        initBackValues();
    }

    @Override
    public InputVoltage getCopy(Timing t) {
        return new PulseVoltage(this, t);
    }

    @Override
    public InputVoltage getRandomInputVoltage(Timing t) {
        PulseVoltage randomVoltage = new PulseVoltage(this, t);
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
                    estimatedState = ST_V1;
                }
            }
            break;
            case ST_V1: {
                if (estimatedPartialTime >= v1Length) {
                    estimatedState = ST_V1_2_V2;
                }
            }
            break;
            case ST_V1_2_V2: {
                if (estimatedPartialTime >= slopeLength) {
                    estimatedState = ST_V2;
                }
            }
            break;
            case ST_V2: {
                if (estimatedPartialTime >= v2Length) {
                    estimatedState = ST_V2_2_V1;
                }
            }
            break;
            case ST_V2_2_V1: {
                if (estimatedPartialTime >= slopeLength) {
                    estimatedState = ST_V1;
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
            case ST_V1: {
                estimation = v1;
            }
            break;
            case ST_V1_2_V2: {
                estimation = tempVoltageValues[0] - timeInc * slopeVPerSec;
            }
            break;
            case ST_V2: {
                estimation = v2;
            }
            break;
            case ST_V2_2_V1: {
                estimation = tempVoltageValues[0] + timeInc * slopeVPerSec;
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
        // the timing.currentTime is already updated
        tempPartialTimeValues[0] += timing.gettStep();
        // update state
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
                    tempStateValues[0] = ST_V1;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_V1: {
                if (tempPartialTimeValues[0] >= v1Length) {
                    tempStateValues[0] = ST_V1_2_V2;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_V1_2_V2: {
                if (tempPartialTimeValues[0] >= slopeLength) {
                    tempStateValues[0] = ST_V2;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_V2: {
                if (tempPartialTimeValues[0] >= v2Length) {
                    tempStateValues[0] = ST_V2_2_V1;
                    tempPartialTimeValues[0] = 0;
                }
            }
            break;
            case ST_V2_2_V1: {
                if (tempPartialTimeValues[0] >= slopeLength) {
                    tempStateValues[0] = ST_V1;
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
            case ST_V1: {
                tempVoltageValues[0] = v1;
            }
            break;
            case ST_V1_2_V2: {
                tempVoltageValues[0] = tempVoltageValues[0] - timing.gettStep() * slopeVPerSec;
            }
            break;
            case ST_V2: {
                tempVoltageValues[0] = v2;
            }
            break;
            case ST_V2_2_V1: {
                tempVoltageValues[0] = tempVoltageValues[0] + timing.gettStep() * slopeVPerSec;
            }
            break;
            default: {
                tempVoltageValues[0] = 0;
            }
            break;
        }
    }
}
