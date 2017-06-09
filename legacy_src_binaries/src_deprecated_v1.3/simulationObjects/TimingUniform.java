package simulationObjects;

public class TimingUniform extends Timing {
	private int totalSteps;

	public TimingUniform(double tStep, boolean hasFinal) {
		super(0, 0, tStep);
		this.hasFinal = hasFinal;
		uniformSteps = true;
	}

	public TimingUniform(double t0, double tf, double tStep) {
		super(t0, tf, tStep);
		timeCounter = t0;
		totalSteps = (int) ((tf - t0) / tStep);
		uniformSteps = true;
		this.hasFinal = true;
	}

	public TimingUniform(double t0, double tf) {
		super(t0, tf);
		this.tStep = (tf - t0) / ResourcesAndUsefulFunctions.DEFAULTSTEPS;
		totalSteps = ResourcesAndUsefulFunctions.DEFAULTSTEPS;
		timeCounter = t0;
		uniformSteps = true;
		this.hasFinal = true;
	}

	public boolean hasNextStep() {
		if(stepCounter >= totalSteps){
			return false;
		}else{
			return true;
		}
//		if (stepCounter >= totalSteps) {
//			return false;
//		} else if (timeCounter < tf) {
//			return false;
//		} else {
//			return true;
//		}
	}

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

	public int getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(int totalSteps) {
		this.totalSteps = totalSteps;
	}

	public int getTotalStepsComputed() {
		return totalStepsComputed;
	}

	public void setTotalStepsComputed(int totalStepsCounted) {
		this.totalStepsComputed = totalStepsCounted;
	}

}
