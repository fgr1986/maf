package simulationObjects;

public class TimingAdaptative extends Timing {
	// dynamic
	private double upRatio = 2;
	private double downRatio = 0.5;
	private double minTStep = ResourcesAndUsefulFunctions.MINSTEPDC;
	private double maxTStep = ResourcesAndUsefulFunctions.MAXSTEPDC;

	public TimingAdaptative(double minTStep, double maxTStep, boolean hasFinal) {
		super(0, 0);
		t0 = 0;
		this.tStep = maxTStep;// ((maxTStep - minTStep) / 2);
		stepCounter = 0;
		uniformSteps = false;
		this.hasFinal = hasFinal;
		this.minTStep = minTStep;
		this.maxTStep = maxTStep;

	}

	public TimingAdaptative(double t0, double tf, double minTStep, double maxTStep) {
		super(false, t0, tf, minTStep, maxTStep);
		// this.tStep = ((tf - t0) / Resources.DEFAULTSTEPS);
		this.tStep = maxTStep;
		stepCounter = 0;
		uniformSteps = false;
		hasFinal = true;
		this.minTStep = minTStep;
		this.maxTStep = maxTStep;

		if (tStep < minTStep) {
			tStep = minTStep;
		}
		if (tStep > maxTStep) {
			tStep = maxTStep;
		}
	}

	public TimingAdaptative(double t0, double tf, double upRatio,
			double downRatio, double minTStep, double maxTStep) {
		super(t0, tf);
		this.t0 = t0;
		this.timeCounter = 0;
		this.tf = tf;
		// this.tStep = ((tf - t0) / Resources.DEFAULTSTEPS);
		this.tStep = maxTStep;
		this.upRatio = upRatio;
		this.downRatio = downRatio;
		this.minTStep = minTStep;
		this.maxTStep = maxTStep;
		uniformSteps = false;
		hasFinal = true;
	}

	public boolean hasNextStep() {
		if (timeCounter < tf) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateNextStep() {
		stepCounter++;
		timeCounter += tStep;
	}

	public double getTimeCounter() {
		return timeCounter;
	}

	public void setTimeCounter(double timeCounter) {
		this.timeCounter = timeCounter;
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

	public int getTotalStepsComputed() {
		return totalStepsComputed;
	}

	public void setTotalStepsComputed(int totalStepsCounted) {
		this.totalStepsComputed = totalStepsCounted;
	}

	public double getUpRatio() {
		return upRatio;
	}

	public void setUpRatio(double upRatio) {
		this.upRatio = upRatio;
	}

	public double getDownRatio() {
		return downRatio;
	}

	public void setDownRatio(double downRatio) {
		this.downRatio = downRatio;
	}

	@Override
	public void decreaseTStep() {
		double aux = tStep * downRatio;
		tStep = Math.max(minTStep, aux);
	}

	@Override
	public void increaseTStep() {
		double aux = tStep * upRatio;
		tStep = Math.min(maxTStep, aux);
	}

	@Override
	public boolean canIncreaseTStep() {
		if (hasFinal) {
			return (tStep < (tf - t0) / ResourcesAndUsefulFunctions.DEFAULTSTEPS)
					&& (tStep < maxTStep);
		} else {
			return (tStep < maxTStep);
		}
	}

	@Override
	public boolean canDecreaseTStep() {
		return (tStep > minTStep);
	}

}