package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import simulationObjects.ResourcesAndUsefulFunctions;

public class About extends Shell {

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			About shell = new About(display);
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
	public About(Display display) {
		super(display, SWT.SHELL_TRIM);
		setImage(SWTResourceManager.getImage(About.class, "/resources/memristor.png"));

		Label lblMemristorTool = new Label(this, SWT.NONE);
		lblMemristorTool.setFont(SWTResourceManager.getFont("Ubuntu", 12,
				SWT.BOLD));
		lblMemristorTool.setBounds(124, 27, 314, 40);
		lblMemristorTool.setText(ResourcesAndUsefulFunctions.APPTITLE);

		Label lblNewLabel = new Label(this, SWT.NONE);
		//lblNewLabel.setImage(SWTResourceManager.getImage(MAF.class, "/resources/memristor.png"));
		lblNewLabel.setImage(SWTResourceManager.getImage(Label.class, "/resources/memristor.png"));
		lblNewLabel.setBounds(26, 10, 92, 58);

		Label lblVersion = new Label(this, SWT.NONE);
		lblVersion.setBounds(87, 102, 92, 16);
		lblVersion.setText("Version: "+ ResourcesAndUsefulFunctions.APPVERSION);

		Label lblDeveloperFernandoGarca = new Label(this, SWT.NONE);
		lblDeveloperFernandoGarca.setBounds(87, 127, 335, 23);
		lblDeveloperFernandoGarca
				.setText("Developer: Fernando García, fgarcia@die.upm.es");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("About");
		setSize(450, 220);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
