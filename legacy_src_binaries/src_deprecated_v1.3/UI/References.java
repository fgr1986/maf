package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.Hyperlink;

import simulationObjects.ResourcesAndUsefulFunctions;

public class References extends Shell {
	private Table table;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			References shell = new References(display);
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
	public References(Display display) {
		super(display, SWT.SHELL_TRIM);
		setImage(SWTResourceManager.getImage(References.class, "/resources/memristor.png"));

		Label lblMemristorTool = new Label(this, SWT.NONE);
		lblMemristorTool.setFont(SWTResourceManager.getFont("Ubuntu", 12,
				SWT.BOLD));
		lblMemristorTool.setBounds(124, 18, 255, 57);
		lblMemristorTool
				.setText("Memristor Analysis Framework\n\nModel references list");

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setImage(SWTResourceManager.getImage(References.class, "/resources/memristor.png"));
		lblNewLabel.setBounds(10, 10, 108, 76);

		Label lblVersion = new Label(this, SWT.NONE);
		lblVersion.setBounds(385, 59, 92, 16);
		lblVersion.setText("Version: " + ResourcesAndUsefulFunctions.APPVERSION);

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 92, 828, 477);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnModel = new TableColumn(table, SWT.NONE);
		tblclmnModel.setWidth(433);
		tblclmnModel.setText("Model");

		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(377);
		tblclmnDescription.setText("Description");

		TableItem tableItem = null;
		Hyperlink link = null;
		TableEditor editor = null;

		for (int i = 0; i < ResourcesAndUsefulFunctions.MODELTITLES.length; i++) {
			link = new Hyperlink(table, SWT.WRAP);
			link.setText(ResourcesAndUsefulFunctions.MODELTITLES[i]);
			link.setUnderlined(true);
			tableItem = new TableItem(table, SWT.NONE);
//			tableItem.setText(0, Resources.MODELTITLES[i]);
			tableItem.setText(1, ResourcesAndUsefulFunctions.MODELDESCRIPTIONS[i]);
			editor = new TableEditor(table);
			editor.setEditor(link, tableItem, 0); // set hyperlinks in column i
			editor.grabHorizontal = true;
			editor.minimumWidth = 50;
		}

		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("References list");
		setSize(850, 602);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
