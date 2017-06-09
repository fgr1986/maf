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
public class MemristorSimmons extends MemristorModel {

	// 1 eV = 1 V * e

	// Constants
	// private final double K = 1.38e-23; // Boltzmann's constant
	private final double constLambda = 1e9 * ResourcesAndUsefulFunctions.e * Math.log(2)
			/ (8 * Math.PI * ResourcesAndUsefulFunctions.e0);
	private double constLambdaFinal;
	// private final double constB = 1e-9 * 4 * Math.PI
	// * Math.sqrt(2 * Resources.m * Resources.e) / Resources.PLANK;
	private final double constB = 10.246;
	private double constPhi1Final;
	private double j0;
	private double w1;
	private final double constIntegration = 1e9;
	// Current parameters
	// private Parameter T; // Temperature in Kelvins
	private double defaultRs = 215; // Internal Resistance in ohms
	private double defaultPhi0 = 0.95; // Electron barrier height in eV
	private double defaultArea = 1e4; // area in nm²
	private double defaultK = 5; // dielectric
	private double defaultI_off = 115e-6;// collected in A
	private double defaultI_on = 8.9e-6;// collected in A
	private double defaultF_off = 3.5e-6;// collected in s
	private double defaultF_on = 40e-6;// collected in s
	private double defaultA_off = 1.2;// collected in nm
	private double defaultA_on = 1.8;// collected in nm
	private double defaultB = 500e-6;// collected in A
	private double defaultW_c = 107e-3;// collected in nm
	private double defaultW_init = 1.21;// collected in nm
	private double defaultRsSigma = 10; // Internal Resistance in ohms
	private double defaultPhi0Sigma = 0.1; // Electron barrier height in eV
	private double defaultAreaSigma = 10; // area in nm²
	private double defaultKSigma = 1; // dielectric
	private double defaultI_offSigma = 1e-7;// collected in A
	private double defaultI_onSigma = 1e-7;// collected in A
	private double defaultF_offSigma = 1e-7;// collected in s
	private double defaultF_onSigma = 1e-7;// collected in s
	private double defaultA_offSigma = 1e-2;// collected in nm
	private double defaultA_onSigma = 1e-2;// collected in nm
	private double defaultBSigma = 1e-6;// collected in A
	private double defaultW_cSigma = 5e-3;// collected in nm
	private double defaultW_initSigma = 1e-2;// collected in nm

	public final int RSINDEX = 0; // Internal Resistance in ohms
	public final int PHI0INDEX = 1; // Electron barrier height in eV
	public final int AREAINDEX = 2; // area in nm²
	public final int KINDEX = 3; // dielectric
	// tunnel barrier parametres
	public final int IOFFINDEX = 4; // collected in A
	public final int IONINDEX = 5;// collected in A
	public final int FOFFINDEX = 6;// collected in s
	public final int FONINDEX = 7;// collected in s
	public final int AOFFINDEX = 8;// collected in nm
	public final int AONINDEX = 9;// collected in nm
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
	public MemristorSimmons() {
		super(ResourcesAndUsefulFunctions.SIMMONSMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.SIMMONSMODEL];
		description =  ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.SIMMONSMODEL];
		stateVariables.add(new Magnitude("x", true,
				XINDEX, 1, ""));
		stateVariables.add(new Magnitude("w", true,
				WINDEX, 1, "nm"));
		stateVariables.add(new Magnitude("wDot", true,
				WDOTINDEX, 1, "nm/s"));
		parameters.add(new MemristorParameter(RSINDEX, "Rs", defaultRs, true,
				 defaultRsSigma));
		parameters.add(new MemristorParameter(PHI0INDEX, "phio", defaultPhi0,
				true,  defaultPhi0Sigma));
		parameters.add(new MemristorParameter(AREAINDEX, "area", defaultArea,
				true,  defaultAreaSigma));
		parameters.add(new MemristorParameter(KINDEX, "k", defaultK, true,
				 defaultKSigma));
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

		constLambdaFinal = constLambda / getParameter(KINDEX).getValue();
		constPhi1Final = 1.15 * constLambdaFinal;
		w1 = constLambdaFinal * 1.2 / getParameter(PHI0INDEX).getValue();
		j0 = ResourcesAndUsefulFunctions.e * ResourcesAndUsefulFunctions.e / (2 * Math.PI * ResourcesAndUsefulFunctions.PLANK)
				* getParameter(AREAINDEX).getValue();

		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		// State variables w and wDot
		getStateVariable(WDOTINDEX).setCurrentValue(0);
		getStateVariable(WINDEX).setCurrentValue(
				getParameter(WINITINDEX).getValue());
		getStateVariable(XINDEX).setCurrentValue(
				(getStateVariable(WINDEX).getCurrentValue() - getParameter(
						AOFFINDEX).getValue())
						/ (getParameter(AONINDEX).getValue() - getParameter(
								AOFFINDEX).getValue()));
		getElectricalMagnitude(RINDEX).setCurrentValue(
				getParameter(RSINDEX).getValue());

