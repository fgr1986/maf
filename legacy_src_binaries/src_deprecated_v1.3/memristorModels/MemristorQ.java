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
public class MemristorQ extends MemristorModel {
	private double defaultA = 10;
	private double defaultB = 0;
	private double defaultC = 1000;
	private double defaultD = 1;

	private double defaultASigma = 1;
	private double defaultBSigma = 1;
	private double defaultCSigma = 10;
	private double defaultDSigma = 1;
	public int AINDEX = 0;
	public int BINDEX = 1;
	public int CINDEX = 2;
	public int DINDEX = 3;
	
	private int QINDEX = 4;

	/**
	 * Default constructor
	 */
	public MemristorQ() {
		super(ResourcesAndUsefulFunctions.QCONTROLLEDMODEL);
		derivedStateVariableControlRatioDown = 2;
		derivedStateVariableControlRatioUp = 0.1;
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.QCONTROLLEDMODEL];
		description =  ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.QCONTROLLEDMODEL];
		electricalMagnitudes.add(new Magnitude("q", false, QINDEX, 1, "C"));

		parameters.add(new MemristorParameter(AINDEX, "a", defaultA, true,  defaultASigma));
		parameters.add(new MemristorParameter(BINDEX, "b", defaultB, true,  defaultBSigma));
		parameters.add(new MemristorParameter(CINDEX, "c", defaultC, true,  defaultCSigma));
		parameters.add(new MemristorParameter(DINDEX, "d", defaultD, true,  defaultDSigma));
		System.out
				.println("Selecting the model of memristor: charge controlleparameters.get(DINDEX).");
	}

	@Override
	public void setInstance() {
		electricalMagnitudes.get(QINDEX).setCurrentValue(0);
		electricalMagnitudes.get(RINDEX).setCurrentValue(parameters.get(DINDEX).getValue());
		electricalMagnitudes.get(IINDEX).setCurrentValue(0);
		electricalMagnitudes.get(VINDEX).setCurrentValue(0);
		// update back values
		updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		// calculate new values
		acceptIncrease = false;
		needDecrease = false;
		try {
			electricalMagnitudes.get(RINDEX).setCurrentValue(parameters.get(AINDEX).getValue() * Math.pow(electricalMagnitudes.get(QINDEX).getCurrentValue(), 3)
					+ parameters.get(BINDEX).getValue() * Math.pow(electricalMagnitudes.get(QINDEX).getCurrentValue(), 2)
					+ parameters.get(CINDEX).getValue() * electricalMagnitudes.get(QINDEX).getCurrentValue() + parameters.get(DINDEX).getValue());
			electricalMagnitudes.get(IINDEX).setCurrentValue(inputVoltage.getCurrentVoltage()
					/ electricalMagnitudes.get(RINDEX).getCurrentValue());

			integrateMagnitude(QINDEX, IINDEX, timing.getTStep());
			electricalMagnitudes.get(VINDEX).setCurrentValue(inputVoltage.getCurrentVoltage());
			electricalMagnitudes.get(TIMEINDEX).setCurrentValue(electricalMagnitudes.get(TIMEINDEX).getBackValue() + timing.getTStep());

			// check magnitudes
			return checkMagnitudes(timing.getStepCounter());
		} catch (Exception exc) {
			return new SimulationResult(true, timing.getStepCounter(),
					exc.getLocalizedMessage());
		}
	}

	@Override
	public boolean acceptStep() {
		// TODO Auto-generated method stub
		needDecrease = (Math.abs(electricalMagnitudes.get(RINDEX).getBackValue()) > this.derivedStateVariableAlmostZero && Math
				.abs(electricalMagnitudes.get(RINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
				* Math.abs(electricalMagnitudes.get(RINDEX).getBackValue()));
		if (!needDecrease) {
			acceptIncrease = (Math.abs(electricalMagnitudes.get(VINDEX).getCurrentValue()) > this.derivedStateVariableAlmostZero && Math
					.abs(electricalMagnitudes.get(RINDEX).getCurrentValue()) < Math.abs(electricalMagnitudes.get(RINDEX).getBackValue())
					* derivedStateVariableControlRatioUp);
		} else {
			acceptIncrease = false;
		}
		return !needDecrease;
	}

}
