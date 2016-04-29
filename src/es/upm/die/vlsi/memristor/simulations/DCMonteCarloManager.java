package es.upm.die.vlsi.memristor.simulations;

import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.ui.DCSimulationUI;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DCMonteCarloManager extends DCThreadManager {

    public DCMonteCarloManager(DCSimulationUI ccui, int threadsNumber, int maxParallelThreads,
            MemristorModel memristorPilot, InputVoltage voltagePilot,
            Timing timing, String title, int magnitudeValueBufferSize,
            boolean reduceStoredData, int storeRatio, String outputFolder) {
        super();
        this.maxParallelThreads = maxParallelThreads;
        this.ccui = ccui;
        this.threadsNumber = threadsNumber;
        this.memristorPilot = memristorPilot;
        this.voltagePilot = voltagePilot;
        this.timing = timing;
        this.title = title;
        this.magnitudeValueBufferSize = magnitudeValueBufferSize;
        this.reduceStoredData = reduceStoredData;
        this.outputFolder = outputFolder;
        this.storeRatio = storeRatio;

        this.interrupted = true;
    }

    public DCMonteCarloManager() {
        super();
    }

    @Override
    protected void prepareThreadsCreation() {
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Ready for Creating threads. ");
    }
    
    @Override
    protected void createThreadsBlock( int threadCounter, int threadCounterLimit ) {
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Ready for Creating threads. ");
        // create the simulation threads
        for (int i = 0; i < threadCounterLimit; i++) {
            Timing t = timing.getCopy();
            String auxFile = outputFolder + "/" + "thread_" + threadCounter + "." + ResourcesMAF.OUTPUTEXTENSION;
            InputVoltage riv = voltagePilot.getRandomInputVoltage(t);
            MemristorModel rm = memristorPilot.getRandomMemristor();
            threads[i] = new DCThread(threadCounter++, rm, riv, t, title, 
                    magnitudeValueBufferSize, reduceStoredData, storeRatio, auxFile);                
        }
    }

    @Override
    protected int getThreadsBlockSize(){
        return maxParallelThreads;
    }
}
