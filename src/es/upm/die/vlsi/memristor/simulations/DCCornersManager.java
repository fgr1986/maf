package es.upm.die.vlsi.memristor.simulations;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltageParameter;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorParameter;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.ui.DCSimulationUI;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DCCornersManager extends DCThreadManager {

    private InputVoltage[] inputVoltageSweep;
    private int memristorSweepNumber;
    private int currentMemristorSweep;
    private int threadsBlockSize;

    public DCCornersManager(DCSimulationUI ccui, int maxParallelThreads,
            MemristorModel memristorPilot, InputVoltage voltagePilot,
            Timing timing, String title, int magnitudeValueBufferSize,
            boolean reduceStoredData, int storeRatio, String outputFolder) {
        super();
        this.maxParallelThreads = maxParallelThreads;
        this.ccui = ccui;
        this.memristorPilot = memristorPilot;
        this.voltagePilot = voltagePilot;
        this.timing = timing;
        this.title = title;
        this.magnitudeValueBufferSize = magnitudeValueBufferSize;
        this.reduceStoredData = reduceStoredData;
        this.outputFolder = outputFolder;
        this.storeRatio = storeRatio;

        this.threadsNumber = 1;
        for (MemristorParameter mp : memristorPilot.getParameters()) {
            if (mp.isStandard()) {
                threadsNumber = threadsNumber * mp.getValueList().length;
            }
        }
        for (InputVoltageParameter ivpor : voltagePilot.getParameters()) {
            threadsNumber = threadsNumber * ivpor.getValueList().length;
        }
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Final threads will be: {0}", threadsNumber);
        this.interrupted = true;
    }

    public DCCornersManager() {
        super();
    }

    @Override
    public void prepareThreadsCreation() {
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Creating matrix of input voltages. ");
        // this matrix is expected to be smaller than the memristor one
        inputVoltageSweep = voltagePilot.getValueIterationCombinations();
        threadsBlockSize = Math.max( maxParallelThreads - maxParallelThreads % inputVoltageSweep.length,
                inputVoltageSweep.length);
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Preparing memristors iterations.");
        memristorSweepNumber = memristorPilot.prepareValueIterationCombinations();
        // initialize memristor sweep count
        currentMemristorSweep = 0;
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Ready for Creating threads. ");
    }

    @Override
    protected void createThreadsBlock(int threadCounter, int threadCounterLimit) {
        // create the simulation threads
        //for (int memristorCount = currentMemristorSweep; memristorCount < memristorSweepNumber; memristorCount++) {
        int i = 0;
        while (i < threadCounterLimit) {
            // sweep memristor
            MemristorModel m = memristorPilot.getNextValueCombination();
            currentMemristorSweep++;
            // sweep voltage
            for (InputVoltage v : inputVoltageSweep) {
                String auxFile = outputFolder + "/" + "thread_" + threadCounter + "." + ResourcesMAF.OUTPUTEXTENSION;
                // Copy of objects
                Timing tt = timing.getCopy();
                InputVoltage tiv = v.getCopy(tt);
                MemristorModel tmm = m.getCopy();
                // Thread
                threads[i] = new DCThread(threadCounter, tmm, tiv, tt, title,
                        magnitudeValueBufferSize, reduceStoredData, storeRatio, auxFile);
                if (threadCounter == threadCounterLimit) {
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Thread #{0} of #{1} created. ", new Object[]{threadCounter, threadsNumber});
                }
                threadCounter++;
                i++;
            }
        }
    }

    @Override
    protected int getThreadsBlockSize() {
        return threadsBlockSize;
    }
}
