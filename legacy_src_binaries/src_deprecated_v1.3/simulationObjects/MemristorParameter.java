package simulationObjects;

public class MemristorParameter {
	private String tag;
	private double value;
	private boolean allowVariations;
	private double[] valueList;
	private double sigma;
	private int id;

	public MemristorParameter(int id, String tag, double value, boolean allowVariations,
			double sigma) {
		super();
		this.id = id;
		this.tag = tag;
		this.value = value;
		this.allowVariations = allowVariations;
		this.sigma = Math.min(Math.abs(sigma), Math.abs(value) / 10);
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

	public double[] getValueList() {
		return valueList;
	}

	public void setValueList(double[] valueList) {
		this.valueList = valueList;
	}

}
