package levelCharacterizationUI;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import simulationObjects.InputVoltage;
import simulationObjects.InputVoltageParameter;
import simulationObjects.ResourcesAndUsefulFunctions;
import simulationObjects.Timing;

public class LC_DCVoltageMenu extends Composite {
	private Text textVMin;
	private Text textVMax;
	private Text textVStep;
	private Text textVSupply;
	private Text textVSigma;
	private boolean montecarlo;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LC_DCVoltageMenu(Composite parent, int style) {
		super(parent, style);

		Group composite = new Group(this, SWT.SHADOW_ETCHED_IN);
		composite.setText("Voltage parameters");
		composite.setBounds(0, 0, 590, 70);

		textVMin = new Text(composite, SWT.BORDER);
		textVMin.setEnabled(false);
		textVMin.setText("1");
		textVMin.setBounds(123, 23, 49, 26);

		Label lblVAmplitudev = new Label(composite, SWT.NONE);
		lblVAmplitudev.setText("Range from (V): ");
		lblVAmplitudev.setBounds(10, 35, 107, 16);

		Label lblTo = new Label(composite, SWT.NONE);
		lblTo.setText("to:");
		lblTo.setBounds(181, 35, 18, 16);

		textVMax = new Text(composite, SWT.BORDER);
		textVMax.setEnabled(false);
		textVMax.setText("1.5");
		textVMax.setBounds(205, 23, 49, 26);

		Label lblVoltageStepv = new Label(composite, SWT.NONE);
		lblVoltageStepv.setText("step:");
		lblVoltageStepv.setBounds(273, 35, 32, 16);

		textVStep = new Text(composite, SWT.BORDER);
		textVStep.setEnabled(false);
		textVStep.setText("0.2");
		textVStep.setBounds(311, 23, 43, 26);

		Label lblVSupplyv = new Label(composite, SWT.NONE);
		lblVSupplyv.setText("V supply\nmean (V):");
		lblVSupplyv.setBounds(382, 23, 81, 38);

		textVSupply = new Text(composite, SWT.BORDER);
		textVSupply.setText("0.8");
		textVSupply.setBounds(464, 23, 43, 26);

		Label label_1 = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBounds(360, 23, 2, 37);

		textVSigma = new Text(composite, SWT.BORDER);
		textVSigma.setText("1e-2");
		textVSigma.setBounds(537, 23, 43, 26);

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(513, 34, 18, 15);
		lblNewLabel.setText(ResourcesAndUsefulFunctions.SIGMA + ":");
		setMontecarlo();
	}

	public InputVoltage getInputVoltage(Timing t) {
		InputVoltage iv = null;
		try {
			if (montecarlo) {
				InputVoltageParameter constV = new InputVoltageParameter(0, "constV", Double.parseDouble(textVSupply
						.getText()), true, Double.parseDouble(textVSigma.getText()),
						Double.parseDouble(textVSigma.getText()));
				InputVoltageParameter constTUp = new InputVoltageParameter(1, "constTUp", 0, false, 0, 0);
				iv = new InputVoltage(t, constV, constTUp);
			} else {
				InputVoltageParameter constRangeVMin = new InputVoltageParameter(0, "constRangeVMin", Double.parseDouble(textVMin
						.getText()), false, 0, 0);
				
				InputVoltageParameter constRangeVMax = new InputVoltageParameter(1, "constRangeVMax", Double.parseDouble(textVMax
						.getText()), false, 0, 0);

				InputVoltageParameter constRangeVStep = new InputVoltageParameter(2, "constRangeVStep", Double.parseDouble(textVStep
						.getText()), false, 0, 0);
				
				iv = new InputVoltage(t, constRangeVMin, constRangeVMax, constRangeVStep);
			}
		} catch (Exception exc) {
			MessageDialog.openWarning(new Shell(), "Parameters error",
					"Timing error: " + exc.getLocalizedMessage());
			return null;
		}
		return iv;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setMontecarlo() {
		textVSupply.setEnabled(true);
		textVSigma.setEnabled(true);
		textVMin.setEnabled(false);
		textVMax.setEnabled(false);
		textVStep.setEnabled(false);
		montecarlo = true;
	}

	public void setSpaceExploration() {
		montecarlo = false;
		textVSupply.setEnabled(false);
		textVSigma.setEnabled(false);
		textVMin.setEnabled(true);
		textVMax.setEnabled(true);
		textVStep.setEnabled(true);
	}
}
