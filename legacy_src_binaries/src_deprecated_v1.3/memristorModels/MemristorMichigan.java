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
 * studengetParameter(TINDEX).
 * 
 * This class allows the simulation of a generic or HP memristor. You can
 * simulate a memristor conected to a sinusoidal input or use the model with an
 * iterative external simulator.
 * 
 * @author Fernando García Redondo, fernando.garca@gmaigetStateVariable(LINDEX).com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorMichigan extends MemristorModel {

	private double defaultKappa;
	private double defaultRinit;
	private double defaultBeta;
	private double defaultArea;
	private double defaultH;
	private double defaultPhi0;
	private double defaultLinit;
	private double defaultUa;
	private double defaultT;

	private double defaultKappaSigma;
	private double defaultRinitSigma;
	private double defaultBetaSigma;
	private double defaultAreaSigma;
	private double defaultHSigma;
	private double defaultPhi0Sigma;
	private double defaultLinitSigma;
	private double defaultUaSigma;
	private double defaultTSigma;
	
	private double alpha;
	private double F0;
	public final int KAPPAINDEX = 0; // rate dependence on applied voltage
	public final int RINITINDEX = 1; // rate dependence on applied voltage
	public final int BETAINDEX= 2; // rate dependence on applied voltage
	public final int AREAINDEX=3; // device area
	public final int HINDEX=4; // Overall thickness of the dielectric
	public final int PHI0INDEX=5; // Electron barrier height
	public final int LINITINDEX=6;
	public final int UAINDEX=7; // Activation potential
	public final int TINDEX=8; // Temperature in Kelvins

	private double kT;
	private double exp_UaeKT;
	private int LINDEX = 0;
	private int LDOTINDEX = 1;

	/**
	 * Default constructor
	 */
	public MemristorMichigan() {
		super(ResourcesAndUsefulFunctions.MICHIGANMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MICHIGANMODEL];
		description =  ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.MICHIGANMODEL];
		stateVariables.add(new Magnitude("l", true,
				LINDEX, 1e6, "um"));
		stateVariables.add(new Magnitude("lDot", true,
				LDOTINDEX, 1e6, "um"));
		parameters.add(new MemristorParameter(KAPPAINDEX, "kappa", defaultKappa,
				true,  defaultKappaSigma));
		parameters.add(new MemristorParameter(RINITINDEX, "Rinit", defaultRinit,
				true,  defaultRinitSigma));
		parameters.add(new MemristorParameter(BETAINDEX, "beta", defaultBeta,
				true,  defaultBetaSigma));
		parameters.add(new MemristorParameter(AREAINDEX, "area", defaultArea,
				true,  defaultAreaSigma));
		parameters.add(new MemristorParameter(HINDEX, "h", defaultH,
				true,  defaultHSigma));
		parameters.add(new MemristorParameter(PHI0INDEX, "Phi0", defaultPhi0,
				true,  defaultPhi0Sigma));
		parameters.add(new MemristorParameter(LINITINDEX, "lInit", defaultLinit,
				true,  defaultLinitSigma));
		parameters.add(new MemristorParameter(UAINDEX, "Ua", defaultUa,
				true,  defaultUaSigma));
		parameters.add(new MemristorParameter(TINDEX, "T", defaultT,
				true,  defaultTSigma));
		System.out.println("Selecting the model of memristor: Michigan.");
	}

	@Override
	public void setInstance() {
		// init the memristor parameters
		alpha = 2 * Math.sqrt(2 * ResourcesAndUsefulFunctions.effMSi) / ResourcesAndUsefulFunctions.reducedPLANK;
		kT = ResourcesAndUsefulFunctions.k * getParameter(TINDEX).getValue();
		exp_UaeKT = Math.exp(-getParameter(UAINDEX).getValue() * ResourcesAndUsefulFunctions.e / kT);
		F0 = 2 * alpha * Math.sqrt(ResourcesAndUsefulFunctions.e * Math.pow(getParameter(PHI0INDEX).getValue(), 3))
				/ 3;
		// init the memristor magnitudes and state variable
		// state variables
		getStateVariable(LINDEX).setCurrentValue(getParameter(LINITINDEX).getValue());
		getStateVariable(LDOTINDEX).setCurrentValue(0);
		// magnitudes
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		getElectricalMagnitude(RINDEX).setCurrentValue(getParameter(RINITINDEX).getValue());
		this.updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		acceptIncrease = false;
		needDecrease = false;
		try {
			// getElectricalMagnitude(VINDEX).setCurrentValue(inputVoltage.getCurrentVoltage()
			// - getElectricalMagnitude(IINDEX).getBackValue() * InSeriesResistance.getValue());
			getElectricalMagnitude(VINDEX).setCurrentValue(inputVoltage.getCurrentVoltage());
			getStateVariable(LDOTINDEX).setCurrentValue(prev_underflow() * ion_flow());
			integrateStateVariable(LINDEX, LDOTINDEX, timing.getTStep());
			if (Math.abs(getElectricalMagnitude(VINDEX).getCurrentValue()) > getParameter(PHI0INDEX).getValue()) {
				getElectricalMagnitude(IINDEX).setCurrentValue(Math.signum(getElectricalMagnitude(VINDEX).getCurrentValue())
						* getParameter(AREAINDEX).getValue() * highBias());
			} else {
				getElectricalMagnitude(IINDEX).setCurrentValue(Math.signum(getElectricalMagnitude(VINDEX).getCurrentValue())
						* getParameter(AREAINDEX).getValue() * lowBias());
			}
			// getElectricalMagnitude(IINDEX).setCurrentValue(Math.signum(getElectricalMagnitude(VINDEX).getCurrentValue())
			// * getParameter(AREAINDEX).getValue()
			// * (lowBias() * (1 - logistic()) + highBias() * logistic()));
			// System.ougetParameter(TINDEX).println(getElectricalMagnitude(IINDEX).getCurrentValue());
			// getElectricalMagnitude(IINDEX).setCurrentValue(fullBias());
			if (Math.abs(getElectricalMagnitude(IINDEX).getCurrentValue()) > ResourcesAndUsefulFunctions.constMinI
					&& Math.abs(getElectricalMagnitude(VINDEX).getCurrentValue()) > ResourcesAndUsefulFunctions.constMinV) {
				getElectricalMagnitude(RINDEX).setCurrentValue(Math.abs(getElectricalMagnitude(VINDEX).getCurrentValue()
						/ getElectricalMagnitude(IINDEX).getCurrentValue()));
			} else {
				getElectricalMagnitude(RINDEX).setCurrentValue(getElectricalMagnitude(RINDEX).getBackValue());
			}
			getElectricalMagnitude(TIMEINDEX).setCurrentValue(getElectricalMagnitude(TIMEINDEX).getBackValue() + timing.getTStep());

			// check magnitudes
			return checkMagnitudes(timing.getStepCounter());
		} catch (Exception exc) {
			return new SimulationResult(true, timing.getStepCounter(),
					exc.getLocalizedMessage());
		}
	}

	private double ion_flow() {
		return getParameter(BETAINDEX).getValue()
				* exp_UaeKT
				* Math.sinh(getElectricalMagnitude(VINDEX).getCurrentValue() * getParameter(KAPPAINDEX).getValue()
						/ getParameter(TINDEX).getValue());
	}

	private double prev_underflow() {
		// return heaviside(Math.signum(getStateVariable(LINDEX).getBackValue() - 1e-12)
		// + Math.signum(ion_flow()) + 1);
		// return 1;
		return Math
				.max(0,
						Math.min(
								1,
								(1 - Math.pow(
										(getStateVariable(LINDEX).getBackValue() / getParameter(HINDEX).getValue() - heaviside(-ion_flow())),
										20))));
	}

	// Give a continuous transition between high and low bias
	// private double logistic() {
	// return 1 / (1 + Math.exp(-Math.pow(
	// Math.abs(getElectricalMagnitude(VINDEX).getCurrentValue()) - getParameter(PHI0INDEX).getValue(), 3)));
	// }

	private double highBias() {
		return 4
				* 3.14
				* ResourcesAndUsefulFunctions.effMSi
				* Math.pow(ResourcesAndUsefulFunctions.e, 2)
				/ (Math.pow(ResourcesAndUsefulFunctions.PLANK, 3) * Math.pow(alpha, 2) * getParameter(PHI0INDEX).getValue()) * Math.pow(F(), 2) // F(0)
				* Math.exp(-F0 / F());

	}

	private double F() {
		return Math.abs(getElectricalMagnitude(VINDEX).getCurrentValue())
				/ (getParameter(HINDEX).getValue() - getStateVariable(LINDEX).getCurrentValue());
	}

	private double lowBias() {
		return 8
				* Math.PI
				* ResourcesAndUsefulFunctions.effMSi
				* ResourcesAndUsefulFunctions.e
				* Math.pow(kT, 2)
				/ Math.pow(ResourcesAndUsefulFunctions.PLANK, 3)
				* Math.PI
				/ (c1LowBias() * kT * Math.sin(Math.PI * c1LowBias() * kT))
				* Math.exp(-alpha * (getParameter(HINDEX).getValue() - getStateVariable(LINDEX).getCurrentValue())
						* Math.sqrt(ResourcesAndUsefulFunctions.e * getParameter(PHI0INDEX).getValue()))
				* Math.sinh(alpha * (getParameter(HINDEX).getValue() - getStateVariable(LINDEX).getCurrentValue())
						* Math.abs(getElectricalMagnitude(VINDEX).getCurrentValue()) / 4
						* Math.sqrt(ResourcesAndUsefulFunctions.e / getParameter(PHI0INDEX).getValue()));
	}

	private double c1LowBias() {
		// Low bias C1
		return alpha * (getParameter(HINDEX).getValue() - getStateVariable(LINDEX).getCurrentValue())
				* (2 * Math.sqrt(getParameter(PHI0INDEX).getValue() * ResourcesAndUsefulFunctions.e));
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

	@Override
	public boolean acceptStep() {
		boolean condWDot = Math.abs(getStateVariable(LDOTINDEX).getBackValue()) > this.derivedStateVariableAlmostZero
				&& Math.abs(getStateVariable(LDOTINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
						* Math.abs(getStateVariable(LDOTINDEX).getBackValue());

		boolean totalCond = needDecrease || condWDot;
		needDecrease = totalCond;
		if (!needDecrease) {
			acceptIncrease = (Math.abs(getStateVariable(LDOTINDEX).getCurrentValue()) > this.derivedStateVariableAlmostZero && Math
					.abs(getStateVariable(LDOTINDEX).getCurrentValue()) < Math
					.abs(getStateVariable(LDOTINDEX).getBackValue())
					* derivedStateVariableControlRatioUp);
		} else {
			acceptIncrease = false;
		}
		return !needDecrease;
	}
	
}
