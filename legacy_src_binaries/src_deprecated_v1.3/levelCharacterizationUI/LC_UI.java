package levelCharacterizationUI;

import java.util.logging.Level;
import java.util.logging.Logger;

import memristorModels.MemristorModel;
import memristorModelsUI.MemristorMenuMIMThreshold;
import memristorModelsUI.MemristorMenuYakopcic;
import memristorModelsUI.MemristorMenuModifiedSimmons;
import memristorModelsUI.MemristorMenuSimmons;
import memristorModelsUI.MemristorMenuNonLineal;
import memristorModelsUI.MemristorMenuQControlled;
import memristorModelsUI.MemristorMenuTEAM;
import memristorModelsUI.MemristorModelMenu;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import UI.DeviceTitleMenu;
import UI.MAF;
import UI.TextEditor;

import simulationObjects.InputVoltage;
import simulationObjects.ResourcesAndUsefulFunctions;
import simulationObjects.Timing;
import simulations.LC_MonteCarloManager;
import simulations.LC_SpaceExplorationManager;
import simulations.LC_ThreadManager;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class LC_UI extends Shell {
	private Text textTitle;
	private MemristorModelMenu memristorMenu;
	private DeviceTitleMenu titleMenu;
	private LC_DCVoltageMenu inputVoltageMenu;
	private Button buttonCharacterize;
	private Button buttonCancel;
	private Button btnReduceTheStored;
	private ScrolledComposite scrolledComposite;
	private LC_ThreadManager tm;
	private Text txtStoredRatio;
	// private MemristorModel memristor;
	private Cursor defaultCursor;
	private Cursor waitCursor;
	private Text txtNumberThreads;
	private Button btnSpaceExploration;
	private Button btnMonteCarlo;
	private Button checkLegend;
	private MAF maf;
	private LC_TimingMenu timingMenu;
	private Group group;
	private Button button_1;
	private Text textStorageLevels;
	private Label label;
	private Label lblMaxTimeComputed;
	private Text textMaxTime;
	private MemristorModel memristorPilot;
	private Button buttonSubcircuit;
	private Button btnMagnitudeBufferSize;
	private Text textBuffer;
	private Button btnStoreDirectlyIn;
	private Button buttonCharacterizeAndPlot;
	private boolean plotAfterSimulate;
	private Group groupDataManagement;
	private Button button;

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public LC_UI(Display display, MAF maf, int memristorType) {
		super(display, SWT.SHELL_TRIM);
		this.maf = maf;
		// this.memristor = memristor;
		defaultCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
		waitCursor = new Cursor(Display.getDefault(), SWT.CURSOR_WAIT);

		setImage(SWTResourceManager.getImage(LC_UI.class, "/resources/lc.png"));

		scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL
				| SWT.V_SCROLL);
		scrolledComposite.setBounds(0, 0, 820, 607);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinHeight(650);
		scrolledComposite.setMinWidth(650);
		FormData fd_mainMenu = new FormData();
		fd_mainMenu.bottom = new FormAttachment(100, -10);
		fd_mainMenu.right = new FormAttachment(100, -10);
		fd_mainMenu.top = new FormAttachment(0, 10);
		fd_mainMenu.left = new FormAttachment(0, 10);
		scrolledComposite.setLayoutData(fd_mainMenu);
		scrolledComposite.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		Composite mainComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(mainComposite);

		Composite compositeTitle = new Composite(mainComposite, SWT.NONE);
		compositeTitle.setBounds(10, 10, 805, 30);

		titleMenu = new DeviceTitleMenu(compositeTitle, SWT.NONE, memristorType);
		titleMenu.setBounds(0, 0, 798, 29);

		TabFolder tabFolder = new TabFolder(mainComposite, SWT.NONE);
		tabFolder.setLocation(10, 46);
		tabFolder.setSize(805, 557);

		TabItem tabItemVoltage = new TabItem(tabFolder, SWT.NONE);
		tabItemVoltage.setText("Timing and voltage");

		Composite compositeVoltage = new Composite(tabFolder, SWT.NONE);
		tabItemVoltage.setControl(compositeVoltage);

		inputVoltageMenu = new LC_DCVoltageMenu(compositeVoltage, SWT.NONE);
		inputVoltageMenu.setBounds(10, 111, 781, 405);

		timingMenu = new LC_TimingMenu(compositeVoltage, SWT.NONE);
		timingMenu.setLocation(10, 10);
		timingMenu.setSize(781, 79);

		TabItem tbtmMemristor = new TabItem(tabFolder, SWT.NONE);
		tbtmMemristor.setText("Memristor parameters");

		Composite compositeMemristor = new Composite(tabFolder, SWT.NONE);
		tbtmMemristor.setControl(compositeMemristor);

		memristorMenu = null;
		String auxTitle = "";
		switch (memristorType) {
		case ResourcesAndUsefulFunctions.QCONTROLLEDMODEL:
			memristorMenu = new MemristorMenuQControlled(compositeMemristor,
					SWT.NONE);
			((MemristorMenuQControlled) memristorMenu).setSize(710, 470);
			((MemristorMenuQControlled) memristorMenu).layout(true);
			auxTitle = "Q";
			break;
		case ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL:
			memristorMenu = new MemristorMenuNonLineal(compositeMemristor,
					SWT.NONE);
			((MemristorMenuNonLineal) memristorMenu).setSize(710, 470);
			((MemristorMenuNonLineal) memristorMenu).layout(true);
			auxTitle = "Biolek";
			break;
		case ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL:
			memristorMenu = new MemristorMenuYakopcic(compositeMemristor,
					SWT.NONE);
			((MemristorMenuYakopcic) memristorMenu).setSize(710, 470);
			((MemristorMenuYakopcic) memristorMenu).layout(true);
			auxTitle = "Generic exponential drift";
			break;
		case ResourcesAndUsefulFunctions.SIMMONSMODEL:
			memristorMenu = new MemristorMenuSimmons(compositeMemristor,
					SWT.NONE);
			((MemristorMenuSimmons) memristorMenu).setSize(710, 470);
			((MemristorMenuSimmons) memristorMenu).layout(true);
			auxTitle = "Simmons HP Labs";
			break;
		case ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL:
			memristorMenu = new MemristorMenuModifiedSimmons(
					compositeMemristor, SWT.NONE);
			((MemristorMenuModifiedSimmons) memristorMenu).setSize(710, 470);
			((MemristorMenuModifiedSimmons) memristorMenu).layout(true);
			auxTitle = "Modified Simmons";
			break;
		case ResourcesAndUsefulFunctions.TEAMMODEL:
			memristorMenu = new MemristorMenuTEAM(compositeMemristor, SWT.NONE);
			((MemristorMenuTEAM) memristorMenu).setSize(710, 470);
			((MemristorMenuTEAM) memristorMenu).layout(true);
			auxTitle = "TEAM";
			break;
		case ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL:
			memristorMenu = new MemristorMenuMIMThreshold(compositeMemristor,
					SWT.NONE);
			((MemristorMenuMIMThreshold) memristorMenu).setSize(710, 470);
			((MemristorMenuMIMThreshold) memristorMenu).layout(true);
			auxTitle = "MIM Threshold";
			break;
		default:
			// labelTitle.setText(mQTitle);
			// labelDescription.setText(mQDescription);
			break;
		}

		TabItem tabItemSimulation = new TabItem(tabFolder, SWT.NONE);
		tabItemSimulation.setText("Simulation");

		Group compositeManage = new Group(tabFolder, SWT.SHADOW_ETCHED_IN);
		tabItemSimulation.setControl(compositeManage);
		compositeManage.setText("Menu");

		//
		textTitle = new Text(compositeManage, SWT.BORDER);
		textTitle.setLocation(137, 36);
		textTitle.setSize(377, 25);
		textTitle.setText(auxTitle);
		buttonCharacterize = new Button(compositeManage, SWT.NONE);
		buttonCharacterize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				plotAfterSimulate = false;
				simulate();
			}
		});
		buttonCharacterize.setLocation(172, 357);
		buttonCharacterize.setSize(98, 27);
		buttonCharacterize.setText("Characterize");

		buttonCancel = new Button(compositeManage, SWT.NONE);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tm != null) {
					tm.setInterrupted(true);
				}
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
						"\nSimulation cancelled");
				buttonCharacterize.setEnabled(true);
				buttonCharacterizeAndPlot.setEnabled(true);
				buttonCancel.setEnabled(false);
				buttonSubcircuit.setEnabled(false);
				buttonCancel.update();
				buttonSubcircuit.update();
				buttonCharacterize.update();
				buttonCharacterizeAndPlot.update();
				getUI().setCursor(defaultCursor);
			}
		});
		buttonCancel.setEnabled(false);
		buttonCancel.setBounds(308, 357, 88, 27);
		buttonCancel.setText("Cancel");

		Label lblSimulationTitle = new Label(compositeManage, SWT.NONE);
		lblSimulationTitle.setText("Simulation title: ");
		lblSimulationTitle.setBounds(10, 36, 121, 25);

		checkLegend = new Button(compositeManage, SWT.CHECK);
		checkLegend.setBounds(10, 138, 172, 22);
		checkLegend.setText("Display legend");

		txtNumberThreads = new Text(compositeManage, SWT.BORDER);
		txtNumberThreads.setBounds(332, 80, 182, 25);
		txtNumberThreads.setText("500");

		btnMonteCarlo = new Button(compositeManage, SWT.RADIO);
		btnMonteCarlo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtNumberThreads.setEnabled(true);
				inputVoltageMenu.setMontecarlo();
				memristorMenu.setMontecarlo();
			}
		});
		btnMonteCarlo.setBounds(10, 79, 316, 25);
		btnMonteCarlo.setText("Monte Carlo Analysis. Number of threads:");
		btnMonteCarlo.setSelection(true);

		btnSpaceExploration = new Button(compositeManage, SWT.RADIO);
		btnSpaceExploration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				MessageDialog.openWarning(new Shell(), "Parameter list",
						"Please, set the different MEMRISTOR parameter values according \n"
						+ "this parameter list format: value1;value2;...;valuen");
				txtNumberThreads.setEnabled(false);
				inputVoltageMenu.setSpaceExploration();
				memristorMenu.setSpaceExploration();
			}
		});
		btnSpaceExploration.setBounds(10, 110, 504, 22);
		btnSpaceExploration.setText("Corner Analysis (Parameter list: value1;value2;...;valuen)");

		group = new Group(compositeManage, SWT.SHADOW_ETCHED_IN);
		group.setText("Multilevel");
		group.setBounds(520, 25, 271, 114);

		button_1 = new Button(group, SWT.RADIO);
		button_1.setVisible(false);
		button_1.setText("Uniform duration");
		button_1.setBounds(10, 65, 139, 15);

		textStorageLevels = new Text(group, SWT.BORDER);
		textStorageLevels.setText("1");
		textStorageLevels.setBounds(197, 21, 64, 27);

		label = new Label(group, SWT.NONE);
		label.setText("2^n levels, n:");
		label.setBounds(10, 24, 181, 23);

		lblMaxTimeComputed = new Label(group, SWT.NONE);
		lblMaxTimeComputed.setBounds(10, 60, 181, 46);
		lblMaxTimeComputed
				.setText("Max time allowed to \nreach the desired levels (s):");

		textMaxTime = new Text(group, SWT.BORDER);
		textMaxTime.setBounds(197, 64, 64, 27);
		textMaxTime.setText("400e-9");

		buttonSubcircuit = new Button(compositeManage, SWT.NONE);
		buttonSubcircuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadSpiceSpectreTextEditor();
			}
		});
		buttonSubcircuit.setText("Get subcircuit");
		buttonSubcircuit.setFont(SWTResourceManager.getFont("Ubuntu", 11,
				SWT.BOLD));
		buttonSubcircuit.setEnabled(false);
		buttonSubcircuit.setBounds(402, 357, 121, 27);

		buttonCharacterizeAndPlot = new Button(compositeManage, SWT.NONE);
		buttonCharacterizeAndPlot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				plotAfterSimulate = true;
				simulate();
			}
		});
		buttonCharacterizeAndPlot.setText("Characterize and plot");
		buttonCharacterizeAndPlot.setBounds(10, 357, 156, 27);

		groupDataManagement = new Group(compositeManage, SWT.SHADOW_ETCHED_IN);
		groupDataManagement.setText("Data management");
		groupDataManagement.setBounds(10, 178, 781, 136);

		button = new Button(groupDataManagement, SWT.RADIO);
		button.setVisible(false);
		button.setText("Uniform duration");
		button.setBounds(10, 65, 139, 15);

		btnReduceTheStored = new Button(groupDataManagement, SWT.CHECK);
		btnReduceTheStored.setBounds(10, 98, 285, 25);
		btnReduceTheStored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtStoredRatio.setEnabled(btnReduceTheStored.getSelection());
			}
		});
		btnReduceTheStored.setText("Reduce data: save one sample every:  ");

		txtStoredRatio = new Text(groupDataManagement, SWT.BORDER);
		txtStoredRatio.setBounds(370, 98, 182, 25);
		txtStoredRatio.setEnabled(false);
		txtStoredRatio.setText("" + ResourcesAndUsefulFunctions.STOREFREQ);

		btnMagnitudeBufferSize = new Button(groupDataManagement, SWT.CHECK);
		btnMagnitudeBufferSize.setBounds(10, 34, 182, 25);
		btnMagnitudeBufferSize.setSelection(true);
		btnMagnitudeBufferSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textBuffer.setEnabled(btnMagnitudeBufferSize.getSelection());
				btnStoreDirectlyIn.setSelection(!btnMagnitudeBufferSize
						.getSelection());
			}
		});
		btnMagnitudeBufferSize.setText("Magnitude buffer size:");

		textBuffer = new Text(groupDataManagement, SWT.BORDER);
		textBuffer.setBounds(370, 34, 182, 25);
		textBuffer.setText(ResourcesAndUsefulFunctions.MAGNITUDEVALUESBUFFERSIZE+"");

		btnStoreDirectlyIn = new Button(groupDataManagement, SWT.CHECK);
		btnStoreDirectlyIn.setBounds(10, 65, 233, 25);
		btnStoreDirectlyIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textBuffer.setEnabled(false);
				btnMagnitudeBufferSize.setSelection(!btnStoreDirectlyIn
						.getSelection());
			}
		});
		btnStoreDirectlyIn.setText("Store directly in hard-disk");

		createContents();
		scrolledComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}

	private void simulate() {
		buttonCharacterize.setEnabled(false);
		buttonCharacterizeAndPlot.setEnabled(false);
		buttonCancel.setEnabled(true);
		buttonSubcircuit.setEnabled(false);
		buttonCancel.update();
		buttonSubcircuit.update();
		buttonCharacterize.update();
		buttonCharacterizeAndPlot.update();
		InputVoltage iv = null;
		MemristorModel m = null;
		Timing t = null;
		String error = "";
		int threadsNumber = 0;
		int memristanceLevels = 0;
		int magnitudeValuesSize = 0;
		int storeRatio = ResourcesAndUsefulFunctions.STOREFREQ;
		double maxTimeComputed = 0;
		try {
			magnitudeValuesSize = Integer.parseInt(textBuffer.getText());

			if (magnitudeValuesSize <= 128 || magnitudeValuesSize > 100 * 1024) {
				error = "The magnitude/state variable buffer size should be between 128 and 102400, depending on the amount of RAM. ";
				MessageDialog.openWarning(new Shell(), "Parameters error",
						error);
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO, error);
				return;
			}
			m = memristorMenu.getMemristor();
			t = timingMenu.getTiming();
			threadsNumber = (int) Double
					.parseDouble(txtNumberThreads.getText());
			maxTimeComputed = Double.parseDouble(textMaxTime.getText());
			memristanceLevels = (int) Math.pow(2,
					Double.parseDouble(textStorageLevels.getText()));
			if (t == null) {
				throw new Exception("Error in timing module");
			}
			iv = inputVoltageMenu.getInputVoltage(t);
			// m.setInstance();
			storeRatio = Integer.parseInt(txtStoredRatio.getText());
			if (storeRatio <= 1) {
				error = "If the stored data is reduced, the stored data ratio should be an integer greater than one. ";
				MessageDialog.openWarning(new Shell(), "Parameters error",
						error);

				Logger.getLogger(getClass().getName()).log(Level.INFO, error);
				return;
			}
		} catch (Exception exc) {
			error = "Memristor creation error: " + exc.getLocalizedMessage();
			MessageDialog.openWarning(new Shell(), "Parameters error", error);
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO, error);
			return;
		}
		try {
			if (btnMonteCarlo.getSelection()) {
				if (threadsNumber > ResourcesAndUsefulFunctions.MAXRECOMMENDEDTHREADS) {
					MessageBox mb = new MessageBox(new Shell(), SWT.OK
							| SWT.CANCEL);
					mb.setText("Simulation warning");
					mb.setMessage("Each OS has a opened file limit. You can continue with the main simulation, but it is posible that some of the scenarios may not be saved.\nWould you like to proceed with the simulation?");
					int val = mb.open();
					if (val == SWT.CANCEL) {
						buttonCharacterize.setEnabled(true);
						buttonCharacterizeAndPlot.setEnabled(true);
						buttonCancel.setEnabled(false);
						buttonSubcircuit.setEnabled(true);
						buttonCancel.update();
						buttonSubcircuit.update();
						buttonCharacterize.update();
						buttonCharacterizeAndPlot.update();
						return;
					}
				}
				tm = new LC_MonteCarloManager(getUI(), threadsNumber,
						memristanceLevels, maxTimeComputed, m, iv, t,
						textTitle.getText(), btnStoreDirectlyIn.getSelection(),
						magnitudeValuesSize, btnReduceTheStored.getSelection(),
						storeRatio, getFolderName(), checkLegend.getSelection());
				// start the thread
				Logger.getLogger(getClass().getName()).log(Level.INFO,
						"\n.......................");
				Logger.getLogger(getClass().getName()).log(Level.INFO,
						"Starting the montecarlo simulation.");
				Logger.getLogger(getClass().getName())
						.log(Level.INFO,
								"Memristor model: "
										+ tm.getMemristorPilot().getTitle());
				getUI().setCursor(waitCursor);
				tm.start();
			} else {
				tm = new LC_SpaceExplorationManager(getUI(), memristanceLevels,
						maxTimeComputed, m, iv, t, textTitle.getText(),
						btnStoreDirectlyIn.getSelection(), magnitudeValuesSize, btnReduceTheStored.getSelection(),
						storeRatio, getFolderName(), checkLegend.getSelection());
				// start the thread
				Logger.getLogger(getClass().getName()).log(Level.INFO,
						"\n.......................");
				Logger.getLogger(getClass().getName()).log(Level.INFO,
						"Starting the space exploration simulation.");
				Logger.getLogger(getClass().getName())
						.log(Level.INFO,
								"Memristor model: "
										+ tm.getMemristorPilot().getTitle());
				getUI().setCursor(waitCursor);
				tm.start();
			}

		} catch (Exception exc) {
			error = "Simulation error: " + exc.getLocalizedMessage();
			MessageDialog.openWarning(new Shell(), "Parameters error",
					"Simulation error: " + exc.getLocalizedMessage());
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO, error);
			return;
		}
	}

	private void loadSpiceSpectreTextEditor() {
		try {
			Display display = Display.getDefault();
			TextEditor teUI = new TextEditor(display, memristorPilot, btnMonteCarlo.getSelection());
			teUI.open();
			teUI.layout();
			while (!teUI.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.close();
	}

	public void reportResults() {
		// 2 different threads, one 2 draw the plots and other for the UI
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				String log = "";
				memristorPilot = tm.getMemristorPilot();
				getUI().setCursor(defaultCursor);
				buttonCharacterizeAndPlot.setEnabled(true);
				buttonCharacterize.setEnabled(true);
				buttonCancel.setEnabled(false);
				buttonSubcircuit.setEnabled(true);
				if (!tm.getSimulationResult().isCorrect()) {
					Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
							Level.INFO,
							"Error in the analysis: "
									+ tm.getSimulationResult()
											.getConsoleMessage());
					MessageDialog.openWarning(new Shell(),
							"Error in the analysis: ", tm.getSimulationResult()
									.getCompleteMessage());
					log = "Error in the analysis: "
							+ tm.getSimulationResult().getCompleteMessage()
							+ "\nSimulation time: "
							+ ResourcesAndUsefulFunctions.getFormatTime((tm.getEndTime() - tm
									.getStartTime()) * 1e-9)
							+ "\n---------------------------------------------------------------\n";
				} else {
					Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(Level.INFO,
							tm.getSimulationResult().getConsoleMessage());
					log = "\n---------------------------------------------------------------\n"
							+ "Level Characterization correctly computed and plotted.";
					if (btnStoreDirectlyIn.getSelection()) {
						log += "\nSimulations directly stored on disk";
					} else {
						log += "\nMagnitudes buffer size: "
								+ tm.getMagnitudeValuesSize();
					}
					log += "\nNumber of threads: "
							+ tm.getThreadsNumber()
							+ "\nNumber of threads correctly computed: "
							+ (tm.getThreadsCorrectlyProcessedOFF() + tm.getThreadsCorrectlyProcessedON())
							+ "\nNumber of threads badly computed: "
							+ (tm.getThreadsBadlyProcessedOFF() + tm.getThreadsBadlyProcessedON())
							+ "\nSimulation time: "
							+ ResourcesAndUsefulFunctions.getFormatTime((tm.getEndTime() - tm
									.getStartTime()) * 1e-9);
				}

				// plot
				if (plotAfterSimulate) {
					tm.prepareStatisticsAndPlots();
					LC_Plots dcp = new LC_Plots(tm);
					dcp.open();
					log += "\nPlotting time: "
							+ ResourcesAndUsefulFunctions.getFormatTime((tm.getEndTimePlotting() - tm
									.getStartTimePlotting()) * 1e-9) + " (s).";
				}
				log +=  "\n---------------------------------------------------------------\n";
				maf.refreshLog(log);
			}
		});

	}

	

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Level Characterization");
		setSize(825, 640);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	// private String getFileNameXML(int counter) {
	// String aux = Resources.SIMULATIONSFOLDER + "/"
	// + Resources.DEVICEMONTECARLOOUTPUT + "_"
	// + textTitle.getText() + "_" + counter + "."
	// + Resources.OUTPUTEXTENSIONXML;
	// return aux.replace(" ", "_");
	// }

	private String getFolderName() {
		String aux = ResourcesAndUsefulFunctions.SIMULATIONSFOLDER + "/";
		if (btnMonteCarlo.getSelection()) {
			aux += ResourcesAndUsefulFunctions.LEVELMONTECARLOOUTPUT;
		} else {
			aux += ResourcesAndUsefulFunctions.LEVELSPACEEXPLORATIONOUTPUT;
		}
		aux += "_" + textTitle.getText();

		return aux.replace(" ", "_");
	}

	private LC_UI getUI() {
		return this;
	}
}
