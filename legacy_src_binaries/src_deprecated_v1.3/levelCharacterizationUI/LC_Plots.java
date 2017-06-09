package levelCharacterizationUI;

import java.awt.Frame;
import java.awt.Panel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.ChartPanel;

import simulationObjects.ResourcesAndUsefulFunctions;
import simulations.LC_ThreadManager;

import org.eclipse.swt.widgets.Composite;
import java.awt.BorderLayout;
import javax.swing.JRootPane;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.wb.swt.SWTResourceManager;

public class LC_Plots {
	private LC_ThreadManager lc;
	private Panel panelXTON;
	private Panel panelXTOFF;
	private Panel panelRTON;
	private Panel panelRTOFF;
	private Panel panelLON;
	private Panel panelLOFF;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LC_Plots window = new LC_Plots();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LC_Plots(LC_ThreadManager lc) {
		this.lc = lc;
	}

	public LC_Plots() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Open the window.
	 */
	public void open() {
		// new ChartComposite(parent, SWT.NONE,
		// chart, true);
		Display display = Display.getDefault();
		Shell shlCharacterizationGraphs = new Shell();
		shlCharacterizationGraphs.setImage(SWTResourceManager.getImage(
				LC_UI.class, "/resources/lc.png"));
		shlCharacterizationGraphs.setToolTipText("Characterization graphs");
		shlCharacterizationGraphs.setSize(display.getClientArea().width,
				display.getClientArea().height);
		// shlCharacterizationGraphs.setMaximized(true);
		shlCharacterizationGraphs.setText("Characterization graphs");
		shlCharacterizationGraphs.setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(shlCharacterizationGraphs, SWT.NONE);

		TabItem tbtmResults = new TabItem(tabFolder, SWT.NONE);
		tbtmResults.setText("Results");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmResults.setControl(composite);

		Group groupOn = new Group(composite, SWT.NONE);
		groupOn.setText("Simulation Statistics (OFF to ON)");
		groupOn.setBounds(10, 74, 1338, 255);

		Label labelTotalON = new Label(groupOn, SWT.NONE);
		labelTotalON.setBounds(10, 51, 464, 17);
		labelTotalON.setText("Total simulations: "
				+ (lc.getThreadsBadlyProcessedON() + lc
						.getThreadsCorrectlyProcessedON()));

		Label labelCorrerctON = new Label(groupOn, SWT.NONE);
		labelCorrerctON.setText("Correcly computed simulations: "
				+ +lc.getThreadsCorrectlyProcessedON());
		labelCorrerctON.setBounds(10, 72, 464, 15);

		Label labelErrorON = new Label(groupOn, SWT.NONE);
		labelErrorON.setText("Simulations computed with errors: "
				+ lc.getThreadsBadlyProcessedON());
		labelErrorON.setBounds(10, 93, 464, 17);

		StyledText textTimeOn = new StyledText(groupOn, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textTimeOn.setEditable(false);
		String timeOn = "Time statistics.\n";
		for (int i = 0; i < lc.getMemristanceLevels(); i++) {
			if (i != 0
					&& lc.getLevelsStatisticsON()[i][3] != lc
							.getThreadsCorrectlyProcessedON()) {
				timeOn += "\nState not reached in all the simulations, please, increase the time range and/or the V supply:\nMean time to reach the state "
						+ i
						+ ": "
						+ ResourcesAndUsefulFunctions
								.getFormatTimeTyneValues(lc
										.getLevelsStatisticsON()[i][0])
						+ " (s), min: "
						+ ResourcesAndUsefulFunctions
								.getFormatTimeTyneValues(lc
										.getLevelsStatisticsON()[i][1])
						+ " (s), max: "
						+ ResourcesAndUsefulFunctions
								.getFormatTimeTyneValues(lc
										.getLevelsStatisticsON()[i][2]);
			} else {
				timeOn += "\n\nMean time to reach the state "
						+ i
						+ ": "
						+ ResourcesAndUsefulFunctions
								.getFormatTimeTyneValues(lc
										.getLevelsStatisticsON()[i][0])
						+ " (s), min: "
						+ ResourcesAndUsefulFunctions
								.getFormatTimeTyneValues(lc
										.getLevelsStatisticsON()[i][1])
						+ " (s), max: "
						+ ResourcesAndUsefulFunctions
								.getFormatTimeTyneValues(lc
										.getLevelsStatisticsON()[i][2]);
			}
		}
		textTimeOn.setText(timeOn);
		textTimeOn.setBounds(10, 131, 700, 108);

		Label labelReachedAllOn = new Label(groupOn, SWT.NONE);
		labelReachedAllOn
				.setText("Total simulations which has reached all the states: "
						+ lc.getThreadsReachedAllStatesON());
		labelReachedAllOn.setBounds(10, 30, 464, 15);

		Group groupOFF = new Group(composite, SWT.NONE);
		groupOFF.setText("Simulation Statistics (ON to OFF)");
		groupOFF.setBounds(10, 335, 1338, 255);

		Label labelTotalOFF = new Label(groupOFF, SWT.NONE);
		labelTotalOFF.setText("Total simulations: "
				+ (lc.getThreadsBadlyProcessedOFF() + lc
						.getThreadsCorrectlyProcessedOFF()));
		labelTotalOFF.setBounds(10, 51, 464, 15);

		Label labelCorrectOFF = new Label(groupOFF, SWT.NONE);
		labelCorrectOFF.setText("Correcly computed simulations:  "
				+ lc.getThreadsCorrectlyProcessedOFF());
		labelCorrectOFF.setBounds(10, 72, 464, 15);

		Label labelErrorOFF = new Label(groupOFF, SWT.NONE);
		labelErrorOFF.setText("Simulations computed with errors:  "
				+ lc.getThreadsBadlyProcessedOFF());
		labelErrorOFF.setBounds(10, 93, 464, 17);

		StyledText textTimeOff = new StyledText(groupOFF, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textTimeOff.setEditable(false);
		String timeOff = "Time statistics.\n";
		if (lc.getSimulationResult().isCorrect()) {
			for (int i = 0; i < lc.getMemristanceLevels(); i++) {
				if (i != 0
						&& lc.getLevelsStatisticsOFF()[i][3] != lc
								.getThreadsCorrectlyProcessedOFF()) {
					timeOff += "\nState not reached in all the simulations, please, increase the time range and/or the V supply:\nMean time to reach the state "
							+ i
							+ ": "
							+ ResourcesAndUsefulFunctions
									.getFormatTimeTyneValues(lc
											.getLevelsStatisticsOFF()[i][0])
							+ " (s), min: "
							+ ResourcesAndUsefulFunctions
									.getFormatTimeTyneValues(lc
											.getLevelsStatisticsOFF()[i][1])
							+ " (s), max: "
							+ ResourcesAndUsefulFunctions
									.getFormatTimeTyneValues(lc
											.getLevelsStatisticsOFF()[i][2]);
				} else {
					timeOff += "\n\nMean time to reach the state "
							+ i
							+ ": "
							+ ResourcesAndUsefulFunctions
									.getFormatTimeTyneValues(lc
											.getLevelsStatisticsOFF()[i][0])
							+ " (s), min: "
							+ ResourcesAndUsefulFunctions
									.getFormatTimeTyneValues(lc
											.getLevelsStatisticsOFF()[i][1])
							+ " (s), max: "
							+ ResourcesAndUsefulFunctions
									.getFormatTimeTyneValues(lc
											.getLevelsStatisticsOFF()[i][2]);
				}
			}
		}
		textTimeOff.setText(timeOff);
		textTimeOff.setBounds(10, 131, 700, 108);

		Label labelReachedAllOFF = new Label(groupOFF, SWT.NONE);
		labelReachedAllOFF
				.setText("Total simulations which has reached all the states: "
						+ lc.getThreadsReachedAllStatesOFF());
		labelReachedAllOFF.setBounds(10, 30, 479, 15);

		Label labelGlobalPerformance = new Label(composite, SWT.NONE);
		labelGlobalPerformance.setFont(SWTResourceManager.getFont("Sans", 12,
				SWT.NORMAL));
		labelGlobalPerformance.setBounds(10, 26, 691, 42);
		String gPerformance = "Simulation performed in "
				+ ResourcesAndUsefulFunctions
						.getFormatTime((lc.getEndTime() - lc.getStartTime()) * 1e-9)
				+ "\nPlots and statistics computed in "
				+ ResourcesAndUsefulFunctions
						.getFormatTime((lc.getEndTimePlotting() - lc
								.getStartTimePlotting()) * 1e-9);
		labelGlobalPerformance.setText(gPerformance);

		TabItem tbi_l_t_on = new TabItem(tabFolder, 0);
		tbi_l_t_on.setText("Levels(t) (going ON)");
		Composite composite_L_t_on = new Composite(tabFolder, SWT.EMBEDDED);
		tbi_l_t_on.setControl(composite_L_t_on);

		Frame frame_4 = SWT_AWT.new_Frame(composite_L_t_on);

		panelLON = new Panel();
		frame_4.add(panelLON);
		panelLON.setLayout(new BorderLayout(0, 0));

		TabItem tbi_l_t_off = new TabItem(tabFolder, 0);
		tbi_l_t_off.setText("Levels(t) (going OFF)");
		Composite composite_L_t_off = new Composite(tabFolder, SWT.EMBEDDED);
		tbi_l_t_off.setControl(composite_L_t_off);

		Frame frame_5 = SWT_AWT.new_Frame(composite_L_t_off);

		panelLOFF = new Panel();
		frame_5.add(panelLOFF);
		panelLOFF.setLayout(new BorderLayout(0, 0));

		TabItem tbi_x_t_on = new TabItem(tabFolder, SWT.NONE);
		tbi_x_t_on.setText("x(t) (going to an ON state)");

		Composite composite_x_t_on = new Composite(tabFolder, SWT.EMBEDDED);
		tbi_x_t_on.setControl(composite_x_t_on);

		Frame frame = SWT_AWT.new_Frame(composite_x_t_on);

		panelXTON = new Panel();
		frame.add(panelXTON);
		panelXTON.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane = new JRootPane();
		panelXTON.add(rootPane);

		TabItem tbi_x_t_off = new TabItem(tabFolder, SWT.NONE);
		tbi_x_t_off.setText("x(t) (going to an OFF state)");

		Composite composite_x_t_off = new Composite(tabFolder, SWT.EMBEDDED);
		tbi_x_t_off.setControl(composite_x_t_off);

		Frame frame_1 = SWT_AWT.new_Frame(composite_x_t_off);

		panelXTOFF = new Panel();
		frame_1.add(panelXTOFF);
		panelXTOFF.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane_1 = new JRootPane();
		panelXTOFF.add(rootPane_1);

		TabItem tbi_m_t_on = new TabItem(tabFolder, SWT.NONE);
		tbi_m_t_on.setText("Rm(t) (going to an ON state)");

		Composite composite_m_t_on = new Composite(tabFolder, SWT.EMBEDDED);
		tbi_m_t_on.setControl(composite_m_t_on);

		Frame frame_2 = SWT_AWT.new_Frame(composite_m_t_on);

		panelRTON = new Panel();
		frame_2.add(panelRTON);
		panelRTON.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane_2 = new JRootPane();
		panelRTON.add(rootPane_2);

		TabItem tbi_m_t_off = new TabItem(tabFolder, SWT.NONE);
		tbi_m_t_off.setText("Rm(t) (going to an OFF state)");

		Composite composite_m_t_off = new Composite(tabFolder, SWT.EMBEDDED);
		tbi_m_t_off.setControl(composite_m_t_off);

		Frame frame_3 = SWT_AWT.new_Frame(composite_m_t_off);

		panelRTOFF = new Panel();
		frame_3.add(panelRTOFF);
		panelRTOFF.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane_3 = new JRootPane();
		panelRTOFF.add(rootPane_3);

		if (lc != null && lc.getSimulationResult().isCorrect()) {
			panelXTON.add(new ChartPanel(lc.getXt_goingOn_chart()));
			panelXTOFF.add(new ChartPanel(lc.getXt_goingOff_chart()));
			panelRTON.add(new ChartPanel(lc.getMt_goingOn_chart()));
			panelRTOFF.add(new ChartPanel(lc.getMt_goingOff_chart()));
			if (lc.isPrintOnHistogram()) {
				panelLON.add(new ChartPanel(lc.getLevelst_goingOn_chart()));
			} else {
				MessageDialog
						.openWarning(new Shell(),
								"State change not accomplished",
								"None of the state changes (going from OFF to ON) has been accomplished");
				composite_L_t_on.setEnabled(false);
			}
			if (lc.isPrintOffHistogram()) {
				panelLOFF.add(new ChartPanel(lc.getLevelst_goingOff_chart()));
			} else {
				MessageDialog
						.openWarning(new Shell(),
								"State change not accomplished",
								"None of the state changes (going from ON to OFF) has been accomplished");
				composite_L_t_off.setEnabled(false);
			}
		}
		shlCharacterizationGraphs.open();
		shlCharacterizationGraphs.layout();
		while (!shlCharacterizationGraphs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}
}
