package es.upm.die.vlsi.memristor.simulation_objects.timing;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;

public class Timing {

    private double tStep; // time increment (s)
    private double minTStep;
    private double maxTStep;
    private final double t0; // time start simulation (s)
    private final double tf; // final simulation time (s)

    // temp values
    private final int tempStepCounterValues[];
    private final double tempTimeCounterValues[];
    private final boolean hasFinal;
    // dynamic

    private final double upRatio = 2;
    private final double downRatio = 0.5;

    public Timing(double t0, double tf, double minTStep, double maxTStep) {
        
        tempStepCounterValues = new int[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
        tempTimeCounterValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
        
        this.t0 = t0;
        this.tf = tf;
        this.minTStep = minTStep;
        this.maxTStep = maxTStep;
        // this.tStep = ((tf - t0) / Resources.DEFAULTSTEPS);
        this.tStep = maxTStep;
        tempStepCounterValues[0] = 0;
        tempTimeCounterValues[0] = 0;
        hasFinal = true;
        this.minTStep = minTStep;
        this.maxTStep = maxTStep;

        if (tStep < minTStep) {
            tStep = minTStep;
        }
        if (tStep > maxTStep) {
            tStep = maxTStep;
        }
    }

    public void updateState() {
        for (int i = 1; i < tempStepCounterValues.length; i++) {
            tempStepCounterValues[tempStepCounterValues.length - i]
                    = tempStepCounterValues[tempStepCounterValues.length - i - 1];
            tempTimeCounterValues[tempStepCounterValues.length - i]
                    = tempTimeCounterValues[i - 1];
        }
        tempStepCounterValues[0]++;
        tempTimeCounterValues[0] += tStep;
    }

    /*
    * Recover the oldest value
    */
    public void recoverTempState() {
        for (int i = 0; i < tempStepCounterValues.length-1; i++) {
            tempStepCounterValues[i] = tempStepCounterValues[tempStepCounterValues.length - 1];
            tempTimeCounterValues[i] = tempTimeCounterValues[tempTimeCounterValues.length - 1];
        }
    }
      
    public void setMinTStep(){
        tStep = minTStep;
    }

    public void decreaseTStep() {
        double aux = tStep * downRatio;
        tStep = Math.max(minTStep, aux);
    }

    public void increaseTStep() {
        double aux = tStep * upRatio;
        tStep = Math.min(maxTStep, aux);
    }
    
    public boolean canIncreaseTStep() {
        if (hasFinal) {
            return (tStep < (tf - t0) / ResourcesMAF.DEFAULTSTEPS)  && (tStep < maxTStep);
        } else {
            return (tStep < maxTStep);
        }
    }
    
    public boolean canDecreaseTStep() {
        return (tStep > minTStep);
    }
    
    public Timing getCopy(){
        return  new Timing( t0,  tf, minTStep,  maxTStep);
    }
    
    
    public boolean hasNextStep() {
        return tempTimeCounterValues[0] < tf;
    }
    
    public int getStepCounter() {
        return tempStepCounterValues[0];
    }
    
    public double getTimeCounter() {
        return tempTimeCounterValues[0];
    }

    public double gettStep() {
        return tStep;
    }

    public double getT0() {
        return t0;
    }

    public double getTf() {
        return tf;
    }
    
    
}
