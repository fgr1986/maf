package es.upm.die.vlsi.memristor.simulations;

import es.upm.die.vlsi.memristor.resources.FilesFoldersManagement;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltageParameter;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorParameter;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class DCThread extends Thread {

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
    private int magnitudeValueBufferSize;
    private int exportedDataSize;

    public DCThread(int threadId, MemristorModel memristor,
            InputVoltage inputVoltage, Timing timing, String title, int magnitudeValueBufferSize,
            boolean reduceStoredData, int storeRatio, String outputFile) {
        super();
        this.threadId = threadId;
        this.memristor = memristor;
        this.inputVoltage = inputVoltage;
        this.timing = timing;
        this.title = title;
        this.magnitudeValueBufferSize = magnitudeValueBufferSize;
        this.reduceStoredData = reduceStoredData;
        this.outputFile = outputFile;
        interrupted = true;
        this.storeRatio = storeRatio;
        this.exportedDataSize = 0;
    }

    public DCThread() {
        super();
    }

    private SimulationResult checkRequierements() {
        if (memristor == null || inputVoltage == null || timing == null) {
            return new SimulationResult(SimulationResult.states.BAD_SET_UP, 0, "Error checking the requirements: memristor, input voltage or timing is null.");
        } else {
            return new SimulationResult(SimulationResult.states.CORRECT, 0, "Correct requierements.");
        }
    }

    @Override
    public void run() {
        interrupted = false;
        simulationResult = null;
        // 1024 multiple
        int bufferWriterSize = 2 * magnitudeValueBufferSize * memristor.getExportedMagnitudesNumber();
        SimulationResult correctlyComputed;
        boolean couldNotDecreaseStep = false;
        Writer writer = null;
        try {
            // init
            writer = new BufferedWriter(new FileWriter(outputFile), bufferWriterSize);
            correctlyComputed = new SimulationResult(SimulationResult.states.CORRECT, timing.getStepCounter(), "Correctly Computed.");
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINEST, "Checking requirements for thread {0}", threadId);
            simulationResult = checkRequierements();
            if (!simulationResult.allCorrect()) {
                return;
            }
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINEST, "Setting the system for thread {0}", threadId);
            memristor.setMagnitudeValuesSize(magnitudeValueBufferSize);
            memristor.setInstance();
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINER, "Running thread {0}", threadId);

            boolean store; // control variable
            // initializing to storeRatio we save the first step
            int skipDataCount = storeRatio;
            int internalCount = 1;
            // recovery control
            long computedStepsSinceRecovery = 0;

            writer.write(memristor.getTitlesMagnitudesInARow());
            while (!interrupted && timing.hasNextStep() && correctlyComputed.allCorrect()) {
                store = false;
                simulationResult = memristor.resolveStep(timing, inputVoltage);
                // If the step result is not correct, we adequate the already
                // computed data to be plotted and finalize the simulation
                if (!simulationResult.allCorrect()) {
                    // fgarcia
                    // try to recover
                    if (computedStepsSinceRecovery > ResourcesMAF.MAGNITUDERECOVERYSTEPS) {
                        computedStepsSinceRecovery = 0;
                        timing.recoverTempState();
                        inputVoltage.recoverTempState();
                        memristor.recoverTempState();
                        // force min tstep
                        timing.setMinTStep();
                    } else {
                        // if tStep can be decreased, we decrease it
                        if (timing.canDecreaseTStep()) {
                            // force min tstep
                            timing.setMinTStep();
            // fgarcia debug
                            System.out.println("Bad Result, tStep decreased at t= {0}, tStep: {1}");
                            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINE, "Bad Result, tStep decreased at t= {0}, tStep: {1}", new Object[]{timing.getTimeCounter(), timing.gettStep()});
                        } else {
                            // Else, can not be recovered,
                            // or the time step decreased,  
                            // so we end simulation
                            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Simulation crashed, time step can not be decreased.");
                            correctlyComputed = simulationResult;
                        }
                    }
                } else {
                    // Correctly simulated
                    if (memristor.acceptStep()) {
                        timing.updateState();
                        inputVoltage.updateState();
                        memristor.updateSaveState(internalCount - 1);
                        store = true;
                        // can timing be increased?
                        if (computedStepsSinceRecovery > ResourcesMAF.MAGNITUDERECOVERYSTEPS
                                && memristor.isAcceptIncrease() && timing.canIncreaseTStep()) {
                            timing.increaseTStep();
            // fgarcia debug
                            System.out.println("tStep increased at t:" + timing.getTimeCounter());
                            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINER, "tStep increased at t= {0}, tStep: {1}", new Object[]{timing.getTimeCounter(), timing.gettStep()});
                        }
                    } else {
                        if (memristor.isNeedDecrease() && timing.canDecreaseTStep()) {
                            timing.decreaseTStep();
            // fgarcia debug
                            System.out.println("tStep decreased at t:" + timing.getTimeCounter());
                            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINE, "tStep decreased at t= {0}, tStep: {1}", new Object[]{timing.getTimeCounter(), timing.gettStep()});

                        } else { // Else, can not be decreased, so stored                            
                            couldNotDecreaseStep = true;
                            timing.updateState();
                            inputVoltage.updateState();
                            memristor.updateSaveState(internalCount - 1);
                            store = true;
                        }
                    }
                    // update recovering
                    computedStepsSinceRecovery++;
                    // If the step result is correctly computed,
                    // it can be saved or not (time step increased or decreased)
                    if (store) {
                        // update the min tstep if needed
                        if (timing.gettStep() <= memristor.getMaxTStepNeeded()) {
                            memristor.setMaxTStepNeeded(timing.gettStep());
                        }
                        if (!reduceStoredData || skipDataCount == storeRatio) {
                            if (internalCount == memristor.getMagnitudeValuesSize()) {
                                for (int i = 0; i < memristor.getMagnitudeValuesSize(); i++) {
                                    writer.write(memristor.getMagnitudesInARow(i));
                                    exportedDataSize++;
                                }
                                internalCount = 1;
                            } else {
                                internalCount++;
                            }
                            skipDataCount = 0;
                        } // end of reduce storing ratio
                        skipDataCount++;
                    } // end of store
                } // if-else correctly simulated
            } // end of while
            // Final values still in memory 2 disk
            for (int i = 0; i < internalCount - 1; i++) {
                writer.write(memristor.getMagnitudesInARow(i));
                exportedDataSize++;
            }
            // save last value 
            // worst case, that the memristor magnitudes buffers where filled
            if (internalCount == memristor.getMagnitudeValuesSize()) {
                // internal count = 1
                memristor.updateSaveState(0);
                writer.write(memristor.getMagnitudesInARow(0));
                exportedDataSize++;
            } else {
                memristor.updateSaveState(internalCount);
                writer.write(memristor.getMagnitudesInARow(internalCount));
                exportedDataSize++;
            }
            // flush
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(DCThread.class.getName()).log(Level.SEVERE, null, ex);
            correctlyComputed = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, timing.getStepCounter(), "I/O error: " + ex.getLocalizedMessage());
        } finally {
            FilesFoldersManagement.close(writer);
        }
        if (correctlyComputed.allCorrect()) {
            if (couldNotDecreaseStep) {
                simulationResult.setState(SimulationResult.states.SIMULATION_WARNING);
                simulationResult.setMessage("Time step required to be reduced but was imposible due the imposed timing constrains.\nConsider reducing the min tstep.");
            }
        } else {
            simulationResult = correctlyComputed;
        }
        if (!interrupted) {
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.FINE, "Simulation {0} done.", threadId);
        } else {
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Simulation Interrupted");
        }
    }

    public void cleanDC_Thread() {
        memristor = null;
        inputVoltage = null;
        timing = null;
        title = null;
        outputFile = null;
        simulationResult = null;
    }

    public String getParametersAsString() {
        String params = "Memristor: ";
        for (MemristorParameter p : memristor.getParameters()) {
            params += p.getTag() + "= " + p.getValue() + ";";
        }
        params += "\tVoltage: ";
        for (InputVoltageParameter p : inputVoltage.getParameters()) {
            params += p.getTag() + "= " + p.getValue() + ";";
        }
        return params;
    }

    public int getThreadId() {
        return threadId;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public int getExportedDataSize() {
        return exportedDataSize;
    }

    public void setExportedDataSize(int exportedDataSize) {
        this.exportedDataSize = exportedDataSize;
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

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
}
