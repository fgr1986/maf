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
public class SineVoltage extends InputVoltage {

    public static final int P_FREQ_INDEX = 0;
    public static final int P_AMP_INDEX = 1;
    public static final int P_OFFSET_INDEX = 2;

    // 
    private double freq;
    private double amp;
    private double offset;

    public SineVoltage() {
        super();
        this.name = "Sine wave";
        this.timing = null;
        parametersNumber = 3;
        parameters = new InputVoltageParameter[parametersNumber];
        parameters[0] = new InputVoltageParameter(P_FREQ_INDEX, "freq", "Hz", 2, true, 0.1, true);
        parameters[1] = new InputVoltageParameter(P_AMP_INDEX, "amp", "V", 2, true, 0.3, false);
        parameters[2] = new InputVoltageParameter(P_OFFSET_INDEX, "offset", "V", 0, true, 0.1, false);
        // recreate internal parameters
        recreateVoltageInternalParameters();
    }

    public SineVoltage(InputVoltage voltagePilot, Timing timing) {
        super();
        this.name = "Sine wave";
        this.timing = timing;
        parametersNumber = 3;
        parameters = new InputVoltageParameter[parametersNumber];
        int paramCounter = 0;
        for (InputVoltageParameter ivpor : voltagePilot.getParameters()) {
            parameters[paramCounter++] = new InputVoltageParameter(ivpor);
        }
        // recreate internal parameters
        recreateVoltageInternalParameters();
    }

    private void recreateVoltageInternalParameters() {
        freq = getParameter(P_FREQ_INDEX).getValue();
        amp = getParameter(P_AMP_INDEX).getValue();
        offset = getParameter(P_OFFSET_INDEX).getValue();
        // temp values
        tempVoltageValues[0] = getParameter(P_OFFSET_INDEX).getValue();
        tempPartialTimeValues[0] = 0;
        initBackValues();
    }

    // overriden
    @Override
    public InputVoltage getCopy(Timing t) {
        return new SineVoltage(this, t);
    }

    @Override
    public InputVoltage getRandomInputVoltage(Timing t) {
        SineVoltage randomVoltage = new SineVoltage(this, t);
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
        return amp * Math.sin(2 * Math.PI * freq * (timing.getTimeCounter() + timeInc)) + offset;
    }

    @Override
    protected void updateVoltage() {
        tempVoltageValues[0] = amp * Math.sin(2 * Math.PI * freq * timing.getTimeCounter()) + offset;
    }

}
