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
public class MemristorYakopcic extends MemristorModel {

	public String[] windowPresets = { "Yakopcic", "Biolek (p=5)",
			"Prodomakis (j=0.75, p=10)" };
	private int presetIndex;
	private String[] presets = { "Air Force Research Laboratory - Boise State University Chalcogenide memristor, SMACD version",
			"HP TiO2 memristor",
			"Air Force Research Laboratory - Boise State University Chalcogenide memristor",
			"University of Michigan memristor" };
	private double[] defaultA1 = {0.097, 2.3e-4, 0.097, 3.7e-7 };
	private double[] defaultA2 = {0.097, 3.8e-4, 0.097, 4.35e-7 };
	private double[] defaultB = {0.05,  1, 0.05, 0.7 };
	private double[] defaultAlphaP = {1, 4, 1, 1.2 };
	private double[] defaultAlphaN = {0.5, 24, 5, 3 };
	private double[] defaultAP = {1e6, 5, 4000, 0.005 };
	private double[] defaultAN = {1e6, 30, 4000, 0.08 };
	private double[] defaultVP = {0.5, 1.2, 0.16, 1.5 };
	private double[] defaultVN = {0.5, 0.6, 0.15, 0.5 };
	private double[] defaultXP = {0.9, 0.7, 0.3, 0.2 };
	private double[] defaultXN = {0.1, 0.8, 0.5, 0.5 };
	private double[] defaultXInit = {0.001, 0.02, 0.001, 0.1 };
	private double[] defaultRInit = {215, 215, 215, 215 };

	private double[] defaultA1Sigma = {1e-2, 1e-5, 1e-2, 1e-9 };
	private double[] defaultA2Sigma = {1e-2, 1e-5, 1e-2, 1e-9 };
	private double[] defaultBSigma = {1e-3, 1e-1, 1e-3, 1e-2 };
	private double[] defaultAlphaPSigma = {1e-2, 1, 1e-2, 1e-2 };
	private double[] defaultAlphaNSigma = {1e-2, 1, 1e-2, 1e-2 };
	private double[] defaultAPSigma = {100, 1, 10, 1e-4 };
	private double[] defaultANSigma = {100, 2, 10, 1e-4 };
	private double[] defaultVPSigma = {1e-2, 1e-1, 1e-2, 1e-1 };
	private double[] defaultVNSigma = {1e-2, 1e-1, 1e-2, 1e-1 };
	private double[] defaultXPSigma = {1e-2, 0.5e-1, 1e-2, 1e-2 };
	private double[] defaultXNSigma = {1e-2, 0.5e-1, 1e-2, 1e-2 };
	private double[] defaultXInitSigma = {1e-2, 1e-2, 1e-2, 1e-2 };
	private double[] defaultRInitSigma = {10, 10, 10, 10 };
	public final int A1INDEX = 0;
	public final int A2INDEX = 1;
	public final int ALPHAPINDEX = 2;
	public final int ALPHANINDEX = 3;
	public final int BINDEX = 4;
	public final int APINDEX = 5;
	public final int ANINDEX = 6;
	public final int VPINDEX = 7;
	public final int VNINDEX = 8;
	public final int XPINDEX = 9;
	public final int XNINDEX = 10;
	public final int XINITINDEX = 11;
	public final int RINITINDEX = 12;
	public final int WINDOWINDEX = 13;
	private double expVp;
	private double expVn;
	private double constMinI = 1e-9;
	private double constMinV = 1e-6;

	private int XINDEX = 0;
	private int XDOTINDEX = 1;

	/**
	 * Default constructor
	 */
	public MemristorYakopcic() {
		super(ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL];
		description =  ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL];
		stateVariables.add(new Magnitude("x", true,
				XINDEX, 1, "nm"));
		stateVariables.add(new Magnitude("xDot", true,
				XDOTINDEX, 1, "nm/s"));
	
