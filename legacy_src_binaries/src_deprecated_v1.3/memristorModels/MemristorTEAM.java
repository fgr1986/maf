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
public class MemristorTEAM extends MemristorModel {

	private final int TEAMWINDOW1 = 0;
	private final int TEAMWINDOW2 = 1;
	private final int GARCIAWINDOW = 2;
	private final int NOWINDOW = 3;
	private double[] windows = { TEAMWINDOW1, TEAMWINDOW2, GARCIAWINDOW,
			NOWINDOW };
	private String[] windowsNames = { "TEAM window 1", "TEAM window 2",
			"Biolek like window", "No window" };

	// private final double constIntegration = 1;
	private double defaultI_off = 115e-6;
	private double defaultI_on = -8.9e-6;
	private double defaultK_off = 3.5e-6;//8e-13;//3.5e-6;
	private double defaultK_on = -40e-6; //-8e-13;//-40e-6;
	private double defaultAlpha_off = 3;// 10;
	private double defaultAlpha_on = 3;// 10;
	private double defaultA_off = 1.2e-9;
	private double defaultA_on = 2e-9;
	private double defaultX_off = 3e-9;
	private double defaultX_on = 0;
	private double defaultW_c = 107e-12;
	private double defaultW_init = 1.21e-9;
	private double defaultRoff = 2e5;
	private double defaultRon = 300;

	private double defaultI_offSigma = 1e-8;
	private double defaultI_onSigma = 1e-8;
	private double defaultK_offSigma = 1e-11;
	private double defaultK_onSigma = 1e-11;
	private double defaultAlpha_offSigma = 1;
	private double defaultAlpha_onSigma = 1;
	private double defaultA_offSigma = 1e-11;
	private double defaultA_onSigma = 1e-11;
	private double defaultX_offSigma = 1e-11;
	private double defaultX_onSigma = 1e-11;
	private double defaultW_cSigma = 5e-13;
	private double defaultW_initSigma = 1e-11;
	private double defaultRoffSigma = 100;
	private double defaultRonSigma = 10;

	private double lambda;

	public final int ROFFINDEX = 0;
	public final int RONINDEX = 1;
	public final int KOFFINDEX = 2;
	public final int KONINDEX = 3;
	public final int IOFFINDEX = 4;
	public final int IONINDEX = 5;
	public final int AOFFINDEX = 6;
	public final int AONINDEX = 7;
	// public final int XOFFINDEX = 8;
	// public final int XONINDEX = 9;
	public final int ALPHAOFFINDEX = 8;
	public final int ALPHAONINDEX = 9;
	public final int XOFFINDEX = 10;
	public final int XONINDEX = 11;
	public final int WCINDEX = 12;
	public final int XINITINDEX = 13;
	public final int WINDOWINDEX = 14;

	private int XINDEX = 0;
	private int XDOTINDEX = 1;
	private int WINDEX = 2;

	// private boolean forceDecreaseStep;

	/**
	 * Default constructor
	 */
	public MemristorTEAM() {
		super(ResourcesAndUsefulFunctions.TEAMMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.TEAMMODEL];
		description = ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.TEAMMODEL];
		stateVariables.add(new Magnitude("x", true,
				XINDEX, 1e9, "nm"));
		stateVariables.add(new Magnitude("xDot", true,
				XDOTINDEX, 1e9, "nm/s"));
		stateVariables.add(new Magnitude("normaliced", true,  WINDEX, 1, ""));

