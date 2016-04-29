/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.math;

import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;

/**
 *
 * @author fgarcia
 */
@FunctionalInterface
public interface ComplexDerivFunction {    
    public double deriv(double tInc,
            double estimatedStateVariable, InputVoltage inputVoltage );
}