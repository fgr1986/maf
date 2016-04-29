/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.math;

import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;

/**
 *
 * @author fgarcia
 */
public class RungeKutta {

    public static double integrate(ComplexDerivFunction df,
            InputVoltage inputVoltage, Magnitude stateVariable,
            double tInc) {
        if (tInc <= 0) {
            return 0;
        }

        double y = stateVariable.getCurrentValue();
        // Computing all of the trial values
        double k1 = tInc * df.deriv(0, y, inputVoltage);
        double k2 = tInc * df.deriv(tInc / 2, y + k1 / 2, inputVoltage);
        double k3 = tInc * df.deriv(tInc / 2, y + k2 / 2, inputVoltage);
        double k4 = tInc * df.deriv(tInc, y + k3, inputVoltage);
        // Incrementing y
        return y + k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
    }
    
    public static double integrate(DerivFunction df, Magnitude stateVariable, double tInc) {
        if (tInc <= 0) {
            return 0;
        }

        double y = stateVariable.getCurrentValue();
        // Computing all of the trial values
        double k1 = tInc * df.deriv( y);
        double k2 = tInc * df.deriv( y + k1 / 2);
        double k3 = tInc * df.deriv( y + k2 / 2);
        double k4 = tInc * df.deriv( y + k3);
        // Incrementing y
//        return y + k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
        return y + k1 / 6 + k2 / 3 + k3 / 3 + k4 / 6;
    }
}
