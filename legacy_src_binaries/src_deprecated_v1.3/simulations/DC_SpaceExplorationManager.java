package simulations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import deviceCharacterizationUI.DC_UI;

import simulationObjects.*;

public class DC_SpaceExplorationManager extends Thread implements
		DC_ThreadManager {

	private MemristorModel memristorPilot;
	private InputVoltage voltagePilot;
	private Timing timing;
	private JFreeChart vi_chart;
	private JFreeChart vit_chart;
	private JFreeChart mt_chart;
	private JFreeChart mv_chart;
	private ArrayList<JFreeChart> sv_charts;
	private int threadsCorrectlyProcessed;
	private int threadsBadlyProcessed;
	private String title;
	private int threadsNumber;
	private int storeRatio;
	private boolean reduceStoredData;
	private String outputFolder;
	private volatile boolean interrupted = false;
	private DC_UI ccui;
	private ArrayList<DC_Thread> threads;
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
	private  boolean storeDirectlyInDisk;

	public DC_SpaceExplorationManager(DC_UI ccui,
			MemristorModel memristorPilot, InputVoltage voltagePilot,
			Timing timing, String title,  boolean storeDirectlyInDisk, int magnitudeValuesSize,
			boolean reduceStoredData,
			int storeRatio, String outputFolder, boolean displayLegend) {
		super();
		this.displayLegend = displayLegend;
		this.ccui = ccui;
		this.threadsNumber = 0;
		this.memristorPilot = memristorPilot;
		this.voltagePilot = voltagePilot;
		this.timing = timing;
		this.title = title;
		this.storeDirectlyInDisk  =  storeDirectlyInDisk;
		this.magnitudeValuesSize = magnitudeValuesSize;
		this.reduceStoredData = reduceStoredData;
		this.outputFolder = outputFolder;
		interrupted = true;
		this.storeRatio = storeRatio;
		seed = System.currentTimeMillis();
		random = new Random(seed);
	}

	public DC_SpaceExplorationManager() {
		super();
	}

	public void run() {
		startTime = System.nanoTime();
		interrupted = false;
		simulationResult = new SimulationResult(true, 0,
				"Every simulation ended OK", "Every simulation ended OK");
		simulationResults = new ArrayList<SimulationResult>();
		threads = new ArrayList<DC_Thread>();
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.CONFIG,
				"Setting the system...");
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
				"Creating and adding the threads");
		boolean createFolder = false;
		boolean exists = (new File(outputFolder)).exists();
		if (exists) {
			deleteDir(new File(outputFolder));
		}
		try {
			createFolder = (new File(outputFolder)).mkdirs();
			if (!createFolder) {
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.SEVERE,
						"Could not create the tmp folder");
				return;
			}
		} catch (Exception ioe) {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.SEVERE,
					"Could not create the tmp folder\n"
							+ ioe.getLocalizedMessage());
			simulationResult = new SimulationResult(false,
					0, "I/O error: " + ioe.getLocalizedMessage());
			ccui.reportResults();
			return;
		}
		// create the simulation threads
		String auxFile;
		int i = 0;
		Timing t1 = null;
		Timing t2 = null;
		for(InputVoltageParameter p : voltagePilot.getInputVoltageParameters()){
			if (p.isAllowVariations() && p.getMaxVariation() != 0 ) {
				if (timing.isUniformSteps()) {
					t1 = new TimingUniform(timing.getT0(), timing.getTf(),
							timing.getTStep());
					t2 = new TimingUniform(timing.getT0(), timing.getTf(),
							timing.getTStep());
				} else {
					t1 = new TimingAdaptative(timing.getT0(), timing.getTf(),
							timing.getMinTStep(), timing.getMaxTStep());
					t2 = new TimingAdaptative(timing.getT0(), timing.getTf(),
							timing.getMinTStep(), timing.getMaxTStep());
				}
				auxFile = outputFolder + "/" + "thread_" + i + "."
						+ ResourcesAndUsefulFunctions.OUTPUTEXTENSION;
				threads.add(new DC_Thread(i, getMemristorCopy(),
						getLimitInputVoltage(t1, true, p.getTag()), t1, title, 
						storeDirectlyInDisk, magnitudeValuesSize, reduceStoredData,
						storeRatio,  auxFile));
				i++;
				auxFile = outputFolder + "/" + "thread_" + i + "."
						+ ResourcesAndUsefulFunctions.OUTPUTEXTENSION;
				threads.add(new DC_Thread(i, getMemristorCopy(),
						getLimitInputVoltage(t1, false, p.getTag()), t2, title, storeDirectlyInDisk,
						magnitudeValuesSize, reduceStoredData,
						storeRatio,  auxFile));
				i++;
				threadsNumber += 2;
			}
		}
		for (MemristorParameter p : memristorPilot.getParameters()) {
			if (p.isAllowVariations()) {
				for(int j=0;j<p.getValueList().length;j++){
					if (timing.isUniformSteps()) {
						t1 = new TimingUniform(timing.getT0(), timing.getTf(),
								timing.getTStep());
					} else {
						t1 = new TimingAdaptative(timing.getT0(), timing.getTf(),
								timing.getMinTStep(), timing.getMaxTStep());
					}
					auxFile = outputFolder + "/" + "thread_" + i + "."
							+ ResourcesAndUsefulFunctions.OUTPUTEXTENSION;
					threads.add(new DC_Thread(i, getSpecificMemristor(p, p.getValueList()[j]),
							new InputVoltage(voltagePilot, t1), t1, title, storeDirectlyInDisk, magnitudeValuesSize, reduceStoredData,
							storeRatio,  auxFile));
					i++;
					threadsNumber++;
				}
			}
		}
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
				"Starting the threads.");
		for (DC_Thread mt : threads) {
			mt.start();
		}
		try {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
					"Waiting for the threads.");
			for (DC_Thread mt : threads) {
				mt.join();
			}
		} catch (InterruptedException e) {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.WARNING,
					"Thread manager Interrupted.");
			simulationResult = new SimulationResult(false, 0, "Thread manager Interrupted", "Thread manager Interrupted");
			
		}

		for (DC_Thread mt : threads) {
			if (!mt.getSimulationResult().isCorrect()) {
				simulationResult = new SimulationResult(false, 0, "Simulation "
						+ mt.getId() + " ended badly", "Simulation "
						+ mt.getId() + " ended badly");
				threadsBadlyProcessed++;
			} else {
				threadsCorrectlyProcessed++;
			}
			simulationResults.add(mt.getSimulationResult());
		}
		Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
				"Simulation done");

		endTime = System.nanoTime();
		if (!interrupted) {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
					"Simulation done");
			ccui.reportResults();
		} else {
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
					"Simulation Interrupted");
			ccui.reportResults();
		}
	}
	
	public MemristorModel getMemristorCopy() {
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
			m.getParameter(p.getId()).setValue(
					p.getValue());
			m.getParameter(p.getId()).setSigma(
					p.getSigma());
			m.getParameter(p.getId()).setValueList(p.getValueList());
		}
		return m;
	}

	public InputVoltage getLimitInputVoltage(Timing t, boolean inverted, String tag) {
		InputVoltage random = new InputVoltage(voltagePilot, t);
		int invertedAmplitude = 1;
		if (inverted) {
			invertedAmplitude = -1;
		}
		for (InputVoltageParameter p : voltagePilot.getInputVoltageParameters()) {
			if (p.getTag().equalsIgnoreCase(tag)) {
				random.getParameter(p.getId()).setValue(
						p.getValue() + invertedAmplitude
								* p.getMaxVariation());
			}
		}
		return random;
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

	public MemristorModel getSpecificMemristor(MemristorParameter p, double value) {
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
		default:
			break;
		}
		m.getParameter(p.getId()).setValue(value);
		return m;
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

	public void prepareStatisticsAndPlots() {
		simulationResult = new SimulationResult(true, 0, "Correctly plotted",
				"Correctly plotted");
		if (threadsNumber > 0) {
			startTimePlotting = System.nanoTime();
			BufferedReader inputStream = null;
			int svIndex = 0;
			int stateVariablesCount = 0;
			try {
				File[] files2beProcessed = filesInFolder(new File(outputFolder));
				int[] totalData = countDataFromFiles(files2beProcessed);
				int skipNLines = 0;

				XYSeriesCollection v_i_data = new XYSeriesCollection();
				XYSeriesCollection v_i_t_data = new XYSeriesCollection();
				XYSeriesCollection m_t_data = new XYSeriesCollection();
				XYSeriesCollection m_v_data = new XYSeriesCollection();
				ArrayList<XYSeriesCollection> stateVariables2Plot_data = new ArrayList<XYSeriesCollection>();
				for (int i = 0; i < memristorPilot.getStateVariables2Plot()
						.size(); i++) {
					stateVariables2Plot_data.add(new XYSeriesCollection());
				}

				XYSeries v_i_series;// = new XYSeries("v-i", false);
				XYSeries v_t_series;// = new XYSeries("v(t)");
				XYSeries i_t_series;// = new XYSeries("i(t)");
				XYSeries m_t_series;// = new XYSeries("Rm(t)");
				XYSeries m_v_series;// = new XYSeries("Rm(v)", false);
				ArrayList<XYSeries> stateVariables2Plot;

				int rowCount = 0;
				int rowCountToSkip = 0;
				String readRow = "";
				String dataS[] = null;
				double data[] = null;

				// for each file
				for (int i = 0; i < threadsNumber; i++) {
					if (totalData[i] > 0) {
						skipNLines = (int) Math.max(0, totalData[i]
								* threadsNumber
								/ (ResourcesAndUsefulFunctions.DEFAULTSTEPSPLOTTEDMULTI) - 1);
						v_i_series = new XYSeries(i + "_v(i)", false);
						m_t_series = new XYSeries(i + "_Rm(t)");
						m_v_series = new XYSeries(i + "_Rm(v)", false);
						v_t_series = new XYSeries(i + "_v(t)");
						i_t_series = new XYSeries(i + "_i(t)");
						stateVariables2Plot = new ArrayList<XYSeries>();
						for (Magnitude sv : memristorPilot
								.getStateVariables2Plot()) {
							stateVariables2Plot.add(new XYSeries(i + "_"
									+ sv.getTag()));
						}

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
						// titles
						inputStream.readLine();
						while (((readRow = inputStream.readLine()) != null)
								&& rowCount < totalData[i]) {
							if (rowCountToSkip == skipNLines) {
								dataS = readRow.split("\t");
								data = String2Double(dataS);
								v_i_series.add(
										data[memristorPilot.VINDEX],
										memristorPilot.getElectricalMagnitude(
												memristorPilot.IINDEX)
												.getPlotScaleFactor()
												* data[memristorPilot.IINDEX]);
								v_t_series.add(data[memristorPilot.TIMEINDEX],
										data[memristorPilot.VINDEX]);
								i_t_series.add(
										data[memristorPilot.TIMEINDEX],
										memristorPilot.getElectricalMagnitude(
												memristorPilot.IINDEX)
												.getPlotScaleFactor()
												* data[memristorPilot.IINDEX]);
								m_t_series.add(data[memristorPilot.TIMEINDEX],
										data[memristorPilot.RINDEX]);
								m_v_series.add(data[memristorPilot.VINDEX],
										data[memristorPilot.RINDEX]);
								svIndex = 0;
								for (Magnitude sv : memristorPilot
										.getStateVariables2Plot()) {
									stateVariables2Plot
											.get(svIndex)
											.add(data[memristorPilot.TIMEINDEX],
													sv.getPlotScaleFactor()
															* data[svIndex
																	+ memristorPilot.RINDEX
																	+ 1]);
									svIndex++;
								}
								rowCountToSkip = 0;
							} else {
								rowCountToSkip++;
							}
							rowCount++;

						}

						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						v_i_data.addSeries(v_i_series);
						v_i_t_data.addSeries(v_t_series);
						v_i_t_data.addSeries(i_t_series);
						m_t_data.addSeries(m_t_series);
						m_v_data.addSeries(m_v_series);
						stateVariablesCount = 0;
						for (XYSeriesCollection xysc : stateVariables2Plot_data) {
							xysc.addSeries(stateVariables2Plot
									.get(stateVariablesCount));
							stateVariablesCount++;
						}
					}
				}
				vi_chart = ChartFactory.createXYLineChart("i(v)  Plot, " + "'"
						+ title + "'", "v (V)", "i (mA)", v_i_data,
						PlotOrientation.VERTICAL, displayLegend, true, true);
				vit_chart = ChartFactory.createXYLineChart(
						"v(t) - i(t)  Plot, " + "'" + title + "'", "time (s)",
						"v(t) (V), i(t) (mA)", v_i_t_data,
						PlotOrientation.VERTICAL, displayLegend, true, true);
				mt_chart = ChartFactory.createXYLineChart("Rm(t)  Plot, " + "'"
						+ title + "'", "time (s)", "Rm(t) " + ResourcesAndUsefulFunctions.OHM2,
						m_t_data, PlotOrientation.VERTICAL, displayLegend,
						true, true);

				mv_chart = ChartFactory.createXYLineChart("Rm(v)  Plot, " + "'"
						+ title + "'", "Voltage(V)", "Rm(v) " + ResourcesAndUsefulFunctions.OHM2,
						m_v_data, PlotOrientation.VERTICAL, displayLegend,
						true, true);
				vi_chart.getPlot().setBackgroundPaint(ResourcesAndUsefulFunctions.PLOTBACKGROUND);
				vit_chart.getPlot()
						.setBackgroundPaint(ResourcesAndUsefulFunctions.PLOTBACKGROUND);
				mt_chart.getPlot().setBackgroundPaint(ResourcesAndUsefulFunctions.PLOTBACKGROUND);
				mv_chart.getPlot().setBackgroundPaint(ResourcesAndUsefulFunctions.PLOTBACKGROUND);
				sv_charts = new ArrayList<JFreeChart>();
				// svIndex = memristorPilot.RINDEX + 1;
				svIndex = 0;
				for (XYSeriesCollection xyc : stateVariables2Plot_data) {
					sv_charts.add(ChartFactory
							.createXYLineChart(
									xyc.getSeries(0)
											.getKey()
											.toString()
											.substring(
													2,
													xyc.getSeries(0).getKey()
															.toString()
															.length())
											+ "",
									"time (s) ",
									xyc.getSeries(0).getKey()
											+ "(t) "
											+ "["
											+ memristorPilot.getStateVariable(
													svIndex).getPrefixUnit()
											+ "]", xyc,
									PlotOrientation.VERTICAL, displayLegend,
									true, true));
					svIndex++;
				}
				for (JFreeChart jfc : sv_charts) {
					jfc.getPlot().setBackgroundPaint(ResourcesAndUsefulFunctions.PLOTBACKGROUND);
				}

			} catch (Exception exc) {
				simulationResult = new SimulationResult(false, 0,
						"Error plotting the results. "
								+ exc.getLocalizedMessage(),
						"Error plotting the results. "
								+ exc.getLocalizedMessage());
			}
			endTimePlotting = System.nanoTime();
			simulationResult = new SimulationResult(true, 0,
					"Correctly Plotted.", "Correctly Plotted.");
		} else {
			simulationResult = new SimulationResult(false, 0,
					"No threads available.", "No threads available.");
		}
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
			}
		} catch (Exception exc) {
			count = null;
		}
		return count;
	}

	public MemristorModel getMemristorPilot() {
		return memristorPilot;
	}

	public void setMemristorPilot(MemristorModel memristorPilot) {
		this.memristorPilot = memristorPilot;
	}

	public InputVoltage getVoltagePilot() {
		return voltagePilot;
	}

	public void setVoltagePilot(InputVoltage voltagePilot) {
		this.voltagePilot = voltagePilot;
	}

	public JFreeChart getVi_chart() {
		return vi_chart;
	}

	public void setVi_chart(JFreeChart vi_chart) {
		this.vi_chart = vi_chart;
	}

	public JFreeChart getVit_chart() {
		return vit_chart;
	}

	public void setVit_chart(JFreeChart vit_chart) {
		this.vit_chart = vit_chart;
	}

	public JFreeChart getMt_chart() {
		return mt_chart;
	}

	public void setMt_chart(JFreeChart mt_chart) {
		this.mt_chart = mt_chart;
	}

	public JFreeChart getMv_chart() {
		return mv_chart;
	}

	public void setMv_chart(JFreeChart mv_chart) {
		this.mv_chart = mv_chart;
	}

	public ArrayList<JFreeChart> getSv_charts() {
		return sv_charts;
	}

	public void setSv_charts(ArrayList<JFreeChart> sv_charts) {
		this.sv_charts = sv_charts;
	}

	public ArrayList<SimulationResult> getSimulationResults() {
		return simulationResults;
	}

	public void setSimulationResults(
			ArrayList<SimulationResult> simulationResults) {
		this.simulationResults = simulationResults;
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

	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		for (DC_Thread mt : threads) {
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

	public int getMagnitudeValuesSize() {
		return magnitudeValuesSize;
	}

	public void setMagnitudeValuesSize(int magnitudeValuesSize) {
		this.magnitudeValuesSize = magnitudeValuesSize;
	}
	
}
