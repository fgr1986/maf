package levelCharacterizationUI;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import simulationObjects.ResourcesAndUsefulFunctions;
import simulationObjects.Timing;

public class LC_TimingMenu extends Composite {
	private Text textTimingStep;
	private Button btnDynamicStep;
	private Button btnFixedStep;
	private Text textTimingMinStep;
	private Text textTimingMaxStep;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LC_TimingMenu(Composite parent, int style) {
		super(parent, SWT.NONE);

		Group composite = new Group(this, SWT.SHADOW_ETCHED_IN);
		composite.setText("Model parameters");
		composite.setBounds(0, 0, 662, 82);

		textTimingStep = new Text(composite, SWT.BORDER);
		textTimingStep.setText(ResourcesAndUsefulFunctions.MAXSTEPLC + "");
		textTimingStep.setEnabled(false);
		textTimingStep.setBounds(139, 32, 75, 26);

		btnDynamicStep = new Button(composite, SWT.RADIO);
		btnDynamicStep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textTimingStep.setEnabled(false);
				textTimingMinStep.setEnabled(true);
			}
		});
		btnDynamicStep.setSelection(true);
		btnDynamicStep.setBounds(273, 36, 204, 22);
		btnDynamicStep.setText("Dynamic step between (s):");

		btnFixedStep = new Button(composite, SWT.RADIO);
		btnFixedStep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textTimingStep.setEnabled(true);
				textTimingMinStep.setEnabled(false);
			}
		});
		btnFixedStep.setText("Fixed step (s)");
		btnFixedStep.setBounds(10, 36, 112, 22);

		textTimingMinStep = new Text(composite, SWT.BORDER);
		textTimingMinStep.setText("" + ResourcesAndUsefulFunctions.MINSTEPLC);
		textTimingMinStep.setBounds(483, 32, 61, 26);
		textTimingMaxStep = new Text(composite, SWT.BORDER);
		textTimingMaxStep.setText(ResourcesAndUsefulFunctions.MAXSTEPLC + "");
		textTimingMaxStep.setBounds(579, 32, 61, 26);

		Label lblAnd = new Label(composite, SWT.NONE);
		lblAnd.setBounds(550, 37, 29, 15);
		lblAnd.setText("and");

		Label label = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(242, 21, 2, 43);
	}

	public Timing getTiming() {
		Timing timing = null;
		try {
			// retrieve timing
			if (btnDynamicStep.getSelection()) {
				timing = new Timing(false, Double.parseDouble(textTimingMinStep.getText()),
						Double.parseDouble(textTimingMaxStep.getText()));
			} else {
				timing = new Timing(true, Double.parseDouble(textTimingStep.getText()));
			}
			if (Double.parseDouble(textTimingMinStep.getText()) >= Double
					.parseDouble(textTimingMaxStep.getText())) {
				MessageDialog.openWarning(new Shell(), "Parameters error",
						"Timing error: define the time range of operation");
				return null;
			}
		} catch (Exception exc) {
			MessageDialog.openWarning(new Shell(), "Parameters error",
					"Timing error: " + exc.getLocalizedMessage());
			return null;
		}
		return timing;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setUniformStep(boolean selection) {
		this.btnFixedStep.setSelection(selection);
		this.btnDynamicStep.setSelection(!selection);
		this.btnDynamicStep.setEnabled(!selection);
		this.textTimingStep.setEnabled(selection);
		this.textTimingMinStep.setEnabled(!selection);
	}

	public void setMaxStep(double newT) {
		textTimingMinStep.setText("" + newT / 10);
		textTimingMaxStep.setText("" + newT);
		textTimingStep.setText("" + newT);
	}
}
