package UI;

//import java.util.logging.Level;
//import java.util.logging.Logger;

import levelCharacterizationUI.LC_UI;
//import memristorModels.MemristorIO;
import memristorModels.MemristorModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import simulationObjects.ResourcesAndUsefulFunctions;
import swing2swt.layout.FlowLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.text.TextViewer;

import deviceCharacterizationUI.DC_UI;

public class MAF {
	protected Shell shlMemristorAnalysisFramework;
	private ScrolledComposite mainMenu;
	private Menu menu;
	private Shell dcUI;
	private StyledText textOutput;
	private boolean plotState = false;
	private MemristorModel temporalMemristor;
	private ScrolledComposite scrolledOutput ;
	private TextViewer textViewer;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MAF window = new MAF();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlMemristorAnalysisFramework.open();
		shlMemristorAnalysisFramework.layout();
		while (!shlMemristorAnalysisFramework.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMemristorAnalysisFramework = new Shell();
		shlMemristorAnalysisFramework.setImage(SWTResourceManager.getImage(MAF.class, "/resources/memristor.png"));
		shlMemristorAnalysisFramework.setSize(677, 493);
		shlMemristorAnalysisFramework.setText("Memristor Analysis Framework");
		shlMemristorAnalysisFramework.setLayout(new FormLayout());
		
		shlMemristorAnalysisFramework.addListener (SWT.Resize,  new Listener () {
		    public void handleEvent (Event e) {
		    	scrolledOutput.setSize(mainMenu.getBounds().width-20, mainMenu.getBounds().height-80);
		    	textViewer.getTextWidget().setSize(mainMenu.getBounds().width-20, mainMenu.getBounds().height-20);		    	
		    }
		  });

		mainMenu = new ScrolledComposite(shlMemristorAnalysisFramework,
				SWT.H_SCROLL | SWT.V_SCROLL);
		mainMenu.setExpandVertical(true);
		mainMenu.setExpandHorizontal(true);
		mainMenu.setMinHeight(650);
		mainMenu.setMinWidth(650);
		FormData fd_mainMenu = new FormData();
		fd_mainMenu.bottom = new FormAttachment(100, -10);
		fd_mainMenu.right = new FormAttachment(100, -10);
		fd_mainMenu.top = new FormAttachment(0, 10);
		fd_mainMenu.left = new FormAttachment(0, 10);
		mainMenu.setLayoutData(fd_mainMenu);
		mainMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Composite composite = new Composite(mainMenu, SWT.NONE);

		Label lblMemristorAnalysisFramework = new Label(composite, SWT.NONE);
		lblMemristorAnalysisFramework.setFont(SWTResourceManager.getFont(
				"Sans", 14, SWT.BOLD));
		lblMemristorAnalysisFramework.setBounds(10, 10, 633, 22);
		lblMemristorAnalysisFramework.setText(ResourcesAndUsefulFunctions.APPTITLE + " " + ResourcesAndUsefulFunctions.APPVERSION);

		scrolledOutput = new ScrolledComposite(composite,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledOutput.setBounds(10, 84, 633, 312);
		scrolledOutput.setExpandHorizontal(true);
		scrolledOutput.setExpandVertical(true);

		textViewer = new TextViewer(scrolledOutput, SWT.H_SCROLL
				| SWT.V_SCROLL);
		textViewer.setEditable(false);
		textOutput = textViewer.getTextWidget();
		textOutput.setEditable(false);
		textOutput.setFont( new Font(shlMemristorAnalysisFramework.getDisplay(), "Monospaced", 10, SWT.NONE));
		textOutput.append(ResourcesAndUsefulFunctions.APPTITLE + " " + ResourcesAndUsefulFunctions.APPVERSION );
		refreshLog("\n\n");
		scrolledOutput.setContent(textOutput);
		scrolledOutput.setMinSize(textOutput.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 63, 68, 15);
		lblNewLabel.setText("Output:");
		mainMenu.setContent(composite);
		mainMenu.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		menu = new Menu(shlMemristorAnalysisFramework, SWT.BAR);
		shlMemristorAnalysisFramework.setMenuBar(menu);

		MenuItem mntmSimulations = new MenuItem(menu, SWT.CASCADE);
		mntmSimulations.setText("Simulations");

		Menu menu_1 = new Menu(mntmSimulations);
		mntmSimulations.setMenu(menu_1);

		MenuItem mntmSingleDeviceSimulation = new MenuItem(menu_1, SWT.CASCADE);
		mntmSingleDeviceSimulation.setText("Device Simulation");

		Menu menu_device = new Menu(mntmSingleDeviceSimulation);
		mntmSingleDeviceSimulation.setMenu(menu_device);

		MenuItem dcCharge = new MenuItem(menu_device, SWT.NONE);
		dcCharge.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.QCONTROLLEDMODEL);
			}
		});
		dcCharge.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.QCONTROLLEDMODEL]);

		MenuItem dcNonLinear = new MenuItem(menu_device, SWT.NONE);
		dcNonLinear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL);
			}
		});
		dcNonLinear
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL]);

		MenuItem dcYakopcic = new MenuItem(menu_device, SWT.NONE);
		dcYakopcic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL);
			}
		});
		dcYakopcic
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL]);

		MenuItem dcSimmons1 = new MenuItem(menu_device, SWT.NONE);
		dcSimmons1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.SIMMONSMODEL);
			}
		});
		dcSimmons1.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.SIMMONSMODEL]);

		MenuItem dcSimmons2 = new MenuItem(menu_device, SWT.NONE);
		dcSimmons2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL);
			}
		});
		dcSimmons2
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL]);

		MenuItem dcTEAM = new MenuItem(menu_device, SWT.NONE);
		dcTEAM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.TEAMMODEL);
			}
		});
		dcTEAM.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.TEAMMODEL]);

		MenuItem dcMIM = new MenuItem(menu_device, SWT.NONE);
		dcMIM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDeviceCharacterization(ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL);
			}
		});
		dcMIM.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL]);

		MenuItem mntmLevelsDynamicsCharacterization = new MenuItem(menu_1,
				SWT.CASCADE);
		mntmLevelsDynamicsCharacterization.setText("Level Characterization");

		Menu menu_complete = new Menu(mntmLevelsDynamicsCharacterization);
		mntmLevelsDynamicsCharacterization.setMenu(menu_complete);

		MenuItem lcCharge = new MenuItem(menu_complete, SWT.NONE);
		lcCharge.setEnabled(false);
		lcCharge.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.QCONTROLLEDMODEL);
			}
		});
		lcCharge.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.QCONTROLLEDMODEL]);

		MenuItem lcNonlinear = new MenuItem(menu_complete, SWT.NONE);
		lcNonlinear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL);
			}
		});
		lcNonlinear
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL]);

		MenuItem lcYakopcic = new MenuItem(menu_complete, SWT.NONE);
		lcYakopcic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL);
			}
		});
		lcYakopcic
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL]);

		MenuItem lcSimmons1 = new MenuItem(menu_complete, SWT.NONE);
		lcSimmons1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.SIMMONSMODEL);
			}
		});
		lcSimmons1.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.SIMMONSMODEL]);

		MenuItem lcSimmons2 = new MenuItem(menu_complete, SWT.NONE);
		lcSimmons2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL);
			}
		});
		lcSimmons2
				.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL]);

		MenuItem lcTEAM = new MenuItem(menu_complete, SWT.NONE);
		lcTEAM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.TEAMMODEL);
			}
		});
		lcTEAM.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.TEAMMODEL]);

		MenuItem lcMIM = new MenuItem(menu_complete, SWT.NONE);
		lcMIM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadLevelCharacterization(ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL);
			}
		});
		lcMIM.setText(ResourcesAndUsefulFunctions.MODELTITLES[ResourcesAndUsefulFunctions.MIMTHRESHOLDMODEL]);

		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.setText("Exit");

		MenuItem mntmHelp_1 = new MenuItem(menu, SWT.CASCADE);
		mntmHelp_1.setText("Help");

		Menu menu_2 = new Menu(mntmHelp_1);
		mntmHelp_1.setMenu(menu_2);

		MenuItem mntmReferences = new MenuItem(menu_2, SWT.NONE);
		mntmReferences.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getDefault();
				References ref = new References(display);
				ref.open();
			}
		});
		mntmReferences.setText("References");

		MenuItem mntmHelp = new MenuItem(menu_2, SWT.NONE);
		mntmHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getDefault();
				About about = new About(display);
				about.open();
			}
		});
		mntmHelp.setText("About");
	}

	private void loadDeviceCharacterization(int model) {
		try {
			if (dcUI != null) {
				dcUI.close();
				dcUI = null;
			}
		} catch (Exception e) {

		}
		try {
			Display display = Display.getDefault();
			dcUI = new DC_UI(display, this, model); // complete charac?

			dcUI.open();
			dcUI.layout();
			while (!dcUI.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadLevelCharacterization(int model) {
		try {
			if (dcUI != null) {
				dcUI.close();
				dcUI = null;
			}
		} catch (Exception e) {

		}
		try {
			Display display = Display.getDefault();
			dcUI = new LC_UI(display, this, model);
			dcUI.open();
			dcUI.layout();
			while (!dcUI.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearOutput() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				textOutput.setText("");
			}
		});

	}

	public void refreshLog(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				textOutput.append("\n" + message);
				textOutput.setTopIndex(textOutput.getLineCount() - 1);
			}
		});
	}

	public void printSimulationRunning() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (plotState) {
					textOutput.append(".");
				} else {
					textOutput.append("*");
				}
			}
		});
	}

	public MemristorModel getTemporalMemristor() {
		return temporalMemristor;
	}

	public void setTemporalMemristor(MemristorModel temporalMemristor) {
		this.temporalMemristor = temporalMemristor;
	}
	/*
	public void change2SpiceSpectreEditor(LC_UI ui) {
		// TODO Auto-generated method stub
		ui.close();
		loadSpiceSpectreTextEditor();
	}*/
	/*
	private void loadSpiceSpectreTextEditor() {
		try {
			if (dcUI != null) {
				dcUI.close();
				dcUI = null;
			}
		} catch (Exception e) {

		}
		try {
			Display display = Display.getDefault();
			dcUI = new TextEditor(display, temporalMemristor);
			dcUI.open();
			dcUI.layout();
			while (!dcUI.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
