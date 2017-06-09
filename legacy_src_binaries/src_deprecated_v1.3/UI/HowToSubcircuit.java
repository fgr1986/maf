package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class HowToSubcircuit extends Shell {

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			HowToSubcircuit shell = new HowToSubcircuit(display);
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
	public HowToSubcircuit(Display display) {
		super(display, SWT.SHELL_TRIM);

		Label lblMemristorTool = new Label(this, SWT.NONE);
		lblMemristorTool.setFont(SWTResourceManager.getFont("Ubuntu", 12,
				SWT.BOLD));
		lblMemristorTool.setBounds(10, 10, 500, 24);
		lblMemristorTool.setText("How to export a memristor model to Spice or Spectre?");

		Label lblDeveloperFernandoGarca = new Label(this, SWT.NONE);
		lblDeveloperFernandoGarca.setBounds(10, 40, 542, 204);
		lblDeveloperFernandoGarca
				.setText("Once the simulation has been computed, we can export the corresponding\n subcircuit file.\nThis file can be referenced by including the subcircuit path in the\nschematic file, like\n\ninclude \"path /subcircuitFile.scs\"\n\nDoing this we can use several instances of our component.\nFor example, in SPICE:\n\"memristor1 port1 port2 memristor\"");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("How to export the model to my circuit simulation program");
		setSize(564, 277);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
