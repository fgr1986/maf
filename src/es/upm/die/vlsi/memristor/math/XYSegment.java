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
public class XYSegment {
    private final double minX;
    private final double maxX;
    private final boolean xAxisGrowing;
    
    private final Double[][] data;

    public XYSegment(boolean xAxisGrowing, double minX, double maxX, Double[][] data) {
        this.xAxisGrowing= xAxisGrowing;
        this.minX = minX;
        this.maxX = maxX;
        this.data = data;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public Double[][] getData() {
        return data;
    }
    
    public double getSize() {
        return data.length;
    }

    public boolean isxAxisGrowing() {
        return xAxisGrowing;
    }
    
}
