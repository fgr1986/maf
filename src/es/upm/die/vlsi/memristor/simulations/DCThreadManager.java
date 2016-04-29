package es.upm.die.vlsi.memristor.simulations;

import es.upm.die.vlsi.memristor.math.CurvesFitter;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.input_sources.InputVoltage;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.ui.DCSimulationUI;
import com.opencsv.CSVReader;
import es.upm.die.vlsi.memristor.resources.FilesFoldersManagement;
import es.upm.die.vlsi.memristor.math.FittingCurvesResult;
import es.upm.die.vlsi.memristor.math.XYSegment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DCThreadManager extends Thread {

    protected MemristorModel memristorPilot;
    protected InputVoltage voltagePilot;
    protected Timing timing;
    protected int maxParallelThreads;
    protected int threadsCorrectlyProcessed;
    protected int threadsBadlyProcessed;
    protected String title;
    protected int threadsNumber;
    protected int storeRatio;
    protected boolean reduceStoredData;
    protected String outputFolder;
    protected volatile boolean interrupted = false;
    protected DCSimulationUI ccui;
    protected DCThread[] threads;
    protected String[] threadParams;
    protected String[] threadOutputFiles;
    protected int[] threadExportedSizes;
    protected SimulationResult[] simulationResults;
    protected SimulationResult simulationResult;
    protected long startTime;
    protected long endTime;
    protected long startTimeFitting;
    protected long endTimeFitting;
    protected boolean plotOnlyFittingCurves;
    protected int magnitudeValueBufferSize;
    protected boolean logI;
    protected double minIPlotted;
    protected boolean importCSV;
    protected String[] csvFiles;
    protected File[] simulationFiles2BeProcessed;
    protected boolean fitCurves;

    // Array[csvFiles.length] of Arraylist of Double[length of csv][2]
    private ArrayList<XYSegment>[] scatterDataSegments;
    private FittingCurvesResult[][] simulationMeasureFitErrors; // [csvFiles.length][threadsNumber]
    private FittingCurvesResult[] bestFittingScenarios; // [csvFiles.length]

    private DCThreadManagerPlotter plotter;

    public DCThreadManager() {
        scatterDataSegments = null;
    }

    @Override
    public void run() {
        threadsCorrectlyProcessed = 0;
        threadsBadlyProcessed = 0;
        startTime = System.nanoTime();
        interrupted = false;
        simulationResult = new SimulationResult(SimulationResult.states.CORRECT, 0, "Every simulation ended OK", "Every simulation ended OK");
        simulationResults = new SimulationResult[threadsNumber];

        threadParams = new String[threadsNumber];
        threadOutputFiles = new String[threadsNumber];
        threadExportedSizes = new int[threadsNumber];
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.CONFIG, "Setting the system...");
        if (!FilesFoldersManagement.prepareFolder(outputFolder)) {
            return;
        }
        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Starting the thread creation and running mechanism.");
        // Run threads in groups of maxParallelThreads
        prepareThreadsCreation();
        int totalThreadBlocks = threadsNumber / getThreadsBlockSize();
        int threadsBlockCounter = 0;
        int threadCounter = 0;
        int threadGlobalCounter = 0;
        while (threadGlobalCounter < threadsNumber) {
            int threadsArraySize = Math.min(threadsNumber - threadGlobalCounter, getThreadsBlockSize());
            // create the simulation threads
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Creating threads block: {0}", threadsBlockCounter);
            threads = new DCThread[threadsArraySize];
            createThreadsBlock(threadGlobalCounter, threadsArraySize);
            // update threadCounter
            threadGlobalCounter += threadsArraySize;
            // run threads
            for (DCThread t : threads) {
                t.start();
            }
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "All threads of block {0} are running.", threadsBlockCounter);
            // join those ones
            try {
                for (DCThread t : threads) {
                    t.join();
                }
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO,
                        "Threads block #{0} of {1} ended.", new Object[]{threadsBlockCounter, totalThreadBlocks});
                // update block counter
                threadsBlockCounter++;
            } catch (InterruptedException e) {
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.WARNING, "Thread manager Interrupted.");
                simulationResult = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, 0, "Thread manager Interrupted", "Thread manager Interrupted");
            }
            // Check all thread results
            for (DCThread t : threads) {
                if (t == null) {
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.SEVERE, "Null thread.");
                    return;
                }
                if (!t.getSimulationResult().allCorrect()) {
                    simulationResult = new SimulationResult(
                            SimulationResult.states.SIMULATION_ERROR, 0, "Simulation " + t.getThreadId() + " ended badly",
                            "Simulation " + t.getThreadId() + " ended badly");
                    threadsBadlyProcessed++;
                } else {
                    if (t.getSimulationResult().getState() == SimulationResult.states.SIMULATION_WARNING) {
                        simulationResult.setState(SimulationResult.states.SIMULATION_WARNING);
                        simulationResult.setMessage("At least one thread required a smaller tstep");
                    }
                    threadsCorrectlyProcessed++;
                }
                threadOutputFiles[threadCounter] = t.getOutputFile();
                threadParams[threadCounter] = t.getParametersAsString();
                threadExportedSizes[threadCounter] = t.getExportedDataSize();
                simulationResults[threadCounter] = t.getSimulationResult();
                // update counter
                threadCounter++;
                // force free memory
                t.cleanDC_Thread();
                t = null;
            }
        }
        // force free memory
        threads = null;

        // Record time
        endTime = System.nanoTime();
        if (!interrupted) {
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Simulation done");
            ccui.reportResults();
        } else {
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Simulation Interrupted");
            ccui.reportResults();
        }
    }

    // overriden
    protected void prepareThreadsCreation() {
    }

    // overriden
    protected void createThreadsBlock(int threadCounter, int threadCounterLimit) {
    }

    // overriden
    protected int getThreadsBlockSize() {
        return 0;
    }

    public void prepareResults() {
        if (threadsNumber > 0) {
            // not ordered, so
//            simulationFiles2BeProcessed = FilesFoldersManagement.filesInFolder(new File(outputFolder));
            // ordered, because that matters when reading the files!!
            simulationFiles2BeProcessed = new File[threadsNumber];
            for (int i = 0; i < threadsNumber; i++) {
                simulationFiles2BeProcessed[i] = new File(threadOutputFiles[i]);
            }
            // arrays cronstructors
            if (csvFiles != null) {
                scatterDataSegments = new ArrayList[csvFiles.length];
            } else {
                scatterDataSegments = new ArrayList[0];
            }
            simulationMeasureFitErrors = new FittingCurvesResult[csvFiles.length][threadsNumber];
            bestFittingScenarios = new FittingCurvesResult[csvFiles.length];
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Importing CSV.");
            if (importCSV) {
                readCSVMeasures();
            }
        }
    }

    private ArrayList<XYSegment> splitArrayByMonotonyOfAxis(Double[][] simData) {
        ArrayList<XYSegment> segments = new ArrayList();
        double firstXValue = simData[0][0];
        int secondValueIndex = 1;
        double secondXValue = simData[secondValueIndex][0];
        while (secondXValue == firstXValue) {
            secondXValue = simData[secondValueIndex][0];
        }
        boolean growing = secondXValue > firstXValue;
        int changeMonotonyIndex = 0;
        int lastChangeMonotonyIndex = 0;
        for (Double[] data : simData) {
            if (changeMonotonyIndex == 0) {
                changeMonotonyIndex++;
                continue;
            }
            if (growing && data[0] < simData[changeMonotonyIndex - 1][0]) {
                Double[][] segmentData = new Double[changeMonotonyIndex - lastChangeMonotonyIndex][2];
                System.arraycopy(simData, lastChangeMonotonyIndex, segmentData, 0, segmentData.length);
                segments.add(new XYSegment(true, 
                        segmentData[0][0], segmentData[segmentData.length - 1][0], segmentData));
                lastChangeMonotonyIndex = changeMonotonyIndex;
                growing = false;
            }
            if (!growing && data[0] > simData[changeMonotonyIndex - 1][0]) {
                Double[][] segmentData = new Double[changeMonotonyIndex - lastChangeMonotonyIndex][2];
                System.arraycopy(simData, lastChangeMonotonyIndex, segmentData, 0, segmentData.length);
                segments.add(new XYSegment(false, 
                        segmentData[segmentData.length - 1][0], segmentData[0][0], segmentData));
                lastChangeMonotonyIndex = changeMonotonyIndex;
                growing = true;
            }
            changeMonotonyIndex++;
        }
        // last segment
        Double[][] segmentData = new Double[changeMonotonyIndex - lastChangeMonotonyIndex][2];
        System.arraycopy(simData, lastChangeMonotonyIndex, segmentData, 0, segmentData.length);
        if (growing) {
            segments.add(new XYSegment(true,
                    segmentData[0][0], segmentData[segmentData.length - 1][0], segmentData));
        } else {
            segments.add(new XYSegment(false, 
                    segmentData[segmentData.length - 1][0], segmentData[0][0], segmentData));
        }

        return segments;
    }

    public void fitCurves() {
        // Default result
        simulationResult = new SimulationResult(SimulationResult.states.CORRECT, 0, "Correctly fitted", "Correctly fitted");
        if (threadsNumber > 0 && fitCurves && importCSV) {
            // time 
            startTimeFitting = System.nanoTime();
            try {
                if (simulationFiles2BeProcessed == null) {
                    prepareResults();
                }
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Fitting curves...");
                // fitCurves
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "\tComputing simulation/measure error.");
                // compute the error with each csv measurement for each simulation
                int partialCount = 0;
                for (int i = 0; i < threadsNumber; i++) {
                    // if data available
                    if (threadExportedSizes[i] > 1) {
                        Double[][] simData;
                        if ((simData = readSimulationResultsFile(i, simulationFiles2BeProcessed[i], threadExportedSizes[i], true)) == null) {
                            throw new Exception("Imported data error: " + simulationFiles2BeProcessed[i]);
                        }
                        // divide the data in segments.
                        // at each segment the data X axis only grows or decreases
                        ArrayList<XYSegment> segments = splitArrayByMonotonyOfAxis(simData);
                        // free memory
                        simData = null;
                        // find error for each measurements file
                        for (int csvCount = 0; csvCount < csvFiles.length; csvCount++) {
                            // segments where the simulation and the scatter measures meet.
                            int coincidentSegments = Math.min(segments.size(), scatterDataSegments[csvCount].size());
                            double totalError = 0;
                            double minX = 0;
                            double maxX = 0;
                            boolean correctlyFitted = true;
                            String message = "";
                            for (int segmentCount = 0; segmentCount < coincidentSegments; segmentCount++) {
                                CurvesFitter cv = new CurvesFitter(i, segments.get(segmentCount),
                                        scatterDataSegments[csvCount].get(segmentCount), logI);
                                FittingCurvesResult fs = cv.findSimulationMeasureError();
                                totalError += fs.getAbsError();
                                minX = Math.min(minX, fs.getMinX());
                                maxX = Math.max(maxX, fs.getMaxX());
                                if (!fs.isCorrectlyFitted()) {
                                    message += "\n\n" + fs.getCompleteMessage();
                                    correctlyFitted = false;
                                }
                            }
                            FittingCurvesResult scenarioFCR = new FittingCurvesResult(
                                    correctlyFitted, i, totalError, minX, maxX, message);
                            scenarioFCR.setParameterValues(threadParams[i]);
                            simulationMeasureFitErrors[csvCount][i] = scenarioFCR;
                        } // end csvFiles fitting
                    } // thread have data
                    partialCount++;
                    if (partialCount == maxParallelThreads) {
                        Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Simulation results {0} of {1} imported.", new Object[]{i, threadsNumber});
                    }
                } // end of threads fitting, find best
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "\tSelecting best scenario.");
                for (int csvCount = 0; csvCount < csvFiles.length; csvCount++) {
                    FittingCurvesResult bfs = simulationMeasureFitErrors[0][0];
                    for (int i = 0; i < threadsNumber; i++) {
                        if (simulationMeasureFitErrors[csvCount][i].getAbsError() < bfs.getAbsError()) {
                            bfs = simulationMeasureFitErrors[csvCount][i];
                        }
                    }
                    bestFittingScenarios[csvCount] = bfs;
                }
            } catch (Exception exc) {
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.SEVERE, "Error plotting the results. {0}", exc.getLocalizedMessage());
                simulationResult = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, 0, "Error fitting the results. " + exc.getLocalizedMessage(),
                        "Error fitting the results. " + exc.getLocalizedMessage());
            }
            endTimeFitting = System.nanoTime();
        } else {
            simulationResult = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, 0, "No threads available.", "No threads available.");
        }
    }

    protected void readCSVMeasures() {
        int csvCount = 0;
        for (String path : csvFiles) {
            readCSVMeasuresFile(csvCount++, path, false, ' ');
        }
    }

    protected void readCSVMeasuresFile(int fileCount, String absPath, boolean skipHeader, char separator) {
        BufferedReader fr = null;
        int linesInFile = FilesFoldersManagement.linesNumberInFile(absPath, true, "#");
        if (linesInFile < 1) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, "File without content:" + absPath);
            return;
        }
        try {
            fr = new BufferedReader(new FileReader(absPath));
            if (skipHeader) {
                fr.readLine();
            }
            String fl = fr.readLine();
            if (!fl.contains("" + separator)) {
                separator = ResourcesMAF.CHARCSVSEPARATOR;
            }
        } catch (IOException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            FilesFoldersManagement.close(fr);
        }
        CSVReader reader = null;
        try {
            if (skipHeader) {
                reader = new CSVReader(new FileReader(absPath), separator, '\'', 1);
                // header does not count :P
                linesInFile--;
            } else {
                reader = new CSVReader(new FileReader(absPath), separator);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] nextLine;
        if (reader == null) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, "Null reader");
            return;
        }
        Double[][] dData = new Double[linesInFile][2];
        try {
            int lineCount = 0;
            while ((nextLine = reader.readNext()) != null ) {
                // remove empty lines etc
                nextLine[0] = nextLine[0].replaceAll("[\\t\\n\\r]","");
                nextLine[1] = nextLine[1].replaceAll("[\\t\\n\\r]","");
                if(nextLine[0].isEmpty() || nextLine[1].isEmpty() ||
                    nextLine[0].startsWith("#") ){
                    continue;
                }
                dData[lineCount][0] = Double.parseDouble(nextLine[0]);
                dData[lineCount++][1] = Double.parseDouble(nextLine[1]);
            }
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            FilesFoldersManagement.close(reader);
        }
        // divide the data in segments.
        // at each segment the data X axis only grows or decreases
        ArrayList<XYSegment> segments = splitArrayByMonotonyOfAxis(dData);
        scatterDataSegments[fileCount] = segments;
    }

    protected Double[][] readSimulationResultsFile(int fileCount, File csvFile, int totalData, boolean skipHeader) {
        // total data
        Double[][] dData = new Double[totalData][2];
        CSVReader reader = null;
        try {
            if (skipHeader) {
                reader = new CSVReader(new FileReader(csvFile), ResourcesMAF.CHARCSVSEPARATOR, '\'', 1);
            } else {
                reader = new CSVReader(new FileReader(csvFile), ResourcesMAF.CHARCSVSEPARATOR);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] nextLine;
        if (reader == null) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, "Null reader");
            return null;
        }
        try {
            int lineCount = 0;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[0].isEmpty() || nextLine[1].isEmpty() ){
                    continue;
                }
                dData[lineCount][0] = Double.parseDouble(nextLine[memristorPilot.VINDEX]);
                dData[lineCount++][1] = Double.parseDouble(nextLine[memristorPilot.IINDEX]);
            }
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
            dData = null;
        } finally {
            FilesFoldersManagement.close(reader);
        }
        return dData;
    }

    public void plotUsingGnuplot() {
        plotter = new DCThreadManagerPlotter(outputFolder, plotOnlyFittingCurves, threadsNumber,
                threadExportedSizes, simulationFiles2BeProcessed,
                fitCurves, importCSV, csvFiles, bestFittingScenarios, simulationMeasureFitErrors,
                memristorPilot, logI, minIPlotted);
        plotter.createGnuPlotCharts();
    }

    public void plotUsingJFreeChart() {
        plotter = new DCThreadManagerPlotter(outputFolder, plotOnlyFittingCurves, threadsNumber,
                threadExportedSizes, simulationFiles2BeProcessed,
                fitCurves, importCSV, csvFiles, bestFittingScenarios, simulationMeasureFitErrors,
                memristorPilot, logI, minIPlotted);
        plotter.createJFreeCharts();
    }

    public DCThreadManagerPlotter getPlotter() {
        return plotter;
    }

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    public FittingCurvesResult[] getBestFittingScenarios() {
        return bestFittingScenarios;
    }

    public void setPlotOnlyFittingCurves(boolean plotOnlyFittingCurves) {
        this.plotOnlyFittingCurves = plotOnlyFittingCurves;
    }

    public void setLogI(boolean logI) {
        this.logI = logI;
    }

    public void setImportCSV(boolean importCSV) {
        this.importCSV = importCSV;
    }

    public void setFitCurves(boolean fitCurves) {
        this.fitCurves = fitCurves;
    }

    public void setMinIPlotted(double minIPlotted) {
        this.minIPlotted = minIPlotted;
    }

    public void setCsvFiles(String[] csvFiles) {
        this.csvFiles = csvFiles;
    }

    public long getStartTimeFitting() {
        return startTimeFitting;
    }

    public long getEndTimeFitting() {
        return endTimeFitting;
    }

    public void setMemristorPilot(MemristorModel memristorPilot) {
        this.memristorPilot = memristorPilot;
    }

    public SimulationResult[] getSimulationResults() {
        return simulationResults;
    }

    public void setSimulationResult(SimulationResult simulationResult) {
        this.simulationResult = simulationResult;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInterrupted(boolean interrupted) {
        if (threads == null) {
            return;
        }
        for (DCThread t : threads) {
            if (t != null) {
                t.setInterrupted(interrupted);
                t.interrupt();
            }
        }
        this.interrupted = interrupted;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public void setThreadsNumber(int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public SimulationResult getSimulationResult() {
        // TODO Auto-generated method stub
        return simulationResult;
    }

    public int getThreadsCorrectlyProcessed() {
        return threadsCorrectlyProcessed;
    }

    public void setThreadsCorrectlyProcessed(int threadsCorrectlyProcessed) {
        this.threadsCorrectlyProcessed = threadsCorrectlyProcessed;
    }

    public int getThreadsBadlyProcessed() {
        return threadsBadlyProcessed;
    }

    public void setThreadsBadlyProcessed(int threadsBadlyProcessed) {
        this.threadsBadlyProcessed = threadsBadlyProcessed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getMagnitudeValuesSize() {
        return magnitudeValueBufferSize;
    }

    public void setMagnitudeValuesSize(int magnitudeValueBufferSize) {
        this.magnitudeValueBufferSize = magnitudeValueBufferSize;
    }

}