		this.updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		double sig = 0;
		double w2 = 0;
		double R = 0;
		// wDot in meters (should be scaled with constIntegration)
		double dw = 0;
		double Phi1 = 0;
		double lambda = 0;
		double vGAP = 0;
		double B = 0;
		acceptIncrease = false;
		needDecrease = false;
		try {
			sig = Math.signum(inputVoltage.getCurrentVoltage());
			getElectricalMagnitude(VINDEX).setCurrentValue(
					inputVoltage.getCurrentVoltage());

			getStateVariable(WDOTINDEX).setCurrentValue(ion_flow(sig));
			integrateStateVariable(WINDEX, WDOTINDEX, timing.getTStep());
			checkWLimits();
			getStateVariable(XINDEX)
					.setCurrentValue(
							(getStateVariable(WINDEX).getCurrentValue() - getParameter(
									AOFFINDEX).getValue())
									/ (getParameter(AONINDEX).getValue() - getParameter(
											AOFFINDEX).getValue()));
			lambda = (constLambdaFinal / getStateVariable(WINDEX)
					.getCurrentValue());
			vGAP = (getElectricalMagnitude(VINDEX).getCurrentValue() -
					getElectricalMagnitude(IINDEX).getBackValue()* getParameter(RSINDEX).getValue());
			w2 = (w1 + getStateVariable(WINDEX).getCurrentValue()
					* (1 - 9.2
							* lambda
							/ (3 * getParameter(PHI0INDEX).getValue() + 4
									* lambda - 2 * Math.abs(vGAP))));

			if (w2 <= w1 + ResourcesAndUsefulFunctions.constMinW) {
				w2 = (w1 + 2 * ResourcesAndUsefulFunctions.constMinW);
				needDecrease = true;
			}
			dw = (w2 - w1);
			B = (constB * dw);
			R = ((w2 / w1) * (getStateVariable(WINDEX).getCurrentValue() - w1) / (getStateVariable(
					WINDEX).getCurrentValue() - w2));
			if (R <= 0) {
				needDecrease = true;
			}
			Phi1 = (getParameter(PHI0INDEX).getValue() - Math.abs(vGAP)
					* (w1 + w2)
					/ (2 * getStateVariable(WINDEX).getCurrentValue()) - (constPhi1Final / dw)
					* Math.log(R));
			if (Phi1 < 0) {
				needDecrease = true;
				Phi1 = 0;
			}
			getElectricalMagnitude(IINDEX)
					.setCurrentValue(
							sig * j0 / Math.pow(dw, 2)
									* (Phi1 * Math.exp(-B * Math.sqrt(Phi1)) - (Phi1 + Math
											.abs(vGAP))
											* Math.exp(-B
													* Math.sqrt(Phi1
															+ Math.abs(vGAP)))));
			if (Math.abs(getElectricalMagnitude(IINDEX).getCurrentValue()) > ResourcesAndUsefulFunctions.constMinI
					&& Math.abs(getElectricalMagnitude(VINDEX)
							.getCurrentValue()) > ResourcesAndUsefulFunctions.constMinV) {
				getElectricalMagnitude(RINDEX).setCurrentValue(
						Math.abs(getElectricalMagnitude(VINDEX)
								.getCurrentValue()
								/ getElectricalMagnitude(IINDEX)
										.getCurrentValue()));
			} else {
				getElectricalMagnitude(RINDEX).setCurrentValue(
						getElectricalMagnitude(RINDEX).getBackValue());
			}

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
		if (getStateVariable(WINDEX).getCurrentValue() < getParameter(AOFFINDEX)
				.getValue()) {
			getStateVariable(WINDEX).setCurrentValue(
					getParameter(AOFFINDEX).getValue());
			getStateVariable(WDOTINDEX).setCurrentValue(0);
			needDecrease = true;
		}
		if (getStateVariable(WINDEX).getCurrentValue() > getParameter(AONINDEX)
				.getValue()) {
			getStateVariable(WINDEX).setCurrentValue(
					getParameter(AONINDEX).getValue());
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
		if (sig > 0
				&& getStateVariable(WINDEX).getBackValue() < getParameter(
						AONINDEX).getValue()) {
			return constIntegration
					* getParameter(FOFFINDEX).getValue()
					* Math.sinh(Math.abs(getElectricalMagnitude(IINDEX)
							.getBackValue())
							/ getParameter(IOFFINDEX).getValue())
					* Math.exp(-Math.exp((getStateVariable(WINDEX)
							.getBackValue() - getParameter(AOFFINDEX)
							.getValue())
							/ getParameter(WCINDEX).getValue())
							- getStateVariable(WINDEX).getBackValue()
							/ getParameter(WCINDEX).getValue());
		} else if (getStateVariable(WINDEX).getBackValue() > getParameter(
				AOFFINDEX).getValue()) {
			return -constIntegration
					* getParameter(FONINDEX).getValue()
					* Math.sinh(Math.abs(getElectricalMagnitude(IINDEX)
							.getBackValue())
							/ getParameter(IONINDEX).getValue())
					* Math.exp(-Math
							.exp((getParameter(AONINDEX).getValue() - getStateVariable(
									WINDEX).getBackValue())
									/ getParameter(WCINDEX).getValue())
							- getStateVariable(WINDEX).getBackValue()
							/ getParameter(WCINDEX).getValue());
		} else {
			return 0;
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
		getElectricalMagnitude(RINDEX).setCurrentValue(
				getParameter(RSINDEX).getValue());

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
		getElectricalMagnitude(RINDEX).setCurrentValue(
				getParameter(RSINDEX).getValue());

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