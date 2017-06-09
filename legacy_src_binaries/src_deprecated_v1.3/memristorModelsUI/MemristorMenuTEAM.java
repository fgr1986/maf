package memristorModelsUI;

import memristorModels.MemristorTEAM;
//import memristorModels.MemristorHPSpice;
//import memristorModels.MemristorHPeV;
import memristorModels.MemristorModel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import simulationObjects.ResourcesAndUsefulFunctions;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MemristorMenuTEAM extends Composite implements MemristorModelMenu {
	private Text textRonVariation;
	private Text textWonVariation;
	private Text textWoffVariation;
	private Text textIonVariation;
	private Text textIoffVariation;
	private Text textKonVariation;
	private Text textKoffVariation;
	private Text textAonVariation;
	private Text textAoffVariation;
	private Text textWcVariation;
	private Text textWInitVariation;
	private Text textAlphaonVariation;
	private Text textAlphaoffVariation;
	private Text textRoffVariation;
	private Button checkRon;
	private Button checkX_on;
	private Button checkX_off;
	private Button checkIOn;
	private Button checkIOff;
	private Button checkKOn;
	private Button checkKOff;
	private Button checkAOn;
	private Button checkAOff;
	private Button checkXc;
	private Button checkWInit;
	private Button checkAlphaOn;
	private Button checkAlphaOff;
	private Button checkRoff;
	private MemristorTEAM memristor;
	private boolean montecarlo;
	private int window;
	private Group grpModelParametersMean;
	private Text textRonMean;
	private Text textWonMean;
	private Text textWoffMean;
	private Text textIonMean;
	private Text textIoffMean;
	private Text textKonMean;
	private Text textKoffMean;
	private Text textAonMean;
	private Text textAoffMean;
	private Text textWcMean;
	private Text textWinitMean;
	private Text textAlphaonMean;
	private Text textAlphaoffMean;
	private Text textRoffMean;
	private Combo combo;

	private Group compositeVariations;
	private Button buttonAll;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MemristorMenuTEAM(Composite parent, int style) {
		super(parent, style);
		// this.memristor = (MemristorTEAM) memristor;
		this.memristor = new MemristorTEAM();
		compositeVariations = new Group(this, SWT.SHADOW_ETCHED_IN);
		compositeVariations.setText("Model parameters variation");
		compositeVariations.setBounds(10, 236, 688, 227);

		checkRon = new Button(compositeVariations, SWT.CHECK);
		checkRon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRonVariation.setEnabled(checkRon.getSelection());
			}
		});
		checkRon.setSelection(true);
		checkRon.setText("Ron " + ResourcesAndUsefulFunctions.OHM2 + ":");
		checkRon.setBounds(10, 30, 137, 26);

		checkX_on = new Button(compositeVariations, SWT.CHECK);
		checkX_on.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWonVariation.setEnabled(checkX_on.getSelection());
			}
		});
		checkX_on.setSelection(true);
		checkX_on.setText("X_on (m)");
		checkX_on.setBounds(10, 130, 137, 22);

		textRonVariation = new Text(compositeVariations, SWT.BORDER);
		textRonVariation.setBounds(153, 30, 62, 26);

		textWonVariation = new Text(compositeVariations, SWT.BORDER);
		textWonVariation.setBounds(153, 130, 62, 26);

		checkX_off = new Button(compositeVariations, SWT.CHECK);
		checkX_off.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWoffVariation.setEnabled(checkX_off.getSelection());
			}
		});
		checkX_off.setSelection(true);
		checkX_off.setText("X_off (m):");
		checkX_off.setBounds(10, 94, 137, 26);

		textWoffVariation = new Text(compositeVariations, SWT.BORDER);
		textWoffVariation.setBounds(153, 94, 62, 26);

		checkAlphaOff = new Button(compositeVariations, SWT.CHECK);
		checkAlphaOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAlphaoffVariation.setEnabled(checkAlphaOff.getSelection());
			}
		});
		checkAlphaOff.setSelection(true);
		checkAlphaOff.setText("Alpha_off:");
		checkAlphaOff.setBounds(238, 126, 136, 26);

		checkIOn = new Button(compositeVariations, SWT.CHECK);
		checkIOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textIonVariation.setEnabled(checkIOn.getSelection());
			}
		});
		checkIOn.setSelection(true);
		checkIOn.setText("i_on (A):");
		checkIOn.setBounds(10, 161, 137, 26);

		textIonVariation = new Text(compositeVariations, SWT.BORDER);
		textIonVariation.setBounds(153, 161, 62, 26);

		checkIOff = new Button(compositeVariations, SWT.CHECK);
		checkIOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textIoffVariation.setEnabled(checkIOff.getSelection());
			}
		});
		checkIOff.setSelection(true);
		checkIOff.setText("i_off (A):");
		checkIOff.setBounds(10, 194, 137, 26);

		textIoffVariation = new Text(compositeVariations, SWT.BORDER);
		textIoffVariation.setBounds(153, 194, 62, 26);

		checkKOn = new Button(compositeVariations, SWT.CHECK);
		checkKOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textKonVariation.setEnabled(checkKOn.getSelection());
			}
		});
		checkKOn.setSelection(true);
		checkKOn.setText("K_on (m/s):");
		checkKOn.setBounds(238, 158, 136, 26);

		textKonVariation = new Text(compositeVariations, SWT.BORDER);
		textKonVariation.setBounds(380, 158, 74, 26);

		checkKOff = new Button(compositeVariations, SWT.CHECK);
		checkKOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textKoffVariation.setEnabled(checkKOff.getSelection());
			}
		});
		checkKOff.setSelection(true);
		checkKOff.setText("K_off (m/s):");
		checkKOff.setBounds(238, 194, 136, 22);

		textKoffVariation = new Text(compositeVariations, SWT.BORDER);
		textKoffVariation.setBounds(380, 194, 74, 26);

		checkAOn = new Button(compositeVariations, SWT.CHECK);
		checkAOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAonVariation.setEnabled(checkAOn.getSelection());
			}
		});
		checkAOn.setSelection(true);
		checkAOn.setText("a_on (m):");
		checkAOn.setBounds(238, 30, 136, 27);

		textAonVariation = new Text(compositeVariations, SWT.BORDER);
		textAonVariation.setBounds(380, 30, 74, 26);

		checkAOff = new Button(compositeVariations, SWT.CHECK);
		checkAOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAoffVariation.setEnabled(checkAOff.getSelection());
			}
		});
		checkAOff.setSelection(true);
		checkAOff.setText("a_off (m):");
		checkAOff.setBounds(238, 63, 136, 26);

		textAoffVariation = new Text(compositeVariations, SWT.BORDER);
		textAoffVariation.setBounds(380, 62, 74, 26);

		checkAlphaOn = new Button(compositeVariations, SWT.CHECK);
		checkAlphaOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAlphaonVariation.setEnabled(checkAlphaOn.getSelection());
			}
		});
		checkAlphaOn.setSelection(true);
		checkAlphaOn.setText("Alpha_on:");
		checkAlphaOn.setBounds(238, 94, 136, 26);

		checkXc = new Button(compositeVariations, SWT.CHECK);
		checkXc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWcVariation.setEnabled(checkXc.getSelection());
			}
		});
		checkXc.setSelection(true);
		checkXc.setText("W_c(m):");
		checkXc.setBounds(460, 63, 135, 26);

		textWcVariation = new Text(compositeVariations, SWT.BORDER);
		textWcVariation.setBounds(601, 63, 74, 26);

		checkWInit = new Button(compositeVariations, SWT.CHECK);
		checkWInit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWInitVariation.setEnabled(checkWInit.getSelection());
			}
		});
		checkWInit.setSelection(true);
		checkWInit.setText("X_init (m):");
		checkWInit.setBounds(460, 34, 137, 22);

		textWInitVariation = new Text(compositeVariations, SWT.BORDER);
		textWInitVariation.setBounds(603, 30, 72, 26);

		textAlphaonVariation = new Text(compositeVariations, SWT.BORDER);
		textAlphaonVariation.setBounds(380, 94, 74, 26);

		textAlphaoffVariation = new Text(compositeVariations, SWT.BORDER);
		textAlphaoffVariation.setBounds(380, 126, 74, 26);

		checkRoff = new Button(compositeVariations, SWT.CHECK);
		checkRoff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRoffVariation.setEnabled(checkRoff.getSelection());
			}
		});
		checkRoff.setSelection(true);
		checkRoff.setText("Roff " + ResourcesAndUsefulFunctions.OHM2 + ":");
		checkRoff.setBounds(10, 62, 137, 26);

		textRoffVariation = new Text(compositeVariations, SWT.BORDER);
		textRoffVariation.setBounds(153, 62, 62, 26);
		
		buttonAll = new Button(compositeVariations, SWT.CHECK);
		buttonAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(buttonAll.getSelection()){
					buttonAll.setText("Select all");
					}else{
						buttonAll.setText("Deselect all");
					}
					setAllChecks(buttonAll.getSelection());
			}
		});
		buttonAll.setText("(De)Select all");
		buttonAll.setSelection(true);
		buttonAll.setBounds(554, 194, 124, 26);

		grpModelParametersMean = new Group(this, SWT.SHADOW_ETCHED_IN);
		grpModelParametersMean.setText("Model parameters mean value");
		grpModelParametersMean.setBounds(10, 0, 688, 227);

		Label label = new Label(grpModelParametersMean, SWT.NONE);
		label.setText("Ron (Ω):");
		label.setBounds(10, 30, 137, 26);

		Label label_1 = new Label(grpModelParametersMean, SWT.NONE);
		label_1.setText("X_on (m)");
		label_1.setBounds(10, 130, 137, 22);

		textRonMean = new Text(grpModelParametersMean, SWT.BORDER);
		textRonMean.setText("0.0");
		textRonMean.setBounds(153, 30, 62, 26);

		textWonMean = new Text(grpModelParametersMean, SWT.BORDER);
		textWonMean.setText("0.0");
		textWonMean.setBounds(153, 130, 62, 26);

		Label label_2 = new Label(grpModelParametersMean, SWT.NONE);
		label_2.setText("X_off (m):");
		label_2.setBounds(10, 94, 137, 26);

		textWoffMean = new Text(grpModelParametersMean, SWT.BORDER);
		textWoffMean.setText("0.0");
		textWoffMean.setBounds(153, 94, 62, 26);

		Label label_3 = new Label(grpModelParametersMean, SWT.NONE);
		label_3.setText("Alpha_off:");
		label_3.setBounds(238, 126, 136, 26);

		Label label_4 = new Label(grpModelParametersMean, SWT.NONE);
		label_4.setText("i_on (A):");
		label_4.setBounds(10, 161, 137, 26);

		textIonMean = new Text(grpModelParametersMean, SWT.BORDER);
		textIonMean.setText("0.0");
		textIonMean.setBounds(153, 161, 62, 26);

		Label label_5 = new Label(grpModelParametersMean, SWT.NONE);
		label_5.setText("i_off (A):");
		label_5.setBounds(10, 194, 137, 26);

		textIoffMean = new Text(grpModelParametersMean, SWT.BORDER);
		textIoffMean.setText("0.0");
		textIoffMean.setBounds(153, 194, 62, 26);

		Label label_6 = new Label(grpModelParametersMean, SWT.NONE);
		label_6.setText("K_on (m/s):");
		label_6.setBounds(238, 158, 136, 26);

		textKonMean = new Text(grpModelParametersMean, SWT.BORDER);
		textKonMean.setText("0.0");
		textKonMean.setBounds(380, 158, 74, 26);

		Label label_7 = new Label(grpModelParametersMean, SWT.NONE);
		label_7.setText("K_off (m/s):");
		label_7.setBounds(238, 194, 136, 22);

		textKoffMean = new Text(grpModelParametersMean, SWT.BORDER);
		textKoffMean.setText("0.0");
		textKoffMean.setBounds(380, 194, 74, 26);

		Label label_8 = new Label(grpModelParametersMean, SWT.NONE);
		label_8.setText("a_on (m):");
		label_8.setBounds(238, 30, 136, 27);

		textAonMean = new Text(grpModelParametersMean, SWT.BORDER);
		textAonMean.setText("0.0");
		textAonMean.setBounds(380, 30, 74, 26);

		Label label_9 = new Label(grpModelParametersMean, SWT.NONE);
		label_9.setText("a_off (m):");
		label_9.setBounds(238, 63, 136, 26);

		textAoffMean = new Text(grpModelParametersMean, SWT.BORDER);
		textAoffMean.setText("0.0");
		textAoffMean.setBounds(380, 62, 74, 26);

		Label label_10 = new Label(grpModelParametersMean, SWT.NONE);
		label_10.setText("Alpha_on:");
		label_10.setBounds(238, 94, 136, 26);

		Label label_11 = new Label(grpModelParametersMean, SWT.NONE);
		label_11.setText("W_c(m):");
		label_11.setBounds(460, 63, 135, 26);

		textWcMean = new Text(grpModelParametersMean, SWT.BORDER);
		textWcMean.setText("0.0");
		textWcMean.setBounds(601, 63, 74, 26);

		Label label_12 = new Label(grpModelParametersMean, SWT.NONE);
		label_12.setText("W_init (m):");
		label_12.setBounds(460, 34, 137, 22);

		textWinitMean = new Text(grpModelParametersMean, SWT.BORDER);
		textWinitMean.setText("0.0");
		textWinitMean.setBounds(603, 30, 72, 26);

		textAlphaonMean = new Text(grpModelParametersMean, SWT.BORDER);
		textAlphaonMean.setText("0.0");
		textAlphaonMean.setBounds(380, 94, 74, 26);

		textAlphaoffMean = new Text(grpModelParametersMean, SWT.BORDER);
		textAlphaoffMean.setText("0.0");
		textAlphaoffMean.setBounds(380, 126, 74, 26);

		Label label_13 = new Label(grpModelParametersMean, SWT.NONE);
		label_13.setText("Roff (Ω):");
		label_13.setBounds(10, 62, 137, 26);

		textRoffMean = new Text(grpModelParametersMean, SWT.BORDER);
		textRoffMean.setText("0.0");
		textRoffMean.setBounds(153, 62, 62, 26);

		combo = new Combo(grpModelParametersMean, SWT.NONE);
		combo.setBounds(460, 189, 218, 27);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				window = combo.getSelectionIndex();
			}
		});
		for (String p : this.memristor.getWindowsNames()) {
			combo.add(p);
		}
		combo.select(0);
		window = 0;

		setMontecarlo();

	}

	private void setAllChecks(boolean selected){		
		for(Control c : compositeVariations.getChildren()){
			if (c.getClass() == Button.class){
				((Button)c).setSelection(selected);
			}
			if (c.getClass() == Text.class){
				((Text)c).setEnabled(selected);
			}
		}	
	}
	
	public void setParameters() {
		try {
			memristor.getParameter(memristor.RONINDEX).setValue(
					Double.parseDouble(textRonMean.getText()));
			memristor.getParameter(memristor.ROFFINDEX).setValue(
					Double.parseDouble(textRoffMean.getText()));
			memristor.getParameter(memristor.XONINDEX).setValue(
					Double.parseDouble(textWonMean.getText()));
			memristor.getParameter(memristor.XOFFINDEX).setValue(
					Double.parseDouble(textWoffMean.getText()));
			memristor.getParameter(memristor.IONINDEX).setValue(
					Double.parseDouble(textIonMean.getText()));
			memristor.getParameter(memristor.IOFFINDEX).setValue(
					Double.parseDouble(textIoffMean.getText()));
			memristor.getParameter(memristor.AONINDEX).setValue(
					Double.parseDouble(textAonMean.getText()));
			memristor.getParameter(memristor.AOFFINDEX).setValue(
					Double.parseDouble(textAoffMean.getText()));
			memristor.getParameter(memristor.ALPHAONINDEX).setValue(
					Double.parseDouble(textAlphaonMean.getText()));
			memristor.getParameter(memristor.ALPHAOFFINDEX).setValue(
					Double.parseDouble(textAlphaoffMean.getText()));
			memristor.getParameter(memristor.KONINDEX).setValue(
					Double.parseDouble(textKonMean.getText()));
			memristor.getParameter(memristor.KOFFINDEX).setValue(
					Double.parseDouble(textKoffMean.getText()));
			memristor.getParameter(memristor.WCINDEX).setValue(
					Double.parseDouble(textWcMean.getText()));
			memristor.getParameter(memristor.XINITINDEX).setValue(
					Double.parseDouble(textWinitMean.getText()));
			memristor.getParameter(memristor.WCINDEX).setValue(
					Double.parseDouble(textWcMean.getText()));

			if (montecarlo) {
				if (checkRon.getSelection()) {
					memristor.getParameter(memristor.RONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.RONINDEX).setSigma(
							Double.parseDouble(textRonVariation.getText()));
				} else {
					memristor.getParameter(memristor.RONINDEX).setAllowVariations(false);
				}
				if (checkRoff.getSelection()) {
					memristor.getParameter(memristor.ROFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.ROFFINDEX).setSigma(
							Double.parseDouble(textRoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.ROFFINDEX).setAllowVariations(false);
				}
				if (checkX_on.getSelection()) {
					memristor.getParameter(memristor.XONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.XONINDEX).setSigma(
							Double.parseDouble(textWonVariation.getText()));
				} else {
					memristor.getParameter(memristor.XONINDEX).setAllowVariations(false);
				}
				if (checkX_off.getSelection()) {
					memristor.getParameter(memristor.XOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.XOFFINDEX).setSigma(
							Double.parseDouble(textWoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.XOFFINDEX).setAllowVariations(false);
				}
				if (checkIOn.getSelection()) {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.IONINDEX).setSigma(
							Double.parseDouble(textIonVariation.getText()));
				} else {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(false);
				}
				if (checkIOff.getSelection()) {
					memristor.getParameter(memristor.IOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.IOFFINDEX).setSigma(
							Double.parseDouble(textIoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.IOFFINDEX).setAllowVariations(false);
				}
				if (checkAOn.getSelection()) {
					memristor.getParameter(memristor.AONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.AONINDEX).setSigma(
							Double.parseDouble(textAonVariation.getText()));
				} else {
					memristor.getParameter(memristor.AONINDEX).setAllowVariations(false);
				}
				if (checkAOff.getSelection()) {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.AOFFINDEX).setSigma(
							Double.parseDouble(textAoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(false);
				}
				if (checkAlphaOn.getSelection()) {
					memristor.getParameter(memristor.ALPHAONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.ALPHAONINDEX).setSigma(
							Double.parseDouble(textAlphaonVariation.getText()));
				} else {
					memristor.getParameter(memristor.ALPHAONINDEX).setAllowVariations(false);
				}
				if (checkAlphaOff.getSelection()) {
					memristor.getParameter(memristor.ALPHAOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.ALPHAOFFINDEX).setSigma(
							Double.parseDouble(textAlphaoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.ALPHAOFFINDEX).setAllowVariations(false);
				}
				if (checkKOn.getSelection()) {
					memristor.getParameter(memristor.KONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.KONINDEX).setSigma(
							Double.parseDouble(textKonVariation.getText()));
				} else {
					memristor.getParameter(memristor.KONINDEX).setAllowVariations(false);
				}
				if (checkKOff.getSelection()) {
					memristor.getParameter(memristor.KOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.KOFFINDEX).setSigma(
							Double.parseDouble(textKoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.KOFFINDEX).setAllowVariations(false);
				}
				if (checkXc.getSelection()) {
					memristor.getParameter(memristor.WCINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.WCINDEX).setSigma(
							Double.parseDouble(textWcVariation.getText()));
				} else {
					memristor.getParameter(memristor.WCINDEX).setAllowVariations(false);
				}
				if (checkWInit.getSelection()) {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.XINITINDEX).setSigma(
							Double.parseDouble(textWInitVariation.getText()));
				} else {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(false);
				}

			} else {
				String[] s_aux;
				double[] d_aux;
				if (checkRon.getSelection()) {
					memristor.getParameter(memristor.RONINDEX).setAllowVariations(true);
					s_aux = textRonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.RONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.RONINDEX).setAllowVariations(false);
				}
				if (checkRoff.getSelection()) {
					memristor.getParameter(memristor.ROFFINDEX).setAllowVariations(true);
					s_aux = textRoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.ROFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.ROFFINDEX).setAllowVariations(false);
				}
				if (checkX_on.getSelection()) {
					memristor.getParameter(memristor.XONINDEX).setAllowVariations(true);
					s_aux = textWonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.XONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.XONINDEX).setAllowVariations(false);
				}
				if (checkX_off.getSelection()) {
					memristor.getParameter(memristor.XOFFINDEX).setAllowVariations(true);
					s_aux = textWoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.XOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.XOFFINDEX).setAllowVariations(false);
				}
				if (checkIOn.getSelection()) {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(true);
					s_aux = textIonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.IONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(false);
				}
				if (checkIOff.getSelection()) {
					memristor.getParameter(memristor.IOFFINDEX).setAllowVariations(true);
					s_aux = textIoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.IOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.IOFFINDEX).setAllowVariations(false);
				}
				if (checkAOn.getSelection()) {
					memristor.getParameter(memristor.AONINDEX).setAllowVariations(true);
					s_aux = textAonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.AONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.AONINDEX).setAllowVariations(false);
				}
				if (checkAOff.getSelection()) {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(true);
					s_aux = textAoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.AOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(false);
				}
				if (checkAlphaOn.getSelection()) {
					memristor.getParameter(memristor.ALPHAONINDEX).setAllowVariations(true);
					s_aux = textAlphaonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.ALPHAONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.ALPHAONINDEX).setAllowVariations(false);
				}
				if (checkAlphaOff.getSelection()) {
					memristor.getParameter(memristor.ALPHAOFFINDEX).setAllowVariations(true);
					s_aux = textAlphaoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.ALPHAOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.ALPHAOFFINDEX).setAllowVariations(false);
				}
				if (checkKOn.getSelection()) {
					memristor.getParameter(memristor.KONINDEX).setAllowVariations(true);
					s_aux = textKonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.KONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.KONINDEX).setAllowVariations(false);
				}
				if (checkKOff.getSelection()) {
					memristor.getParameter(memristor.KOFFINDEX).setAllowVariations(true);
					s_aux = textKoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.KOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.KOFFINDEX).setAllowVariations(false);
				}
				if (checkXc.getSelection()) {
					memristor.getParameter(memristor.WCINDEX).setAllowVariations(true);
					s_aux = textWcVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.WCINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.WCINDEX).setAllowVariations(false);
				}
				if (checkWInit.getSelection()) {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(true);
					s_aux = textWInitVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.XINITINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(false);
				}
			}

			memristor.getParameter(memristor.WINDOWINDEX).setValue(window);
		} catch (Exception exc) {
			MessageDialog.openWarning(new Shell(), "Parameters error",
					"Model parameters error: " + exc.getLocalizedMessage());
			return;
		}
	}

	public MemristorModel getMemristor() {
		setParameters();
		return memristor;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setMontecarlo() {
		compositeVariations.setText("Parameter variations, standard deviation");
		checkRon.setText("Ron, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkRoff.setText("Roff, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkX_on.setText("X_on, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkX_off.setText("X_off, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkIOn.setText("i_on, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkIOff.setText("i_off, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAlphaOn.setText("alpha_n, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAlphaOff.setText("alpha_off, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAOn.setText("Aon, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAOff.setText("Aoff, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkKOn.setText("k_on, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkKOff.setText("k_off, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkXc.setText("w_c, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkWInit.setText("w_init, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		textRonMean.setText("" + memristor.getParameter(memristor.RONINDEX).getValue());
		textRoffMean.setText("" + memristor.getParameter(memristor.ROFFINDEX).getValue());
		textWonMean.setText("" + memristor.getParameter(memristor.XONINDEX).getValue());
		textWoffMean.setText("" + memristor.getParameter(memristor.XOFFINDEX).getValue());
		textIonMean.setText("" + memristor.getParameter(memristor.IONINDEX).getValue());
		textIoffMean.setText("" + memristor.getParameter(memristor.IOFFINDEX).getValue());
		textAlphaonMean.setText("" + memristor.getParameter(memristor.ALPHAONINDEX).getValue());
		textAlphaoffMean.setText("" + memristor.getParameter(memristor.ALPHAOFFINDEX).getValue());
		textAonMean.setText("" + memristor.getParameter(memristor.AONINDEX).getValue());
		textAoffMean.setText("" + memristor.getParameter(memristor.AOFFINDEX).getValue());
		textKonMean.setText("" + memristor.getParameter(memristor.KONINDEX).getValue());
		textKoffMean.setText("" + memristor.getParameter(memristor.KOFFINDEX).getValue());
		textWcMean.setText("" + memristor.getParameter(memristor.WCINDEX).getValue());
		textWinitMean.setText("" + memristor.getParameter(memristor.XINITINDEX).getValue());
		textRonVariation.setText("" + memristor.getParameter(memristor.RONINDEX).getSigma());
		textRoffVariation.setText("" + memristor.getParameter(memristor.ROFFINDEX).getSigma());
		textWonVariation.setText("" + memristor.getParameter(memristor.XONINDEX).getSigma());
		textWoffVariation.setText("" + memristor.getParameter(memristor.XOFFINDEX).getSigma());
		textIonVariation.setText("" + memristor.getParameter(memristor.IONINDEX).getSigma());
		textIoffVariation.setText("" + memristor.getParameter(memristor.IOFFINDEX).getSigma());
		textAlphaonVariation.setText("" + memristor.getParameter(memristor.ALPHAONINDEX).getSigma());
		textAlphaoffVariation.setText("" + memristor.getParameter(memristor.ALPHAOFFINDEX).getSigma());
		textAonVariation.setText("" + memristor.getParameter(memristor.AONINDEX).getSigma());
		textAoffVariation.setText("" + memristor.getParameter(memristor.AOFFINDEX).getSigma());
		textKonVariation.setText("" + memristor.getParameter(memristor.KONINDEX).getSigma());
		textKoffVariation.setText("" + memristor.getParameter(memristor.KOFFINDEX).getSigma());
		textWcVariation.setText("" + memristor.getParameter(memristor.WCINDEX).getSigma());
		textWInitVariation.setText("" + memristor.getParameter(memristor.XINITINDEX).getSigma());
		montecarlo = true;
	}

	@Override
	public void setSpaceExploration() {
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variation, alternative value list");
		checkRon.setText("Ron: ");
		checkRoff.setText("Roff: ");
		checkX_on.setText("X_on: ");
		checkX_off.setText("X_off: ");
		checkIOn.setText("i_on: ");
		checkIOff.setText("i_off: ");
		checkAlphaOn.setText("alpha_n: ");
		checkAlphaOff.setText("alpha_off: ");
		checkAOn.setText("Aon: ");
		checkAOff.setText("Aoff: ");
		checkKOn.setText("k_on: ");
		checkKOff.setText("k_off: ");
		checkXc.setText("w_c: ");
		checkWInit.setText("w_init: ");

		textRonMean.setText("" + memristor.getParameter(memristor.RONINDEX).getValue());
		textRoffMean.setText("" + memristor.getParameter(memristor.ROFFINDEX).getValue());
		textWonMean.setText("" + memristor.getParameter(memristor.XONINDEX).getValue());
		textWoffMean.setText("" + memristor.getParameter(memristor.XOFFINDEX).getValue());
		textIonMean.setText("" + memristor.getParameter(memristor.IONINDEX).getValue());
		textIoffMean.setText("" + memristor.getParameter(memristor.IOFFINDEX).getValue());
		textAlphaonMean.setText("" + memristor.getParameter(memristor.ALPHAONINDEX).getValue());
		textAlphaoffMean.setText("" + memristor.getParameter(memristor.ALPHAOFFINDEX).getValue());
		textAonMean.setText("" + memristor.getParameter(memristor.AONINDEX).getValue());
		textAoffMean.setText("" + memristor.getParameter(memristor.AOFFINDEX).getValue());
		textKonMean.setText("" + memristor.getParameter(memristor.KONINDEX).getValue());
		textKoffMean.setText("" + memristor.getParameter(memristor.KOFFINDEX).getValue());
		textWcMean.setText("" + memristor.getParameter(memristor.WCINDEX).getValue());
//		textWinitMean.setText("" + memristor.getParameter(memristor.XINITINDEX).getValue());
//		textRonVariation.setText(""
//				+ memristor.getParameter(memristor.RONINDEX).getMaxVariation());
//		textRoffVariation.setText(""
//				+ memristor.getParameter(memristor.ROFFINDEX).getMaxVariation());
//		textWonVariation.setText(""
//				+ memristor.getParameter(memristor.XONINDEX).getMaxVariation());
//		textWoffVariation.setText(""
//				+ memristor.getParameter(memristor.XOFFINDEX).getMaxVariation());
//		textIonVariation.setText(""
//				+ memristor.getParameter(memristor.IONINDEX).getMaxVariation());
//		textIoffVariation.setText(""
//				+ memristor.getParameter(memristor.IOFFINDEX).getMaxVariation());
//		textAlphaonVariation.setText(""
//				+ memristor.getParameter(memristor.ALPHAONINDEX).getMaxVariation());
//		textAlphaoffVariation.setText(""
//				+ memristor.getParameter(memristor.ALPHAOFFINDEX).getMaxVariation());
//		textAonVariation.setText(""
//				+ memristor.getParameter(memristor.AONINDEX).getMaxVariation());
//		textAoffVariation.setText(""
//				+ memristor.getParameter(memristor.AOFFINDEX).getMaxVariation());
//		textKonVariation.setText(""
//				+ memristor.getParameter(memristor.KONINDEX).getMaxVariation());
//		textKoffVariation.setText(""
//				+ memristor.getParameter(memristor.KOFFINDEX).getMaxVariation());
//		textWcVariation.setText(""
//				+ memristor.getParameter(memristor.WCINDEX).getMaxVariation());
//		textWInitVariation.setText(""
//				+ memristor.getParameter(memristor.XINITINDEX).getMaxVariation());
		
		montecarlo = false;

	}
}
