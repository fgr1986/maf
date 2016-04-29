package es.upm.die.vlsi.memristor.simulation_objects;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;

public class Magnitude {

    private String tag;
    private boolean saved;
    // temp values
    private final double tempValues[];
    // temp already computed and APPROVED valid values
    // Ready to be stored in disk
    private double[] values;
    private int valuesSize;
    private int id;
    private double plotScaleFactor = 1;
    private String prefixUnit = "";
    private boolean hasBoundaries;
    private boolean hasHardBoundaries;
    private double[] boundaries;

    public Magnitude(String tag, boolean saved,
            double currentValue, double backValue, double twoBackValue,
            int valuesSize, int id, double plotScaleFactor,
            String prefixUnit) {
        super();
        this.tag = tag;
        this.saved = saved;
        this.id = id;
        this.prefixUnit = prefixUnit;
        tempValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
    }

    public Magnitude(String tag, boolean saved,
            int id, double plotScaleFactor, String prefixUnit) {
        this.tag = tag;
        this.saved = saved;
        this.id = id;
        this.plotScaleFactor = plotScaleFactor;
        this.prefixUnit = prefixUnit;
        this.hasBoundaries = false;
        tempValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
    }

    public Magnitude(String tag, boolean saved,
            int id, double plotScaleFactor, String prefixUnit,
            boolean hasBoundaries, double[] boundaries) {
        this.tag = tag;
        this.saved = saved;
        this.id = id;
        this.plotScaleFactor = plotScaleFactor;
        this.prefixUnit = prefixUnit;
        this.hasBoundaries = hasBoundaries;
        this.boundaries = boundaries;
        tempValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
    }

    public Magnitude(String tag, double dsaved, boolean savedInMemory,
            int id, double plotScaleFactor, String prefixUnit) {
        this.tag = tag;
        this.saved = dsaved > 0;
        this.id = id;
        this.plotScaleFactor = plotScaleFactor;
        this.prefixUnit = prefixUnit;
        this.hasBoundaries = false;
        tempValues = new double[ResourcesMAF.MAGNITUDERECOVERYSTEPS];
    }
    
    
    public void checkBoundaries() {
        if (hasBoundaries) {
            if ( tempValues[0] > boundaries[1]) {
                if( hasHardBoundaries ){
                    setCurrentValue((boundaries[1] + getBackTempValue(1)) / 2);
                }else{
                    setCurrentValue( boundaries[1] );
                }
            } else if ( tempValues[0] < boundaries[0]) {
                if( hasHardBoundaries ){
                    setCurrentValue((boundaries[0] + getBackTempValue(1)) / 2);
                }else{
                    setCurrentValue( boundaries[0] );
                }
            }
        }
    }

    public String getRecentHistory() {
        String history = "";
        for (int i = 0; i < tempValues.length; i++) {
            history += "  [n-" + i + "]:" + tempValues[i];
        }
        return getTag() + " -->    " + history;
    }

    private void setValueInPosition(double value, int position) {
        if (saved) {
            values[position] = value;
        } else {
            values = null;
        }
    }

    public void updateTempState() {
        for (int i = 1; i < tempValues.length; i++) {
            tempValues[i] = tempValues[i - 1];
        }
    }

    /*
     * Recover the oldest value
     */
    public void recoverTempState() {
        for (int i = 0; i < tempValues.length - 1; i++) {
            tempValues[i] = tempValues[tempValues.length - 1];
        }
    }

    public void updateState(int index) {
        for (int i = 1; i < tempValues.length; i++) {
            tempValues[tempValues.length - i] = tempValues[tempValues.length - i - 1];
        }
        setValueInPosition(tempValues[0], index);
    }

    public void setCurrentValue(double currentValue) {
        tempValues[0] = currentValue;
//        System.out.println( "[debug] " + tag + " " + currentValue );
    }

    public double getCurrentValue() {
        return tempValues[0];
    }

    public double getBackTempValue(int index) {
        if (index < tempValues.length && index > 0) {
            return tempValues[index];
        } else {
            return tempValues[tempValues.length];
        }
    }

    public int getValuesSize() {
        return valuesSize;
    }

    public void setValuesSize(int valuesSize) {
        this.valuesSize = valuesSize;
        if (saved) {
            this.values = new double[valuesSize];
        } else {
            values = null;
        }
    }

    public boolean isHasBoundaries() {
        return hasBoundaries;
    }

    public void setHasBoundaries(boolean hasBoundaries) {
        this.hasBoundaries = hasBoundaries;
    }

    public void setBoundaries(double[] boundaries) {
        this.boundaries = boundaries;
    }

    public double[] getBoundaries() {
        return boundaries;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
        if (!saved) {
            values = null;
        }
    }

    public double getValueInPosition(int index) {
        return values[index];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPlotScaleFactor() {
        return plotScaleFactor;
    }

    public void setPlotScaleFactor(double plotScaleFactor) {
        this.plotScaleFactor = plotScaleFactor;
    }

    public String getPrefixUnit() {
        return prefixUnit;
    }

    public void setPrefixUnit(String prefixUnit) {
        this.prefixUnit = prefixUnit;
    }

    public boolean isHasHardBoundaries() {
        return hasHardBoundaries;
    }

    public void setHasHardBoundaries(boolean hasHardBoundaries) {
        this.hasHardBoundaries = hasHardBoundaries;
    }

}
