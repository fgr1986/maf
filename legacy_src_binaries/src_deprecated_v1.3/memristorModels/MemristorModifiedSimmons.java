package memristorModels;

import simulationObjects.InputVoltage;
import simulationObjects.Magnitude;
import simulationObjects.MemristorParameter;
import simulationObjects.ResourcesAndUsefulFunctions;
import simulationObjects.SimulationResult;
import simulationObjects.Timing;

/**
 * Memristor simulator. This program is included in the PFC 'DINÁMICA DE
 * CIRCUITOS NO LINEALES CON MEMRISTORES,' Fernando García Redondo, ETSIT UPM
 * student.
 * 
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 * 
 * @author Fernando García Redondo, fernando.garca@gmail.com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorModifiedSimmons extends MemristorModel {

	// 1 eV = 1 V * e

	// Constants
	private double constLambdaFinal;
	private final double constIntegration = 1e9;
	// Current parameters
	// private Parameter T; // Temperature in Kelvins
	private double defaultRoff = 2e4;
	private double defaultRon = 100;
	private double defaultI_off = 115e-6;// collected in A
	private double defaultI_on = 8.9e-6;// collected in A
	private double defaultF_off = 3.5e-6;// collected in s
	private double defaultF_on = 40e-6;// collected in s
	private double defaultA_off = 1.2;// collected in nm
	private double defaultA_on = 1.8;// collected in nm
	private double defaultX_off = 3;// collected in nm
	private double defaultX_on = 0;// collected in nm
	private double defaultB = 500e-6;// collected in A
	private double defaultW_c = 107e-3;// collected in nm
	private double defaultW_init = 1.21;// collected in nm

	private double defaultI_offSigma = 1e-7;// collected in A
	private double defaultI_onSigma = 1e-7;// collected in A
	private double defaultF_offSigma = 1e-7;// collected in s
	private double defaultF_onSigma = 1e-7;// collected in s
	private double defaultA_offSigma = 1e-11;// collected in nm
	private double defaultA_onSigma = 1e-11;// collected in nm
	private double defaultX_offSigma = 1e-11;// collected in nm
	private double defaultX_onSigma = 1e-11;// collected in nm
	private double defaultBSigma = 1e-6;// collected in A
	private double defaultW_cSigma = 1e-10;// collected in nm
	private double defaultW_initSigma = 1e-11;// collected in nm
	private double defaultRoffSigma = 100;
	private double defaultRonSigma = 10;

	public final int ROFFINDEX = 0;
	public final int RONINDEX = 1;
	// tunnel barrier parametres
	public final int IOFFINDEX = 2; // collected in A
	public final int IONINDEX = 3;// collected in A
	public final int FOFFINDEX = 4;// collected in s
	public final int FONINDEX = 5;// collected in s
	public final int AOFFINDEX = 6;// collected in nm
	public final int AONINDEX = 7;// collected in nm
	public final int XOFFINDEX = 8;// collected in nm
	public final int XONINDEX = 9;// collected in nm
	public final int BINDEX = 10;// collected in A
	public final int WCINDEX = 11;// collected in nm
	public final int WINITINDEX = 12;// collected in nm
	// Magnitudes w, w_i in nm
	private int XINDEX = 0;
	private int WINDEX = 1;
	private int WDOTINDEX = 2;

	// private boolean forceDecreaseStep;

	/**
	 * Default constructor
	 */
	public MemristorModifiedSimmons() {
		super(ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL];
		description = ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL];
		stateVariables.add(new Magnitude("x", true,
				XINDEX, 1, ""));
		stateVariables.add(new Magnitude("w", true,
				WINDEX, 1, "nm"));
		stateVariables.add(new Magnitude("wDot", true,
				WDOTINDEX, 1, "nm/s"));
		parameters.add(new MemristorParameter(ROFFINDEX, "Roff", defaultRoff,
				true,  defaultRoffSigma));
		parameters.add(new MemristorParameter(RONINDEX, "Ron", defaultRon,
				true,  defaultRonSigma));
		parameters.add(new MemristorParameter(IOFFINDEX,
				"i_off", defaultI_off, true,
				 defaultI_offSigma));
		parameters.add(new MemristorParameter(IONINDEX,
				"i_on", defaultI_on, true, 
				defaultI_onSigma));
		parameters.add(new MemristorParameter(FOFFINDEX, "f_off", defaultF_off,
				true,  defaultF_offSigma));
		parameters.add(new MemristorParameter(FONINDEX,
				"f_on", defaultF_on, true, 
				defaultF_onSigma));
		parameters.add(new MemristorParameter(AOFFINDEX, "a_off", defaultA_off,
				true,  defaultA_offSigma));
		parameters.add(new MemristorParameter(AONINDEX, "a_on", defaultA_on,
				true,  defaultA_onSigma));
		parameters.add(new MemristorParameter(XOFFINDEX, "x_off", defaultX_off,
				true,  defaultX_offSigma));
		parameters.add(new MemristorParameter(XONINDEX, "x_on", defaultX_on,
				true,  defaultX_onSigma));
		parameters.add(new MemristorParameter(BINDEX, "b", defaultB, true,
				 defaultBSigma));
		parameters.add(new MemristorParameter(WCINDEX, "w_c", defaultW_c, true,
				 defaultW_cSigma));
		parameters.add(new MemristorParameter(WINITINDEX, "w_init",
				defaultW_init, true,  defaultW_initSigma));

		derivedStateVariableControlRatioDown = 5;
		derivedStateVariableControlRatioUp = 0.1;
		System.out.println("Selecting the model of memristor: HP.");
	}

	@Override
	public void setInstance() {

		constLambdaFinal = Math.log(getParameter(ROFFINDEX).getValue()
				/ getParameter(RONINDEX).getValue());
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		// State variables w and wDot
		getStateVariable(WDOTINDEX).setCurrentValue(0);
		getStateVariable(WINDEX).setCurrentValue(
				getParameter(WINITINDEX).getValue());
		getStateVariable(XINDEX).setCurrentValue(
				(getStateVariable(WINDEX).getCurrentValue() - getParameter(
						XONINDEX).getValue())
						/ (getParameter(XOFFINDEX).getValue() - getParameter(
								XONINDEX).getValue()));
		electricalMagnitudes
				.get(RINDEX)
				.setCurrentValue(
						(getParameter(RONINDEX).getValue() * Math
								.exp(constLambdaFinal
										* (stateVariables.get(WINDEX)
												.getCurrentValue() - getParameter(
												XONINDEX).getValue())
										/ (getParameter(XOFFINDEX).getValue() - getParameter(
												XONINDEX).getValue()))));

		this.updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		double sig = 0;
		acceptIncrease = false;
		needDecrease = false;
		try {
			sig = Math.signum(inputVoltage.getCurrentVoltage());
			getElectricalMagnitude(VINDEX).setCurrentValue(
					inputVoltage.getCurrentVoltage());

			getStateVariable(WDOTINDEX).setCurrentValue(constIntegration*ion_flow(sig));
			integrateStateVariable(WINDEX, WDOTINDEX, timing.getTStep());
			checkWLimits();
			getStateVariable(XINDEX).setCurrentValue(
					(getStateVariable(WINDEX).getCurrentValue() - getParameter(
							XONINDEX).getValue())
							/ (getParameter(XOFFINDEX).getValue() - getParameter(
									XONINDEX).getValue()));

			electricalMagnitudes
					.get(RINDEX)
					.setCurrentValue(
							(getParameter(RONINDEX).getValue() * Math
									.exp(constLambdaFinal
											* (stateVariables.get(WINDEX)
													.getCurrentValue() - getParameter(
													XOFFINDEX).getValue())
											/ (getParameter(XONINDEX)
													.getValue() - getParameter(
													XOFFINDEX).getValue()))));
			// v=r*i
			electricalMagnitudes.get(IINDEX).setCurrentValue(
					electricalMagnitudes.get(VINDEX).getCurrentValue()
							/ electricalMagnitudes.get(RINDEX)
									.getCurrentValue());
			getElectricalMagnitude(TIMEINDEX).setCurrentValue(
					getElectricalMagnitude(TIMEINDEX).getBackValue()
							+ timing.getTStep());
			// check magnitudes
			return checkMagnitudes(timing.getStepCounter());
		} catch (Exception exc) {
			return new SimulationResult(true, timing.getStepCounter(),
					exc.getLocalizedMessage());
		}
	}

	private void checkWLimits() {
		if (getStateVariable(WINDEX).getCurrentValue() < getParameter(XONINDEX)
				.getValue()) {
			getStateVariable(WINDEX).setCurrentValue(
					getParameter(XOFFINDEX).getValue());
			getStateVariable(WDOTINDEX).setCurrentValue(0);
			needDecrease = true;
		}
		if (getStateVariable(WINDEX).getCurrentValue() > getParameter(XOFFINDEX)
				.getValue()) {
			getStateVariable(WINDEX).setCurrentValue(
					getParameter(XONINDEX).getValue());
			getStateVariable(WDOTINDEX).setCurrentValue(0);
			needDecrease = true;
		}
	}

	/*
	 * Auxiliar function
	 * 
	 * @returns the heaviside function of a variable
	 */
	public double heaviside(double number) {
		if (number >= 0) {
			return 1;
		} else {
			return 0;
		}
	}

	private double ion_flow(double sig) {
		if (sig > 0) {
			return getParameter(FOFFINDEX).getValue()
					* Math.sinh(getElectricalMagnitude(IINDEX)
							.getBackValue()
							/ getParameter(IOFFINDEX).getValue())
					* Math.exp(-Math.exp((getStateVariable(WINDEX)
							.getBackValue() - getParameter(AOFFINDEX)
							.getValue())
							/ getParameter(WCINDEX).getValue())
							- getStateVariable(WINDEX).getBackValue()
							/ getParameter(WCINDEX).getValue());
		} else{
			return getParameter(FONINDEX).getValue()
					* Math.sinh(getElectricalMagnitude(IINDEX)
							.getBackValue()
							/ getParameter(IONINDEX).getValue())
					* Math.exp(-Math
							.exp((getParameter(AONINDEX).getValue() - getStateVariable(
									WINDEX).getBackValue())
									/ getParameter(WCINDEX).getValue())
							- getStateVariable(WINDEX).getBackValue()
							/ getParameter(WCINDEX).getValue());
		} 
	}

	@Override
	public boolean acceptStep() {
		boolean condWDot = Math.abs(getStateVariable(WDOTINDEX).getBackValue()) > this.derivedStateVariableAlmostZero
				&& Math.abs(getStateVariable(WDOTINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
						* Math.abs(getStateVariable(WDOTINDEX).getBackValue());

		boolean totalCond = needDecrease || condWDot;
		needDecrease = totalCond;
		if (!needDecrease) {
			acceptIncrease = (Math.abs(getStateVariable(WDOTINDEX)
					.getCurrentValue()) > this.derivedStateVariableAlmostZero && Math
					.abs(getStateVariable(WDOTINDEX).getCurrentValue()) < Math
					.abs(getStateVariable(WDOTINDEX).getBackValue())
					* derivedStateVariableControlRatioUp);
		} else {
			acceptIncrease = false;
		}
		return !needDecrease;
	}

	@Override
	public void setInstanceGoingON(int memristanceLevels) {
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		// State variables w and wDot
		getStateVariable(WDOTINDEX).setCurrentValue(0);
		getStateVariable(XINDEX).setCurrentValue(0.5 / memristanceLevels);
		getStateVariable(WINDEX).setCurrentValue(
				(getParameter(AONINDEX).getValue() - getParameter(AOFFINDEX)
						.getValue())
						* getStateVariable(XINDEX).getCurrentValue()
						+ getParameter(AOFFINDEX).getValue());
		electricalMagnitudes
				.get(RINDEX)
				.setCurrentValue(
						(getParameter(RONINDEX).getValue() * Math
								.exp(constLambdaFinal
										* (stateVariables.get(WINDEX)
												.getCurrentValue() - getParameter(
												AONINDEX).getValue())
										/ (getParameter(AOFFINDEX).getValue() - getParameter(
												AONINDEX).getValue()))));
		this.updateBackValuesMagnitudes();
	}

	@Override
	public void setInstanceGoingOFF(int memristanceLevels) {
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		// State variables w and wDot
		getStateVariable(WDOTINDEX).setCurrentValue(0);
		getStateVariable(XINDEX).setCurrentValue(
				(memristanceLevels - 0.5) / memristanceLevels);
		getStateVariable(WINDEX).setCurrentValue(
				(getParameter(AONINDEX).getValue() - getParameter(AOFFINDEX)
						.getValue())
						* getStateVariable(XINDEX).getCurrentValue()
						+ getParameter(AOFFINDEX).getValue());
		electricalMagnitudes
				.get(RINDEX)
				.setCurrentValue(
						(getParameter(RONINDEX).getValue() * Math
								.exp(constLambdaFinal
										* (stateVariables.get(WINDEX)
												.getCurrentValue() - getParameter(
												AONINDEX).getValue())
										/ (getParameter(AOFFINDEX).getValue() - getParameter(
												AONINDEX).getValue()))));

		this.updateBackValuesMagnitudes();
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
			return getStateVariable(XINDEX).getCurrentValue() <= (memristanceLevels
					- stateCount - 0.5)
					/ memristanceLevels;
		}
	}
	
}