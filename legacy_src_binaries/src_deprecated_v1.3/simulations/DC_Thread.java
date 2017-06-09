package simulations;

import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import memristorModels.MemristorModel;

import simulationObjects.*;

public class DC_Thread extends Thread {
	private MemristorModel memristor;
	private InputVoltage inputVoltage;
	private Timing timing;
	private String title;
	private int storeRatio;
	private boolean reduceStoredData;
	private String outputFile;
	private SimulationResult simulationResult;
	private volatile boolean interrupted = false;
	private int threadId;
	private int magnitudeValuesSize;
	private boolean storeDirectlyInDisk;

	public DC_Thread(int threadId, MemristorModel memristor,
			InputVoltage inputVoltage, Timing timing, String title,
			boolean storeDirectlyInDisk, int magnitudeValuesSize,
			boolean reduceStoredData, int storeRatio, String outputFile) {
		super();
		this.threadId = threadId;
		this.memristor = memristor;
		this.inputVoltage = inputVoltage;
		this.timing = timing;
		this.title = title;
		this.storeDirectlyInDisk = storeDirectlyInDisk;
		this.magnitudeValuesSize = magnitudeValuesSize;
		this.reduceStoredData = reduceStoredData;
		this.outputFile = outputFile;
		interrupted = true;
		this.storeRatio = storeRatio;
	}

	public DC_Thread() {
		super();
	}

	private SimulationResult checkRequierements() {
		if (memristor == null || inputVoltage == null || timing == null) {
			return new SimulationResult(false, 0,
					"Error checking the requirements: memristor, input voltage or timing is null.");
		} else {
			return new SimulationResult(true, 0, "Correct requierements.");
		}
	}

