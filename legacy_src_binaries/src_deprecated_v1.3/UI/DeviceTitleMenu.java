package UI;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import simulationObjects.ResourcesAndUsefulFunctions;

public class DeviceTitleMenu extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DeviceTitleMenu(Composite parent, int style, int memristorType) {
		super(parent, style);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setBounds(0, 0, 690, 29);

		Label labelTitle = new Label(composite, SWT.NONE);
		labelTitle.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.BOLD));
		labelTitle.setBounds(0, 0, 680, 19);
		labelTitle.setText(ResourcesAndUsefulFunctions.MODELTITLES[memristorType]);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
