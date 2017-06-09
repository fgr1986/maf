package simulations;

import java.util.ArrayList;

import org.jfree.chart.JFreeChart;

import memristorModels.MemristorModel;
import simulationObjects.SimulationResult;

public interface DC_ThreadManager{
	public void start();
	public String getTitle();
	public void prepareStatisticsAndPlots();
	public void setInterrupted(boolean interrupted);
	public SimulationResult getSimulationResult();
	public MemristorModel getMemristorPilot();
	public int getThreadsBadlyProcessed();
	public int getThreadsCorrectlyProcessed();
	public int getMagnitudeValuesSize();
	public long getEndTime();
	public long getStartTime();
	public long getEndTimePlotting();
	public long getStartTimePlotting();
	public JFreeChart getVi_chart();
	public JFreeChart getVit_chart();
	public JFreeChart getMt_chart();
	public JFreeChart getMv_chart();
	public ArrayList<JFreeChart> getSv_charts();
}