		parameters.add(new MemristorParameter(ROFFINDEX, "Roff", defaultRoff,
				true,  defaultRoffSigma));
		parameters.add(new MemristorParameter(RONINDEX, "Ron", defaultRon,
				true,  defaultRonSigma));
		parameters.add(new MemristorParameter(KOFFINDEX, "k_off", defaultK_off,
				true,  defaultK_offSigma));
		parameters.add(new MemristorParameter(KONINDEX, "k_on", defaultK_on,
				true,  defaultK_onSigma));
		parameters.add(new MemristorParameter(IOFFINDEX, "i_off", defaultI_off,
				true,  defaultI_offSigma));
		parameters.add(new MemristorParameter(IONINDEX, "i_on", defaultI_on,
				true,  defaultI_onSigma));
		parameters.add(new MemristorParameter(AOFFINDEX, "a_off", defaultA_off,
				true,  defaultA_offSigma));
		parameters.add(new MemristorParameter(AONINDEX, "a_on", defaultA_on,
				true,  defaultA_onSigma));
		parameters.add(new MemristorParameter(ALPHAOFFINDEX, "alpha_off",
				defaultAlpha_off, true, 
				defaultAlpha_offSigma));
		parameters.add(new MemristorParameter(ALPHAONINDEX, "alpha_on",
				defaultAlpha_on, true, 
				defaultAlpha_onSigma));
		parameters.add(new MemristorParameter(XOFFINDEX, "x_off", defaultX_off,
				true,  defaultX_offSigma));
		parameters.add(new MemristorParameter(XONINDEX, "x_on", defaultX_on,
				true,  defaultX_onSigma));
		parameters.add(new MemristorParameter(WCINDEX, "w_c", defaultW_c, true,
				 defaultW_cSigma));
		parameters.add(new MemristorParameter(XINITINDEX, "x_init",
				defaultW_init, true,  defaultW_initSigma));
		parameters.add(new MemristorParameter(WINDOWINDEX, "window", 0, false,
				 0));

		derivedStateVariableControlRatioDown = 2;
		derivedStateVariableControlRatioUp = 0.1;

