package simulationObjects;

public class Timing {
	protected double tStep; // time increment (s)
	protected double minTStep;
	protected double maxTStep;
	protected double t0; // time start simulation (s)
	protected double tf; // final simulation time (s)
	protected int stepCounter;
	protected double timeCounter;
	protected int totalStepsComputed;
	protected boolean uniformSteps;
	protected boolean hasFinal;

	public Timing(double t0, double tf) {
		super();
		this.t0 = t0;
		this.tf = tf;
		this.tStep = ((tf - t0) / ResourcesAndUsefulFunctions.DEFAULTSTEPS);
		this.stepCounter = 0;
		this.timeCounter = 0;
		this.totalStepsComputed = 0;
	}
	
	public Timing(boolean uniformSteps, double tStep) {
		super();
		this.tStep = tStep;
		this.stepCounter = 0;
		this.timeCounter = 0;
		this.totalStepsComputed = 0;
		this.uniformSteps = uniformSteps;
	}

	public Timing(boolean uniformSteps,  double minTStep, double maxTStep) {
		this.minTStep = minTStep;
		this.maxTStep = maxTStep;
		this.stepCounter = 0;
		this.timeCounter = 0;
		this.totalStepsComputed = 0;
		this.uniformSteps = uniformSteps;
	}
	
	public Timing(boolean uniformSteps, double t0, double tf, double minTStep, double maxTStep) {

		this.t0 = t0;
		this.tf = tf;
		this.minTStep = minTStep;
		this.maxTStep = maxTStep;
		this.stepCounter = 0;
		this.timeCounter = 0;
		this.totalStepsComputed = 0;
		this.uniformSteps = uniformSteps;
	}

	public Timing(double t0, double tf, double tStep) {
		super();
		this.tStep = tStep;
		this.t0 = t0;
		this.tf = tf;
		this.stepCounter = 0;
		this.timeCounter = 0;
		this.totalStepsComputed = 0;
		this.uniformSteps = true;
	}

	public double getTStep() {
		return tStep;
	}

	public void setTStep(double tStep) {
		this.tStep = tStep;
	}

	public double getT0() {
		return t0;
	}

	public void setT0(double t0) {
		this.t0 = t0;
	}

	public double getTf() {
		return tf;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public int getStepCounter() {
		return stepCounter;
	}

	public void setStepCounter(int stepCounter) {
		this.stepCounter = stepCounter;
	}

	public double getTimeCounter() {
		return timeCounter;
	}

	public void setTimeCounter(double timeCounter) {
		this.timeCounter = timeCounter;
	}

	public int getTotalStepsComputed() {
		return totalStepsComputed;
	}

	public void setTotalStepsComputed(int totalStepsComputed) {
		this.totalStepsComputed = totalStepsComputed;
	}

	public int getDEFAULTSTEPS() {
		return ResourcesAndUsefulFunctions.DEFAULTSTEPS;
	}

	public boolean hasNextStep() {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateNextStep() {
		// TODO Auto-generated method stub

	}

	public boolean isUniformSteps() {
		return uniformSteps;
	}

	public void setUniformSteps(boolean uniformSteps) {
		this.uniformSteps = uniformSteps;
	}

	public void decreaseTStep() {
	}

	public void increaseTStep() {
	}

	public boolean canDecreaseTStep() {
		return tStep > ResourcesAndUsefulFunctions.MINSTEPDC;
	}

	public boolean canIncreaseTStep() {
		return tStep < (tf - t0) / ResourcesAndUsefulFunctions.DEFAULTSTEPS;
	}

	public boolean isHasFinal() {
		return hasFinal;
	}

	public void setHasFinal(boolean hasFinal) {
		this.hasFinal = hasFinal;
	}

	public double getMinTStep() {
		return minTStep;
	}

	public void setMinTStep(double minTStep) {
		this.minTStep = minTStep;
	}

	public double getMaxTStep() {
		return maxTStep;
	}

	public void setMaxTStep(double maxTStep) {
		this.maxTStep = maxTStep;
	}
	
}
