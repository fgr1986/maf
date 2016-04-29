/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.math;


/**
 *
 * @author fgarcia
 */
@FunctionalInterface
public interface DerivFunction {    
    public double deriv( double estimatedStateVariable );
}