		System.out.println("Selecting the model of memristor: TEAM.");
	}

	@Override
	public void setInstance() {
		lambda = Math.log(getParameter(ROFFINDEX).getValue()
				/ getParameter(RONINDEX).getValue());
		electricalMagnitudes.get(VINDEX).setCurrentValue(0);
		electricalMagnitudes.get(IINDEX).setCurrentValue(0);
		stateVariables.get(XDOTINDEX).setCurrentValue(0);
		stateVariables.get(XINDEX).setCurrentValue(
				getParameter(XINITINDEX).getValue());
		stateVariables.get(WINDEX).setCurrentValue(
				(stateVariables.get(XINDEX).getCurrentValue() - getParameter(
						XONINDEX).getValue())
						/ (getParameter(XOFFINDEX).getValue() - getParameter(
								XONINDEX).getValue()));
		electricalMagnitudes.get(RINDEX).setCurrentValue(0);
		this.updateBackValuesMagnitudes();
		// for(MemristorParameter p : getParameters()){
		// System.out.println("P: " + p.getTag() + " " + p.getValue());
		// }
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		acceptIncrease = false;
		needDecrease = false;

		try {
			electricalMagnitudes.get(VINDEX).setCurrentValue(
					inputVoltage.getCurrentVoltage());

			stateVariables.get(XDOTINDEX).setCurrentValue(f() * ion_flow());
			integrateStateVariable(XINDEX, XDOTINDEX, timing.getTStep());
			if (stateVariables.get(XINDEX).getCurrentValue() > getParameter(
					XOFFINDEX).getValue()) {
				stateVariables.get(XINDEX).setCurrentValue(
						getParameter(XOFFINDEX).getValue());
				needDecrease = true;
			}
			if (stateVariables.get(XINDEX).getCurrentValue() < getParameter(
					XONINDEX).getValue()) {
				stateVariables.get(XINDEX).setCurrentValue(
						getParameter(XONINDEX).getValue());
				needDecrease = true;
			}
			stateVariables.get(WINDEX).setCurrentValue(
					(stateVariables.get(XINDEX).getCurrentValue() - getParameter(
							XONINDEX).getValue())
							/ (getParameter(XOFFINDEX).getValue() - getParameter(
									XONINDEX).getValue()));
			electricalMagnitudes
			.get(RINDEX)
			.setCurrentValue(
					(getParameter(RONINDEX).getValue() * Math
							.exp(lambda
									* getStateVariable(WINDEX).getCurrentValue())));
//			electricalMagnitudes
//					.get(RINDEX)
//					.setCurrentValue(
//							(getParameter(RONINDEX).getValue() * Math
//									.exp(lambda
//											* (stateVariables.get(XINDEX)
//													.getCurrentValue() - getParameter(
//													XONINDEX).getValue())
//											/ (getParameter(XOFFINDEX)
//													.getValue() - getParameter(
//													XONINDEX).getValue()))));
			// v=r*i
			electricalMagnitudes.get(IINDEX).setCurrentValue(
					electricalMagnitudes.get(VINDEX).getCurrentValue()
							/ electricalMagnitudes.get(RINDEX)
									.getCurrentValue());

			electricalMagnitudes.get(TIMEINDEX).setCurrentValue(
					electricalMagnitudes.get(TIMEINDEX).getBackValue()
							+ timing.getTStep());

			return checkMagnitudes(timing.getStepCounter());
		} catch (Exception exc) {
			return new SimulationResult(true, timing.getStepCounter(),
					exc.getLocalizedMessage());
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

	private double ion_flow() {
		if (electricalMagnitudes.get(IINDEX).getBackValue() >= getParameter(
				IOFFINDEX).getValue()) {
			return getParameter(KOFFINDEX).getValue()
					* Math.pow(Math.abs(electricalMagnitudes.get(IINDEX)
							.getBackValue()
							/ getParameter(IOFFINDEX).getValue()) - 1,
							getParameter(ALPHAOFFINDEX).getValue());
		} else if (electricalMagnitudes.get(IINDEX).getBackValue() <= getParameter(
				IONINDEX).getValue()) {
			return getParameter(KONINDEX).getValue()
					* Math.pow(Math
							.abs(electricalMagnitudes.get(IINDEX)
									.getBackValue()
									/ getParameter(IONINDEX).getValue()) - 1,
							getParameter(ALPHAONINDEX).getValue());
		} else {
			return 0;
		}
	}

	private double f() {
		double f = 0;
		switch ((int) getParameter(WINDOWINDEX).getValue()) {
		case TEAMWINDOW1:
			if (electricalMagnitudes.get(IINDEX).getBackValue() == 0) {
				f = 0;
			} else if (electricalMagnitudes.get(IINDEX).getBackValue() > 0) {
				f = Math.exp(-Math.exp((stateVariables.get(XINDEX)
						.getBackValue() - getParameter(AOFFINDEX).getValue())
						/ getParameter(WCINDEX).getValue()));
			} else {
				f = Math.exp(-Math
						.exp((getParameter(AONINDEX).getValue() - stateVariables
								.get(XINDEX).getBackValue())
								/ getParameter(WCINDEX).getValue()));
			}
			break;
		case TEAMWINDOW2:// team 1
			if (electricalMagnitudes.get(IINDEX).getBackValue() == 0) {
				f = 0;
			} else if (electricalMagnitudes.get(IINDEX).getBackValue() > 0) {
				f = Math.exp(-Math.exp((stateVariables.get(XINDEX)
						.getBackValue() - getParameter(XOFFINDEX).getValue())
						/ getParameter(WCINDEX).getValue()));
			} else {
				f = Math.exp(-Math
						.exp((getParameter(XONINDEX).getValue() - stateVariables
								.get(XINDEX).getBackValue())
								/ getParameter(WCINDEX).getValue()));
			}
			break;
		case GARCIAWINDOW: // biolek like
			if (electricalMagnitudes.get(IINDEX).getBackValue() == 0) {
				f = 0;
			} else if (electricalMagnitudes.get(IINDEX).getBackValue() > 0) {
				f = 1 - Math.pow(stateVariables.get(XINDEX).getCurrentValue()
						- getParameter(AONINDEX).getValue() + 1, 20);
			} else {
				f = 1 - Math.pow(stateVariables.get(XINDEX).getCurrentValue()
						- getParameter(AOFFINDEX).getValue() - 1, 20);
			}
			break;
		default: // NOWINDOW
			f = 1;
			break;
		}
		return f;
		// return f = Math.min(1, Math.max(0, f));
	}

	@Override
	public boolean acceptStep() {
		boolean condWDot = Math.abs(stateVariables.get(XDOTINDEX)
				.getBackValue()) > this.derivedStateVariableAlmostZero
				&& Math.abs(stateVariables.get(XDOTINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
						* Math.abs(stateVariables.get(XDOTINDEX).getBackValue());

		boolean totalCond = needDecrease || condWDot;
		needDecrease = totalCond;
		if (!needDecrease) {
			acceptIncrease = (Math.abs(stateVariables.get(XDOTINDEX)
					.getCurrentValue()) > this.derivedStateVariableAlmostZero && Math
					.abs(stateVariables.get(XDOTINDEX).getCurrentValue()) < Math
					.abs(stateVariables.get(XDOTINDEX).getBackValue())
					* derivedStateVariableControlRatioUp);
		} else {
			acceptIncrease = false;
		}
		return !needDecrease;
	}

	@Override
	public void setInstanceGoingON(int memristanceLevels) {
		lambda = Math.log(getParameter(ROFFINDEX).getValue()
				/ getParameter(RONINDEX).getValue());
		electricalMagnitudes.get(VINDEX).setCurrentValue(0);
		electricalMagnitudes.get(IINDEX).setCurrentValue(0);
		// State variables w and wDot
		stateVariables.get(XDOTINDEX).setCurrentValue(0);
		getStateVariable(WINDEX).setCurrentValue(
				(memristanceLevels - 0.5) / memristanceLevels);

		stateVariables.get(XINDEX).setCurrentValue(
				( getParameter(
						XONINDEX).getValue())
						+ stateVariables.get(WINDEX).getCurrentValue() * (getParameter(XOFFINDEX).getValue() - getParameter(
								XONINDEX).getValue()));
		electricalMagnitudes.get(RINDEX).setCurrentValue(0);
		this.updateBackValuesMagnitudes();
		// System.out.println("Going ON: x " +
		// getStateVariable(WINDEX).getCurrentValue());
	}

	@Override
	public void setInstanceGoingOFF(int memristanceLevels) {
		lambda = Math.log(getParameter(ROFFINDEX).getValue()
				/ getParameter(RONINDEX).getValue());
		electricalMagnitudes.get(VINDEX).setCurrentValue(0);
		electricalMagnitudes.get(IINDEX).setCurrentValue(0);
		// State variables w and wDot
		stateVariables.get(XDOTINDEX).setCurrentValue(0);
		getStateVariable(WINDEX).setCurrentValue(0.5 / memristanceLevels);

		stateVariables.get(XINDEX).setCurrentValue(
				( getParameter(
						XONINDEX).getValue())
						+ stateVariables.get(WINDEX).getCurrentValue() * (getParameter(XOFFINDEX).getValue() - getParameter(
								XONINDEX).getValue()));
		electricalMagnitudes.get(RINDEX).setCurrentValue(0);
		this.updateBackValuesMagnitudes();
		// System.out.println("Going OFF: x " +
		// getStateVariable(WINDEX).getCurrentValue());
	}

	// The characteristics of the model forces this:
	@Override
	public boolean reachedON(int memristanceLevels) {
		return stateVariables.get(WINDEX).getCurrentValue() <= 0.5 / memristanceLevels;
	}

	@Override
	public boolean reachedOFF(int memristanceLevels) {
		return stateVariables.get(WINDEX).getCurrentValue() >= (memristanceLevels - 0.5)
				/ memristanceLevels;
	}

	@Override
	public int getNormalizedStateIndex() {
		return WINDEX;
	}

	@Override
	public int getAmplitudeSignGoingON() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public boolean reachedState(int stateCount, int memristanceLevels,
			boolean goingON) {
		// ON-> x = 0; OFF -> x = 1
		if (goingON) {
			return stateVariables.get(WINDEX).getCurrentValue() <= (memristanceLevels
					- stateCount - 0.5)
					/ memristanceLevels;
		} else {
			return stateVariables.get(WINDEX).getCurrentValue() >= (stateCount + 0.5)
					/ memristanceLevels;
		}
	}

	public double[] getWindows() {
		return windows;
	}

	public void setWindows(double[] windows) {
		this.windows = windows;
	}

	public String[] getWindowsNames() {
		return windowsNames;
	}

	public void setWindowsNames(String[] windowsNames) {
		this.windowsNames = windowsNames;
	}

}