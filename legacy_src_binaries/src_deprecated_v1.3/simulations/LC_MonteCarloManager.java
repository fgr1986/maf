package simulations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import levelCharacterizationUI.LC_UI;
import memristorModels.MemristorMIMThreshold;
import memristorModels.MemristorNonLinearDrift;
import memristorModels.MemristorYakopcic;
import memristorModels.MemristorModifiedSimmons;
import memristorModels.MemristorModel;
import memristorModels.MemristorQ;
import memristorModels.MemristorSimmons;
import memristorModels.MemristorTEAM;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import simulationObjects.*;

public class LC_MonteCarloManager extends Thread implements LC_ThreadManager {

	private MemristorModel memristorPilot;
	private InputVoltage voltagePilot;
	private Timing timing;
	private JFreeChart xt_goingOn_chart;
	private JFreeChart xt_goingOff_chart;
	private JFreeChart mt_goingOn_chart;
	private JFreeChart mt_goingOff_chart;;
	private JFreeChart levelst_goingOn_chart;
	private JFreeChart levelst_goingOff_chart;
	private double[][] levelsStatisticsON;
	private int threadsCorrectlyProcessedON;
	private int threadsBadlyProcessedON;
	private double[][] levelsStatisticsOFF;
	private int threadsCorrectlyProcessedOFF;
	private int threadsBadlyProcessedOFF;
	private int threadsReachedAllStatesON;
	private int threadsReachedAllStatesOFF;
	private boolean printOnHistogram;
	private boolean printOffHistogram;
	private String title;
	private int threadsNumber;
	private int threadsNumberGoingON;
	private int threadsNumberGoingOFF;
	private int memristanceLevels;
	private double maxTimeComputed;
	private int storeRatio;
	private boolean reduceStoredData;
	private String outputFolder;
	private volatile boolean interrupted = false;
	private LC_UI ccui;
	private ArrayList<LC_Thread> threads;
	private ArrayList<SimulationResult> simulationResults;
	private SimulationResult simulationResult;
	private long seed;
	private Random random;
	private long startTime;
	private long endTime;
	private long startTimePlotting;
	private long endTimePlotting;
	private boolean displayLegend;
	private int magnitudeValuesSize;
	private boolean storeDirectlyInDisk;

	public LC_MonteCarloManager(LC_UI ccui, int threadsNumber,
			int memristanceLevels, double maxTimeComputed,
			MemristorModel memristorPilot, InputVoltage voltagePilot,
			Timing timing, String title, boolean storeDirectlyInDisk,
			int magnitudeValuesSize, boolean reduceStoredData, int storeRatio,
			String outputFolder, boolean displayLegend) {
		super();
		this.displayLegend = displayLegend;
		this.memristanceLevels = memristanceLevels;
		this.maxTimeComputed = maxTimeComputed;
		this.ccui = ccui;
		this.threadsNumber = threadsNumber;
		this.memristorPilot = memristorPilot;
		this.voltagePilot = voltagePilot;
		this.timing = timing;
		this.title = title;
		this.storeDirectlyInDisk = storeDirectlyInDisk;
		this.magnitudeValuesSize = magnitudeValuesSize;
		this.reduceStoredData = reduceStoredData;
		this.outputFolder = outputFolder;
		interrupted = true;
		this.storeRatio = storeRatio;
		seed = System.currentTimeMillis();
		random = new Random(seed);
	}

	public LC_MonteCarloManager() {
		super();
	}

