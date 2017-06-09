package deviceCharacterizationUI;

import java.util.logging.Level;
import java.util.logging.Logger;

import memristorModels.MemristorModel;
import memristorModelsUI.MemristorMenuYakopcic;
import memristorModelsUI.MemristorMenuModifiedSimmons;
import memristorModelsUI.MemristorMenuSimmons;
import memristorModelsUI.MemristorMenuNonLineal;
import memristorModelsUI.MemristorMenuQControlled;
import memristorModelsUI.MemristorMenuTEAM;
import memristorModelsUI.MemristorModelMenu;
import memristorModelsUI.MemristorMenuMIMThreshold;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
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
import simulations.DC_MonteCarloManager;
import simulations.DC_SpaceExplorationManager;
import simulations.DC_ThreadManager;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DC_UI extends Shell {
	private Text textTitle;
	private MemristorModelMenu memristorMenu;
	private DeviceTitleMenu titleMenu;
	private DC_TimingMenu timingMenu;
	private DC_InputVoltageMenu inputVoltageMenu;
	private Button buttonCharacterizeAndPlot;
	private Button buttonCancel;
	private Button btnReduceTheStored;
	private ScrolledComposite scrolledComposite;
	private DC_ThreadManager tm;
	private Text txtStoredRatio;
	// private MemristorModel memristor;
	private Cursor defaultCursor;
	private Cursor waitCursor;
	private Text txtNumberThreads;
	private Button btnSpaceExploration;
	private Button btnMonteCarloN;
	private Button buttonLegend;
	// private Composite compositeMemristor;
	private MAF maf;
	private Button buttonSubcircuit;
	private MemristorModel memristorPilot;
	private Button btnMagnitudeBufferSize;
	private Text textBuffer;
	private Button buttonCharacterize;
	private boolean plotAfterSimulate;
	private Composite mainComposite;
	private TabFolder tabFolder;
	private Button btnStoreDirectlyIn;
	private Group group;
	private Button button;

	public int getNewHeight() {
		return getBounds().height - 30;
	}

	public int getNewWidth() {
		return getBounds().width - 30;
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public DC_UI(Display display, MAF maf, int memristorType) {
		super(display, SWT.SHELL_TRIM);
		this.maf = maf;
		// this.memristor = memristor;
		defaultCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);
		waitCursor = new Cursor(Display.getDefault(), SWT.CURSOR_WAIT);

		this.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				scrolledComposite.setSize(getNewWidth(), getNewHeight());
				mainComposite.setSize(getNewWidth() - 10, getNewHeight() - 10);
				tabFolder.setSize(getNewWidth() - 10, getNewHeight() - 60);
			}
		});

		setImage(SWTResourceManager.getImage(DC_UI.class, "/resources/dc.png"));

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
		mainComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(mainComposite);

		Composite compositeTitle = new Composite(mainComposite, SWT.NONE);
		compositeTitle.setBounds(10, 10, 805, 30);

		titleMenu = new DeviceTitleMenu(compositeTitle, SWT.NONE, memristorType);
		titleMenu.setBounds(0, 0, 798, 29);

		tabFolder = new TabFolder(mainComposite, SWT.NONE);
		tabFolder.setLocation(10, 46);
		tabFolder.setSize(805, 557);

		TabItem tbtmTimingParameters = new TabItem(tabFolder, SWT.NONE);
		tbtmTimingParameters.setText("Timing parameters");

		Composite compositeTiming = new Composite(tabFolder, SWT.NONE);
		tbtmTimingParameters.setControl(compositeTiming);

		timingMenu = new DC_TimingMenu(compositeTiming, SWT.NONE);
		timingMenu.setBounds(21, 34, 705, 187);

		TabItem tabItemVoltage = new TabItem(tabFolder, SWT.NONE);
		tabItemVoltage.setText("Input signal parameters");

		Composite compositeVoltage = new Composite(tabFolder, SWT.NONE);
		tabItemVoltage.setControl(compositeVoltage);

		inputVoltageMenu = new DC_InputVoltageMenu(compositeVoltage, SWT.NONE);
		inputVoltageMenu.setBounds(10, 20, 781, 496);

		TabItem tbtmMemristor = new TabItem(tabFolder, SWT.NONE);
		tbtmMemristor.setText("Memristor parameters");

		Composite compositeMemristor = new Composite(tabFolder, SWT.NONE);
		// compositeMemristor.setBackground(SWTResourceManager.getColor(204,
		// 204,204));
		tbtmMemristor.setControl(compositeMemristor);

		memristorMenu = null;
		switch (memristorType) {
		case ResourcesAndUsefulFunctions.QCONTROLLEDMODEL:
			memristorMenu = new MemristorMenuQControlled(compositeMemristor,
					SWT.NONE);
			((MemristorMenuQControlled) memristorMenu).setSize(800, 500);
			((MemristorMenuQControlled) memristorMenu).layout(true);
			break;
		case ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL:
			memristorMenu = new MemristorMenuNonLineal(compositeMemristor,
					SWT.NONE);
			((MemristorMenuNonLineal) memristorMenu).setSize(800, 500);
			((MemristorMenuNonLineal) memristorMenu).layout(true);
			break;
		case ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL:
			memristorMenu = new MemristorMenuYakopcic(compositeMemristor,
					SWT.NONE);
			((MemristorMenuYakopcic) memristorMenu).setSize(800, 500);
			((MemristorMenuYakopcic) memristorMenu).layout(true);
			break;
		case ResourcesAndUsefulFunctions.SIMMONSMODEL:
			memristorMenu = new MemristorMenuSimmons(compositeMemristor,
					SWT.NONE);
			((MemristorMenuSimmons) memristorMenu).setSize(800, 500);
			((MemristorMenuSimmons) memristorMenu).layout(true);
			break;
		case ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL:
			memristorMenu = new MemristorMenuModifiedSimmons(
					compositeMemristor, SWT.NONE);
			((MemristorMenuModifiedSimmons) memristorMenu).setSize(800, 500);
			((MemristorMenuModifiedSimmons) memristorMenu).layout(true);
			break;
		case ResourcesAndUsefulFunctions.TEAMMODEL:
			memristorMenu = new MemristorMenuTEAM(compositeMemristor, SWT.NONE);
			((MemristorMenuTEAM) memristorMenu).setSize(800, 500);
			((MemristorMenuTEAM) memristorMenu).layout(true);
			break;
		case ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL:
			memristorMenu = new MemristorMenuMIMThreshold(compositeMemristor,
					SWT.NONE);
			((MemristorMenuMIMThreshold) memristorMenu).setSize(800, 500);
			((MemristorMenuMIMThreshold) memristorMenu).layout(true);
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
		textTitle.setLocation(136, 39);
		textTitle.setSize(431, 25);
		textTitle
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[memristorType]);
		buttonCharacterizeAndPlot = new Button(compositeManage, SWT.NONE);
		buttonCharacterizeAndPlot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				plotAfterSimulate = true;
				simulate();
			}
		});
		buttonCharacterizeAndPlot.setLocation(10, 318);
		buttonCharacterizeAndPlot.setSize(156, 27);
		buttonCharacterizeAndPlot.setText("Characterize and plot");

		buttonCancel = new Button(compositeManage, SWT.NONE);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tm != null) {
					tm.setInterrupted(true);
				}
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
						Level.INFO, "\nSimulation cancelled");
				buttonCharacterizeAndPlot.setEnabled(true);
				buttonCharacterize.setEnabled(true);
				buttonCancel.setEnabled(false);
				getUI().setCursor(defaultCursor);
			}
		});
		buttonCancel.setEnabled(false);
		buttonCancel.setBounds(308, 318, 88, 27);
		buttonCancel.setText("Cancel");

		Label lblSimulationTitle = new Label(compositeManage, SWT.NONE);
		lblSimulationTitle.setText("Simulation title: ");
		lblSimulationTitle.setBounds(10, 39, 110, 17);

		buttonLegend = new Button(compositeManage, SWT.CHECK);
		buttonLegend.setBounds(10, 129, 172, 22);
		buttonLegend.setText("Display legend");

		txtNumberThreads = new Text(compositeManage, SWT.BORDER);
		txtNumberThreads.setBounds(330, 70, 237, 25);
		txtNumberThreads.setText("500");

		btnMonteCarloN = new Button(compositeManage, SWT.RADIO);
		btnMonteCarloN.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtNumberThreads.setEnabled(true);
				inputVoltageMenu.setMontecarlo();
				memristorMenu.setMontecarlo();
			}
		});
		btnMonteCarloN.setBounds(10, 70, 317, 25);
		btnMonteCarloN.setText("Monte Carlo Analysis. Number of threads:");
		btnMonteCarloN.setSelection(true);

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
		btnSpaceExploration.setBounds(10, 101, 557, 22);
		btnSpaceExploration
				.setText("Corner Analysis ( Parameter list given as: value1;value2;..valuen )");

		buttonSubcircuit = new Button(compositeManage, SWT.NONE);
		buttonSubcircuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadSpiceSpectreTextEditor();
			}
		});
		buttonSubcircuit.setFont(SWTResourceManager.getFont("Ubuntu", 11,
				SWT.BOLD));
		buttonSubcircuit.setEnabled(false);
		buttonSubcircuit.setText("Get subcircuit");
		buttonSubcircuit.setBounds(416, 318, 121, 27);

		buttonCharacterize = new Button(compositeManage, SWT.NONE);
		buttonCharacterize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				plotAfterSimulate = false;
				simulate();
			}
		});
		buttonCharacterize.setText("Characterize");
		buttonCharacterize.setBounds(172, 318, 98, 27);

		group = new Group(compositeManage, SWT.SHADOW_ETCHED_IN);
		group.setText("Data management");
		group.setBounds(10, 168, 781, 133);

		button = new Button(group, SWT.RADIO);
		button.setVisible(false);
		button.setText("Uniform duration");
		button.setBounds(10, 65, 139, 15);

		btnReduceTheStored = new Button(group, SWT.CHECK);
		btnReduceTheStored.setBounds(10, 101, 303, 22);
		btnReduceTheStored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtStoredRatio.setEnabled(btnReduceTheStored.getSelection());
			}
		});
		btnReduceTheStored.setText("Reduce data: save one sample every:  ");

		txtStoredRatio = new Text(group, SWT.BORDER);
		txtStoredRatio.setBounds(365, 98, 182, 25);
		txtStoredRatio.setEnabled(false);
		txtStoredRatio.setText("" + ResourcesAndUsefulFunctions.STOREFREQ);

		btnMagnitudeBufferSize = new Button(group, SWT.CHECK);
		btnMagnitudeBufferSize.setBounds(10, 24, 233, 22);
		btnMagnitudeBufferSize.setSelection(true);
		btnMagnitudeBufferSize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textBuffer.setEnabled(btnMagnitudeBufferSize.getSelection());
				btnStoreDirectlyIn.setSelection(!btnMagnitudeBufferSize
						.getSelection());
			}
		});
		btnMagnitudeBufferSize.setText("Use magnitude buffers. Size:");

		textBuffer = new Text(group, SWT.BORDER);
		textBuffer.setBounds(365, 24, 182, 25);
		textBuffer
				.setText(ResourcesAndUsefulFunctions.MAGNITUDEVALUESBUFFERSIZE
						+ "");

		btnStoreDirectlyIn = new Button(group, SWT.CHECK);
		btnStoreDirectlyIn.setBounds(10, 52, 233, 25);
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
		Timing t = null;
		String error = "";
		int threadsNumber = 0;
		int magnitudeValuesSize = 0;
		int storeRatio = ResourcesAndUsefulFunctions.STOREFREQ;
		try {
			magnitudeValuesSize = Integer.parseInt(textBuffer.getText());

			if (magnitudeValuesSize < ResourcesAndUsefulFunctions.MINBUFFER
					|| magnitudeValuesSize > ResourcesAndUsefulFunctions.MAXBUFFER) {
				error = "The magnitude/state variable buffer size should be between 128 and 102400, depending on the amount of RAM. ";
				MessageDialog.openWarning(new Shell(), "Parameters error",
						error);
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
						Level.INFO, error);
				return;
			}
			// create memristor
			threadsNumber = Integer.parseInt(txtNumberThreads.getText());
			memristorPilot = memristorMenu.getMemristor();
			t = timingMenu.getTiming();
			if (t == null) {
				throw new Exception("Error in timing module");
			}
			iv = inputVoltageMenu.getInputVoltage(t);
			// m.setParameters(memristorMenu.getParameters());
			// m.setInstance();
			// if (t.isUniformSteps()) {
			// memristorPilot.setmag(((TimingUniform) t)
			// .getTotalSteps());
			// }
			storeRatio = Integer.parseInt(txtStoredRatio.getText());
			if (storeRatio <= 1) {
				error = "If the stored data is reduced, the stored data ratio should be an integer greater than one. ";
				MessageDialog.openWarning(new Shell(), "Parameters error",
						error);
				Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
						Level.INFO, error);
				return;
			}
		} catch (Exception exc) {
			error = "Memristor creation error: " + exc.getLocalizedMessage();
			MessageDialog.openWarning(new Shell(), "Parameters error", error);
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.INFO, error);
			return;
		}
		try {
			// create simulation (object + thread)
			if (btnMonteCarloN.getSelection()) {
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
				tm = new DC_MonteCarloManager(getUI(), threadsNumber,
						memristorPilot, iv, t, textTitle.getText(),
						btnStoreDirectlyIn.getSelection(), magnitudeValuesSize,
						btnReduceTheStored.getSelection(), storeRatio,
						getFolderName(), buttonLegend.getSelection());
			} else {
				tm = new DC_SpaceExplorationManager(getUI(), memristorPilot,
						iv, t, textTitle.getText(),
						btnStoreDirectlyIn.getSelection(), magnitudeValuesSize,
						btnReduceTheStored.getSelection(), storeRatio,
						getFolderName(), buttonLegend.getSelection());
			}
			// start the thread
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.INFO, "\n.......................");
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.INFO, "Starting the simulator.");
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.INFO, "Memristor model: " + tm.getTitle());
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.INFO, "Initial tStep: " + t.getTStep());
			getUI().setCursor(waitCursor);
			tm.start();

		} catch (Exception exc) {
			error = "Simulation error: " + exc.getLocalizedMessage();
			MessageDialog.openWarning(new Shell(), "Parameters error",
					"Simulation error: " + exc.getLocalizedMessage());
			Logger.getLogger(ResourcesAndUsefulFunctions.loggerName).log(
					Level.INFO, error);
			return;
		}
	}

	private void loadSpiceSpectreTextEditor() {
		try {
			Display display = Display.getDefault();
			TextEditor teUI = new TextEditor(display, memristorPilot,
					btnMonteCarloN.getSelection());
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
					Logger.getLogger(ResourcesAndUsefulFunctions.loggerName)
							.log(Level.INFO,
									"Error in the analysis: "
											+ tm.getSimulationResult()
													.getConsoleMessage());
					MessageDialog.openWarning(new Shell(),
							"Error in the analysis: ", tm.getSimulationResult()
									.getCompleteMessage());
					log = "Error in the analysis: "
							+ tm.getSimulationResult().getCompleteMessage()
							+ "\nSimulation time: "
							+ ResourcesAndUsefulFunctions.getFormatTime((tm
									.getEndTime() - tm.getStartTime()) * 1e-9)
							+ "\n---------------------------------------------------------------\n";
				} else {
					Logger.getLogger(ResourcesAndUsefulFunctions.loggerName)
							.log(Level.INFO,
									tm.getSimulationResult()
											.getConsoleMessage());
					log = "\n---------------------------------------------------------------\n"
							+ "Device Characterization correctly computed and plotted.";
					if (btnStoreDirectlyIn.getSelection()) {
						log += "\nSimulations directly stored on disk";
					} else {
						log += "\nMagnitudes buffer size: "
								+ tm.getMagnitudeValuesSize();
					}
					log += "\nNumber of threads: "
							+ (tm.getThreadsCorrectlyProcessed() + tm
									.getThreadsBadlyProcessed())
							+ "\nNumber of threads correctly computed: "
							+ tm.getThreadsCorrectlyProcessed()
							+ "\nNumber of threads badly computed: "
							+ tm.getThreadsBadlyProcessed()
							+ "\nSimulation time: "
							+ ResourcesAndUsefulFunctions.getFormatTime((tm
									.getEndTime() - tm.getStartTime()) * 1e-9);
				}

				// plot
				if (plotAfterSimulate) {
					tm.prepareStatisticsAndPlots();
					DC_Plots dcp = new DC_Plots(tm);
					dcp.open();
					log += "\nPlotting time: "
							+ ResourcesAndUsefulFunctions.getFormatTime((tm
									.getEndTimePlotting() - tm
									.getStartTimePlotting()) * 1e-9) + " (s).";
				}
				log += "\n---------------------------------------------------------------\n";
				maf.refreshLog(log);
			}
		});

	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Device Model Characterization");
		setSize(876, 640);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private String getFolderName() {
		String aux = ResourcesAndUsefulFunctions.SIMULATIONSFOLDER + "/";
		// if(btnMontecarloSimulation.getSelection()){
		// aux+= Resources.MONTECARLOOUTPUT ;
		// }else{
		aux += ResourcesAndUsefulFunctions.DEVICEMONTECARLOOUTPUT;
		// }
		aux += "_" + textTitle.getText();

		return aux.replace(" ", "_");
	}

	private DC_UI getUI() {
		return this;
	}
}
