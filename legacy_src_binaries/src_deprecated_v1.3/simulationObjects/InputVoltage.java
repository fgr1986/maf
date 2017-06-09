package simulationObjects;

import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author fernando garcía fgarcia@die.upm.es
 */
public class InputVoltage {

	// private InputVoltageParameter voltage;
	private int voltageType; // 0 sinusoidal 1 pulse 2 triangle 3 const, 4
								// VCONSTANTRANGE
	private Timing timing; // timing control
	public final int CONSTRANGEVMININDEX = 0;
	public final int CONSTRANGEVMAXINDEX = 1;
	public final int CONSTRANGEVSTEPINDEX = 2;
	public final int TRIANGLETUPINDEX = 0; // time triangle up (s)
	public final int TRIANGLETDOWNINDEX = 1; // time triangle down (s)
	public final int TRIANGLEAMPINDEX = 2;
	public final int TRIANGLEOFFSETINDEX = 3;
	public final int CONSTVINDEX = 0;
	public final int CONSTTUPINDEX = 1;
	public final int PULSETDELAYINDEX = 0; // time pulse delay (s)
	public final int PULSETSLOPEINDEX = 1; // time pulse delay (s)
	public final int PULSETV1INDEX = 2; // time V1
	public final int PULSETV2INDEX = 3; // time V2 (s)
	public final int PULSEV1INDEX = 4;
	public final int PULSEV2INDEX = 5;
	public final int SINEFREQINDEX = 0;
	public final int SINEAMPINDEX = 1;
	public final int SINEOFFSETINDEX = 2;
	private ArrayList<InputVoltageParameter> parameters;
	private double currentVoltage;
	private double backVoltage;
	private int auxTState;
	private double tAux;
	private double auxSlope;
	private double auxSlopeUp;
	private double auxSlopeDown;

	public InputVoltage(InputVoltage ivOr, Timing tOr) {
		parameters = new ArrayList<InputVoltageParameter>();
		this.timing = tOr;
		for(InputVoltageParameter ivpor : ivOr.getParameters()){
			parameters.add(new InputVoltageParameter(ivpor.getId(), ivpor.getTag(), ivpor.getValue(),
					ivpor.isAllowVariations(), ivpor.getSigma(), ivpor.getMaxVariation()));
		}
		switch (ivOr.getVoltageType()) {
		case ResourcesAndUsefulFunctions.VSINE:
			this.currentVoltage = 0;
			this.backVoltage = ivOr.getParameter(SINEOFFSETINDEX).getValue();
			voltageType = ResourcesAndUsefulFunctions.VSINE;
			break;
		case ResourcesAndUsefulFunctions.VPULSE:
			this.currentVoltage = 0;
			this.backVoltage = 0;
			voltageType = ResourcesAndUsefulFunctions.VPULSE;
			auxSlope = 0;
			if (getParameter(PULSETSLOPEINDEX).getValue() > 0) {
				auxSlope = (getParameter(PULSEV1INDEX).getValue() - getParameter(
						PULSEV2INDEX).getValue())
						/ (getParameter(PULSETSLOPEINDEX).getValue());
			} 
			// 0 uploading, 1 up , 2 downloading 3 down, 4  starting (half uploading)
			auxTState = 4;
			tAux = 0;
			break;
		case ResourcesAndUsefulFunctions.VTRIANGLE:
			this.currentVoltage = 0;
			this.backVoltage = this.getParameter(TRIANGLEOFFSETINDEX)
					.getValue();
			auxTState = 2; // 0 uploading, 1 down, 2 half up (init)
			auxSlopeUp = 2 * getParameter(TRIANGLEAMPINDEX).getValue()
					/ (getParameter(TRIANGLETUPINDEX).getValue());
			auxSlopeDown = -2 * getParameter(TRIANGLEAMPINDEX).getValue()
					/ (getParameter(TRIANGLETDOWNINDEX).getValue());
			voltageType = ResourcesAndUsefulFunctions.VTRIANGLE;
			break;
		case ResourcesAndUsefulFunctions.VCONSTANT:
			this.currentVoltage = 0;
			this.backVoltage = 0;
			this.auxSlope = getParameter(CONSTVINDEX).getValue()
					/ getParameter(CONSTTUPINDEX).getValue();
			voltageType = ResourcesAndUsefulFunctions.VCONSTANT;
			break;
		case ResourcesAndUsefulFunctions.VCONSTANTRANGE:
			this.currentVoltage = 0;
			this.backVoltage = 0;
			voltageType = ResourcesAndUsefulFunctions.VCONSTANTRANGE;
			break;
		default:
			break;
		}
	}

