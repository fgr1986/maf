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
 * @author Fernando García Redondo, fernando.garca@gmaigetStateVariable(LINDEX).com
 * @author License: use, copy or modify, but contact the author
 */
public class MemristorMIMThreshold extends MemristorModel {

	private double defaultA1 = 9e-8;
	private double defaultA2 = 0.01e-8;
	private double defaultP = 5;
	private double defaultB1 = 2;
	private double defaultB2 = 4;
	private double defaultFon = 40e-3;
	private double defaultFoff = 40e-3;
	private double defaultSfo = 4;
	private double defaultSfm = 20;
	private double defaultWmin = 0.05;
	private double defaultWmax = 0.95;
	private double defaultP0 = 1.2;
	private double defaultN = 4;
	private double defaultWInit = 0.5;
	private double defaultC =8e-5;
	
	private double defaultA1Sigma = 0.1e-3;
	private double defaultA2Sigma = 0.001e-3;
	private double defaultPSigma = 1;
	private double defaultB1Sigma = 0.1;
	private double defaultB2Sigma = 0.2;
	private double defaultFonSigma = 4e-4;
	private double defaultFoffSigma = 4e-4;
	private double defaultSfoSigma = 0.1;
	private double defaultSfmSigma = 1;
	private double defaultWminSigma = 0.001;
	private double defaultWmaxSigma = 0.001;
//	private double defaultLSigma = 1e-10;
	private double defaultP0Sigma = 0.01;
	private double defaultNSigma = 0;
	private double defaultWInitSigma = 0.05;
	private double defaultCSigma = 0;
	public final int A1INDEX=0;
	public final int A2INDEX=1;
	public final int B1INDEX=2;
	public final int B2INDEX=3;
	public final int PINDEX=4;
	public final int FOFFINDEX=5;
	public final int FONINDEX=6;
	public final int SFOINDEX=7;
	public final int SFMINDEX=8;
	public final int WMININDEX=9;
	public final int WMAXINDEX=10;
	public final int P0INDEX=11;
	public final int NINDEX=12;
	public final int WINITINDEX=13;
	public final int CINDEX=14;
//	public final int LINDEX=11;
	private double constMinI = 1e-12;
	private double constMinV = 1e-9;

	private int WINDEX = 0;
	private int WDOTINDEX = 1;
//	private double integrationConstant = 1;//1/(8e-5);//12500;
	private double p2;
	private double c_1;
	private double wmax_wmin;
	private double[] new_levels;

