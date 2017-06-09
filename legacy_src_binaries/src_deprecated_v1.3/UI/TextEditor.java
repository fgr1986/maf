package UI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import memristorModels.MemristorModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;

import simulationObjects.MemristorParameter;
import simulationObjects.ResourcesAndUsefulFunctions;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class TextEditor extends Shell {
	private boolean unsaved;
	private boolean montecarlo;
	private StyledText styledText;
	private String lastDirectory;
	private MemristorModel memristor;
	private MenuItem SaveTo;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			TextEditor shell = new TextEditor(display, null, false);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public TextEditor(Display display, MemristorModel memristor, boolean montecarlo) {
		super(display, SWT.SHELL_TRIM);
		setImage(SWTResourceManager.getImage(TextEditor.class, "/resources/editor.png"));
		//user memristor / analysis 
		this.memristor = memristor;
		this.montecarlo = montecarlo;

		styledText = new StyledText(this, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				unsaved = true;
			}
		});
		styledText.setBounds(10, 10, 778, 578);
		styledText.setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		MenuItem mntmHowTo = new MenuItem(menu, SWT.CASCADE);
		mntmHowTo.setText("How to...");

		Menu menu_1 = new Menu(mntmHowTo);
		mntmHowTo.setMenu(menu_1);

		MenuItem mntmAboutTheModels = new MenuItem(menu_1, SWT.NONE);
		mntmAboutTheModels.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				Display display = Display.getDefault();
				References ref = new References(display);
				ref.open();
			}
		});
		mntmAboutTheModels.setText("About the models");

		MenuItem mntmAboutTheSubcircuit = new MenuItem(menu_1, SWT.NONE);
		mntmAboutTheSubcircuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getDefault();
				HowToSubcircuit howTo = new HowToSubcircuit(display);
				howTo.open();
			}
		});
		mntmAboutTheSubcircuit.setText("About the subcircuit parameters");

		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Spice model");

		Menu menu_2 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_2);

		MenuItem mntmLoadSpiceLSISingle = new MenuItem(menu_2, SWT.NONE);
		mntmLoadSpiceLSISingle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				readMemristor(true, false, true);
			}
		});
		mntmLoadSpiceLSISingle
				.setText("Load LSI Spice Model: single memristor");

		MenuItem loadSpiceLSISweep = new MenuItem(menu_2, SWT.NONE);
		loadSpiceLSISweep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				readMemristor(true, false, false);
			}
		});
		loadSpiceLSISweep
				.setText("Load LSI Spice Model: memristor and parameter sweep");

		MenuItem mntmLoadClassicSpiceSingle = new MenuItem(menu_2, SWT.NONE);
		mntmLoadClassicSpiceSingle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				readMemristor(true, true, true);
			}
		});
		mntmLoadClassicSpiceSingle
				.setText("Load Classic Spice Model: single memristor");

		MenuItem mntmLoadClassicSpiceSweep = new MenuItem(menu_2, SWT.NONE);
		mntmLoadClassicSpiceSweep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				readMemristor(true, true, false);
			}
		});
		mntmLoadClassicSpiceSweep
				.setText("Load CLassic Spice Model: memristor and parameter sweep");

		MenuItem mntmSpectreModel = new MenuItem(menu, SWT.CASCADE);
		mntmSpectreModel.setText("Spectre model");

		Menu menu_3 = new Menu(mntmSpectreModel);
		mntmSpectreModel.setMenu(menu_3);

		MenuItem mntmLoadLsiSpectre = new MenuItem(menu_3, SWT.NONE);
		mntmLoadLsiSpectre.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				readMemristor(false, true, true);
			}
		});
		mntmLoadLsiSpectre.setText("Load LSI Spectre Model: single memristor");

		MenuItem mntmLoadLsiSpectreSweep = new MenuItem(menu_3, SWT.NONE);
		mntmLoadLsiSpectreSweep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				readMemristor(false, false, false);
			}
		});
		mntmLoadLsiSpectreSweep
				.setText("Load LSI Spectre Model: memristor and parameter sweep");

		MenuItem mntmLoadClassicSpectreSingle = new MenuItem(menu_3, SWT.NONE);
		mntmLoadClassicSpectreSingle
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						readMemristor(false, true, true);
					}
				});
		mntmLoadClassicSpectreSingle
				.setText("Load Classic Spectre Model: single memristor");

		MenuItem mntmLoadClassicSpectreSweep = new MenuItem(menu_3, SWT.NONE);
		mntmLoadClassicSpectreSweep
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						readMemristor(false, true, false);
					}
				});
		mntmLoadClassicSpectreSweep
				.setText("Load Classic Spectre Model: memristor and parameter sweep");

		SaveTo = new MenuItem(menu, SWT.NONE);
		SaveTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveChanges();
			}
		});
		SaveTo.setEnabled(false);
		SaveTo.setText("Save to...");
		createContents();

	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Subcircuit editor");
		setSize(800, 650);

	}

	boolean saveText() {
		FileDialog fileDialog = new FileDialog(this, SWT.SAVE);
		if (lastDirectory != null) {
			fileDialog.setFilterPath(lastDirectory);
		}
		String selectedFile = fileDialog.open();
		if (selectedFile == null) {
			System.out.println("File is not saved");
			return false;
		}
		File file = new File(selectedFile);
		lastDirectory = file.getParent();

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(styledText.getText());
			writer.close();
			unsaved = false;
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	boolean saveChanges() {
		if (!unsaved)
			return true;
		MessageBox box = new MessageBox(this, SWT.ICON_WARNING | SWT.YES
				| SWT.NO | SWT.CANCEL);
		box.setMessage("save changes? ");
		box.setText("Editor");
		int condition = box.open();
		if (condition == SWT.YES) {
			return saveText();
		} else if (condition == SWT.NO) {
			return true;
		} else {
			return false;
		}
	}

	private void readMemristor(boolean langSpice, boolean classic,
			boolean single) {
		String file1;
		String file2;
		try {
			String path = ResourcesAndUsefulFunctions.RESOURCESFOLDER;
			path += langSpice ? (ResourcesAndUsefulFunctions.LTSPICEFOLDER + "/")
					: (ResourcesAndUsefulFunctions.SPECTREFOLDER + "/");
			path += classic ? (ResourcesAndUsefulFunctions.CLASSICFOLDER + "/")
					: (ResourcesAndUsefulFunctions.LSIFOLDER + "/");
			String extension = langSpice ? (ResourcesAndUsefulFunctions.LTSPICEEXTENSION)
					: (ResourcesAndUsefulFunctions.SPECTREEXTENSION);
			file1 = path
					+ ResourcesAndUsefulFunctions.MODELFILENAMES[memristor.getMemristorType()]
					+ "1" + extension;
			file2 = path
					+ ResourcesAndUsefulFunctions.MODELFILENAMES[memristor.getMemristorType()]
					+ "2" + extension;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			readSubckFileError();
			return;
		}
		String model = "";
		int ch;
		StringBuffer strContent = new StringBuffer("");
		InputStream in ;
		BufferedReader input;
		try {
			in = getClass().getResourceAsStream(file1);
			if(in.available() < 1){
				readSubckFileError();
				return;
			}
			input  = new BufferedReader(new InputStreamReader(in));
			while ((ch = input.read()) != -1){
				strContent.append((char) ch);
			}
			input.close();
		} catch (Exception e) {
			System.out.println(e);
			readSubckFileError();
			return;
		}
		model += strContent.toString();
		if (langSpice) {
			model += "\n+";
		} else {
			model += "parameters ";
		}
		for (MemristorParameter p : memristor.getParameters()) {
			model += p.getTag() + "=" + p.getValue() + " ";
		}
		model += "\n";
		strContent = new StringBuffer("");
		try {
			in = getClass().getResourceAsStream(file2);
			if(in.available() < 1){
				readSubckFileError();
				return;
			}
			input  = new BufferedReader(new InputStreamReader(in));
			while ((ch = input.read()) != -1){
				strContent.append((char) ch);
			}
			input.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		model += strContent.toString();
		if (!single) {
			if (langSpice) {
				if(montecarlo){
					model += "\n";
					model += "\n**********************************************";
					model += "\n*** To be included in the full netlist     ***";
					model += "\n*** Montecarlo parameter definition        ***";
					model += "\n**********************************************";
					model += "\n** Normal distribution function             **";
					model += "\n.function normal(nom,tol) nom*(1+gauss(tol/3))";
					model += "\n** Parameters                               **";
					for (MemristorParameter p : memristor.getParameters()) {
						if (p.isAllowVariations()) {
							model += "\n.param " + p.getTag() + "=normal("
									+ p.getValue() + ", " + p.getSigma() + ") ";
						}
					}
				}else{
					model += "\n**********************************************";
					model += "\n*** To be included in the full netlist     ***";
					model += "\n*** Space exploration parameter definition ***";
					model += "\n**********************************************";
					for (MemristorParameter p : memristor.getParameters()) {
						if (p.isAllowVariations()) {
							model += "\n.step param " + p.getTag() + " list ";
							for(int j=0;j<p.getValueList().length;j++){
								model += p.getValueList()[j] + " ";
							}
						}
					}
				}
			} else {
				if(montecarlo){
					model += "\n//////////////////////////////////////////////////////////";
					model += "\n/// To be included in the full netlist                 ///";
					model += "\n/// Montecarlo        parameter definition             ///";
					model += "\n/// Repeat simulation using ocean                      ///";
					model += "\n//////////////////////////////////////////////////////////";
					// A statistics block that specifies process variations
					model += "\nstatistics {";
					model += "\nprocess {";
					for (MemristorParameter p : memristor.getParameters()) {
						if (p.isAllowVariations()) {
							model += "\nvary " + p.getTag() + " dist=gauss std=" + p.getSigma();
						}
					}
					model += "\n}";
					model += "\n}";
				}else{
					/*sweepb2_1 sweep param=b2 values=[0.0 1.2] {
						  sweepb1_1 sweep param=b1 values=[0.0 1.2] {
						    sweepb0_1 sweep param=b0 values=[0.0 1.2] {
						    tran_1 tran stop=1u
						    }
						  }
						}*/
					model += "\n///////////////////////////////////////////////";
					model += "\n/// To be included in the full netlist     ///";
					model += "\n/// Space exploration parameter definition ///";
					model += "\n//////////////////////////////////////////////";
					int sweepCount = 0;
					for (MemristorParameter p : memristor.getParameters()) {
						if (p.isAllowVariations()) {
							model += "\nsweep_p_" + sweepCount + " sweep param=" + p.getTag() + " values=[";
							for(int j=0;j<p.getValueList().length;j++){
								model += p.getValueList()[j] + " ";
							}
							model += "]{";
							sweepCount++;
						}
					}
					model += "// YOUR SIMULATION (tran, etc)";
					for (int i=0; i <sweepCount; i++) {
						model += "}";
					}
				}
			}
		}
		styledText.setText("");
		styledText.append(model);
		SaveTo.setEnabled(true);
	}
	public void readSubckFileError(){
		styledText.setText("Error while loading the subcircuit file");
		MessageBox box = new MessageBox(this, SWT.ICON_WARNING );
		box.setMessage("Error while loading the subcircuit file ");
		box.setText("Error");
		box.open();
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