	public InputVoltage(InputVoltageParameter constRangeVMin,
			InputVoltageParameter constRangeVMax,
			InputVoltageParameter constRangeVStep) {
		super();
		parameters = new ArrayList<InputVoltageParameter>();
		this.currentVoltage = 0;
		this.backVoltage = 0;
		parameters.add(constRangeVMin);
		parameters.add(constRangeVMax);
		parameters.add(constRangeVStep);
		voltageType = ResourcesAndUsefulFunctions.VCONSTANTRANGE;
	}

	public InputVoltage(Timing timing, InputVoltageParameter constV,
			InputVoltageParameter constVTUp) {
		super();
		parameters = new ArrayList<InputVoltageParameter>();
		this.timing = timing;
		parameters.add(constV);
		parameters.add(constVTUp);
		this.currentVoltage = 0;
		this.backVoltage = 0;
		this.auxSlope = getParameter(CONSTVINDEX).getValue()
				/ getParameter(CONSTTUPINDEX).getValue();
		voltageType = ResourcesAndUsefulFunctions.VCONSTANT;
	}

	public InputVoltage(Timing timing, InputVoltageParameter sineFreq,
			InputVoltageParameter sineAmp, InputVoltageParameter sineOffset) {
		super();
		parameters = new ArrayList<InputVoltageParameter>();
		this.timing = timing;
		parameters.add(sineFreq);
		parameters.add(sineAmp);
		parameters.add(sineOffset);
		this.currentVoltage = 0;
		this.backVoltage = getParameter(SINEOFFSETINDEX).getValue();
		voltageType = ResourcesAndUsefulFunctions.VSINE;
	}

	public InputVoltage(Timing timing, InputVoltageParameter pulseTDelay,
			InputVoltageParameter pulseTSlope, InputVoltageParameter pulseTV1,
			InputVoltageParameter pulseTV2, InputVoltageParameter pulseV1,
			InputVoltageParameter pulseV2) {
		super();
		parameters = new ArrayList<InputVoltageParameter>();
		this.timing = timing;
		parameters.add(pulseTDelay);
		parameters.add(pulseTSlope);
		parameters.add(pulseTV1);
		parameters.add(pulseTV2);
		parameters.add(pulseV1);
		parameters.add(pulseV2);
		this.currentVoltage = 0;
		this.backVoltage = 0;
		voltageType = ResourcesAndUsefulFunctions.VPULSE;
		auxSlope = 0;
		if (getParameter(PULSETSLOPEINDEX).getValue() > 0) {
			auxSlope = (getParameter(PULSEV1INDEX).getValue() - getParameter(
					PULSEV2INDEX).getValue())
					/ (getParameter(PULSETSLOPEINDEX).getValue());
		}
		// 0 uploading, 1 up , 2 downloading 3 down, 4 starting (half uploading)
		auxTState = 4;
		tAux = 0;
	}

	public InputVoltage(Timing timing, InputVoltageParameter triangleTUp,
			InputVoltageParameter triangleTDown,
			InputVoltageParameter triangleAmp,
			InputVoltageParameter triangleOffset) {
		super();
		parameters = new ArrayList<InputVoltageParameter>();
		this.timing = timing;
		parameters.add(triangleTUp);
		parameters.add(triangleTDown);
		parameters.add(triangleAmp);
		parameters.add(triangleOffset);
		this.currentVoltage = 0;
		this.backVoltage = triangleOffset.getValue();
		auxTState = 2; // 0 uploading, 1 down, 2 half up (init)
		auxSlopeUp = 2 * triangleAmp.getValue()
				/ (getParameter(TRIANGLETUPINDEX).getValue());
		auxSlopeDown = -2 * triangleAmp.getValue()
				/ (getParameter(TRIANGLETDOWNINDEX).getValue());
		voltageType = ResourcesAndUsefulFunctions.VTRIANGLE;
	}

	public void createVDC() {
		if (timing.getTimeCounter() < getParameter(CONSTTUPINDEX).getValue()) {
			currentVoltage = timing.getTimeCounter() * auxSlope;
		} else {
			currentVoltage = getParameter(CONSTVINDEX).getValue();
		}
	}

	public void createSine() {
		currentVoltage = getParameter(SINEAMPINDEX).getValue()
				* Math.sin(2 * Math.PI * getParameter(SINEFREQINDEX).getValue()
						* timing.getTimeCounter())
				+ getParameter(SINEOFFSETINDEX).getValue();
	}

	public void createPulse() {
		if (timing.getTimeCounter() <= getParameter(PULSETDELAYINDEX)
				.getValue()) {
			currentVoltage = 0;
		}
		// 0 uploading, 1 up , 2 downloading 3 down, 4 starting (half uploading)
		switch (auxTState) {
		case 0:
			currentVoltage = backVoltage + timing.getTStep() * auxSlope;
			break;
		case 1:
			currentVoltage = getParameter(PULSEV1INDEX).getValue();
			break;
		case 2:
			currentVoltage = backVoltage - timing.getTStep() * auxSlope;
			break;
		case 3:
			currentVoltage = getParameter(PULSEV2INDEX).getValue();
			break;
		case 4:
			currentVoltage = backVoltage + timing.getTStep() * auxSlope;
			break;
		default:
			break;
		}

	}

