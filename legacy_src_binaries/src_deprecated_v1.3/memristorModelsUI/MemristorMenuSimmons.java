package memristorModelsUI;

import memristorModels.MemristorSimmons;
import memristorModels.MemristorModel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import simulationObjects.ResourcesAndUsefulFunctions;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MemristorMenuSimmons extends Composite implements MemristorModelMenu {
	private Text textRsVariation;
	private Text textPhi0Variation;
	private Text textAreaVariation;
	private Text textKVariation;
	private Text textIOnVariation;
	private Text textIOffVariation;
	private Text textFOnVariation;
	private Text textFOffVariation;
	private Text textAOnVariation;
	private Text textAOffVariation;
	private Text textBVariation;
	private Text textWcVariation;
	private Text textWInitVariation;
	private Button checkRs;
	private Button checkPhi0;
	private Button checkArea;
	private Button checkK;
	private Button checkIOn;
	private Button checkIOff;
	private Button checkFOn;
	private Button checkFOff;
	private Button checkAOn;
	private Button checkAOff;
	private Button checkB;
	private Button checkWc;
	private Button checkWInit;

	private MemristorSimmons memristor;
	private boolean montecarlo;
	private Group grpModelParameterMean;
	private Text textRsMean;
	private Text textPhi0Mean;
	private Text textAreaMean;
	private Text textKMean;
	private Text textIOnMean;
	private Text textIOffMean;
	private Text textFOnMean;
	private Text textFOffMean;
	private Text textAOnMean;
	private Text textAOffMean;
	private Text textBMean;
	private Text textWcMean;
	private Text textWInitMean;
	private Button buttonAll;
	private Group compositeVariations;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MemristorMenuSimmons(Composite parent, int style) {
		super(parent, style);
		// this.memristor = (MemristorHP) memristor;
		this.memristor = new MemristorSimmons();
		compositeVariations = new Group(this, SWT.SHADOW_ETCHED_IN);
		compositeVariations.setText("Model parameter variations");
		compositeVariations.setBounds(10, 228, 699, 212);

		checkRs = new Button(compositeVariations, SWT.CHECK);
		checkRs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRsVariation.setEnabled(checkRs.getSelection());
			}
		});
		checkRs.setSelection(true);
		checkRs.setBounds(10, 77, 100, 26);

		checkPhi0 = new Button(compositeVariations, SWT.CHECK);
		checkPhi0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textPhi0Variation.setEnabled(checkPhi0.getSelection());
			}
		});
		checkPhi0.setSelection(true);
		checkPhi0.setBounds(10, 113, 100, 22);

		textRsVariation = new Text(compositeVariations, SWT.BORDER);
		textRsVariation.setBounds(116, 73, 68, 26);

		textPhi0Variation = new Text(compositeVariations, SWT.BORDER);
		textPhi0Variation.setBounds(116, 109, 68, 26);

		checkArea = new Button(compositeVariations, SWT.CHECK);
		checkArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAreaVariation.setEnabled(checkArea.getSelection());
			}
		});
		checkArea.setSelection(true);
		checkArea.setBounds(10, 45, 100, 26);

		textAreaVariation = new Text(compositeVariations, SWT.BORDER);
		textAreaVariation.setBounds(116, 41, 68, 26);

		textKVariation = new Text(compositeVariations, SWT.BORDER);
		textKVariation.setBounds(505, 45, 74, 26);

		checkK = new Button(compositeVariations, SWT.CHECK);
		checkK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textKVariation.setEnabled(checkK.getSelection());
			}
		});
		checkK.setSelection(true);
		checkK.setBounds(401, 45, 98, 26);

		checkIOn = new Button(compositeVariations, SWT.CHECK);
		checkIOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textIOnVariation.setEnabled(checkIOn.getSelection());
			}
		});
		checkIOn.setSelection(true);
		checkIOn.setBounds(10, 141, 100, 26);

		textIOnVariation = new Text(compositeVariations, SWT.BORDER);
		textIOnVariation.setBounds(116, 141, 68, 26);

		checkIOff = new Button(compositeVariations, SWT.CHECK);
		checkIOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textIOffVariation.setEnabled(checkIOff.getSelection());
			}
		});
		checkIOff.setSelection(true);
		checkIOff.setBounds(10, 173, 100, 26);

		textIOffVariation = new Text(compositeVariations, SWT.BORDER);
		textIOffVariation.setBounds(116, 173, 68, 26);

		checkFOn = new Button(compositeVariations, SWT.CHECK);
		checkFOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textFOnVariation.setEnabled(checkFOn.getSelection());
			}
		});
		checkFOn.setSelection(true);
		checkFOn.setBounds(190, 41, 100, 26);

		textFOnVariation = new Text(compositeVariations, SWT.BORDER);
		textFOnVariation.setBounds(296, 41, 74, 26);

		checkFOff = new Button(compositeVariations, SWT.CHECK);
		checkFOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textFOffVariation.setEnabled(checkFOff.getSelection());
			}
		});
		checkFOff.setSelection(true);
		checkFOff.setBounds(190, 77, 100, 22);

		textFOffVariation = new Text(compositeVariations, SWT.BORDER);
		textFOffVariation.setBounds(296, 73, 74, 26);

		checkAOn = new Button(compositeVariations, SWT.CHECK);
		checkAOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAOnVariation.setEnabled(checkAOn.getSelection());
			}
		});
		checkAOn.setSelection(true);
		checkAOn.setBounds(190, 113, 98, 16);

		textAOnVariation = new Text(compositeVariations, SWT.BORDER);
		textAOnVariation.setBounds(296, 109, 74, 26);

		checkAOff = new Button(compositeVariations, SWT.CHECK);
		checkAOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAOffVariation.setEnabled(checkAOff.getSelection());
			}
		});
		checkAOff.setSelection(true);
		checkAOff.setBounds(190, 151, 98, 16);

		textAOffVariation = new Text(compositeVariations, SWT.BORDER);
		textAOffVariation.setBounds(296, 141, 74, 26);

		checkB = new Button(compositeVariations, SWT.CHECK);
		checkB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textBVariation.setEnabled(checkB.getSelection());
			}
		});
		checkB.setSelection(true);
		checkB.setBounds(401, 77, 98, 26);

		textBVariation = new Text(compositeVariations, SWT.BORDER);
		textBVariation.setBounds(505, 77, 74, 26);

		checkWc = new Button(compositeVariations, SWT.CHECK);
		checkWc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWcVariation.setEnabled(checkWc.getSelection());
			}
		});
		checkWc.setSelection(true);
		checkWc.setBounds(401, 113, 98, 22);

		textWcVariation = new Text(compositeVariations, SWT.BORDER);
		textWcVariation.setBounds(505, 109, 74, 26);

		checkWInit = new Button(compositeVariations, SWT.CHECK);
		checkWInit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWInitVariation.setEnabled(checkWInit.getSelection());
			}
		});
		checkWInit.setSelection(true);
		checkWInit.setBounds(401, 141, 98, 22);

		textWInitVariation = new Text(compositeVariations, SWT.BORDER);
		textWInitVariation.setBounds(505, 141, 74, 26);
		
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
		buttonAll.setBounds(565, 173, 124, 26);

		grpModelParameterMean = new Group(this, SWT.SHADOW_ETCHED_IN);
		grpModelParameterMean.setText("Model parameter mean values");
		grpModelParameterMean.setBounds(10, 10, 699, 212);

		Label label = new Label(grpModelParameterMean, SWT.NONE);
		label.setText("Rs: ");
		label.setBounds(10, 77, 100, 26);

		Label label_1 = new Label(grpModelParameterMean, SWT.NONE);
		label_1.setText("Phi0: ");
		label_1.setBounds(10, 113, 100, 22);

		textRsMean = new Text(grpModelParameterMean, SWT.BORDER);
		textRsMean.setText("0.0");
		textRsMean.setBounds(116, 73, 68, 26);

		textPhi0Mean = new Text(grpModelParameterMean, SWT.BORDER);
		textPhi0Mean.setText("0.0");
		textPhi0Mean.setBounds(116, 109, 68, 26);

		Label label_2 = new Label(grpModelParameterMean, SWT.NONE);
		label_2.setText("Area: ");
		label_2.setBounds(10, 45, 100, 26);

		textAreaMean = new Text(grpModelParameterMean, SWT.BORDER);
		textAreaMean.setText("0.0");
		textAreaMean.setBounds(116, 41, 68, 26);

		textKMean = new Text(grpModelParameterMean, SWT.BORDER);
		textKMean.setText("0.0");
		textKMean.setBounds(505, 45, 74, 26);

		Label label_3 = new Label(grpModelParameterMean, SWT.NONE);
		label_3.setText("k: ");
		label_3.setBounds(401, 45, 98, 26);

		Label label_4 = new Label(grpModelParameterMean, SWT.NONE);
		label_4.setText("Ion: ");
		label_4.setBounds(10, 141, 100, 26);

		textIOnMean = new Text(grpModelParameterMean, SWT.BORDER);
		textIOnMean.setText("0.0");
		textIOnMean.setBounds(116, 141, 68, 26);

		Label label_5 = new Label(grpModelParameterMean, SWT.NONE);
		label_5.setText("Ioff: ");
		label_5.setBounds(10, 173, 100, 26);

		textIOffMean = new Text(grpModelParameterMean, SWT.BORDER);
		textIOffMean.setText("0.0");
		textIOffMean.setBounds(116, 173, 68, 26);

		Label label_6 = new Label(grpModelParameterMean, SWT.NONE);
		label_6.setText("Fon: ");
		label_6.setBounds(190, 41, 100, 26);

		textFOnMean = new Text(grpModelParameterMean, SWT.BORDER);
		textFOnMean.setText("0.0");
		textFOnMean.setBounds(296, 41, 74, 26);

		Label label_7 = new Label(grpModelParameterMean, SWT.NONE);
		label_7.setText("Foff: ");
		label_7.setBounds(190, 77, 100, 22);

		textFOffMean = new Text(grpModelParameterMean, SWT.BORDER);
		textFOffMean.setText("0.0");
		textFOffMean.setBounds(296, 73, 74, 26);

		Label label_8 = new Label(grpModelParameterMean, SWT.NONE);
		label_8.setText("Aon: ");
		label_8.setBounds(190, 113, 98, 16);

		textAOnMean = new Text(grpModelParameterMean, SWT.BORDER);
		textAOnMean.setText("0.0");
		textAOnMean.setBounds(296, 109, 74, 26);

		Label label_9 = new Label(grpModelParameterMean, SWT.NONE);
		label_9.setText("Aoff: ");
		label_9.setBounds(190, 151, 98, 16);

		textAOffMean = new Text(grpModelParameterMean, SWT.BORDER);
		textAOffMean.setText("0.0");
		textAOffMean.setBounds(296, 141, 74, 26);

		Label label_10 = new Label(grpModelParameterMean, SWT.NONE);
		label_10.setText("B: ");
		label_10.setBounds(401, 77, 98, 26);

		textBMean = new Text(grpModelParameterMean, SWT.BORDER);
		textBMean.setText("0.0");
		textBMean.setBounds(505, 77, 74, 26);

		Label label_11 = new Label(grpModelParameterMean, SWT.NONE);
		label_11.setText("Wc: ");
		label_11.setBounds(401, 113, 98, 22);

		textWcMean = new Text(grpModelParameterMean, SWT.BORDER);
		textWcMean.setText("0.0");
		textWcMean.setBounds(505, 109, 74, 26);

		Label label_12 = new Label(grpModelParameterMean, SWT.NONE);
		label_12.setText("w_init: ");
		label_12.setBounds(401, 141, 98, 22);

		textWInitMean = new Text(grpModelParameterMean, SWT.BORDER);
		textWInitMean.setText("0.0");
		textWInitMean.setBounds(505, 141, 74, 26);
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

			memristor.getParameter(memristor.RSINDEX)
					.setValue(Double.parseDouble(textRsMean.getText()));
			memristor.getParameter(memristor.PHI0INDEX).setValue(
					Double.parseDouble(textPhi0Mean.getText()));
			memristor.getParameter(memristor.KINDEX).setValue(Double.parseDouble(textKMean.getText()));
			memristor.getParameter(memristor.AREAINDEX).setValue(
					Double.parseDouble(textAreaMean.getText()));
			memristor.getParameter(memristor.IONINDEX).setValue(
					Double.parseDouble(textIOnMean.getText()));
			memristor.getParameter(memristor.IOFFINDEX).setValue(
					Double.parseDouble(textIOffMean.getText()));
			memristor.getParameter(memristor.AONINDEX).setValue(
					Double.parseDouble(textAOnMean.getText()));
			memristor.getParameter(memristor.AOFFINDEX).setValue(
					Double.parseDouble(textAOffMean.getText()));
			memristor.getParameter(memristor.FONINDEX).setValue(
					Double.parseDouble(textFOnMean.getText()));
			memristor.getParameter(memristor.FOFFINDEX).setValue(
					Double.parseDouble(textFOffMean.getText()));
			memristor.getParameter(memristor.BINDEX).setValue(Double.parseDouble(textBMean.getText()));
			memristor.getParameter(memristor.WCINDEX).setValue(
					Double.parseDouble(textWcMean.getText()));
			memristor.getParameter(memristor.WINITINDEX).setValue(
					Double.parseDouble(textWInitMean.getText()));
			// retrieve parameters
			if (montecarlo) {
				if (checkRs.getSelection()) {
					memristor.getParameter(memristor.RSINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.RSINDEX).setSigma(
							Double.parseDouble(textRsVariation.getText()));
				} else {
					memristor.getParameter(memristor.RSINDEX).setAllowVariations(false);
				}
				if (checkPhi0.getSelection()) {
					memristor.getParameter(memristor.PHI0INDEX).setAllowVariations(true);
					memristor.getParameter(memristor.PHI0INDEX).setSigma(
							Double.parseDouble(textPhi0Variation.getText()));
				} else {
					memristor.getParameter(memristor.PHI0INDEX).setAllowVariations(false);
				}
				if (checkK.getSelection()) {
					memristor.getParameter(memristor.KINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.KINDEX).setSigma(
							Double.parseDouble(textKVariation.getText()));
				} else {
					memristor.getParameter(memristor.KINDEX).setAllowVariations(false);
				}
				if (checkArea.getSelection()) {
					memristor.getParameter(memristor.AREAINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.AREAINDEX).setSigma(
							Double.parseDouble(textAreaVariation.getText()));
				} else {
					memristor.getParameter(memristor.AREAINDEX).setAllowVariations(false);
				}
				if (checkIOn.getSelection()) {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.IONINDEX).setSigma(
							Double.parseDouble(textIOnVariation.getText()));
				} else {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(false);
				}
				if (checkIOff.getSelection()) {
					memristor.getParameter(memristor.IOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.IOFFINDEX).setSigma(
							Double.parseDouble(textIOffVariation.getText()));
				} else {
					memristor.getParameter(memristor.IOFFINDEX).setAllowVariations(false);
				}
				if (checkAOn.getSelection()) {
					memristor.getParameter(memristor.AONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.AONINDEX).setSigma(
							Double.parseDouble(textAOnVariation.getText()));
				} else {
					memristor.getParameter(memristor.AONINDEX).setAllowVariations(false);
				}
				if (checkAOff.getSelection()) {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.AOFFINDEX).setSigma(
							Double.parseDouble(textAOffVariation.getText()));
				} else {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(false);
				}
				if (checkFOn.getSelection()) {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.FONINDEX).setSigma(
							Double.parseDouble(textFOnVariation.getText()));
				} else {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(false);
				}
				if (checkFOff.getSelection()) {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.FOFFINDEX).setSigma(
							Double.parseDouble(textFOffVariation.getText()));
				} else {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(false);
				}
				if (checkB.getSelection()) {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.BINDEX).setSigma(
							Double.parseDouble(textBVariation.getText()));
				} else {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(false);
				}
				if (checkWc.getSelection()) {
					memristor.getParameter(memristor.WCINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.WCINDEX).setSigma(
							Double.parseDouble(textWcVariation.getText()));
				} else {
					memristor.getParameter(memristor.WCINDEX).setAllowVariations(false);
				}
				if (checkWInit.getSelection()) {
					memristor.getParameter(memristor.WINITINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.WINITINDEX).setSigma(
							Double.parseDouble(textWInitVariation.getText()));
				} else {
					memristor.getParameter(memristor.WINITINDEX).setAllowVariations(false);
				}
			} else {

				String[] s_aux;
				double[] d_aux;
				if (checkRs.getSelection()) {
					memristor.getParameter(memristor.RSINDEX).setAllowVariations(true);
					s_aux = textRsVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.RSINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.RSINDEX).setAllowVariations(false);
				}
				if (checkPhi0.getSelection()) {
					memristor.getParameter(memristor.PHI0INDEX).setAllowVariations(true);
					s_aux = textPhi0Variation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.PHI0INDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.PHI0INDEX).setAllowVariations(false);
				}
				if (checkK.getSelection()) {
					memristor.getParameter(memristor.KINDEX).setAllowVariations(true);
					s_aux = textKVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.KINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.KINDEX).setAllowVariations(false);
				}
				if (checkArea.getSelection()) {
					memristor.getParameter(memristor.AREAINDEX).setAllowVariations(true);
					s_aux = textAreaVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.AREAINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.AREAINDEX).setAllowVariations(false);
				}
				if (checkIOn.getSelection()) {
					memristor.getParameter(memristor.IONINDEX).setAllowVariations(true);
					s_aux = textIOnVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
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
					s_aux = textIOffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
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
					s_aux = textAOnVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
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
					s_aux = textAOffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.AOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.AOFFINDEX).setAllowVariations(false);
				}
				if (checkFOn.getSelection()) {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(true);
					s_aux = textFOnVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.FONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(false);
				}
				if (checkFOff.getSelection()) {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(true);
					s_aux = textFOffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.FOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(false);
				}
				if (checkB.getSelection()) {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(true);
					s_aux = textBVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.BINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(false);
				}
				if (checkWc.getSelection()) {
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
					memristor.getParameter(memristor.WINITINDEX).setAllowVariations(true);
					s_aux = textWInitVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.WINITINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.WINITINDEX).setAllowVariations(false);
				}
			}

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
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variations, standard deviation");
		checkRs.setText("Rs, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkPhi0.setText("Phi0, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkArea.setText("Area, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkK.setText("k, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkIOn.setText("Ion, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkIOff.setText("Ioff, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkFOn.setText("Fon, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkFOff.setText("Foff, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAOn.setText("Aon, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAOff.setText("Aoff, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkB.setText("B, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkWc.setText("Wc, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkWInit.setText("w_init, " + ResourcesAndUsefulFunctions.SIGMA + ": ");

		textRsMean.setText("" + memristor.getParameter(memristor.RSINDEX).getValue());
		textPhi0Mean.setText("" + memristor.getParameter(memristor.PHI0INDEX).getValue());
		textAreaMean.setText("" + memristor.getParameter(memristor.AREAINDEX).getValue());
		textKMean.setText("" + memristor.getParameter(memristor.KINDEX).getValue());
		textIOnMean.setText("" + memristor.getParameter(memristor.IONINDEX).getValue());
		textIOffMean.setText("" + memristor.getParameter(memristor.IOFFINDEX).getValue());
		textFOnMean.setText("" + memristor.getParameter(memristor.FONINDEX).getValue());
		textFOffMean.setText("" + memristor.getParameter(memristor.FOFFINDEX).getValue());
		textAOnMean.setText("" + memristor.getParameter(memristor.AONINDEX).getValue());
		textAOffMean.setText("" + memristor.getParameter(memristor.AOFFINDEX).getValue());
		textBMean.setText("" + memristor.getParameter(memristor.BINDEX).getValue());
		textWcMean.setText("" + memristor.getParameter(memristor.WCINDEX).getValue());
		textWInitMean.setText("" + memristor.getParameter(memristor.WINITINDEX).getValue());

		textRsVariation.setText("" + memristor.getParameter(memristor.RSINDEX).getSigma());
		textPhi0Variation.setText("" + memristor.getParameter(memristor.PHI0INDEX).getSigma());
		textAreaVariation.setText("" + memristor.getParameter(memristor.AREAINDEX).getSigma());
		textKVariation.setText("" + memristor.getParameter(memristor.KINDEX).getSigma());
		textIOnVariation.setText("" + memristor.getParameter(memristor.IONINDEX).getSigma());
		textIOffVariation.setText("" + memristor.getParameter(memristor.IOFFINDEX).getSigma());
		textFOnVariation.setText("" + memristor.getParameter(memristor.FONINDEX).getSigma());
		textFOffVariation.setText("" + memristor.getParameter(memristor.FOFFINDEX).getSigma());
		textAOnVariation.setText("" + memristor.getParameter(memristor.AONINDEX).getSigma());
		textAOffVariation.setText("" + memristor.getParameter(memristor.AOFFINDEX).getSigma());
		textBVariation.setText("" + memristor.getParameter(memristor.BINDEX).getSigma());
		textWcVariation.setText("" + memristor.getParameter(memristor.WCINDEX).getSigma());
		textWInitVariation.setText("" + memristor.getParameter(memristor.WINITINDEX).getSigma());
		montecarlo = true;
	}

	@Override
	public void setSpaceExploration() {
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variation, alternative value list");
		
		checkRs.setText("Rs: ");
		checkPhi0.setText("Phi0: ");
		checkArea.setText("Area: ");
		checkK.setText("k: ");
		checkIOn.setText("Ion: ");
		checkIOff.setText("Ioff: ");
		checkFOn.setText("Fon: ");
		checkFOff.setText("Foff: ");
		checkAOn.setText("Aon: ");
		checkAOff.setText("Aoff: ");
		checkB.setText("B: ");
		checkWc.setText("Wc: ");
		checkWInit.setText("w_init: ");

//		textRsMean.setText("" + memristor.getParameter(memristor.RSINDEX).getValue());
//		textPhi0Mean.setText("" + memristor.getParameter(memristor.PHI0INDEX).getValue());
//		textAreaMean.setText("" + memristor.getParameter(memristor.AREAINDEX).getValue());
//		textKMean.setText("" + memristor.getParameter(memristor.KINDEX).getValue());
//		textIOnMean.setText("" + memristor.getParameter(memristor.IONINDEX).getValue());
//		textIOffMean.setText("" + memristor.getParameter(memristor.IOFFINDEX).getValue());
//		textFOnMean.setText("" + memristor.getParameter(memristor.FONINDEX).getValue());
//		textFOffMean.setText("" + memristor.getParameter(memristor.FOFFINDEX).getValue());
//		textAOnMean.setText("" + memristor.getParameter(memristor.AONINDEX).getValue());
//		textAOffMean.setText("" + memristor.getParameter(memristor.AOFFINDEX).getValue());
//		textBMean.setText("" + memristor.getParameter(memristor.BINDEX).getValue());
//		textWcMean.setText("" + memristor.getParameter(memristor.WCINDEX).getValue());
//		textWInitMean.setText("" + memristor.getParameter(memristor.WINITINDEX).getValue());
//
//		textRsVariation
//				.setText("" + memristor.getParameter(memristor.RSINDEX).getMaxVariation());
//		textPhi0Variation.setText(""
//				+ memristor.getParameter(memristor.PHI0INDEX).getMaxVariation());
//		textAreaVariation.setText(""
//				+ memristor.getParameter(memristor.AREAINDEX).getMaxVariation());
//		textKVariation.setText("" + memristor.getParameter(memristor.KINDEX).getMaxVariation());
//		textIOnVariation.setText(""
//				+ memristor.getParameter(memristor.IONINDEX).getMaxVariation());
//		textIOffVariation.setText(""
//				+ memristor.getParameter(memristor.IOFFINDEX).getMaxVariation());
//		textFOnVariation.setText(""
//				+ memristor.getParameter(memristor.FONINDEX).getMaxVariation());
//		textFOffVariation.setText(""
//				+ memristor.getParameter(memristor.FOFFINDEX).getMaxVariation());
//		textAOnVariation.setText(""
//				+ memristor.getParameter(memristor.AONINDEX).getMaxVariation());
//		textAOffVariation.setText(""
//				+ memristor.getParameter(memristor.AOFFINDEX).getMaxVariation());
//		textBVariation.setText("" + memristor.getParameter(memristor.BINDEX).getMaxVariation());
//		textWcVariation.setText(""
//				+ memristor.getParameter(memristor.WCINDEX).getMaxVariation());
//		textWInitVariation.setText(""
//				+ memristor.getParameter(memristor.WINITINDEX).getMaxVariation());
		
		montecarlo = false;

	}

}
