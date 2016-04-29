package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

import java.util.Random;

public class MemristorParameter {

    private String tag;
    private String units;
    private double value;
    private boolean allowVariations;
    private double[] valueList;
    private double sigma;
    private int id;
    // true if the value is a number
    private boolean standard;
    private String[] nonStandardTags;
    private boolean strictlyNonNegative;

    // Random generation
    private final Random random;
    
    public enum distributions { GAUSSIAN, POISSON };
    private final distributions distribution;

    public MemristorParameter(MemristorParameter mp) {
        random = new Random();
        this.id = mp.getId();
        this.tag = mp.getTag();
        this.units = mp.getUnits();
        this.value = mp.getValue();
        this.standard = mp.isStandard();
        this.distribution = distributions.GAUSSIAN;
        if( !standard && null!=mp.getNonStandardTags() ){
            nonStandardTags = new String[mp.getNonStandardTags().length];
            System.arraycopy(mp.getNonStandardTags(), 0, nonStandardTags, 0, mp.getNonStandardTags().length);
        }
        this.allowVariations = mp.isAllowVariations();
        this.sigma = mp.getSigma();
        this.strictlyNonNegative = mp.isStrictlyNonNegative();
        // copy valuelist
        valueList = new double[mp.getValueList().length];
        System.arraycopy(mp.getValueList(), 0, valueList, 0, mp.getValueList().length);
    }

    public MemristorParameter(int id, String tag, String units, double value) {
        random = new Random();
        this.id = id;
        this.tag = tag;
        this.units = units;
        this.value = value;
        this.standard = true;
        this.distribution = distributions.GAUSSIAN;
        this.allowVariations = false;
        this.sigma = 0;
        this.strictlyNonNegative = false;
    }

    public MemristorParameter(int id, String tag, String units, double value,
            boolean allowVariations, double sigma, boolean strictlyNonNegative) {
        random = new Random();
        this.id = id;
        this.tag = tag;
        this.units = units;
        this.value = value;
        this.standard = true;
        this.distribution = distributions.GAUSSIAN;
        this.allowVariations = allowVariations;
        this.sigma = Math.abs(sigma);
        this.strictlyNonNegative = strictlyNonNegative;
    }

    public MemristorParameter(int id, String tag, String units, double value,
            boolean standard, String[] nonStandardTags) {
        random = new Random();
        this.id = id;
        this.tag = tag;
        this.units = units;
        this.value = value;
        this.allowVariations = false;
        this.sigma = 0;
        this.strictlyNonNegative = true;
        this.standard = standard;
        this.distribution = distributions.GAUSSIAN;
        if (!standard) {
            valueList = new double[1];
            valueList[0] = value;
        }
        this.nonStandardTags = nonStandardTags;
    }

    public MemristorParameter(int id, String tag, String units, double value,
            boolean allowVariations, double sigma, boolean strictlyNonNegative,
            boolean standard, String[] nonStandardTags) {
        random = new Random();
        this.id = id;
        this.tag = tag;
        this.units = units;
        this.value = value;
        this.allowVariations = allowVariations;
        this.sigma = Math.abs(sigma);
        this.strictlyNonNegative = strictlyNonNegative;
        this.standard = standard;
        this.distribution = distributions.GAUSSIAN;
        if (!standard) {
            valueList = new double[1];
            valueList[0] = value;
        }
        this.nonStandardTags = nonStandardTags;
    }

    

    private int getPoisson(double mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * random.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }
    
    public double getRandomValue() {
        double rv;
        if (strictlyNonNegative) {
            rv = Math.abs(sigma * getGaussian());
        } else {
            rv = sigma * getGaussian();
        }
        return value + rv;
    }

    public double getGaussian() {
        return random.nextGaussian();
    }

    public boolean isStrictlyNonNegative() {
        return strictlyNonNegative;
    }

    public void setStrictlyNonNegative(boolean strictlyNonNegative) {
        this.strictlyNonNegative = strictlyNonNegative;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        if (!standard) {
            valueList = new double[1];
            valueList[0] = value;
        }
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

    public double[] getValueList() {
        return valueList;
    }

    public void setValueList(double[] valueList) {
        this.valueList = valueList;
    }

    public boolean isStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public String[] getNonStandardTags() {
        return nonStandardTags;
    }

    public void setNonStandardTags(String[] nonStandardTags) {
        this.nonStandardTags = nonStandardTags;
    }
}