		parameters.add(new MemristorParameter(A1INDEX, "a1", defaultA1[0], true, 
				defaultA1Sigma[0]));
		parameters.add(new MemristorParameter(A2INDEX, "a2", defaultA2[0], true, 
				defaultA2Sigma[0]));
		parameters.add(new MemristorParameter(ALPHAPINDEX, "alphaP", defaultAlphaP[0], true,
				 defaultAlphaPSigma[0]));
		parameters.add(new MemristorParameter(ALPHANINDEX, "alphaN", defaultAlphaN[0], true,
				defaultAlphaNSigma[0]));
		parameters.add( new MemristorParameter(BINDEX, "b", defaultB[0], true, 				defaultBSigma[0]));
		parameters.add(new MemristorParameter(APINDEX, "Ap", defaultAP[0], true, 
				defaultAPSigma[0]));
		parameters.add(new MemristorParameter(ANINDEX, "An", defaultAN[0], true, 
				defaultANSigma[0]));
		parameters.add(new MemristorParameter(VPINDEX, "Vp", defaultVP[0], true, 
				defaultVPSigma[0]));
		parameters.add(new MemristorParameter(VNINDEX, "Vn", defaultVN[0], true, 
				defaultVNSigma[0]));
		parameters.add( new MemristorParameter(XPINDEX, "Xp", defaultXP[0], true, 
				defaultXPSigma[0]));
		parameters.add(new MemristorParameter(XNINDEX, "Xn", defaultXN[0], true, 
				defaultXNSigma[0]));
		parameters.add(new MemristorParameter(XINITINDEX, "x_init", defaultXInit[0], true,
				 defaultXInitSigma[0]));
		parameters.add(new MemristorParameter(RINITINDEX, "R_init", defaultRInit[0], true,
				 defaultRInitSigma[0]));
		parameters.add(new MemristorParameter(WINDOWINDEX,"window", 0, false, 0));
		System.out.println("Selecting the model of memristor: General Device.");
	}

	@Override
	public void setInstance() {
		// util constants
		expVp = Math.exp(parameters.get(VPINDEX).getValue());
		expVn = Math.exp(parameters.get(VNINDEX).getValue());
		// Please, remark
		// expVn = Math.exp(-parameters.get(VNINDEX).getValue());
		stateVariables.get(XINDEX).setCurrentValue(parameters.get(XINITINDEX).getValue());
		stateVariables.get(XDOTINDEX).setCurrentValue(0);
		electricalMagnitudes.get(RINDEX).setCurrentValue(parameters.get(RINITINDEX).getValue());
		updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		acceptIncrease = false;
		needDecrease = false;
		try {
			double g = 0;
			double f = 0;
			// g
			if (inputVoltage.getCurrentVoltage() > parameters.get(VPINDEX).getValue()) {
				g = parameters.get(APINDEX).getValue()
						* (Math.exp(inputVoltage.getCurrentVoltage()) - expVp);
			} else if (inputVoltage.getCurrentVoltage() < -parameters.get(VNINDEX).getValue()) {
				g = -parameters.get(ANINDEX).getValue()
						* (Math.exp(-inputVoltage.getCurrentVoltage()) - expVn);
			} else {
				g = 0;
			}
			// f
			if (electricalMagnitudes.get(VINDEX).getCurrentValue() > 0) {
				if (stateVariables.get(XINDEX).getBackValue() >= parameters.get(XPINDEX).getValue()) {
					f = Math.exp(-parameters.get(ALPHAPINDEX).getValue()
							* (stateVariables.get(XINDEX).getBackValue() - parameters.get(XPINDEX).getValue()))
							* window();
				} else {
					f = 1;
				}
			} else if (stateVariables.get(XINDEX).getBackValue() <= (1 - parameters.get(XNINDEX).getValue())) {
				f = Math.exp(parameters.get(ALPHANINDEX).getValue()
						* (stateVariables.get(XINDEX).getBackValue()
								+ parameters.get(XNINDEX).getValue() - 1))
						* window();
			} else {
				f = 1;
			}
			stateVariables.get(XDOTINDEX).setCurrentValue(g * f);
			integrateStateVariable(XINDEX, XDOTINDEX, timing.getTStep());
			if (inputVoltage.getCurrentVoltage() > 0) {
				electricalMagnitudes.get(IINDEX).setCurrentValue(
						parameters.get(A1INDEX).getValue()
								* stateVariables.get(XINDEX).getCurrentValue()
								* Math.sinh(parameters.get(BINDEX).getValue()
										* inputVoltage.getCurrentVoltage()));
			} else {
				electricalMagnitudes.get(IINDEX).setCurrentValue(
						parameters.get(A2INDEX).getValue()
								* stateVariables.get(XINDEX).getCurrentValue()
								* Math.sinh(parameters.get(BINDEX).getValue()
										* inputVoltage.getCurrentVoltage()));
			}

			if (Math.abs(electricalMagnitudes.get(IINDEX).getCurrentValue()) > constMinI
					&& Math.abs(inputVoltage.getCurrentVoltage()) > constMinV) {
				electricalMagnitudes.get(RINDEX)
						.setCurrentValue(
								Math.abs(inputVoltage.getCurrentVoltage()
										/ electricalMagnitudes.get(IINDEX)
												.getCurrentValue()));
			} else {
				electricalMagnitudes.get(RINDEX).setCurrentValue(
						electricalMagnitudes.get(RINDEX).getBackValue());
			}
			electricalMagnitudes.get(VINDEX).setCurrentValue(
					inputVoltage.getCurrentVoltage());
			electricalMagnitudes.get(TIMEINDEX).setCurrentValue(
					electricalMagnitudes.get(TIMEINDEX).getBackValue()
							+ timing.getTStep());
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
			w = windowYakopcic();
			break;
		case 1:
			w = windowBiolek();
			break;
		case 2:
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
		return 1 - Math.pow(stateVariables.get(XINDEX).getBackValue()
				- stp(-electricalMagnitudes.get(IINDEX).getCurrentValue()), 10);
	}

	/*
	 * Fernando window function
	 * 
	 * @returns func f(x,i,p)={1-(x-stp(-i))^(2*p)} x between [0,1], 0 if other
	 */
	public double windowProdromakis() {
		// j(1-[(w-0.5)2+0.75]p)
		double result = 0;
		result = 0.75 * Math.pow(1 - Math.pow(stateVariables.get(XINDEX)
				.getBackValue() - 0.5, 2) + 0.75, 10);
		return result;
	}

	public double windowYakopcic() {
		if (stateVariables.get(XINDEX).getBackValue() >= parameters.get(XPINDEX).getValue()) {
			if (electricalMagnitudes.get(VINDEX).getCurrentValue() > 0) {
				return ((parameters.get(XPINDEX).getValue() - stateVariables.get(XINDEX)
						.getBackValue()) / (1 - parameters.get(XPINDEX).getValue()) + 1);
			} else {
				return 1;
			}
		} else if (stateVariables.get(XINDEX).getBackValue() <= (1 - parameters.get(XNINDEX).getValue())) {
			if (electricalMagnitudes.get(VINDEX).getCurrentValue() < 0) {
				return (stateVariables.get(XINDEX).getBackValue() / (1 - parameters.get(XNINDEX).getValue()));
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

	/*
	 * Auxiliar function
	 * 
	 * @returns the heaviside function of a variable
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
		needDecrease = (Math.abs(stateVariables.get(XDOTINDEX).getBackValue()) > this.derivedStateVariableAlmostZero && Math
				.abs(stateVariables.get(XDOTINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
				* Math.abs(stateVariables.get(XDOTINDEX).getBackValue()));
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
		// util constants
		expVp = Math.exp(parameters.get(VPINDEX).getValue());
		expVn = Math.exp(parameters.get(VNINDEX).getValue());
		//
		stateVariables.get(XINDEX).setCurrentValue(0.5 / memristanceLevels);
		stateVariables.get(XDOTINDEX).setCurrentValue(0);
		electricalMagnitudes.get(RINDEX).setCurrentValue(parameters.get(RINITINDEX).getValue());
		electricalMagnitudes.get(IINDEX).setCurrentValue(0);
		electricalMagnitudes.get(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public void setInstanceGoingOFF(int memristanceLevels) {
		expVp = Math.exp(parameters.get(VPINDEX).getValue());
		expVn = Math.exp(parameters.get(VNINDEX).getValue());
		//
		stateVariables.get(XINDEX).setCurrentValue(
				(memristanceLevels - 0.5) / memristanceLevels);
		stateVariables.get(XDOTINDEX).setCurrentValue(0);
		electricalMagnitudes.get(RINDEX).setCurrentValue(parameters.get(RINITINDEX).getValue());
		electricalMagnitudes.get(IINDEX).setCurrentValue(0);
		electricalMagnitudes.get(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public boolean reachedON(int memristanceLevels) {
		return stateVariables.get(XINDEX).getCurrentValue() >= (memristanceLevels - 0.5)
				/ memristanceLevels; // numLevels - 1 + 1/2
	}

	@Override
	public boolean reachedOFF(int memristanceLevels) {
		return stateVariables.get(XINDEX).getCurrentValue() <= 0.5 / memristanceLevels;
	}

	// @Override
	// public Magnitude getNormalizedState() {
	// return x;
	// }

	@Override
	public int getNormalizedStateIndex() {
		return XINDEX;
	}

	@Override
	public boolean reachedState(int stateCount, int memristanceLevels,
			boolean goingON) {
		// ON-> x = 1; OFF -> x = 0
		if (goingON) {
			return stateVariables.get(XINDEX).getCurrentValue() >= (stateCount + 0.5)
					/ memristanceLevels;
		} else {
			return stateVariables.get(XINDEX).getCurrentValue() <= (memristanceLevels
					- stateCount - 0.5)
					/ memristanceLevels;
		}
	}

	public double[] getDefaultA1() {
		return defaultA1;
	}

	public void setDefaultA1(double[] defaultA1) {
		this.defaultA1 = defaultA1;
	}

	public double[] getDefaultA2() {
		return defaultA2;
	}

	public void setDefaultA2(double[] defaultA2) {
		this.defaultA2 = defaultA2;
	}

	public double[] getDefaultB() {
		return defaultB;
	}

	public void setDefaultB(double[] defaultB) {
		this.defaultB = defaultB;
	}

	public double[] getDefaultAlphaP() {
		return defaultAlphaP;
	}

	public void setDefaultAlphaP(double[] defaultAlphaP) {
		this.defaultAlphaP = defaultAlphaP;
	}

	public double[] getDefaultAlphaN() {
		return defaultAlphaN;
	}

	public void setDefaultAlphaN(double[] defaultAlphaN) {
		this.defaultAlphaN = defaultAlphaN;
	}

	public double[] getDefaultAP() {
		return defaultAP;
	}

	public void setDefaultAP(double[] defaultAP) {
		this.defaultAP = defaultAP;
	}

	public double[] getDefaultAN() {
		return defaultAN;
	}

	public void setDefaultAN(double[] defaultAN) {
		this.defaultAN = defaultAN;
	}

	public double[] getDefaultVP() {
		return defaultVP;
	}

	public void setDefaultVP(double[] defaultVP) {
		this.defaultVP = defaultVP;
	}

	public double[] getDefaultVN() {
		return defaultVN;
	}

	public void setDefaultVN(double[] defaultVN) {
		this.defaultVN = defaultVN;
	}

	public double[] getDefaultXP() {
		return defaultXP;
	}

	public void setDefaultXP(double[] defaultXP) {
		this.defaultXP = defaultXP;
	}

	public double[] getDefaultXN() {
		return defaultXN;
	}

	public void setDefaultXN(double[] defaultXN) {
		this.defaultXN = defaultXN;
	}

	public double[] getDefaultXInit() {
		return defaultXInit;
	}

	public void setDefaultXInit(double[] defaultXInit) {
		this.defaultXInit = defaultXInit;
	}

	public double[] getDefaultRInit() {
		return defaultRInit;
	}

	public void setDefaultRInit(double[] defaultRInit) {
		this.defaultRInit = defaultRInit;
	}

	public double[] getDefaultA1Sigma() {
		return defaultA1Sigma;
	}

	public void setDefaultA1Sigma(double[] defaultA1Sigma) {
		this.defaultA1Sigma = defaultA1Sigma;
	}

	public double[] getDefaultA2Sigma() {
		return defaultA2Sigma;
	}

	public void setDefaultA2Sigma(double[] defaultA2Sigma) {
		this.defaultA2Sigma = defaultA2Sigma;
	}

	public double[] getDefaultBSigma() {
		return defaultBSigma;
	}

	public void setDefaultBSigma(double[] defaultBSigma) {
		this.defaultBSigma = defaultBSigma;
	}

	public double[] getDefaultAlphaPSigma() {
		return defaultAlphaPSigma;
	}

	public void setDefaultAlphaPSigma(double[] defaultAlphaPSigma) {
		this.defaultAlphaPSigma = defaultAlphaPSigma;
	}

	public double[] getDefaultAlphaNSigma() {
		return defaultAlphaNSigma;
	}

	public void setDefaultAlphaNSigma(double[] defaultAlphaNSigma) {
		this.defaultAlphaNSigma = defaultAlphaNSigma;
	}

	public double[] getDefaultAPSigma() {
		return defaultAPSigma;
	}

	public void setDefaultAPSigma(double[] defaultAPSigma) {
		this.defaultAPSigma = defaultAPSigma;
	}

	public double[] getDefaultANSigma() {
		return defaultANSigma;
	}

	public void setDefaultANSigma(double[] defaultANSigma) {
		this.defaultANSigma = defaultANSigma;
	}

	public double[] getDefaultVPSigma() {
		return defaultVPSigma;
	}

	public void setDefaultVPSigma(double[] defaultVPSigma) {
		this.defaultVPSigma = defaultVPSigma;
	}

	public double[] getDefaultVNSigma() {
		return defaultVNSigma;
	}

	public void setDefaultVNSigma(double[] defaultVNSigma) {
		this.defaultVNSigma = defaultVNSigma;
	}

	public double[] getDefaultXPSigma() {
		return defaultXPSigma;
	}

	public void setDefaultXPSigma(double[] defaultXPSigma) {
		this.defaultXPSigma = defaultXPSigma;
	}

	public double[] getDefaultXNSigma() {
		return defaultXNSigma;
	}

	public void setDefaultXNSigma(double[] defaultXNSigma) {
		this.defaultXNSigma = defaultXNSigma;
	}

	public double[] getDefaultXInitSigma() {
		return defaultXInitSigma;
	}

	public void setDefaultXInitSigma(double[] defaultXInitSigma) {
		this.defaultXInitSigma = defaultXInitSigma;
	}

	public double[] getDefaultRInitSigma() {
		return defaultRInitSigma;
	}

	public void setDefaultRInitSigma(double[] defaultRInitSigma) {
		this.defaultRInitSigma = defaultRInitSigma;
	}

	public double getExpVp() {
		return expVp;
	}

	public void setExpVp(double expVp) {
		this.expVp = expVp;
	}

	public double getExpVn() {
		return expVn;
	}

	public void setExpVn(double expVn) {
		this.expVn = expVn;
	}

	public double getConstMinI() {
		return constMinI;
	}

	public void setConstMinI(double constMinI) {
		this.constMinI = constMinI;
	}

	public double getConstMinV() {
		return constMinV;
	}

	public void setConstMinV(double constMinV) {
		this.constMinV = constMinV;
	}

	public String[] getPresets() {
		return presets;
	}

	public void setPresets(String[] presets) {
		this.presets = presets;
	}

	public int getPresetIndex() {
		return presetIndex;
	}

	public void setPresetIndex(int presetIndex) {
		this.presetIndex = presetIndex;
	}

	public String getPresetTitle() {
		return presets[presetIndex];
	}

}
