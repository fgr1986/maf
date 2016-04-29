package es.upm.die.vlsi.memristor.simulation_objects.input_sources;

import java.util.Random;

public class InputVoltageParameter {

    private String tag;
    private String units;
    private double value;
    private boolean allowVariations;
    private double sigma;
    private int id;
    private boolean strictlyNonNegative;
    private double valueList[];

    // Random generation
    protected Random random;

    public InputVoltageParameter(int id, String tag, String units, double value,
            boolean allowVariations, double sigma, boolean strictlyNonNegative) {
        random = new Random( );
        this.tag = tag;
        this.units = units;
        this.value = value;
        this.allowVariations = allowVariations;
        this.sigma = sigma;
        this.id = id;
        this.strictlyNonNegative = strictlyNonNegative;
        valueList = new double[0];
    }
    
    public InputVoltageParameter( InputVoltageParameter ivModel ) {
        random = new Random( );
        this.tag = ivModel.getTag();
        this.value = ivModel.getValue();
        this.allowVariations = ivModel.isAllowVariations();
        this.sigma = ivModel.getSigma();
        this.id = ivModel.getId();
        this.strictlyNonNegative = ivModel.isStrictlyNonNegative();
        valueList = new double[ivModel.getValueList().length];
        int i=0;
        for( double d : ivModel.getValueList() ){
            valueList[i++] = d;
        }
    }
    
    public InputVoltageParameter( int id, String tag, String units, double valueList[] ) {
        random = new Random( );
        this.tag = tag;
        this.units = units;
        this.value = 0;
        this.allowVariations = false;
        this.sigma = 0;
        this.id = id;
        this.strictlyNonNegative = false;
        this.valueList = new double[valueList.length];
        int i=0;
        for( double d : valueList ){
            valueList[i++] = d;
        }
    }
    
    public double GetRandomValue(){
        double rv;
        if( strictlyNonNegative ){
            rv = Math.abs( sigma * gaussian() );
        }else{
            rv = sigma * gaussian();
        }
        return value + rv;
    }

    public double[] getValueList() {
        return valueList;
    }

    public void setValueList(double[] valueList) {
        this.valueList = valueList;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public boolean isStrictlyNonNegative() {
        return strictlyNonNegative;
    }

    public void setStrictlyNonNegative(boolean strictlyNonNegative) {
        this.strictlyNonNegative = strictlyNonNegative;
    }
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isAllowVariations() {
        return allowVariations;
    }

    public void setAllowVariations(boolean allowVariations) {
        this.allowVariations = allowVariations;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double gaussian() {
        return random.nextGaussian();
    }
}
