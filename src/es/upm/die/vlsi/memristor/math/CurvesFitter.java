/*
 * To change this license hearight, choose License Hearights in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.math;

/**
 *
 * @author fgarcia
 */
public class CurvesFitter {

    private static final double MIN_Y = 1e-20;

    private int computedPoints;
    // indexes
    private int simXIndex;
    private int backSimXIndex;
    private int scatterXIndex;

    private double currentX;
    private double error;
    private boolean scatterXGrowing;
    private boolean simXGrowing;

    private final boolean logY;

    private final XYSegment scatterSegment;
    private final XYSegment simSegment;
    private final int index;

    private final Double[][] scatterData;
    private final Double[][] simData;

    public CurvesFitter(int index, XYSegment scatterSegment, XYSegment simSegment, boolean logY) {
        this.index = index;
        computedPoints = 0;
        // indexes
        simXIndex = 0;
        backSimXIndex = 0;
        scatterXIndex = 0;

        currentX = 0;
        error = 0;
        scatterXGrowing = true;
        simXGrowing = true;

        this.scatterSegment = scatterSegment;
        this.simSegment = simSegment;
        this.scatterData = scatterSegment.getData();
        this.simData = simSegment.getData();
        this.logY = logY;
    }

    public FittingCurvesResult findSimulationMeasureError() {
//        Arrays.sort(scatterData, new Comparator<Double[]>() {
//            @Override
//            public int compare(Double[] p_o1, Double[] p_o2) {
//                return p_o1[0].compareTo(p_o2[0]);
//            }
//        });
        // sort scatterData by X axis
//        Arrays.sort(scatterData, (Double[] p_o1, Double[] p_o2) -> p_o1[0].compareTo(p_o2[0]));
        return getFittingSegmentError();
    }

    private FittingCurvesResult getFittingSegmentError() {
        try {
            currentX = scatterData[0][0];
            error = 0;
            scatterXGrowing = scatterSegment.isxAxisGrowing();
            simXGrowing = simSegment.isxAxisGrowing();
            if (scatterXGrowing != simXGrowing) {
                throw new Exception("scatterData and simulationData do not have the same monotony");
            }
            double minX = Math.max(scatterSegment.getMinX(), simSegment.getMinX());
            double maxX = Math.min(scatterSegment.getMaxX(), simSegment.getMaxX());
            outerloop:
            while (true) {
                // loop control
                if (scatterXIndex == scatterData.length || simXIndex == simData.length) {
                    break;
                }
                if (scatterXGrowing && currentX > maxX
                        || !scatterXGrowing && currentX < minX) {
                    break;
                }
                // find indexes
                if (scatterXGrowing) {
                    while (simXIndex < simData.length && simData[simXIndex][0] < currentX) {
                        updateSimulationXIndex();
                    }
                } else {
                    while (simXIndex < simData.length && simData[simXIndex][0] > currentX) {
                        updateSimulationXIndex();
                    }
                }
                // loop control 2
                if (scatterXIndex == scatterData.length || simXIndex == simData.length) {
                    break;
                }
                if (scatterXGrowing && currentX > maxX
                        || !scatterXGrowing && currentX < minX) {
                    break;
                }
                // Compute error
                // correct conditions to compute error
                if (simData[simXIndex][0] == currentX) {
                    error += getError(simData[simXIndex][1], scatterData[scatterXIndex][1], logY);
                } else {
                    // interpolate
                    if (simXIndex == 0) {
                        // update scatter
                        updateScatterXIndex();
                        continue outerloop;
                    }
                    // interpolate
                    // find backSimXIndex
                    backSimXIndex = simXIndex;
                    if (simXGrowing) {
                        while (simData[backSimXIndex][0] > currentX) {
                            backSimXIndex--;
                            if (backSimXIndex < 0) {
                                throw new Exception("error claramente, backSimXIndex < 0" + simXIndex
                                        + " currentX:" + currentX + " SimX growing and simDataX:" + simData[backSimXIndex][0]);
                            }
                        }
                    } else {
                        while (simData[backSimXIndex][0] < currentX) {
                            backSimXIndex--;
                            if (backSimXIndex < 0) {
                                throw new Exception("error claramente, backSimXIndex < 0" + simXIndex
                                        + " currentX:" + currentX + " SimX decreasing and simDataX:" + simData[backSimXIndex][0]);
                            }
                        }
                    }
                    // interpolate
                    double simY = interpolateY(simData[backSimXIndex][0], simData[simXIndex][0],
                            simData[backSimXIndex][1], simData[simXIndex][1], currentX);
                    // get error
                    error += getError(simY, scatterData[scatterXIndex][1], logY);
                }
                // update variables
                computedPoints++;
                // update scatter
                updateScatterXIndex();
            }
            // final error
            double finalError = error / computedPoints;
            return new FittingCurvesResult(true, index, finalError, minX, maxX, "Correcty Computed");
        } catch (Exception excp) {
            System.out.println("Exception: " + excp.getMessage());
            return new FittingCurvesResult(false, index, Double.POSITIVE_INFINITY, 0, 0, "Exception: " + excp.getMessage());
        }
    }

    private void updateScatterXIndex() throws Exception {
        scatterXIndex++;
        if (scatterXIndex < scatterSegment.getSize()) {
            // update currentX
            currentX = scatterData[scatterXIndex][0];
            // check growing condition
            if (scatterXGrowing && scatterData[scatterXIndex][0] < scatterData[scatterXIndex - 1][0]) {
                throw new Exception("scatter X axis monotony has changed!");
            } else if (!scatterXGrowing && scatterData[scatterXIndex][0] > scatterData[scatterXIndex - 1][0]) {
                throw new Exception("scatter X axis monotony has changed!");
            }
        }
    }

    private void updateSimulationXIndex() throws Exception {
        simXIndex++;
        if (simXIndex < simSegment.getSize()) {
            // check monotony condition
            if (simXGrowing && simData[simXIndex][0] < simData[simXIndex - 1][0]) {
                throw new Exception("sim X axis monotony has changed!");
            } else if (!simXGrowing && simData[simXIndex][0] > simData[simXIndex - 1][0]) {
                throw new Exception("scatter X axis monotony has changed!");
            }
        }
    }

    private static double interpolateY(double x1, double x2, double y1, double y2, double a) throws Exception {
        if (x2 == x1) {
            throw new Exception("Interpolation exception thrown.");
        }
        return (a - x1) * (y2 - y1) / (x2 - x1) + y1;
    }

    private static double getError(double ySim, double yMeasure, boolean logY) throws Exception {
            // quadratic error
        if( logY ){
          if( ySim==0 ){
            return Math.pow( Math.log10(Math.abs(yMeasure)), 2);
          }
          if( yMeasure==0 ){
            Math.pow( Math.log10(Math.abs(ySim)), 2);
          }
          return Math.pow( Math.log10(Math.abs(ySim))- Math.log10(Math.abs(yMeasure)), 2);
        }
        return Math.pow(ySim-yMeasure, 2);
//        // quadratic error
//        if (yMeasure != 0) {
//            return Math.pow((ySim - yMeasure) / yMeasure, 2);
//        }
//        return Math.pow(ySim / MIN_Y, 2);
    }
}
