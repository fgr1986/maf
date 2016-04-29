/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.simulations;

import com.opencsv.CSVReader;
import es.upm.die.vlsi.memristor.resources.ExternalPlotter;
import es.upm.die.vlsi.memristor.resources.FilesFoldersManagement;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.math.FittingCurvesResult;
import es.upm.die.vlsi.memristor.math.FittingErrorStatistics;
import es.upm.die.vlsi.memristor.simulation_objects.SimulationResult;
import es.upm.die.vlsi.memristor.simulation_objects.memristor_models.MemristorModel;
import java.awt.Font;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

/**
 *
 * @author fgarcia
 */
public class DCThreadManagerPlotter {

    private JFreeChart viChart;
    private ArrayList<JFreeChart> allCharts;

    protected long startTimePlotting;
    protected long endTimePlotting;

    private final String outputFolder;
    private final boolean plotOnlyFittingCurves;
    private final int threadsNumber;
    private final int[] threadsExportedSize;
    private final File[] simulationFiles2BeProcessed;
    private final boolean fitCurves;
    private boolean importCSV;
    private final String[] csvFiles;
    private final FittingCurvesResult[] bestFittingScenarios;
    private final FittingCurvesResult[][] simulationMeasureFitErrors;

    private final MemristorModel memristorPilot;

    private final boolean logI;
    private final double minIPlotted;

    SimulationResult simulationResult;

    public DCThreadManagerPlotter(String outputFolder, boolean plotOnlyFittingCurves,
            int threadsNumber, int[] threadsExportedSize, File[] simulationFiles2BeProcessed,
            boolean fitCurves, boolean importCSV, String[] csvFiles,
            FittingCurvesResult[] bestFittingScenarios, FittingCurvesResult[][] simulationMeasureFitErrors, 
            MemristorModel memristorPilot, boolean logI, double minIPlotted) {
        this.outputFolder = outputFolder;
        this.plotOnlyFittingCurves = plotOnlyFittingCurves;
        this.threadsNumber = threadsNumber;
        this.threadsExportedSize = threadsExportedSize;
        this.simulationFiles2BeProcessed = simulationFiles2BeProcessed;
        this.fitCurves = fitCurves;
        this.importCSV = importCSV;
        this.csvFiles = csvFiles;
        this.bestFittingScenarios = bestFittingScenarios;
        this.simulationMeasureFitErrors= simulationMeasureFitErrors;
        this.memristorPilot = memristorPilot;
        this.logI = logI;
        this.minIPlotted = minIPlotted;
    }

    public void createGnuPlotCharts() {
        ExternalPlotter ep = new ExternalPlotter();
        ep.prepareScriptsFolder(outputFolder);
        String graphsFolder = outputFolder + "/" + ResourcesMAF.EXTERNALGRAPHSFOLDER;
        if (!FilesFoldersManagement.prepareFolder(graphsFolder)) {
            return;
        }
        String simulationsGraphsFolder = graphsFolder + "/" + ResourcesMAF.SIMULATIONGRAPHSFOLDER;
        if (!FilesFoldersManagement.prepareFolder(simulationsGraphsFolder)) {
            return;
        }
        String fittingGraphsFolder = graphsFolder + "/" + ResourcesMAF.FITTINGGRAPHSFOLDER;
        if (!FilesFoldersManagement.prepareFolder(fittingGraphsFolder)) {
            return;
        }
        // Simulations
        String absPathFolderSimulations = new File(simulationsGraphsFolder).getAbsolutePath();
        if (!plotOnlyFittingCurves) {
            for (int i = 0; i < threadsNumber; i++) {
                if (threadsExportedSize[i] > 0) {
                    ep.plotSimulation(absPathFolderSimulations, simulationFiles2BeProcessed[i].getAbsolutePath(), i);
                }
            }
        }
        // fitting graphs
        String absPathFittingFolder = new File(fittingGraphsFolder).getAbsolutePath();
        if (fitCurves) {
            for (int csvCount = 0; csvCount < csvFiles.length; csvCount++) {
                ep.plotAdjustedCycleMeasures(absPathFittingFolder, csvFiles[csvCount],
                        simulationFiles2BeProcessed[bestFittingScenarios[csvCount].getScenarioIndex()].getAbsolutePath(),
                        csvCount, bestFittingScenarios[csvCount].getScenarioIndex(),
                        bestFittingScenarios[csvCount].getMinX(), bestFittingScenarios[csvCount].getMaxX());
                ep.plotAdjustedMemristance(absPathFittingFolder, csvFiles[csvCount],
                        simulationFiles2BeProcessed[bestFittingScenarios[csvCount].getScenarioIndex()].getAbsolutePath(),
                        csvCount, bestFittingScenarios[csvCount].getScenarioIndex(),
                        bestFittingScenarios[csvCount].getMinX(), bestFittingScenarios[csvCount].getMaxX());
                if (plotOnlyFittingCurves) {
                    ep.plotSimulation(absPathFolderSimulations,
                            simulationFiles2BeProcessed[bestFittingScenarios[csvCount].getScenarioIndex()].getAbsolutePath(),
                            bestFittingScenarios[csvCount].getScenarioIndex());
                }
            }
            // error files
            FittingErrorStatistics fes = new FittingErrorStatistics(simulationMeasureFitErrors, absPathFittingFolder);
            fes.writeErrorFiles();
            int csvCount = 0;
            for (String s : fes.getErrorFilePaths()) {
                ep.plotAdjustedError(absPathFittingFolder, s, csvCount++);
            }
        }
    }