	public void createTriangle() {
		switch (auxTState) {
		case 0:
			currentVoltage = backVoltage + timing.getTStep() * auxSlopeUp;
			break;
		case 1:
			currentVoltage = backVoltage + timing.getTStep() * auxSlopeDown;
			break;
		case 2:
			currentVoltage = backVoltage + timing.getTStep() * auxSlopeUp;
			break;
		default:
			break;
		}

	}

	public double getCurrentVoltage() {
		switch (voltageType) {
		case ResourcesAndUsefulFunctions.VSINE:
			createSine();
			break;
		case ResourcesAndUsefulFunctions.VPULSE:
			createPulse();
			break;
		case ResourcesAndUsefulFunctions.VTRIANGLE:
			createTriangle();
			break;
		case ResourcesAndUsefulFunctions.VCONSTANT:
			createVDC();
			break;
		default:
			break;
		}
		return currentVoltage;
	}

	public int getVoltageType() {
		return voltageType;
	}

	public void updateVoltage() {
		this.backVoltage = currentVoltage;
		tAux += timing.getTStep();
		if (voltageType == ResourcesAndUsefulFunctions.VPULSE) {
			switch (auxTState) {
			case 0:
				if (tAux >= getParameter(PULSETSLOPEINDEX).getValue()) {
					auxTState = 1;
					tAux = 0;
				}
				break;
			case 1:
				if (tAux >= getParameter(PULSETV1INDEX).getValue()) {
					auxTState = 2;
					tAux = 0;
				}
				break;
			case 2:
				if (tAux >= getParameter(PULSETSLOPEINDEX).getValue()) {
					auxTState = 3;
					tAux = 0;
				}
				break;
			case 3:
				if (tAux >= getParameter(PULSETV2INDEX).getValue()) {
					auxTState = 0;
					tAux = 0;
				}
				break;
			case 4:
				if (tAux >= getParameter(PULSETSLOPEINDEX).getValue() / 2) {
					auxTState = 1;
					tAux = 0;
				}
				break;
			default:
				break;
			}
		} else if (voltageType == ResourcesAndUsefulFunctions.VTRIANGLE) {
			switch (auxTState) {
			case 0:
				if (tAux >= getParameter(TRIANGLETUPINDEX).getValue()) {
					auxTState = 1;
					tAux = 0;
				}
				break;
			case 1:
				if (tAux >= getParameter(TRIANGLETDOWNINDEX).getValue()) {
					auxTState = 0;
					tAux = 0;
					// vAux = -triangleAmp;
				}
				break;
			case 2:
				if (tAux >= getParameter(TRIANGLETUPINDEX).getValue() / 2) {
					auxTState = 1;
					tAux = 0;
					// vAux = triangleAmp;
				}
				break;
			default:
				break;
			}
		}

	}


	public InputVoltageParameter getParameter(int index) {
		return parameters.get(index);
	}

	public int getAuxTState() {
		return auxTState;
	}

	public void setAuxTState(int auxTState) {
		this.auxTState = auxTState;
	}

	public double gettAux() {
		return tAux;
	}

	public void settAux(double tAux) {
		this.tAux = tAux;
	}

	public double getAuxSlope() {
		return auxSlope;
	}

	public void setAuxSlope(double auxSlope) {
		this.auxSlope = auxSlope;
	}

	public double getAuxSlopeUp() {
		return auxSlopeUp;
	}

	public void setAuxSlopeUp(double auxSlopeUp) {
		this.auxSlopeUp = auxSlopeUp;
	}

	public double getAuxSlopeDown() {
		return auxSlopeDown;
	}

	public void setAuxSlopeDown(double auxSlopeDown) {
		this.auxSlopeDown = auxSlopeDown;
	}

	public ArrayList<InputVoltageParameter> getInputVoltageParameters() {
		return parameters;
	}

	public void setInputVoltageParameters(
			ArrayList<InputVoltageParameter> parameters) {
		this.parameters = parameters;
	}

	public void setVoltageType(int voltageType) {
		this.voltageType = voltageType;
	}

	public Timing getTiming() {
		return timing;
	}

	public void setTiming(Timing timing) {
		this.timing = timing;
	}

	public void setCurrentVoltage(double currentVoltage) {
		this.currentVoltage = currentVoltage;
	}

	public double getBackVoltage() {
		return backVoltage;
	}

	public void setBackVoltage(double backVoltage) {
		this.backVoltage = backVoltage;
	}

	public ArrayList<InputVoltageParameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<InputVoltageParameter> parameters) {
		this.parameters = parameters;
	}

}
