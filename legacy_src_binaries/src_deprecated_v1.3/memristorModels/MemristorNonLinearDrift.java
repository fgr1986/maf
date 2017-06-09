package memristorModels;

import simulationObjects.InputVoltage;
import simulationObjects.Magnitude;
import simulationObjects.MemristorParameter;
import simulationObjects.ResourcesAndUsefulFunctions;
import simulationObjects.SimulationResult;
import simulationObjects.Timing;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEARES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 * 
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 * 
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorNonLinearDrift extends MemristorModel {

	public String[] windowPresets = { "Biolek", "Prodomakis" };
	private double defaultRon = 100;
	private double defaultRoff = 100000;
	private double defaultD = 10e-9;
	private double defaultUv = 1e-14;
	private double defaultX_init = 0.8;
	private double defaultP = 10;
	private double defaultJ = 1;
	private double defaultRonSigma = 10;
	private double defaultRoffSigma = 10;
	private double defaultDSigma = 1e-11;
	private double defaultUvSigma = 1e-15;
	private double defaultX_initSigma = 1e-2;
	private double defaultJSigma = 0;
	private double defaultPSigma = 0;

	public final int RONINDEX = 0;
	public final int ROFFINDEX = 1;
	public final int DINDEX = 2;
	public final int UVINDEX = 3;
	public final int XINITINDEX = 4;
	public final int PINDEX = 5;
	public final int JINDEX = 6;
	public final int WINDOWINDEX = 7;
	private double k;
	private int p2;

	private int XINDEX = 0;
	private int XDOTINDEX = 1;

	/**
	 * Default constructor
	 */
	public MemristorNonLinearDrift() {
		super(ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL];
		description =  ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL];
		stateVariables.add(new Magnitude("x", true, XINDEX, 1, ""));
		stateVariables.add(new Magnitude("xDot", true, XDOTINDEX, 1, "s^-1"));
		parameters.add(new MemristorParameter(RONINDEX, "Ron", defaultRon, true, 
				defaultRonSigma));
		parameters.add(new MemristorParameter(ROFFINDEX, "Roff", defaultRoff, true, 
				defaultRoffSigma));
		parameters.add(new MemristorParameter(DINDEX, "D", defaultD, true,  defaultDSigma));
		parameters.add(new MemristorParameter(UVINDEX, "uv", defaultUv, true, 
				defaultUvSigma));
		parameters.add( new MemristorParameter(XINITINDEX, "w_init", defaultX_init, true,
				 defaultX_initSigma));
		parameters.add(new MemristorParameter(PINDEX, "p", defaultP, true,  defaultPSigma));
		parameters.add(new MemristorParameter(JINDEX, "j", defaultJ, true,  defaultJSigma));
		parameters.add(new MemristorParameter(WINDOWINDEX, "window", 0, false, 0));
		System.out
				.println("Selecting the model of memristor: Biolek non-lineal dopant drift.");
	}

	@Override
	public void setInstance() {
		p2 = (int)parameters.get(PINDEX).getValue()*2;
		k = parameters.get(UVINDEX).getValue() * parameters.get(RONINDEX).getValue() / (parameters.get(DINDEX).getValue() * parameters.get(DINDEX).getValue());
		getStateVariable(XINDEX).setCurrentValue(parameters.get(XINITINDEX).getValue());
		getStateVariable(XDOTINDEX).setCurrentValue(0);
		getElectricalMagnitude(RINDEX).setCurrentValue(parameters.get(RONINDEX).getValue() * getStateVariable(XINDEX).getCurrentValue()
				+ parameters.get(ROFFINDEX).getValue() * (1 - getStateVariable(XINDEX).getCurrentValue()));
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		// calculate new values
		acceptIncrease = false;
		needDecrease = false;
		try {
			getElectricalMagnitude(VINDEX).setCurrentValue(inputVoltage.getCurrentVoltage());
			getElectricalMagnitude(IINDEX).setCurrentValue(getElectricalMagnitude(VINDEX).getCurrentValue() / getElectricalMagnitude(RINDEX).getBackValue());
			getStateVariable(XDOTINDEX).setCurrentValue(getElectricalMagnitude(IINDEX).getCurrentValue() * k * window());
			integrateStateVariable(XINDEX, XDOTINDEX, timing.getTStep());
			if (getStateVariable(XINDEX).getCurrentValue() > 1) {
				getStateVariable(XINDEX).setCurrentValue(1);
				getStateVariable(XDOTINDEX).setCurrentValue(0);
			}
			if (getStateVariable(XINDEX).getCurrentValue() < 0) {
				getStateVariable(XINDEX).setCurrentValue(0);
				getStateVariable(XDOTINDEX).setCurrentValue(0);
			}
			getElectricalMagnitude(RINDEX).setCurrentValue(parameters.get(RONINDEX).getValue() * getStateVariable(XINDEX).getCurrentValue()
					+ parameters.get(ROFFINDEX).getValue() * (1 - getStateVariable(XINDEX).getCurrentValue()));
			getElectricalMagnitude(TIMEINDEX).setCurrentValue(timing.getTimeCounter());
			// check magnitudes
			return checkMagnitudes(timing.getStepCounter());
		} catch (Exception exc) {
			return new SimulationResult(true, timing.getStepCounter(),
					exc.getLocalizedMessage());
		}
	}

	public double window() {
		double w = 0;
		switch ((int) parameters.get(WINDOWINDEX).getValue()) {
		case 0:
			w = windowBiolek();
			break;
		case 1:
			w = windowProdromakis();
			break;
		}
		return w;
	}

	
	/*
	 * 
	 * Fernando window function
	 * 
	 * @returns func f(x,i,p)={1-(x-stp(-i))^(2*p)} x between [0,1], 0 if other
	 */
	public double windowBiolek() {		
		return  1 - Math.pow(getStateVariable(XINDEX).getBackValue() - stp(-getElectricalMagnitude(IINDEX).getCurrentValue()), p2);		
	}

	/*
	 * Fernando window function
	 * 
	 * @returns func f(x,i,p)={1-(x-stp(-i))^(2*p)} x between [0,1], 0 if other
	 */
	public double windowProdromakis() {
		// j(1-[(w-0.5)2+0.75]p)
		double result = 0;
		result = parameters.get(JINDEX).getValue()
				* Math.pow(1 - Math.pow(getStateVariable(XINDEX).getBackValue() - 0.5, 2) + 0.75,
						parameters.get(PINDEX).getValue());
//		result = Math.max(0, result);
		return result;
	}

	/*
	 * Auxiliar function
	 * 
	 * @returns the stp function of a variable
	 */
	public double stp(double number) {
		if (number >= 0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean acceptStep() {
		needDecrease = (Math.abs(getStateVariable(XDOTINDEX).getBackValue()) > this.derivedStateVariableAlmostZero && Math
				.abs(getStateVariable(XDOTINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
				* Math.abs(getStateVariable(XDOTINDEX).getBackValue()));
		if (!needDecrease) {
			acceptIncrease = (Math.abs(getStateVariable(XDOTINDEX).getCurrentValue()) > this.derivedStateVariableAlmostZero && Math
					.abs(getStateVariable(XDOTINDEX).getCurrentValue()) < Math
					.abs(getStateVariable(XDOTINDEX).getBackValue())
					* derivedStateVariableControlRatioUp);
		} else {
			acceptIncrease = false;
		}
		return !needDecrease;
	}

	@Override
	public void setInstanceGoingON(int memristanceLevels) {
		p2 = (int)parameters.get(PINDEX).getValue()*2;
		k = parameters.get(UVINDEX).getValue() * parameters.get(RONINDEX).getValue() / (parameters.get(DINDEX).getValue() * parameters.get(DINDEX).getValue());
		getStateVariable(XINDEX).setCurrentValue(0.5 / memristanceLevels);
		getStateVariable(XDOTINDEX).setCurrentValue(0);
		getElectricalMagnitude(RINDEX).setCurrentValue(parameters.get(RONINDEX).getValue() * getStateVariable(XINDEX).getCurrentValue()
				+ parameters.get(ROFFINDEX).getValue() * (1 - getStateVariable(XINDEX).getCurrentValue()));
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public void setInstanceGoingOFF(int memristanceLevels) {
		p2 = (int)parameters.get(PINDEX).getValue()*2;
		k = parameters.get(UVINDEX).getValue() * parameters.get(RONINDEX).getValue() / (parameters.get(DINDEX).getValue() * parameters.get(DINDEX).getValue());
		getStateVariable(XINDEX).setCurrentValue((memristanceLevels - 0.5) / memristanceLevels);
		getStateVariable(XDOTINDEX).setCurrentValue(0);
		getElectricalMagnitude(RINDEX).setCurrentValue(parameters.get(RONINDEX).getValue() * getStateVariable(XINDEX).getCurrentValue()
				+ parameters.get(ROFFINDEX).getValue() * (1 - getStateVariable(XINDEX).getCurrentValue()));
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public boolean reachedON(int memristanceLevels) {
		return getStateVariable(XINDEX).getCurrentValue() >= (memristanceLevels - 0.5)
				/ memristanceLevels; // numLevels - 1 + 1/2
	}

	@Override
	public boolean reachedOFF(int memristanceLevels) {
		return getStateVariable(XINDEX).getCurrentValue() <= 0.5 / memristanceLevels;
	}

	@Override
	public int getNormalizedStateIndex() {
		return XINDEX;
	}

	@Override
	public boolean reachedState(int stateCount, int memristanceLevels,
			boolean goingON) {
		// ON-> x = 1; OFF -> x = 0
		if (goingON) {
			return getStateVariable(XINDEX).getCurrentValue() >= (stateCount + 0.5)
					/ memristanceLevels;
		} else {
			return getStateVariable(XINDEX).getCurrentValue() <= (memristanceLevels - stateCount - 0.5)
					/ memristanceLevels;
		}
	}
}