    public void createJFreeCharts() {
        // Default result
        simulationResult = new SimulationResult(SimulationResult.states.CORRECT, 0, "Correctly plotted", "Correctly plotted");
        if (threadsNumber > 0) {
            // time 
            startTimePlotting = System.nanoTime();
            try {
                if (simulationFiles2BeProcessed == null) {
                    return;
                }
                // prepare charts
                allCharts = new ArrayList<>();
                XYSeriesCollection viSeriesCollection = new XYSeriesCollection();
                ArrayList<XYSeriesCollection> magXYSeriesCollections = new ArrayList<>();
                int plottedMagnitudesNumber = plotOnlyFittingCurves ? 3 : memristorPilot.getExportedMagnitudesNumber();
                for (int i = 1; i < plottedMagnitudesNumber; i++) {
                    magXYSeriesCollections.add(new XYSeriesCollection());
                }
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Reading result files...");
                // if csv measures
                if (importCSV) {
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "CSV Measures import.");
                    int csvCount = 0;
                    for (String path : csvFiles) {
                        XYSeries series = readCSVsAsSeries(csvCount++, path, false, ' ');
                        if (series == null) {
                            Logger.getLogger(ResourcesMAF.loggerName).log(Level.SEVERE, "CSV not correctly imported.");
                            importCSV = false;
                            break;
                        }
                        viSeriesCollection.addSeries(series);
                    }
                    Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "CSV Measures imported.");
                }
                // for each file
                for (int i = 0; i < threadsNumber; i++) {
                    // if data available
                    if (threadsExportedSize[i] > 0) {
                        // Prepare series
                        XYSeries[] series = readMagnitudesAsSeries(i, plottedMagnitudesNumber,
                                simulationFiles2BeProcessed[i], threadsExportedSize[i], true);
                        // add VI
                        viSeriesCollection.addSeries(series[0]);
                        // the magnitudes vs time (but time)
                        for (int m = 0; m < series.length - 1; m++) {
                            (magXYSeriesCollections.get(m)).addSeries(series[m + 1]);
                        }
                    } // end if foreach thread
                } // end threads loop
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.INFO, "Series Created, Plotting...");
                // Charts
                viChart = ChartFactory.createXYLineChart("i(v)  Plot, " + "'", "v (V)", "i (mA)", viSeriesCollection,
                        PlotOrientation.VERTICAL, false, true, true);
                // if csv measures create scatter like
                if (importCSV) {
                    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
                    Shape cross = ShapeUtilities.createDownTriangle(2);
                    for (int csvCount = 0; csvCount < csvFiles.length; csvCount++) {
                        renderer.setSeriesShapesVisible(csvCount, true);
                        renderer.setSeriesLinesVisible(csvCount, true);
                        renderer.setSeriesShape(csvCount, cross);
                    }
                    for (int simulationCount = 0; simulationCount < threadsNumber; simulationCount++) {
                        renderer.setSeriesShapesVisible(simulationCount + csvFiles.length, false);
                        renderer.setSeriesLinesVisible(simulationCount + csvFiles.length, true);
                    }
                    viChart.getXYPlot().setRenderer(renderer);
                }
                applyXYChartTheme(viChart, logI, minIPlotted);
                allCharts.add(viChart);
                // magnitudes vs time, but time
                for (int m = 0; m < magXYSeriesCollections.size(); m++) {
                    JFreeChart chart = ChartFactory.createXYLineChart(memristorPilot.getMagnitudeName(m + 1) + "(t)", "time [s]",
                            memristorPilot.getMagnitudeName(m + 1) + "[" + memristorPilot.getMagnitudeUnits(m + 1) + "]",
                            magXYSeriesCollections.get(m), PlotOrientation.VERTICAL, false, true, true);
                    applyXYChartTheme(chart, false, minIPlotted);
                    allCharts.add(chart);
                }
                // fitCurves
                if (fitCurves && importCSV) {
                    // Create charts
                    createFitJFreeCharts(viSeriesCollection);
                }
            } catch (Exception exc) {
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.SEVERE, "Error plotting the results. {0}", exc.getLocalizedMessage());
                simulationResult = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, 0, "Error plotting the results. " + exc.getLocalizedMessage(),
                        "Error plotting the results. " + exc.getLocalizedMessage());
            }
            endTimePlotting = System.nanoTime();
        } else {
            simulationResult = new SimulationResult(SimulationResult.states.SIMULATION_ERROR, 0, "No threads available.", "No threads available.");
        }
    }

    private void createFitJFreeCharts(XYSeriesCollection viSeriesCollection) {
        for (int csvCount = 0; csvCount < csvFiles.length; csvCount++) {
            // create chart
            XYSeriesCollection xysc = new XYSeriesCollection();
            xysc.addSeries(viSeriesCollection.getSeries(bestFittingScenarios[csvCount].getScenarioIndex() + csvFiles.length));
            xysc.addSeries(viSeriesCollection.getSeries(csvCount));
            JFreeChart fcchart = ChartFactory.createXYLineChart("Fitting i(v) curve to csv #" + csvCount, "v (V)", "i (mA)",
                    xysc, PlotOrientation.VERTICAL, false, true, true);
            XYLineAndShapeRenderer bfsRenderer = new XYLineAndShapeRenderer();
            bfsRenderer.setSeriesShapesVisible(0, false);
            bfsRenderer.setSeriesLinesVisible(0, true);
            bfsRenderer.setSeriesShapesVisible(1, true);
            bfsRenderer.setSeriesLinesVisible(1, true);
            fcchart.getXYPlot().setRenderer(bfsRenderer);
            applyXYChartTheme(fcchart, logI, minIPlotted);
            allCharts.add(fcchart);
        }
    }

    protected XYSeries readCSVsAsSeries(int fileCount, String absPath, boolean skipHeader, char separator) {
        BufferedReader fr = null;
        boolean forceReturn = false;
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
            forceReturn = true;
        } finally {
            FilesFoldersManagement.close(fr);
        }
        if (forceReturn) {
            return null;
        }
        CSVReader reader = null;
        try {
            if (skipHeader) {
                reader = new CSVReader(new FileReader(absPath), separator, '\'', 1);
            } else {
                reader = new CSVReader(new FileReader(absPath), separator);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
            forceReturn = true;
        }
        if (forceReturn) {
            return null;
        }
        String[] nextLine;
        if (reader == null) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, "Null reader");
            FilesFoldersManagement.close(reader);
            return null;
        }
        XYSeries csvSeries = new XYSeries("csv_" + fileCount, false);
        try {
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].isEmpty() || nextLine[1].isEmpty() ||
                    nextLine[0].startsWith("#") ){
                    continue;
                }
                if (logI) {
                    csvSeries.add(Double.parseDouble(nextLine[0]), Math.abs(Double.parseDouble(nextLine[1])));
                } else {
                    csvSeries.add(Double.parseDouble(nextLine[0]), Double.parseDouble(nextLine[1]));
                }
            }
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            FilesFoldersManagement.close(reader);
        }
        return csvSeries;
    }

    // Used with jfreecharts
    protected XYSeries[] readMagnitudesAsSeries(int fileCount,
            int plottedMagnitudesNumber, File csvFile, int totalData, boolean skipHeader) {
        CSVReader reader = null;
        int skipNLines = Math.max(0, totalData * threadsNumber / (ResourcesMAF.DEFAULTSTEPSPLOTTEDMULTI) - 1);
        XYSeries[] exportedMagnitudes = new XYSeries[plottedMagnitudesNumber];
        exportedMagnitudes[0] = new XYSeries(fileCount + "s_v(i)", false);
        String[] nextLine;
        // all but time
        for (int i = 1; i < plottedMagnitudesNumber; i++) {
            exportedMagnitudes[i] = new XYSeries(fileCount + "_" + memristorPilot.getMagnitudeName(i) + "(t)", false);
        }
        try {
            if (skipHeader) {
                reader = new CSVReader(new FileReader(csvFile), ResourcesMAF.CHARCSVSEPARATOR, '\'', 1);
            } else {
                reader = new CSVReader(new FileReader(csvFile), ResourcesMAF.CHARCSVSEPARATOR);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (reader == null) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, "Null reader");
            FilesFoldersManagement.close(reader);
            return null;
        }
        try {
            int partialLineCount = 0;
            readline:
            while ((nextLine = reader.readNext()) != null && nextLine.length > 0) {
                for (int m = 0; m < plottedMagnitudesNumber; m++) {
                    if (nextLine[m].isEmpty()) {
                        continue readline;
                    }
                }
                if (partialLineCount == skipNLines) {
                    partialLineCount = 0;
                    for (int m = 0; m < plottedMagnitudesNumber; m++) {
                        // first v-i instead of time
                        if (m == 0) {
                            if (logI) {
                                exportedMagnitudes[m].add(Double.parseDouble(nextLine[memristorPilot.VINDEX]),
                                        Math.abs(Double.parseDouble(nextLine[memristorPilot.IINDEX])));
                            } else {
                                exportedMagnitudes[m].add(Double.parseDouble(nextLine[memristorPilot.VINDEX]),
                                        Double.parseDouble(nextLine[memristorPilot.IINDEX]));
                            }
                        } else {
                            if (m == memristorPilot.IINDEX && logI) {
                                exportedMagnitudes[m].add(Double.parseDouble(nextLine[memristorPilot.TIMEINDEX]),
                                        Math.abs(Double.parseDouble(nextLine[m])));
                            } else {
                                exportedMagnitudes[m].add(Double.parseDouble(nextLine[memristorPilot.TIMEINDEX]),
                                        Double.parseDouble(nextLine[m]));
                            }
                        }
                    }
                } else {
                    partialLineCount++;
                }
            }
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            FilesFoldersManagement.close(reader);
        }
        return exportedMagnitudes;
    }

    protected static void applyXYChartTheme(JFreeChart chart, boolean isVI, double minIPlotted) {
        NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        DecimalFormat newFormat = new DecimalFormat("00.00E0");
        xAxis.setNumberFormatOverride(newFormat);
        xAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));
        xAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        if (isVI) {
            LogAxis yAxis = new LogAxis(); //(LogAxis) chart.getXYPlot().getRangeAxis();
            yAxis.setBase(10);
            yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            yAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));
            yAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
            yAxis.setLowerBound(minIPlotted);
            chart.getXYPlot().setRangeAxis((ValueAxis) yAxis);
        } else {
            NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
            yAxis.setNumberFormatOverride(newFormat);
            xAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));
            yAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));
            yAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        }

        chart.getPlot().setBackgroundPaint(ResourcesMAF.PLOTBACKGROUND);
        chart.getTitle().setFont(new Font("Arial", Font.PLAIN, 14));
    }

    public ArrayList<JFreeChart> getAllCharts() {
        return allCharts;
    }

    public long getStartTimePlotting() {
        return startTimePlotting;
    }

    public long getEndTimePlotting() {
        return endTimePlotting;
    }

}
