package simulationObjects;

public class InputVoltageParameter {
	private String tag;
	private double value;
	private boolean allowVariations;
	private double maxVariation;
	private double sigma;
	private int id;

	// public Parameter(String tag, double value) {
	// super();
	// this.tag = tag;
	// this.value = value;
	// this.allowVariations = false;
	// this.maxVariation = 0;
	// }

	public InputVoltageParameter(int id, String tag, double value, boolean allowVariations,
			double maxVariation, double sigma) {
		super();
		this.tag = tag;
		this.value = value;
		this.allowVariations = allowVariations;
		this.maxVariation = Math.abs(maxVariation);
		this.sigma = Math.min(Math.abs(sigma), Math.abs(value) / 10);
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

	public double getMaxVariation() {
		return maxVariation;
	}

	public void setMaxVariation(double maxVariation) {
		this.maxVariation = maxVariation;
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
	
}