	public void run() {
		interrupted = false;
		simulationResult = null;
		Writer out = null;
		SimulationResult correctlyComputed = new SimulationResult(true,
				timing.getStepCounter(), "Correctly Computed.");
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
				"Checking requirements for thread " + threadId);
		simulationResult = checkRequierements();
		if (!simulationResult.isCorrect()) {
			return;
		}
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.CONFIG,
				"Setting the system for thread " + threadId);
		try {
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			out.write(memristor.getTitlesMagnitudesInARow());
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		memristor.setMagnitudeValuesSize(magnitudeValuesSize);
		memristor.setInstance();
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
				"Running thread " + threadId);

		boolean store = false; // control variable
		int skipDataCount = 0;
		int internalCount = 1;
		while (!interrupted && timing.hasNextStep()
				&& correctlyComputed.isCorrect()) {
			store = false;
			simulationResult = memristor.resolveStep(timing, inputVoltage);
			// If the step result is not correct, we adequate the already
			// computed data to be plotted and finalize the simulation
			if (!simulationResult.isCorrect()) {
				// if it can be decreased, we decrease it
				if (timing.canDecreaseTStep()) {
					timing.decreaseTStep();
					Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
							Level.WARNING,
							"Bad Result, tStep decreased at t= "
									+ timing.getTimeCounter() + ", tStep: "
									+ timing.getTStep());
				} else { // Else, can not be decreased, or increased, so en
							// simulation
					Logger.getLogger(ResourcesAndUsefulFunctions.loggerName)
							.log(Level.SEVERE,
									"Simulation crashed, time step can not be decreased.");
					timing.setTotalStepsComputed(Math.max(0,
							timing.getStepCounter() - 1));
					correctlyComputed = simulationResult;
				}
			} else { // Correctly simulated
				// If uniform step --> compute and save
				if (timing.isUniformSteps()) {
					if (storeDirectlyInDisk) {
						memristor.updateBackValuesMagnitudes();
					} else {
						memristor.updateBackValuesMagnitudes(internalCount - 1);
					}
					inputVoltage.updateVoltage();
					timing.updateNextStep();
					store = true;
					// If dynamic timing:
				} else {
					if (memristor.acceptStep()) {
						if (storeDirectlyInDisk) {
							memristor.updateBackValuesMagnitudes();
						} else {
							memristor
									.updateBackValuesMagnitudes(internalCount - 1);
						}
						timing.updateNextStep();
						inputVoltage.updateVoltage();
						store = true;
						// can timing be increased?
						if (memristor.isAcceptIncrease()
								&& timing.canIncreaseTStep()) {
							timing.increaseTStep();
							Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
									Level.INFO,
									"tStep increased at t= "
											+ timing.getTimeCounter()
											+ ", tStep: " + timing.getTStep());
						}
					} else {
						if (memristor.isNeedDecrease()
								&& timing.canDecreaseTStep()) {
							timing.decreaseTStep();
							Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
									Level.INFO,
									"tStep decreased at t= "
											+ timing.getTimeCounter()
											+ ", tStep: " + timing.getTStep());

						} else { // Else, can not be decreased, so stored
							if (storeDirectlyInDisk) {
								memristor.updateBackValuesMagnitudes();
							} else {
								memristor
										.updateBackValuesMagnitudes(internalCount - 1);
							}
							timing.updateNextStep();
							inputVoltage.updateVoltage();
							store = true;
						}
					}
				}

				// If the step result is correctly computed,
				// it can be saved or not (time step increased or decreased)
				if (store) {
					// update the min tstep if needed
					if (timing.getTStep() <= memristor.getMaxTStepNeeded()) {
						memristor.setMaxTStepNeeded(timing.getTStep());
					}
					if (!reduceStoredData || skipDataCount == storeRatio) {
						// store in hard disk
						if (storeDirectlyInDisk) {
							try {
								out.write(memristor.getMagnitudesInARow());
//								out.flush();
							} catch (IOException e) {
								e.printStackTrace(System.err);
								correctlyComputed = new SimulationResult(false,
										timing.getStepCounter(), "I/O error: " + e.getLocalizedMessage());
							}
						}
						// store in memory
						else {
							if (internalCount == memristor
									.getMagnitudeValuesSize()) {
								try {
									for (int i = 0; i < memristor
											.getMagnitudeValuesSize(); i++) {
										out.write(memristor
												.getMagnitudesInARow(i));
									}
								} catch (IOException e) {
									correctlyComputed = new SimulationResult(false,
											timing.getStepCounter(), "I/O error: " + e.getLocalizedMessage());
								}
								internalCount = 1;
							} else {
								internalCount++;
							}
						}
						skipDataCount = 0;
					}
					skipDataCount++;
				}
			}
		}
		if (!storeDirectlyInDisk) {
			try {
				for (int i = 0; i < internalCount - 1; i++) {
					out.write(memristor.getMagnitudesInARow(i));
				}
			} catch (IOException e) {
				correctlyComputed = new SimulationResult(false,
						timing.getStepCounter(), "I/O error: " + e.getLocalizedMessage());
			}
		} else {
			try {
				out.write(memristor.getMagnitudesInARow());
			} catch (IOException e) {
				correctlyComputed = new SimulationResult(false,
						timing.getStepCounter(), "I/O error: " + e.getLocalizedMessage());
			}
		}
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			correctlyComputed = new SimulationResult(false,
					timing.getStepCounter(), "I/O error: " + e.getLocalizedMessage());
		}
		if (correctlyComputed.isCorrect()) {
			timing.setTotalStepsComputed(timing.getStepCounter());
		} else {
			simulationResult = correctlyComputed;
		}
		if (!interrupted) {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
					"Simulation " + threadId + " done.");
		} else {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
					"Simulation Interrupted");
		}
	}

	public MemristorModel getMemristor() {
		return memristor;
	}

	public void setMemristor(MemristorModel memristor) {
		this.memristor = memristor;
	}

	public InputVoltage getInputVoltage() {
		return inputVoltage;
	}

	public void setInputVoltage(InputVoltage inputVoltage) {
		this.inputVoltage = inputVoltage;
	}

	public Timing getTiming() {
		return timing;
	}

	public void setTiming(Timing timing) {
		this.timing = timing;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SimulationResult getSimulationResult() {
		return simulationResult;
	}

	public void setSimulationResult(SimulationResult simulationResult) {
		this.simulationResult = simulationResult;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public boolean isStoreDirectlyInDisk() {
		return storeDirectlyInDisk;
	}

	public void setStoreDirectlyInDisk(boolean storeDirectlyInDisk) {
		this.storeDirectlyInDisk = storeDirectlyInDisk;
	}

}