	/**
	 * Default constructor
	 */
	public MemristorMIMThreshold() {
		super(ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL);
		title = ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL];
		description =  ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL];

		stateVariables.add(new Magnitude("w", true,
				WINDEX, 1, ""));
		stateVariables.add(new Magnitude("wDot", true,
				WDOTINDEX, 1, ""));
		parameters.add(new MemristorParameter(A1INDEX, "a1", defaultA1, true,
				defaultA1Sigma));
		parameters.add(new MemristorParameter(A2INDEX, "a2", defaultA2, true,
				 defaultA2Sigma));
		parameters.add(new MemristorParameter(B1INDEX, "b1", defaultB1, true,
				 defaultB1Sigma));
		parameters.add(new MemristorParameter(B2INDEX, "b2", defaultB2, true,
				 defaultB2Sigma));
		parameters.add(new MemristorParameter(PINDEX, "p", defaultP, true,
				 defaultPSigma));
		parameters.add(new MemristorParameter(FOFFINDEX, "f_off", defaultFon,
				true,  defaultFonSigma));
		parameters.add(new MemristorParameter(FONINDEX, "f_on", defaultFoff,
				true,  defaultFoffSigma));
		parameters.add(new MemristorParameter(SFOINDEX, "sfo", defaultSfo,
				true,  defaultSfoSigma));
		parameters.add(new MemristorParameter(SFMINDEX, "sfm", defaultSfm,
				true,  defaultSfmSigma));
		parameters.add( new MemristorParameter(WMININDEX, "w_min", defaultWmin, true,
				 defaultWminSigma));
		parameters.add( new MemristorParameter(WMAXINDEX, "w_max", defaultWmax, true,
				 defaultWmaxSigma));
		parameters.add( new MemristorParameter(P0INDEX, "p0", defaultP0, true, 
				defaultP0Sigma));
		parameters.add( new MemristorParameter(NINDEX, "n", defaultN, true, 
				defaultNSigma));
		parameters.add( new MemristorParameter(WINITINDEX,"w_init", defaultWInit, true,
				 defaultWInitSigma));
		parameters.add( new MemristorParameter(CINDEX,"c (scale f_i)", defaultC, true,
				 defaultCSigma));
		
		derivedStateVariableControlRatioDown = 5;
		derivedStateVariableControlRatioUp = 0.1;
		System.out.println("Selecting the model: MIM Threshold memristor.");
	}

	@Override
	public void setInstance() {
		// util constants
		wmax_wmin = getParameter(WMAXINDEX).getValue() -  getParameter(WMININDEX).getValue();
		c_1 = 1/getParameter(CINDEX).getValue();
		p2 = (int) (2 * getParameter(PINDEX).getValue());
		getStateVariable(WINDEX).setCurrentValue(getParameter(WINITINDEX).getValue());
		getStateVariable(WDOTINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public SimulationResult resolveStep(Timing timing, InputVoltage inputVoltage) {
		acceptIncrease = false;
		needDecrease = false;
		double ro = 0;
		double g = 0;
		try {
			getElectricalMagnitude(VINDEX).setCurrentValue(inputVoltage.getCurrentVoltage());
			ro = sf(getStateVariable(WINDEX).getBackValue());
			g = g(getElectricalMagnitude(VINDEX).getCurrentValue(), getStateVariable(WINDEX).getBackValue(), ro);
			getStateVariable(WDOTINDEX).setCurrentValue(g); // * integrationConstant);
			integrateStateVariable(WINDEX, WDOTINDEX, timing.getTStep());
			if (getStateVariable(WINDEX).getCurrentValue() > getParameter(WMAXINDEX).getValue()) {
				getStateVariable(WINDEX).setCurrentValue(getParameter(WMAXINDEX).getValue());
			}
			if (getStateVariable(WINDEX).getCurrentValue() < getParameter(WMININDEX).getValue()) {
				getStateVariable(WINDEX).setCurrentValue(getParameter(WMININDEX).getValue());
			}
			getElectricalMagnitude(IINDEX).setCurrentValue(//getParameter(LINDEX).getValue() *
					Math.pow(getStateVariable(WINDEX).getCurrentValue(), getParameter(NINDEX).getValue())
					* getParameter(A1INDEX).getValue()
					* Math.sinh(getParameter(B1INDEX).getValue() * getElectricalMagnitude(VINDEX).getCurrentValue())
					+ getParameter(A2INDEX).getValue()
					* (Math.exp(getParameter(B2INDEX).getValue() * getElectricalMagnitude(VINDEX).getCurrentValue()) - 1));

			if (Math.abs(getElectricalMagnitude(IINDEX).getCurrentValue()) > constMinI
					&& Math.abs(inputVoltage.getCurrentVoltage()) > constMinV) {
				getElectricalMagnitude(RINDEX).setCurrentValue(Math.abs(inputVoltage.getCurrentVoltage()
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

	private double g(double vApplied, double wBack, double ro) {
		if (vApplied > 0 && wBack <= getParameter(WMAXINDEX).getValue()) {
			double auxV1 = 1 - vApplied / (2 * getParameter(P0INDEX).getValue());
			return c_1 * getParameter(FONINDEX).getValue() * auxV1
					* Math.exp(ro * getParameter(P0INDEX).getValue() * (1 - Math.sqrt(auxV1)));

		} else if (vApplied < 0 && wBack >= getParameter(WMININDEX).getValue()) {
			double auxV2 = 1 + vApplied / (2 * getParameter(P0INDEX).getValue());
			return -c_1 * getParameter(FOFFINDEX).getValue() * auxV2
					* Math.exp(ro * getParameter(P0INDEX).getValue() * (1 - Math.sqrt(auxV2)));
		} else {
			return 0;
		}
	}

	private double sf(double wVal) {
		return getParameter(SFOINDEX).getValue() + getParameter(SFMINDEX).getValue()
				* (1 - Math.pow((2 * wVal - 1), p2));
	}

	@Override
	public boolean acceptStep() {
		needDecrease = (Math.abs(getStateVariable(WDOTINDEX).getBackValue()) > this.derivedStateVariableAlmostZero && Math
				.abs(getStateVariable(WDOTINDEX).getCurrentValue()) > derivedStateVariableControlRatioDown
				* Math.abs(getStateVariable(WDOTINDEX).getBackValue()));
		if (!needDecrease) {
			acceptIncrease = (Math.abs(getStateVariable(WDOTINDEX).getCurrentValue()) > this.derivedStateVariableAlmostZero && Math
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
		//from 0 to 1 (w)
		// ideal 1->0;
		// real 0.8->0.2
		// i = r*wmas_wmin + wmin
		p2 = (int) (2 * getParameter(PINDEX).getValue());
		c_1 = 1/getParameter(CINDEX).getValue();
		wmax_wmin = getParameter(WMAXINDEX).getValue() -  getParameter(WMININDEX).getValue();
		getStateVariable(WINDEX).setCurrentValue((0.5 / memristanceLevels)*wmax_wmin + getParameter(WMININDEX).getValue());
		new_levels = new double[memristanceLevels];
		for(int i=0;i<memristanceLevels;i++){
			new_levels[i] = ((i + 0.5)/memristanceLevels)* wmax_wmin + getParameter(WMININDEX).getValue();
		}
		getStateVariable(WDOTINDEX).setCurrentValue(0);
//		getElectricalMagnitude(RINDEX).setCurrentValue(getParameter(P0INDEX).getValue());
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public void setInstanceGoingOFF(int memristanceLevels) {
		//
		p2 = (int) (2 * getParameter(PINDEX).getValue());
		c_1 = 1/getParameter(CINDEX).getValue();
		wmax_wmin = getParameter(WMAXINDEX).getValue() -  getParameter(WMININDEX).getValue();
		getStateVariable(WINDEX).setCurrentValue(((memristanceLevels - 0.5) / memristanceLevels)*wmax_wmin + getParameter(WMININDEX).getValue());
		new_levels = new double[memristanceLevels];
		for(int i=0;i<memristanceLevels;i++){
			new_levels[i] = ((memristanceLevels -i - 0.5)/memristanceLevels)* wmax_wmin + getParameter(WMININDEX).getValue();
		}
		getStateVariable(WDOTINDEX).setCurrentValue(0);
//		getElectricalMagnitude(RINDEX).setCurrentValue(getParameter(P0INDEX).getValue());
		getElectricalMagnitude(IINDEX).setCurrentValue(0);
		getElectricalMagnitude(VINDEX).setCurrentValue(0);
		updateBackValuesMagnitudes();
	}

	@Override
	public boolean reachedON(int memristanceLevels) {
		return getStateVariable(WINDEX).getCurrentValue() >= new_levels[memristanceLevels-1];
	}

	@Override
	public boolean reachedOFF(int memristanceLevels) {
		return getStateVariable(WINDEX).getCurrentValue() <=  new_levels[memristanceLevels-1];
	}

	@Override
	public boolean reachedState(int stateCount, int memristanceLevels,
			boolean goingON) {
		// ON-> w = 1; OFF -> w = 0
		if(stateCount == 0){
			return true;
		}
		if (goingON) {
			return getStateVariable(WINDEX).getCurrentValue() >=  new_levels[stateCount-1];
		} else {
			if(getStateVariable(WINDEX).getCurrentValue() <=  new_levels[stateCount-1]){
				return true;
			}else{
				return false;
			}
		}
	}

	@Override
	public int getNormalizedStateIndex() {
		return WINDEX;
	}

	public double getDefaultA1() {
		return defaultA1;
	}

	public void setDefaultA1(double defaultA1) {
		this.defaultA1 = defaultA1;
	}

	public double getDefaultA2() {
		return defaultA2;
	}

	public void setDefaultA2(double defaultA2) {
		this.defaultA2 = defaultA2;
	}

	public double getDefaultP() {
		return defaultP;
	}

	public void setDefaultP(double defaultP) {
		this.defaultP = defaultP;
	}

	public double getDefaultB1() {
		return defaultB1;
	}

	public void setDefaultB1(double defaultB1) {
		this.defaultB1 = defaultB1;
	}

	public double getDefaultB2() {
		return defaultB2;
	}

	public void setDefaultB2(double defaultB2) {
		this.defaultB2 = defaultB2;
	}

	public double getDefaultFon() {
		return defaultFon;
	}

	public void setDefaultFon(double defaultFon) {
		this.defaultFon = defaultFon;
	}

	public double getDefaultFoff() {
		return defaultFoff;
	}

	public void setDefaultFoff(double defaultFoff) {
		this.defaultFoff = defaultFoff;
	}

	public double getDefaultSfo() {
		return defaultSfo;
	}

	public void setDefaultSfo(double defaultSfo) {
		this.defaultSfo = defaultSfo;
	}

	public double getDefaultSfm() {
		return defaultSfm;
	}

	public void setDefaultSfm(double defaultSfm) {
		this.defaultSfm = defaultSfm;
	}

	public double getDefaultWmin() {
		return defaultWmin;
	}

	public void setDefaultWmin(double defaultWmin) {
		this.defaultWmin = defaultWmin;
	}

	public double getDefaultWmax() {
		return defaultWmax;
	}

	public void setDefaultWmax(double defaultWmax) {
		this.defaultWmax = defaultWmax;
	}

//	public double getDefaultL() {
//		return defaultL;
//	}
//
//	public void setDefaultL(double defaultL) {
//		this.defaultL = defaultL;
//	}

	public double getDefaultP0() {
		return defaultP0;
	}

	public void setDefaultP0(double defaultP0) {
		this.defaultP0 = defaultP0;
	}

	public double getDefaultN() {
		return defaultN;
	}

	public void setDefaultN(double defaultN) {
		this.defaultN = defaultN;
	}


	public double getDefaultA1Sigma() {
		return defaultA1Sigma;
	}

	public void setDefaultA1Sigma(double defaultA1Sigma) {
		this.defaultA1Sigma = defaultA1Sigma;
	}

	public double getDefaultA2Sigma() {
		return defaultA2Sigma;
	}

	public void setDefaultA2Sigma(double defaultA2Sigma) {
		this.defaultA2Sigma = defaultA2Sigma;
	}

	public double getDefaultPSigma() {
		return defaultPSigma;
	}

	public void setDefaultPSigma(double defaultPSigma) {
		this.defaultPSigma = defaultPSigma;
	}

	public double getDefaultB1Sigma() {
		return defaultB1Sigma;
	}

	public void setDefaultB1Sigma(double defaultB1Sigma) {
		this.defaultB1Sigma = defaultB1Sigma;
	}

	public double getDefaultB2Sigma() {
		return defaultB2Sigma;
	}

	public void setDefaultB2Sigma(double defaultB2Sigma) {
		this.defaultB2Sigma = defaultB2Sigma;
	}

	public double getDefaultFonSigma() {
		return defaultFonSigma;
	}

	public void setDefaultFonSigma(double defaultFonSigma) {
		this.defaultFonSigma = defaultFonSigma;
	}

	public double getDefaultFoffSigma() {
		return defaultFoffSigma;
	}

	public void setDefaultFoffSigma(double defaultFoffSigma) {
		this.defaultFoffSigma = defaultFoffSigma;
	}

	public double getDefaultSfoSigma() {
		return defaultSfoSigma;
	}

	public void setDefaultSfoSigma(double defaultSfoSigma) {
		this.defaultSfoSigma = defaultSfoSigma;
	}

	public double getDefaultSfmSigma() {
		return defaultSfmSigma;
	}

	public void setDefaultSfmSigma(double defaultSfmSigma) {
		this.defaultSfmSigma = defaultSfmSigma;
	}

	public double getDefaultWminSigma() {
		return defaultWminSigma;
	}

	public void setDefaultWminSigma(double defaultWminSigma) {
		this.defaultWminSigma = defaultWminSigma;
	}

	public double getDefaultWmaxSigma() {
		return defaultWmaxSigma;
	}

	public void setDefaultWmaxSigma(double defaultWmaxSigma) {
		this.defaultWmaxSigma = defaultWmaxSigma;
	}

	public double getDefaultP0Sigma() {
		return defaultP0Sigma;
	}

	public void setDefaultP0Sigma(double defaultP0Sigma) {
		this.defaultP0Sigma = defaultP0Sigma;
	}

	public double getDefaultNSigma() {
		return defaultNSigma;
	}

	public void setDefaultNSigma(double defaultNSigma) {
		this.defaultNSigma = defaultNSigma;
	}

	public double getDefaultWInit() {
		return defaultWInit;
	}

	public void setDefaultWInit(double defaultWInit) {
		this.defaultWInit = defaultWInit;
	}

	public double getDefaultWInitSigma() {
		return defaultWInitSigma;
	}

	public void setDefaultWInitSigma(double defaultWInitSigma) {
		this.defaultWInitSigma = defaultWInitSigma;
	}

}
