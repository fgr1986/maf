package memristorModels;

import java.util.ArrayList;

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
 * @author Fernando García Redondo, fgarcia@die.upm.es
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorModel {

	protected int memristorType; // 0 Q, 1 Bioleck non lineal dopant drift,
									// 2 HP, 3 Michigan
	protected ArrayList<MemristorParameter> parameters;
	protected ArrayList<Magnitude> electricalMagnitudes;
	protected ArrayList<Magnitude> stateVariables;
	protected String title;
	protected String description;
	// Magnitudes
	public final int TIMEINDEX = 0;
	public final int VINDEX = 1;
	public final int IINDEX = 2;
	public final int RINDEX = 3;
	// State Variables
	// public final int XINDEX = 0;
	protected double derivedStateVariableControlRatioDown = 10;
	protected double derivedStateVariableControlRatioUp = 0.1;
	protected double derivedStateVariableAlmostZero = 5e-3;
	private double maxTStepNeeded = -666;
	protected boolean needDecrease;
	protected boolean acceptIncrease;
	protected int amplitudeSignGoingON = 1;
	protected int magnitudeValuesSize;

	/**
	 * Default constructor
	 */
	public MemristorModel(int memristorType) {
		this.memristorType = memristorType;
		System.out.println("Init memristor...");
		parameters = new ArrayList<MemristorParameter>();
		electricalMagnitudes = new ArrayList<Magnitude>();
		stateVariables = new ArrayList<Magnitude>();
		// common magnitudes to all the models
		electricalMagnitudes
				.add(new Magnitude("time", true, TIMEINDEX, 1, "s"));
		electricalMagnitudes.add(new Magnitude("v", true, VINDEX, 1, "V"));
		electricalMagnitudes.add(new Magnitude("i", true, IINDEX, 1e3, "mA"));
		electricalMagnitudes.add(new Magnitude("Rm", true, RINDEX, 1e-3, "k"
				+ ResourcesAndUsefulFunctions.OHM1));
	}

	public int getNormalizedStateIndex() {
		return 0;
	}
	
	public int getMagnitudeValuesSize() {
		return magnitudeValuesSize;
	}

	public Magnitude getElectricalMagnitude(int index) {
		return electricalMagnitudes.get(index);
	}

	public Magnitude getStateVariable(int index) {
		return stateVariables.get(index);
	}

	public MemristorParameter getParameter(int index) {
		return parameters.get(index);
	}

	public void updateBackValuesMagnitudes() {
		for (Magnitude m : this.getElectricalMagnitudes()) {
			m.setTwoBackValue(m.getBackValue());
			m.setBackValue(m.getCurrentValue());

		}
		for (Magnitude m : this.getStateVariables()) {
			m.setTwoBackValue(m.getBackValue());
			m.setBackValue(m.getCurrentValue());
		}
	}
	
	public void updateBackValuesMagnitudes(int i) {
		for (Magnitude m : this.getElectricalMagnitudes()) {
			m.setTwoBackValue(m.getBackValue());
			m.setBackValue(m.getCurrentValue());
			m.setValueInPosition(m.getCurrentValue(), i);
		}
		for (Magnitude m : this.getStateVariables()) {
			m.setTwoBackValue(m.getBackValue());
			m.setBackValue(m.getCurrentValue());
			m.setValueInPosition(m.getCurrentValue(), i);
		}
	}

	public String getTitlesMagnitudesInARow() {
		String row = "";
		for (Magnitude m : this.getElectricalMagnitudes()) {
			if (m.isSaved()) {
				row += "%" + m.getTag() + "\t";
			}
		}
		for (Magnitude m : this.getStateVariables()) {
			if (m.isSaved()) {
				row += "%" + m.getTag() + "\t";
			}
		}
		row += "\n";
		return row;
	}
	
	public String getMagnitudesInARow(int index) {
		String row = "";
		for (Magnitude m : this.getElectricalMagnitudes()) {
			if (m.isSaved()) {
				row += m.getValueInPosition(index) + "\t";
			}
		}
		for (Magnitude m : this.getStateVariables()) {
			if (m.isSaved()) {
				row +=  m.getValueInPosition(index) + "\t";
			}
		}
		row += "\n";
		return row;
	}

	public String getMagnitudesInARow() {
		String row = "";
		for (Magnitude m : this.getElectricalMagnitudes()) {
			if (m.isSaved()) {
				row += m.getCurrentValue() + "\t";
			}
		}
		for (Magnitude m : this.getStateVariables()) {
			if (m.isSaved()) {
				row += m.getCurrentValue() + "\t";
			}
		}
		row += "\n";
		return row;
	}


//	public void saveMagnitudeValueInMemory(int index) {
//		for (Magnitude m : this.getElectricalMagnitudes()) {
//			if (m.isSaved()) {
//				m.setValueInPosition(m.getCurrentValue(), index);
//			}
//		}
//		for (Magnitude m : this.getStateVariables()) {
//			if (m.isSaved()) {
//				m.setValueInPosition(m.getCurrentValue(), index);
//
//			}
//		}
//	}

	protected SimulationResult checkMagnitudes(int stepCounter) {
		SimulationResult result = new SimulationResult(true, stepCounter,
				"Correct Magnitudes.");
		boolean partialCheck = false;
		for (Magnitude m : this.getElectricalMagnitudes()) {
			if (Double.isNaN(m.getCurrentValue())
					|| Double.isInfinite(m.getCurrentValue())) {
				partialCheck = true;
			}

		}
		for (Magnitude m : this.getStateVariables()) {
			if (Double.isNaN(m.getCurrentValue())
					|| Double.isInfinite(m.getCurrentValue())) {
				partialCheck = true;
			}
		}
		if (partialCheck) {
			result = new SimulationResult(false, stepCounter, getCheckProblem());
		}
		return result;
	}

	private String getCheckProblem() {
		String auxString1 = "Electrical magnitudes: ";
		String auxString2 = "\n\nState variables: ";
		for (Magnitude m : this.getElectricalMagnitudes()) {
			auxString1 += "\n" + m.getRecentHistory();
		}
		for (Magnitude m : this.getStateVariables()) {
			auxString2 += "\n" + m.getRecentHistory();
		}
		return auxString1 + auxString2;
	}

	/*
	 * Auxiliar function Performs a time integration of a magnitude
	 */
	public void integrateMagnitude(int magnitudeIndex,
			int derivedMagnitudeIndex, double tinc) {
		Magnitude mag = electricalMagnitudes.get(magnitudeIndex);
		Magnitude derMag = electricalMagnitudes.get(derivedMagnitudeIndex);
		if (electricalMagnitudes.get(derivedMagnitudeIndex).getCurrentValue() != 0) {
			mag.setCurrentValue(mag.getBackValue()
					+ derMag.getCurrentValue()
					* tinc
					- (1 / 2)
					* ((derMag.getCurrentValue() - derMag.getBackValue()) * tinc));
		}
		electricalMagnitudes.set(magnitudeIndex, mag);
		electricalMagnitudes.set(derivedMagnitudeIndex, derMag);
	}

	/*
	 * Auxiliar function Performs a time integration of a state variable
	 */
	public void integrateStateVariable(int magnitudeIndex,
			int derivedMagnitudeIndex, double tinc) {
		Magnitude mag = stateVariables.get(magnitudeIndex);
		Magnitude derMag = stateVariables.get(derivedMagnitudeIndex);
		if (stateVariables.get(derivedMagnitudeIndex).getCurrentValue() != 0) {
			mag.setCurrentValue(mag.getBackValue()
					+ derMag.getCurrentValue()
					* tinc
					- (1 / 2)
					* ((derMag.getCurrentValue() - derMag.getBackValue()) * tinc));
		}
		stateVariables.set(magnitudeIndex, mag);
		stateVariables.set(derivedMagnitudeIndex, derMag);
	}


	public boolean acceptStep() {
		return true;
	}

	 public void setValueInParameter(int parameterIndex, double value) {
		 if(parameterIndex == 9){
			 System.out.println( parameters.get(parameterIndex).getValue());
		 }
		 parameters.get(parameterIndex).setValue(value);		
		 if(parameterIndex == 9){
			 System.out.println( parameters.get(parameterIndex).getValue());
		 }
	 }

	public ArrayList<MemristorParameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<MemristorParameter> parameters) {
		this.parameters = parameters;
	}

	public ArrayList<Magnitude> getElectricalMagnitudes() {
		return electricalMagnitudes;
	}

	public void setElectricalMagnitudes(
			ArrayList<Magnitude> electricalMagnitudes) {
		this.electricalMagnitudes = electricalMagnitudes;
	}

	public ArrayList<Magnitude> getStateVariables() {
		return stateVariables;
	}

	public ArrayList<Magnitude> getStateVariables2Plot() {
		ArrayList<Magnitude> sv2p = new ArrayList<Magnitude>();
		for (Magnitude sv : stateVariables) {
			if (sv.isSaved()) {
				sv2p.add(sv);
			}
		}
		return sv2p;
	}

	public void setStateVariables(ArrayList<Magnitude> stateVariables) {
		this.stateVariables = stateVariables;
	}

	public int getMemristorType() {
		return memristorType;
	}

	public SimulationResult resolveStep(Timing timing, InputVoltage voltage) {
		return null;
	}

	public void setInstance() {

	}

	public void setMemristorType(int memristorType) {
		this.memristorType = memristorType;
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

	public void setMagnitudeValuesSize(int magnitudeValuesSize) {
		this.magnitudeValuesSize = magnitudeValuesSize;
		for (Magnitude m : electricalMagnitudes) {
			if (m.isSaved()) {
				m.setValuesSize(magnitudeValuesSize);
			}
		}
		for (Magnitude m : stateVariables) {
			if (m.isSaved()) {
				m.setValuesSize(magnitudeValuesSize);
			}
		}
	}

	public void setInstanceGoingON(int memristanceLevels) {

	}

	public void setInstanceGoingOFF(int memristanceLevels) {

	}

	public boolean reachedState(int stateCount, int memristanceLevels,
			boolean goingON) {
		return true;
	}

	public boolean reachedON(int memristanceLevels) {
		return true;
	}

	public boolean reachedOFF(int memristanceLevels) {
		return true;
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

	public double getMaxTStepNeeded() {
		return maxTStepNeeded;
	}

	public void setMaxTStepNeeded(double maxTStepNeeded) {
		this.maxTStepNeeded = maxTStepNeeded;
	}

	public int getAmplitudeSignGoingON() {
		return amplitudeSignGoingON;
	}

	public void setAmplitudeSignGoingON(int amplitudeSignGoingON) {
		this.amplitudeSignGoingON = amplitudeSignGoingON;
	}

}
