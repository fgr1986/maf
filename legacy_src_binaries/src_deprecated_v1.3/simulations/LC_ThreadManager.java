package simulations;

import org.jfree.chart.JFreeChart;

import memristorModels.MemristorModel;
import simulationObjects.SimulationResult;

public interface LC_ThreadManager{
	public void start();
	public SimulationResult prepareStatisticsAndPlots();
	public void setInterrupted(boolean interrupted);
	public SimulationResult getSimulationResult();
	public MemristorModel getMemristorPilot();
	public int getThreadsBadlyProcessedON();
	public int getThreadsBadlyProcessedOFF();
	public int getThreadsCorrectlyProcessedON();
	public int getThreadsCorrectlyProcessedOFF();
	public int getThreadsReachedAllStatesON();
	public int getThreadsReachedAllStatesOFF();
	public int getMemristanceLevels();
	public long getEndTime();
	public long getStartTime();
	public long getEndTimePlotting();
	public long getStartTimePlotting();
	public JFreeChart getXt_goingOn_chart();
	public JFreeChart getXt_goingOff_chart();
	public JFreeChart getMt_goingOn_chart();
	public JFreeChart getMt_goingOff_chart();
	public JFreeChart getLevelst_goingOn_chart();
	public JFreeChart getLevelst_goingOff_chart();
	public double[][]getLevelsStatisticsON();
	public double [][]getLevelsStatisticsOFF();
	public int getMagnitudeValuesSize();
	public int getThreadsNumber();
	public boolean isPrintOffHistogram();
	public boolean isPrintOnHistogram();
}
