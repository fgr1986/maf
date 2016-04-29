/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.math;

import es.upm.die.vlsi.memristor.simulation_objects.Magnitude;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;

public class Simpson {

//    /**
//     * Approximates the definite integral using Simpson's Rule. The function
//     * should be continuous in the range [a,b]. The specified number of
//     * subintervals should be even, but the value is incremented if it isn't.
//     *
//     * @param df the function to be integrated
//     * @param inputVoltage
//     * @param stateVariable
//     * @param tInc
//     * @param n the number of subintervals to be used in computing the integral
//     *
//     * @return the approximate value of the definite integral
//     *
//     * @throws IllegalArgumentException if n < 0, n > 100,000, or b < a
//     */
//    public static double integrate(ComplexDerivFunction df,
//            InputVoltage inputVoltage, Magnitude stateVariable,
//            double tInc, int n) {
//        if (n < 0 || n > 100_000) {
//            String message = n + "out of range";
//            throw new IllegalArgumentException(message);
//        }
//
//        if (tInc<=0) {
//            return 0;
//        }
//
//        double deltaX, sum2 = 0.0, sum4;
//        // make sure that n is even
//        if (n % 2 != 0) {
//            ++n;
//        }
//
//        deltaX = tInc / n;
//        sum4 = df.stateVariableDotFunction(deltaX, inputVoltage, stateVariable);
//
//        for (int i = 1; i <= (n - 2) / 2; ++i) {
//            sum2 = sum2 + df.stateVariableDotFunction(2*deltaX,  inputVoltage, stateVariable);
//            sum4 = sum4 + df.stateVariableDotFunction(3*deltaX,  inputVoltage, stateVariable);
//        }
//        return (deltaX / 3.0) * (df.stateVariableDotFunction(0,  inputVoltage, stateVariable)
//                + df.stateVariableDotFunction(tInc,  inputVoltage, stateVariable) + 4.0 * sum4 + 2.0 * sum2);
//    }

    /**
     * Approximates the definite integral using Simpson's Rule. The function
     * should be continuous in the range [a,b]. The specified number of
     * subintervals should be even, but the value is incremented if it isn't.
     *
     * @param df the function to be integrated
     * @param a the lower bound of integration
     * @param b the upper bound of integration
     * @param n the number of subintervals to be used in computing the integral
     *
     * @return the approximate value of the definite integral
     *
     * @throws IllegalArgumentException if n < 0, n > 100,000, or b < a
     */
    public static double integrate(SimpleDerivFunction df, double a, double b, int n) {
        if (n < 0 || n > 100_000) {
            String message = n + "out of range";
            throw new IllegalArgumentException(message);
        } else if (b < a) {
            String message = "a must be less than b; a =" + a + ", b = " + b;
            throw new IllegalArgumentException(message);
        }

        if (a == b) {
            return 0;
        }

        double x, deltaX, sum2 = 0.0, sum4;
        // make sure that n is even
        if (n % 2 != 0) {
            ++n;
        }

        deltaX = (b - a) / n;
        x = a + deltaX;
        sum4 = df.simpleFunction(x);

        for (int i = 1; i <= (n - 2) / 2; ++i) {
            x = x + deltaX;
            sum2 = sum2 + df.simpleFunction(x);
            x = x + deltaX;
            sum4 = sum4 + df.simpleFunction(x);
        }

        return (deltaX / 3.0) * (df.simpleFunction(a) + df.simpleFunction(b) + 4.0 * sum4 + 2.0 * sum2);
    }
}
