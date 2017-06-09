package simulationObjects;

public class Magnitude {
	private String tag;
	private boolean saved;
	private double currentValue;
	private double backValue;
	private double twoBackValue;
	private double[] values;
	// private int actualPosition;
	private int valuesSize;
	private int id;
	private double plotScaleFactor = 1;
	private String prefixUnit = "";

	public Magnitude(String tag, boolean saved, 
			double currentValue, double backValue, double twoBackValue,
			int valuesSize, int id, double plotScaleFactor,
			String prefixUnit) {
		super();
		this.tag = tag;
		this.saved = saved;
		this.currentValue = currentValue;
		this.backValue = backValue;
		this.twoBackValue = twoBackValue;
		this.id = id;
		this.prefixUnit = prefixUnit;
		// this.actualPosition = 0;
	}

	public Magnitude(String tag, boolean saved,
			int id, double plotScaleFactor, String prefixUnit) {
		super();
		this.tag = tag;
		this.saved = saved;
		this.currentValue = 0;
		this.backValue = 0;
		this.twoBackValue = 0;
		this.id = id;
		this.plotScaleFactor = plotScaleFactor;
		this.prefixUnit = prefixUnit;
		// this.actualPosition = 0;
	}

	public Magnitude(String tag, double dsaved, boolean savedInMemory,
			int id, double plotScaleFactor, String prefixUnit) {
		super();
		this.tag = tag;
		this.saved = dsaved > 0;
		this.currentValue = 0;
		this.backValue = 0;
		this.twoBackValue = 0;
		this.id = id;
		this.plotScaleFactor = plotScaleFactor;
		this.prefixUnit = prefixUnit;
		// this.actualPosition = 0;
	}

	public String getRecentHistory() {
		return getTag() + " -->    [i]: " + getCurrentValue() + ".    [i-1]:"
				+ getBackValue() + ".    [i-2]:" + getTwoBackValue();
	}

	public void setValueInPosition(double value, int position) {
		if (saved) {
			values[position] = value;
		}else{
			values = null;
		}
	}

	public int getValuesSize() {
		return valuesSize;
	}

	public void setValuesSize(int valuesSize) {
		this.valuesSize = valuesSize;
		if (saved) {
			this.values = new double[valuesSize];
		}else{
			values = null;
		}
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
		if(!saved){
			values = null;
		}
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public double getBackValue() {
		return backValue;
	}

	public void setBackValue(double backValue) {
		this.backValue = backValue;
	}

	public double getValueInPosition(int index) {
		return values[index];
	}


	public double getTwoBackValue() {
		return twoBackValue;
	}

	public void setTwoBackValue(double twoBackValue) {
		this.twoBackValue = twoBackValue;
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

}
