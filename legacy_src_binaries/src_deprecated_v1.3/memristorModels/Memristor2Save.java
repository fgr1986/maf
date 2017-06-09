package memristorModels;

import java.util.ArrayList;

import simulationObjects.MemristorParameter;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 * 
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 * 
 * @author Fernando García Redondo, fgarcia@die.upm.es
 * @author License: use, copy or modify, but contact the author
 */
public class Memristor2Save {

	private int memristorType; // 0 Q, 1 Bioleck non lineal dopant drift,
								// 2 HP, 3 Michigan
	private ArrayList<MemristorParameter> parameters;
	private String title;
	private String description;
	private double derivedStateVariableControlRatioDown;
	private double derivedStateVariableControlRatioUp;
	private double derivedStateVariableAlmostZero;
	private double maxTStepNeeded;
	private boolean needDecrease;
	private boolean acceptIncrease;

	/**
	 * Default constructor
	 */
	public Memristor2Save(MemristorModel mSource) {
		this.memristorType = mSource.getMemristorType();
		this.parameters = mSource.getParameters();
		this.maxTStepNeeded = mSource.getMaxTStepNeeded();
		this.derivedStateVariableAlmostZero = mSource
				.getDerivedStateVariableAlmostZero();
		this.derivedStateVariableControlRatioUp = mSource
				.getDerivedStateVariableControlRatioUp();
		this.derivedStateVariableControlRatioDown = mSource
				.getDerivedStateVariableControlRatioDown();
		this.title = mSource.getTitle();
		this.description = mSource.getDescription();
	}

	public int getMemristorType() {
		return memristorType;
	}

	public void setMemristorType(int memristorType) {
		this.memristorType = memristorType;
	}

	public ArrayList<MemristorParameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<MemristorParameter> parameters) {
		this.parameters = parameters;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getDerivedStateVariableControlRatioDown() {
		return derivedStateVariableControlRatioDown;
	}

	public void setDerivedStateVariableControlRatioDown(
			double derivedStateVariableControlRatioDown) {
		this.derivedStateVariableControlRatioDown = derivedStateVariableControlRatioDown;
	}

	public double getDerivedStateVariableControlRatioUp() {
		return derivedStateVariableControlRatioUp;
	}

	public void setDerivedStateVariableControlRatioUp(
			double derivedStateVariableControlRatioUp) {
		this.derivedStateVariableControlRatioUp = derivedStateVariableControlRatioUp;
	}

	public double getDerivedStateVariableAlmostZero() {
		return derivedStateVariableAlmostZero;
	}

	public void setDerivedStateVariableAlmostZero(
			double derivedStateVariableAlmostZero) {
		this.derivedStateVariableAlmostZero = derivedStateVariableAlmostZero;
	}

	public double getMaxTStepNeeded() {
		return maxTStepNeeded;
	}

	public void setMaxTStepNeeded(double maxTStepNeeded) {
		this.maxTStepNeeded = maxTStepNeeded;
	}

	public boolean isNeedDecrease() {
		return needDecrease;
	}

	public void setNeedDecrease(boolean needDecrease) {
		this.needDecrease = needDecrease;
	}

	public boolean isAcceptIncrease() {
		return acceptIncrease;
	}

	public void setAcceptIncrease(boolean acceptIncrease) {
		this.acceptIncrease = acceptIncrease;
	}

}