	public void run() {
		threadsNumberGoingON = 0;
		threadsNumberGoingOFF = 0;
		threadsBadlyProcessedON = 0;
		threadsBadlyProcessedOFF = 0;
		threadsBadlyProcessedOFF = 0;
		threadsCorrectlyProcessedON = 0;
		threadsCorrectlyProcessedOFF = 0;
		startTime = System.nanoTime();
		interrupted = false;
		simulationResult = new SimulationResult(true, 0,
				"Every simulation ended OK", "Every simulation ended OK");
		simulationResults = new ArrayList<SimulationResult>();
		threads = new ArrayList<LC_Thread>();
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
				Level.CONFIG, "Setting the system");
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
				Level.CONFIG, "Creating and adding the threads");
		boolean createFolder = false;
		boolean exists = (new File(outputFolder)).exists();
		if (exists) {
			deleteDir(new File(outputFolder));
		}
		try {
			createFolder = (new File(outputFolder)).mkdirs();
			if (!createFolder) {
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
						Level.WARNING, "Could not create the tmp folder");
				return;
			}
		} catch (Exception ioe) {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.SEVERE,
					"Could not create the tmp folder\n"
							+ ioe.getLocalizedMessage());
			simulationResult = new SimulationResult(false, 0, "I/O error: "
					+ ioe.getLocalizedMessage());
			ccui.reportResults();
			return;
		}
		// create the simulation threads
		String auxFile = "";
		boolean goingON = true;
		threadsNumberGoingON = 0;
		threadsNumberGoingOFF = 0;
		Timing t = null;
		int invertA = 1;
		for (int i = 0; i < threadsNumber; i++) {
			if (timing.isUniformSteps()) {
				t = new TimingUniform(timing.getTStep(), false);
			} else {
				t = new TimingAdaptative(timing.getMinTStep(),
						timing.getMaxTStep(), false);
			}
			// half the simulations write '1', half '0'
			if (i >= threadsNumber / 2) {
				goingON = false;
			}
			// System.out.println(goingON ? "goingON" : "goingOFF");
			if (goingON) {
				auxFile = outputFolder + "/" + "off2on_thread_" + i + "."
						+ ResourcesAndUsefulFunctions.OUTPUTEXTENSION;
				threadsNumberGoingON++;
			} else {
				auxFile = outputFolder + "/" + "on2off_thread_" + i + "."
						+ ResourcesAndUsefulFunctions.OUTPUTEXTENSION;
				threadsNumberGoingOFF++;
				invertA = -1;
			}
			if (memristorPilot.getAmplitudeSignGoingON() != 1) {
				invertA = -1 * invertA;
			}
			threads.add(new LC_Thread(i, memristanceLevels, maxTimeComputed,
					goingON, getRandomMemristor(), getRandomInputVoltage(t,
							invertA), t, title, storeDirectlyInDisk,
					magnitudeValuesSize, reduceStoredData, storeRatio, auxFile));
		}
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
				Level.INFO, "Starting the threads");
		for (LC_Thread mt : threads) {
			mt.start();
		}
		try {
			for (LC_Thread mt : threads) {
				mt.join();
			}
		} catch (InterruptedException e) {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.WARNING, "Thread manager Interrupted.");
			simulationResult = new SimulationResult(false, 0,
					"Thread manager Interrupted", "Thread manager Interrupted");
		}
		for (LC_Thread lt : threads) {
			if (!lt.getSimulationResult().isCorrect()) {
				simulationResult = new SimulationResult(false, 0, "Simulation "
						+ lt.getId() + " ended badly", "Simulation "
						+ lt.getId() + " ended badly");
				if (lt.isGoingON()) {
					threadsBadlyProcessedON++;
				} else {
					threadsBadlyProcessedOFF++;
				}
			} else {
				if (lt.isGoingON()) {
					threadsCorrectlyProcessedON++;
				} else {
					threadsCorrectlyProcessedOFF++;
				}
			}
			simulationResults.add(lt.getSimulationResult());
		}

		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
				Level.INFO, "Simulation done");
		endTime = System.nanoTime();
		if (!interrupted) {
			ccui.reportResults();
		}
	}

	private File[] filesInFolder(File folder) {
		if (folder.isDirectory()) {
			return folder.listFiles();
		} else {
			return null;
		}

	}

	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

	public MemristorModel getRandomMemristor() {
		MemristorModel m = null;
		switch (memristorPilot.getMemristorType()) {
		case ResourcesAndUsefulFunctions.QCONTROLLEDMODEL:
			m = new MemristorQ();
			break;
		case ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL:
			m = new MemristorNonLinearDrift();
			break;
		case ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL:
			m = new MemristorYakopcic();
			break;
		case ResourcesAndUsefulFunctions.SIMMONSMODEL:
			m = new MemristorSimmons();
			break;
		case ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL:
			m = new MemristorModifiedSimmons();
			break;
		case ResourcesAndUsefulFunctions.TEAMMODEL:
			m = new MemristorTEAM();
			break;
		case ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL:
			m = new MemristorMIMThreshold();
			break;
		default:
			break;
		}
		for (MemristorParameter p : memristorPilot.getParameters()) {
			if (p.isAllowVariations()) {
				m.getParameter(p.getId()).setValue(
						p.getValue() + p.getSigma() * gaussian());
			} else {
				m.getParameter(p.getId()).setValue(p.getValue());
			}
		}

		return m;
	}

	public InputVoltage getRandomInputVoltage(Timing t, int amplitudeSign) {
		InputVoltage random = new InputVoltage(voltagePilot, t);
		for (InputVoltageParameter p : voltagePilot.getInputVoltageParameters()) {
			if (p.isAllowVariations()) {
				random.getParameter(p.getId()).setValue(
						p.getValue() + p.getSigma() * gaussian());
			} else {
				random.getParameter(p.getId()).setValue(p.getValue());
			}
		}
		random.getParameter(voltagePilot.CONSTVINDEX).setValue(
				amplitudeSign
						* random.getParameter(voltagePilot.CONSTVINDEX)
								.getValue());
		return random;
	}

	public double gaussian() {
		return random.nextGaussian();
	}

	public double[] getSelectedRow(double[][] data, int rowIndex) {
		try {
			double[] row = new double[data[rowIndex].length];
			for (int c = 0; c < data[rowIndex].length; c++) {
				row[c] = data[rowIndex][c];
			}
			return row;
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
			return null;
		}
	}

	public SimulationResult prepareStatisticsAndPlots() {
		startTimePlotting = System.nanoTime();
		BufferedReader inputStream = null;

		try {
			boolean exportHistogram = true;
			File[] files2beProcessed = filesInFolder(new File(outputFolder));
			int[] totalData = countDataFromFiles(files2beProcessed);
			Writer histOut;
			
			int skipNLines = 0;
			// [mean, min,max][]
			levelsStatisticsON = new double[memristanceLevels][4];
			levelsStatisticsOFF = new double[memristanceLevels][4];
			threadsReachedAllStatesON = 0;
			threadsReachedAllStatesOFF = 0;
			XYSeriesCollection x_t_goingOn_data = new XYSeriesCollection();
			XYSeriesCollection x_t_goingOff_data = new XYSeriesCollection();
			XYSeriesCollection m_t_goingOn_data = new XYSeriesCollection();
			XYSeriesCollection m_t_goingOff_data = new XYSeriesCollection();
			XYSeriesCollection levels_t_goingOn_data = new XYSeriesCollection();
			XYSeriesCollection levels_t_goingOff_data = new XYSeriesCollection();

			XYSeries x_t_series;
			XYSeries m_t_series;
			XYSeries levels_t_series;
			boolean tGoingOn;

			int rowCount = 0;
			int rowCountToSkip = 0;
			String readRow = "";
			String dataS[] = null;
			double data[] = null;

			HistogramDataset dataset_goingOn = new HistogramDataset();
			dataset_goingOn.setType(HistogramType.FREQUENCY);
			double[][] voltageTimeNormalizedStateLevelsAux_goingOn = new double[memristanceLevels][threadsNumberGoingON];
			HistogramDataset dataset_goingOff = new HistogramDataset();
			dataset_goingOff.setType(HistogramType.FREQUENCY);
			double[][] voltageTimeNormalizedStateLevelsAux_goingOff = new double[memristanceLevels][threadsNumberGoingOFF];
			int bins = Math.max(5, threadsNumber / 10);
			int onCounter = 0;
			int offCounter = 0;
			int threadCounter = 0;
			int reachedOnCounter = 0;
			int reachedOffCounter = 0;
			double parcialSumON = 0;
			double parcialSumOFF = 0;
			printOnHistogram = false;
			printOffHistogram = false;
			for (int i = 1; i < memristanceLevels; i++) {
				// set a minimum which will be overwritten
				levelsStatisticsON[i][1] = 1e5;
				// set a maximun which will be overwritten
				levelsStatisticsON[i][2] = 0;
				// set a minimum which will be overwritten
				levelsStatisticsOFF[i][1] = 1e5;
				// set a maximun which will be overwritten
				levelsStatisticsOFF[i][2] = 0;
				threadCounter = 0;
				onCounter = 0;
				offCounter = 0;
				reachedOnCounter = 0;
				reachedOffCounter = 0;
				parcialSumON = 0;
				parcialSumOFF = 0;
				for (LC_Thread mt : threads) {
					if (totalData[threadCounter] > 0) {
						if (mt.isGoingON()) {
							if (i == 1) {
								if (mt.isReachedAllStates()) {
									threadsReachedAllStatesON++;
								}
							}
							if (mt.getStateReached(i)) {
								reachedOnCounter++;
								voltageTimeNormalizedStateLevelsAux_goingOn[i][onCounter] = mt
										.getVoltageTimeNormalizedStateLevels()[i][1];
								// Min value
								if (mt.getVoltageTimeNormalizedStateLevels()[i][1] != 0
										&& mt.getVoltageTimeNormalizedStateLevels()[i][1] < levelsStatisticsON[i][1]) {
									levelsStatisticsON[i][1] = mt
											.getVoltageTimeNormalizedStateLevels()[i][1];
								}
								// max value
								if (mt.getVoltageTimeNormalizedStateLevels()[i][1] > levelsStatisticsON[i][2]) {
									levelsStatisticsON[i][2] = mt
											.getVoltageTimeNormalizedStateLevels()[i][1];
								}
							} else {
								voltageTimeNormalizedStateLevelsAux_goingOn[i][onCounter] = Double.NaN;
							}
							parcialSumON += mt
									.getVoltageTimeNormalizedStateLevels()[i][1];
							onCounter++;
						} else {
							if (i == 1) {
								if (mt.isReachedAllStates()) {
									threadsReachedAllStatesOFF++;
								}
							}
							if (mt.getStateReached(i)) {
								reachedOffCounter++;
								voltageTimeNormalizedStateLevelsAux_goingOff[i][offCounter] = mt
										.getVoltageTimeNormalizedStateLevels()[i][1];
								// Min value
								if (mt.getVoltageTimeNormalizedStateLevels()[i][1] != 0
										&& mt.getVoltageTimeNormalizedStateLevels()[i][1] < levelsStatisticsOFF[i][1]) {
									levelsStatisticsOFF[i][1] = mt
											.getVoltageTimeNormalizedStateLevels()[i][1];
								}
								// max value
								if (mt.getVoltageTimeNormalizedStateLevels()[i][1] > levelsStatisticsOFF[i][2]) {
									levelsStatisticsOFF[i][2] = mt
											.getVoltageTimeNormalizedStateLevels()[i][1];
								}
//								outOFF.write(voltageTimeNormalizedStateLevelsAux_goingOn[i][onCounter]+ ";");
							} else {
								voltageTimeNormalizedStateLevelsAux_goingOff[i][offCounter] = Double.NaN;
//								outOFF.write( "NaN;");
							}
							parcialSumOFF += mt
									.getVoltageTimeNormalizedStateLevels()[i][1];
							offCounter++;
						}
						threadCounter++;
					}
				}
				// compute the mean only with the reached all states
				if (reachedOnCounter == 0) {
					levelsStatisticsON[i][0] = Double.NaN;
				} else {
					printOnHistogram = true;
					levelsStatisticsON[i][3] = reachedOnCounter;
					levelsStatisticsON[i][0] = parcialSumON / reachedOnCounter;
					dataset_goingOn
							.addSeries(
									"Level " + i,
									getSelectedRow(
											voltageTimeNormalizedStateLevelsAux_goingOn,
											i), bins);
				}
				if (reachedOffCounter == 0) {
					levelsStatisticsOFF[i][0] = Double.NaN;
				} else {
					printOffHistogram = true;
					levelsStatisticsOFF[i][3] = reachedOffCounter;
					levelsStatisticsOFF[i][0] = parcialSumOFF
							/ reachedOffCounter;
					dataset_goingOff
							.addSeries(
									"Level " + i,
									getSelectedRow(
											voltageTimeNormalizedStateLevelsAux_goingOff,
											i), bins);
				}
			}
			if(exportHistogram){
				histOut = new OutputStreamWriter(new FileOutputStream(
						outputFolder + "/histogram.m"));
				int aux_thread_count = Math.min(threadsNumberGoingON, threadsNumberGoingOFF);
				histOut.write("%Going_OFF2ON");
				for(int i=0;i< memristanceLevels;i++){
					histOut.write("\n%Level_" + i + "\n");
					histOut.write("off_on_" + i + "=[");
					for(int j=0;j < (aux_thread_count-1);j++){
						histOut.write(voltageTimeNormalizedStateLevelsAux_goingOn[i][j]+ ";");
					}
					histOut.write(voltageTimeNormalizedStateLevelsAux_goingOn[i][aux_thread_count-1] + "");
					histOut.write("];\n");			
				}
				histOut.write("%Going_ON2OFF");
				for(int i=0;i< memristanceLevels;i++){
					histOut.write("\n%Level_" + i + "\n");
					histOut.write("on_off_" + i + "=[");
					for(int j=0;j < (aux_thread_count-1);j++){
						histOut.write(voltageTimeNormalizedStateLevelsAux_goingOff[i][j]+ ";");
					}
					histOut.write(voltageTimeNormalizedStateLevelsAux_goingOff[i][aux_thread_count-1] + "");
					histOut.write("];\n");			
				}
				histOut.close();
			}
			if (printOnHistogram) {
				levelst_goingOn_chart = ChartFactory.createHistogram("",
						"Time to reach a specific level (going ON)",
						"Frequency", dataset_goingOn, PlotOrientation.VERTICAL,
						displayLegend, true, false);
			}
			if (printOffHistogram) {
				levelst_goingOff_chart = ChartFactory.createHistogram("",
						"Time to reach a specific level (going OFF)",
						"Frequency", dataset_goingOff,
						PlotOrientation.VERTICAL, displayLegend, true, false);
			}
			// for each file

			for (int i = 0; i < threadsNumber; i++) {
				if (totalData[i] > 0) {
					skipNLines = (int) Math
							.max(0,
									totalData[i]
											* threadsNumber
											/ (ResourcesAndUsefulFunctions.DEFAULTSTEPSPLOTTEDMULTI)
											- 1);
					if (files2beProcessed[i].getName().startsWith("off2on")) {
						tGoingOn = true;
					} else {
						tGoingOn = false;
					}
					x_t_series = new XYSeries(i + "_x(t)");
					m_t_series = new XYSeries(i + "_Rm(t)");
					levels_t_series = new XYSeries(i + "_time needed(level)");
					rowCount = 0;
					rowCountToSkip = 0;
					readRow = "";
					dataS = null;
					data = null;
					inputStream = new BufferedReader(new FileReader(
							files2beProcessed[i]));

					// titles
					inputStream.readLine();
					rowCount++;
					while (((readRow = inputStream.readLine()) != null)
							&& rowCount < totalData[i]) {
						if (rowCountToSkip == skipNLines) {
							dataS = readRow.split("\t");
							data = String2Double(dataS);
							x_t_series
									.add(data[memristorPilot.TIMEINDEX],
											data[memristorPilot.RINDEX
													+ 1
													+ memristorPilot
															.getNormalizedStateIndex()]);
							m_t_series.add(data[memristorPilot.TIMEINDEX],
									data[memristorPilot.RINDEX]);
							levels_t_series.add(data[memristorPilot.VINDEX],
									data[memristorPilot.RINDEX]);
							rowCountToSkip = 0;
						} else {
							rowCountToSkip++;
						}
						rowCount++;

					}
					if (tGoingOn) {
						x_t_goingOn_data.addSeries(x_t_series);
						m_t_goingOn_data.addSeries(m_t_series);
						levels_t_goingOn_data.addSeries(levels_t_series);
					} else {
						x_t_goingOff_data.addSeries(x_t_series);
						m_t_goingOff_data.addSeries(m_t_series);
						levels_t_goingOff_data.addSeries(levels_t_series);
					}

					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			xt_goingOn_chart = ChartFactory.createXYLineChart(
					"x(t) going ON Plot, " + "'" + title + "'", "t (s)", "x",
					x_t_goingOn_data, PlotOrientation.VERTICAL, displayLegend,
					true, true);
			mt_goingOn_chart = ChartFactory.createXYLineChart("Rm(t)  Plot, "
					+ "'" + title + "'", "time (s)", "Rm(t) "
					+ ResourcesAndUsefulFunctions.OHM2, m_t_goingOn_data,
					PlotOrientation.VERTICAL, displayLegend, true, true);
			xt_goingOff_chart = ChartFactory.createXYLineChart(
					"x(t) Going OFF Plot, " + "'" + title + "'", "t (s)", "x",
					x_t_goingOff_data, PlotOrientation.VERTICAL, displayLegend,
					true, true);
			mt_goingOff_chart = ChartFactory.createXYLineChart("Rm(t)  Plot, "
					+ "'" + title + "'", "time (s)", "Rm(t) "
					+ ResourcesAndUsefulFunctions.OHM2, m_t_goingOff_data,
					PlotOrientation.VERTICAL, displayLegend, true, true);

			if (printOnHistogram) {
				levelst_goingOn_chart.getPlot().setBackgroundPaint(
						ResourcesAndUsefulFunctions.PLOTBACKGROUND);
			}
			if (printOffHistogram) {
				levelst_goingOff_chart.getPlot().setBackgroundPaint(
						ResourcesAndUsefulFunctions.PLOTBACKGROUND);
			}
			xt_goingOn_chart.getPlot().setBackgroundPaint(
					ResourcesAndUsefulFunctions.PLOTBACKGROUND);
			mt_goingOn_chart.getPlot().setBackgroundPaint(
					ResourcesAndUsefulFunctions.PLOTBACKGROUND);
			xt_goingOff_chart.getPlot().setBackgroundPaint(
					ResourcesAndUsefulFunctions.PLOTBACKGROUND);
			mt_goingOff_chart.getPlot().setBackgroundPaint(
					ResourcesAndUsefulFunctions.PLOTBACKGROUND);

		} catch (Exception exc) {
			return new SimulationResult(false, 0,
					"Error plotting the results. " + exc.getLocalizedMessage(),
					"Error plotting the results. " + exc.getLocalizedMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		endTimePlotting = System.nanoTime();
		return new SimulationResult(true, 0, "Correctly Plotted.",
				"Correctly Plotted.");
	}

	public double[][] getLevelsStatisticsON() {
		return levelsStatisticsON;
	}

	public void setLevelsStatisticsON(double[][] levelsStatisticsON) {
		this.levelsStatisticsON = levelsStatisticsON;
	}

	public double[][] getLevelsStatisticsOFF() {
		return levelsStatisticsOFF;
	}

	public void setLevelsStatisticsOFF(double[][] levelsStatisticsOFF) {
		this.levelsStatisticsOFF = levelsStatisticsOFF;
	}

	private double[] String2Double(String[] s) {
		double data[] = new double[s.length];
		int i = 0;
		for (String ss : s) {
			data[i] = Double.parseDouble(ss);
			i++;
		}
		return data;
	}

	public int[] countDataFromFiles(File[] files) {
		LineNumberReader lnr = null;
		int[] count = new int[files.length];
		try {
			for (int i = 0; i < files.length; i++) {
				// if (!files[i].getName().startsWith("histogramGoing")) {
				try {
					lnr = new LineNumberReader(new FileReader(files[i]));
					lnr.skip(Long.MAX_VALUE);
					count[i] = lnr.getLineNumber();
					if (lnr != null) {
						lnr.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					count[i] = -666;
				}
				// }
			}
		} catch (Exception exc) {
			count = null;
		}
		return count;
	}

	public MemristorModel getMemristorPilot() {
		return memristorPilot;
	}

	public void setMemristor(MemristorModel memristorPilot) {
		this.memristorPilot = memristorPilot;
	}

	public JFreeChart getXt_goingOn_chart() {
		return xt_goingOn_chart;
	}

	public void setXt_goingOn_chart(JFreeChart xt_goingOn_chart) {
		this.xt_goingOn_chart = xt_goingOn_chart;
	}

	public JFreeChart getXt_goingOff_chart() {
		return xt_goingOff_chart;
	}

	public void setXt_goingOff_chart(JFreeChart xt_goingOff_chart) {
		this.xt_goingOff_chart = xt_goingOff_chart;
	}

	public JFreeChart getMt_goingOn_chart() {
		return mt_goingOn_chart;
	}

	public void setMt_goingOn_chart(JFreeChart mt_goingOn_chart) {
		this.mt_goingOn_chart = mt_goingOn_chart;
	}

	public JFreeChart getMt_goingOff_chart() {
		return mt_goingOff_chart;
	}

	public void setMt_goingOff_chart(JFreeChart mt_goingOff_chart) {
		this.mt_goingOff_chart = mt_goingOff_chart;
	}

	public JFreeChart getLevelst_goingOn_chart() {
		return levelst_goingOn_chart;
	}

	public void setLevelst_goingOn_chart(JFreeChart levelst_goingOn_chart) {
		this.levelst_goingOn_chart = levelst_goingOn_chart;
	}

	public JFreeChart getLevelst_goingOff_chart() {
		return levelst_goingOff_chart;
	}

	public void setLevelst_goingOff_chart(JFreeChart levelst_goingOff_chart) {
		this.levelst_goingOff_chart = levelst_goingOff_chart;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		for (LC_Thread mt : threads) {
			mt.setInterrupted(interrupted);
		}
		this.interrupted = interrupted;
	}

	public int getThreadsNumber() {
		return threadsNumber;
	}

	public void setThreadsNumber(int threadsNumber) {
		this.threadsNumber = threadsNumber;
	}

	public int getMemristanceLevels() {
		return memristanceLevels;
	}

	public void setMemristanceLevels(int memristanceLevels) {
		this.memristanceLevels = memristanceLevels;
	}

	public SimulationResult getSimulationResult() {
		// TODO Auto-generated method stub
		return simulationResult;
	}

	public int getThreadsCorrectlyProcessedON() {
		return threadsCorrectlyProcessedON;
	}

	public void setThreadsCorrectlyProcessedON(int threadsCorrectlyProcessedON) {
		this.threadsCorrectlyProcessedON = threadsCorrectlyProcessedON;
	}

	public int getThreadsBadlyProcessedON() {
		return threadsBadlyProcessedON;
	}

	public void setThreadsBadlyProcessedON(int threadsBadlyProcessedON) {
		this.threadsBadlyProcessedON = threadsBadlyProcessedON;
	}

	public int getThreadsCorrectlyProcessedOFF() {
		return threadsCorrectlyProcessedOFF;
	}

	public void setThreadsCorrectlyProcessedOFF(int threadsCorrectlyProcessedOFF) {
		this.threadsCorrectlyProcessedOFF = threadsCorrectlyProcessedOFF;
	}

	public int getThreadsBadlyProcessedOFF() {
		return threadsBadlyProcessedOFF;
	}

	public void setThreadsBadlyProcessedOFF(int threadsBadlyProcessedOFF) {
		this.threadsBadlyProcessedOFF = threadsBadlyProcessedOFF;
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

	public long getStartTimePlotting() {
		return startTimePlotting;
	}

	public void setStartTimePlotting(long startTimePlotting) {
		this.startTimePlotting = startTimePlotting;
	}

	public long getEndTimePlotting() {
		return endTimePlotting;
	}

	public void setEndTimePlotting(long endTimePlotting) {
		this.endTimePlotting = endTimePlotting;
	}

	public int getThreadsReachedAllStatesON() {
		return threadsReachedAllStatesON;
	}

	public void setThreadsReachedAllStatesON(int threadsReachedAllStatesON) {
		this.threadsReachedAllStatesON = threadsReachedAllStatesON;
	}

	public int getThreadsReachedAllStatesOFF() {
		return threadsReachedAllStatesOFF;
	}

	public void setThreadsReachedAllStatesOFF(int threadsReachedAllStatesOFF) {
		this.threadsReachedAllStatesOFF = threadsReachedAllStatesOFF;
	}

	public int getThreadsNumberGoingON() {
		return threadsNumberGoingON;
	}

	public void setThreadsNumberGoingON(int threadsNumberGoingON) {
		this.threadsNumberGoingON = threadsNumberGoingON;
	}

	public int getThreadsNumberGoingOFF() {
		return threadsNumberGoingOFF;
	}

	public void setThreadsNumberGoingOFF(int threadsNumberGoingOFF) {
		this.threadsNumberGoingOFF = threadsNumberGoingOFF;
	}

	public int getMagnitudeValuesSize() {
		// TODO Auto-generated method stub
		return magnitudeValuesSize;
	}

	public boolean isPrintOnHistogram() {
		return printOnHistogram;
	}

	public void setPrintOnHistogram(boolean printOnHistogram) {
		this.printOnHistogram = printOnHistogram;
	}

	public boolean isPrintOffHistogram() {
		return printOffHistogram;
	}

	public void setPrintOffHistogram(boolean printOffHistogram) {
		this.printOffHistogram = printOffHistogram;
	}

}
