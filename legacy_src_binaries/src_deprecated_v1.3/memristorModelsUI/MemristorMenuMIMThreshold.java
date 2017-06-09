package memristorModelsUI;

import memristorModels.MemristorMIMThreshold;
import memristorModels.MemristorModel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import simulationObjects.ResourcesAndUsefulFunctions;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MemristorMenuMIMThreshold extends Composite implements
		MemristorModelMenu {
	private Button checkN;
	private Button checkA2;
	private Button checkA1;
	private Button checkB1;
	private Button checkB2;
	private Button checkFon;
	private Button checkFoff;
	private Button checkWMin;
	private Button checkWMax;
	private Button checkP0;
	private Button checkSfm;
	private Button checkSfo;

	private Text textNVariation;
	private Text textA2Variation;
	private Text textA1Variation;
	private Text textB1Variation;
	private Text textB2Variation;
	private Text textFonVariation;
	private Text textFoffVariation;
	private Text textWMinVariation;
	private Text textWMaxVariation;
	private Text textP0Variation;
	private Text textSfmVariation;
	private Text textSfoVariation;
	private Text textNMean;
	private Text textA2Mean;
	private Text textA1Mean;
	private Text textB1Mean;
	private Text textB2Mean;
	private Text textFonMean;
	private Text textFoffMean;
	private Text textWMinMean;
	private Text textWMaxMean;
	private Text textP0Mean;
	private Text textSfmMean;
	private Text textSfoMean;
	private MemristorMIMThreshold memristor;
	private boolean montecarlo;
	private Label lblP;
	private Text textPMean;
	private Label lblWindowParameters;
	private Text textPVariation;
	private Button checkP;
	private Label lblWinit;
	private Text textWInitMean;
	private Button checkWInit;
	private Text textWInitVariation;
	private Button buttonAll;
	private Group compositeVariations;
	private Label labelC;
	private Text textCMean;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MemristorMenuMIMThreshold(Composite parent, int style) {
		super(parent, style);
		memristor = new MemristorMIMThreshold();
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLocation(10, 10);
		group.setSize(700, 159);
		group.setText("Model parameters");

		Label lblN = new Label(group, SWT.NONE);
		lblN.setText("n:");
		lblN.setBounds(10, 88, 54, 26);

		textNMean = new Text(group, SWT.BORDER);
		textNMean.setText("4");
		textNMean.setBounds(99, 84, 54, 26);

//		textlMean = new Text(group, SWT.BORDER);
//		textlMean.setText("215.0");
//		textlMean.setBounds(99, 116, 54, 26);

		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("a2:");
		label_3.setBounds(10, 56, 64, 26);
		textA2Mean = new Text(group, SWT.BORDER);
		textA2Mean.setText("3.8E-4");
		textA2Mean.setBounds(99, 56, 54, 26);

		textA1Mean = new Text(group, SWT.BORDER);
		textA1Mean.setText("2.3E-4");
		textA1Mean.setBounds(99, 20, 54, 26);

		Label label_4 = new Label(group, SWT.NONE);
		label_4.setText("a1:");
		label_4.setBounds(10, 24, 64, 26);

		Label lblB = new Label(group, SWT.NONE);
		lblB.setText("b1:");
		lblB.setBounds(187, 20, 64, 26);

		textB1Mean = new Text(group, SWT.BORDER);
		textB1Mean.setText("4.0");
		textB1Mean.setBounds(257, 20, 46, 26);

		Label lblB_1 = new Label(group, SWT.NONE);
		lblB_1.setText("b2:");
		lblB_1.setBounds(187, 56, 64, 26);

		textB2Mean = new Text(group, SWT.BORDER);
		textB2Mean.setText("24.0");
		textB2Mean.setBounds(257, 52, 46, 26);

		Label label_7 = new Label(group, SWT.NONE);
		label_7.setText("Fon (V):");
		label_7.setBounds(187, 88, 64, 26);

		textFonMean = new Text(group, SWT.BORDER);
		textFonMean.setText("1.2");
		textFonMean.setBounds(257, 84, 46, 26);

		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("Foff (V):");
		label_8.setBounds(187, 120, 64, 22);

		textFoffMean = new Text(group, SWT.BORDER);
		textFoffMean.setText("0.6");
		textFoffMean.setBounds(257, 116, 46, 26);

		Label label_9 = new Label(group, SWT.NONE);
		label_9.setText("w_min:");
		label_9.setBounds(314, 20, 56, 16);

		textWMinMean = new Text(group, SWT.BORDER);
		textWMinMean.setText("0.7");
		textWMinMean.setBounds(376, 21, 54, 26);

		Label label_10 = new Label(group, SWT.NONE);
		label_10.setText("w_max:");
		label_10.setBounds(314, 56, 56, 26);

		textWMaxMean = new Text(group, SWT.BORDER);
		textWMaxMean.setText("0.8");
		textWMaxMean.setBounds(376, 53, 54, 26);

		Label label_11 = new Label(group, SWT.NONE);
		label_11.setText("p0:");
		label_11.setBounds(10, 122, 54, 22);

		textP0Mean = new Text(group, SWT.BORDER);
		textP0Mean.setText("0.02");
		textP0Mean.setBounds(99, 120, 54, 26);

		Label lblSfm = new Label(group, SWT.NONE);
		lblSfm.setText("sfm:");
		lblSfm.setBounds(516, 24, 40, 22);

		textSfmMean = new Text(group, SWT.BORDER);
		textSfmMean.setText("5.0");
		textSfmMean.setBounds(578, 20, 54, 26);

		Label lblSfi = new Label(group, SWT.NONE);
		lblSfi.setText("sfo:");
		lblSfi.setBounds(516, 56, 40, 22);

		textSfoMean = new Text(group, SWT.BORDER);
		textSfoMean.setText("30.0");
		textSfoMean.setBounds(578, 52, 54, 26);

		lblP = new Label(group, SWT.NONE);
		lblP.setText("p:");
		lblP.setBounds(516, 92, 40, 22);

		textPMean = new Text(group, SWT.BORDER);
		textPMean.setText("30.0");
		textPMean.setBounds(578, 88, 54, 26);

		lblWindowParameters = new Label(group, SWT.NONE);
		lblWindowParameters.setText("Shape factor:");
		lblWindowParameters.setBounds(516, 0, 116, 22);
		
		lblWinit = new Label(group, SWT.NONE);
		lblWinit.setText("w_init:");
		lblWinit.setBounds(314, 88, 56, 26);
		
		textWInitMean = new Text(group, SWT.BORDER);
		textWInitMean.setText("0.01");
		textWInitMean.setBounds(376, 85, 54, 26);
		
		labelC = new Label(group, SWT.NONE);
		labelC.setText("C, scales f_i during w integration:");
		labelC.setBounds(314, 118, 258, 26);
		
		textCMean = new Text(group, SWT.BORDER);
		textCMean.setText("0.01");
		textCMean.setBounds(578, 116, 54, 26);

		compositeVariations = new Group(this, SWT.SHADOW_ETCHED_IN);
		compositeVariations.setLocation(10, 175);
		compositeVariations.setSize(700, 279);
		compositeVariations.setText("Model parameters");

		checkN = new Button(compositeVariations, SWT.CHECK);
		checkN.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNVariation.setEnabled(checkN.getSelection());
			}
		});
		checkN.setSelection(true);
		checkN.setText("n:");
		checkN.setBounds(10, 89, 124, 26);


		textNVariation = new Text(compositeVariations, SWT.BORDER);
		textNVariation.setBounds(140, 85, 54, 26);

		checkA2 = new Button(compositeVariations, SWT.CHECK);
		checkA2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textA2Variation.setEnabled(checkA2.getSelection());
			}
		});
		checkA2.setSelection(true);
		checkA2.setText("a2:");
		checkA2.setBounds(10, 57, 124, 26);

		textA2Variation = new Text(compositeVariations, SWT.BORDER);
		textA2Variation.setBounds(140, 53, 54, 26);

		textA1Variation = new Text(compositeVariations, SWT.BORDER);
		textA1Variation.setBounds(140, 21, 54, 26);

		checkA1 = new Button(compositeVariations, SWT.CHECK);
		checkA1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textA1Variation.setEnabled(checkA1.getSelection());
			}
		});
		checkA1.setSelection(true);
		checkA1.setText("a1:");
		checkA1.setBounds(10, 25, 124, 26);

		checkB1 = new Button(compositeVariations, SWT.CHECK);
		checkB1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textB1Variation.setEnabled(checkB1.getSelection());
			}
		});
		checkB1.setSelection(true);
		checkB1.setText("alpha P:");
		checkB1.setBounds(10, 121, 124, 26);

		textB1Variation = new Text(compositeVariations, SWT.BORDER);
		textB1Variation.setBounds(140, 121, 54, 26);

		checkB2 = new Button(compositeVariations, SWT.CHECK);
		checkB2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textB2Variation.setEnabled(checkB2.getSelection());
			}
		});
		checkB2.setSelection(true);
		checkB2.setText("alpha N:");
		checkB2.setBounds(10, 157, 124, 26);

		textB2Variation = new Text(compositeVariations, SWT.BORDER);
		textB2Variation.setBounds(140, 153, 54, 26);

		checkFon = new Button(compositeVariations, SWT.CHECK);
		checkFon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textFonVariation.setEnabled(checkFon.getSelection());
			}
		});
		checkFon.setSelection(true);
		checkFon.setText("Fon (V):");
		checkFon.setBounds(10, 189, 124, 26);

		textFonVariation = new Text(compositeVariations, SWT.BORDER);
		textFonVariation.setBounds(140, 185, 54, 26);

		checkFoff = new Button(compositeVariations, SWT.CHECK);
		checkFoff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textFoffVariation.setEnabled(checkFoff.getSelection());
			}
		});
		checkFoff.setSelection(true);
		checkFoff.setText("Foff (V):");
		checkFoff.setBounds(10, 221, 124, 22);

		textFoffVariation = new Text(compositeVariations, SWT.BORDER);
		textFoffVariation.setBounds(140, 217, 54, 26);

		checkWMin = new Button(compositeVariations, SWT.CHECK);
		checkWMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWMinVariation.setEnabled(checkWMin.getSelection());
			}
		});
		checkWMin.setSelection(true);
		checkWMin.setText("w_min:");
		checkWMin.setBounds(251, 24, 201, 23);

		textWMinVariation = new Text(compositeVariations, SWT.BORDER);
		textWMinVariation.setBounds(458, 21, 54, 26);

		checkWMax = new Button(compositeVariations, SWT.CHECK);
		checkWMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWMaxVariation.setEnabled(checkWMax.getSelection());
			}
		});
		checkWMax.setSelection(true);
		checkWMax.setText("w_max:");
		checkWMax.setBounds(251, 53, 201, 26);

		textWMaxVariation = new Text(compositeVariations, SWT.BORDER);
		textWMaxVariation.setBounds(458, 50, 54, 26);

		checkP0 = new Button(compositeVariations, SWT.CHECK);
		checkP0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textP0Variation.setEnabled(checkP0.getSelection());
			}
		});
		checkP0.setSelection(true);
		checkP0.setText("p0:");
		checkP0.setBounds(251, 125, 201, 22);

		textP0Variation = new Text(compositeVariations, SWT.BORDER);
		textP0Variation.setBounds(458, 121, 54, 26);

		checkSfm = new Button(compositeVariations, SWT.CHECK);
		checkSfm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textSfmVariation.setEnabled(checkSfm.getSelection());
			}
		});
		checkSfm.setSelection(true);
		checkSfm.setText("Ap:");
		checkSfm.setBounds(251, 221, 201, 22);

		textSfmVariation = new Text(compositeVariations, SWT.BORDER);
		textSfmVariation.setBounds(458, 217, 54, 26);

		checkSfo = new Button(compositeVariations, SWT.CHECK);
		checkSfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textSfoVariation.setEnabled(checkSfo.getSelection());
			}
		});
		checkSfo.setSelection(true);
		checkSfo.setText("sfo:");
		checkSfo.setBounds(251, 192, 201, 22);

		textSfoVariation = new Text(compositeVariations, SWT.BORDER);
		textSfoVariation.setBounds(458, 185, 54, 26);

		textA1Variation.setText(""
				+ ((MemristorMIMThreshold) memristor).getDefaultA1());
		textA2Variation.setText(""
				+ ((MemristorMIMThreshold) memristor).getDefaultA2());
		textNVariation.setText("1");
		textSfmVariation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.SFMINDEX));
		textSfoVariation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.SFOINDEX));
		textFonVariation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.FONINDEX));
		textFoffVariation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.FOFFINDEX));
		textWMinVariation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.WMININDEX));
		textWMaxVariation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.WMAXINDEX));
		textB1Variation.setText(""
				+ ((MemristorMIMThreshold) memristor).getDefaultB1());
		textB2Variation.setText(""
				+ ((MemristorMIMThreshold) memristor).getDefaultB2());
//		textlVariation.setText(""
//				+ ((MemristorMIMThreshold) memristor).getDefaultL());
		textP0Variation.setText(""
				+ ((MemristorMIMThreshold) memristor).getParameter(memristor.P0INDEX));

		textPVariation = new Text(compositeVariations, SWT.BORDER);
		textPVariation.setText("30.0");
		textPVariation.setBounds(458, 154, 54, 26);

		checkP = new Button(compositeVariations, SWT.CHECK);
		checkP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textPVariation.setEnabled(checkP.getSelection());
			}
		});
		checkP.setText("p:");
		checkP.setSelection(true);
		checkP.setBounds(251, 161, 201, 22);
		
		checkWInit = new Button(compositeVariations, SWT.CHECK);
		checkWInit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textWInitVariation.setEnabled(checkWInit.getSelection());
			}
		});
		checkWInit.setText("w_init:");
		checkWInit.setSelection(true);
		checkWInit.setBounds(251, 88, 201, 26);
		
		textWInitVariation = new Text(compositeVariations, SWT.BORDER);
		textWInitVariation.setText("0.01");
		textWInitVariation.setBounds(458, 85, 54, 26);
		
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
		buttonAll.setBounds(566, 243, 124, 26);
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
			// retrieve parameters
			memristor.setValueInParameter(memristor.A1INDEX, Double.parseDouble(textA1Mean.getText()));
			memristor.setValueInParameter(memristor.A2INDEX, Double.parseDouble(textA2Mean.getText()));
			memristor.setValueInParameter(memristor.NINDEX, Double.parseDouble(textNMean.getText()));
			memristor.setValueInParameter(memristor.SFMINDEX, 					Double.parseDouble(textSfmMean.getText()));
			memristor.setValueInParameter(memristor.SFOINDEX, Double.parseDouble(textSfoMean.getText()));
			memristor.setValueInParameter(memristor.B1INDEX, Double.parseDouble(textB1Mean.getText()));
			memristor.setValueInParameter(memristor.B2INDEX, Double.parseDouble(textB2Mean.getText()));
			memristor.setValueInParameter(memristor.FONINDEX, 
					Double.parseDouble(textFonMean.getText()));
			memristor.setValueInParameter(memristor.FOFFINDEX, 
					Double.parseDouble(textFoffMean.getText()));
			memristor.setValueInParameter(memristor.WMININDEX, 
					Double.parseDouble(textWMinMean.getText()));
			memristor.setValueInParameter(memristor.WMAXINDEX, 
					Double.parseDouble(textWMaxMean.getText()));
			memristor.setValueInParameter(memristor.WINITINDEX, Double.parseDouble(textWInitMean.getText()));
			memristor.setValueInParameter(memristor.P0INDEX, Double.parseDouble(textP0Mean.getText()));
			memristor.setValueInParameter(memristor.NINDEX, Double.parseDouble(textNMean.getText()));
			memristor.setValueInParameter(memristor.PINDEX, Double.parseDouble(textPMean.getText()));
			memristor.setValueInParameter(memristor.CINDEX, Double.parseDouble(textCMean.getText()));
			if (montecarlo) {
				// retrieve parameters
				if (checkA1.getSelection()) {
					memristor.getParameter(memristor.A1INDEX).setAllowVariations(true);
					memristor.getParameter(memristor.A1INDEX).setSigma(
							Double.parseDouble(textA1Variation.getText()));
				} else {
					memristor.getParameter(memristor.A1INDEX).setAllowVariations(false);
				}
				if (checkA2.getSelection()) {
					memristor.getParameter(memristor.A2INDEX).setAllowVariations(true);
					memristor.getParameter(memristor.A2INDEX).setSigma(
							Double.parseDouble(textA2Variation.getText()));
				} else {
					memristor.getParameter(memristor.A2INDEX).setAllowVariations(false);
				}
				if (checkN.getSelection()) {
					memristor.getParameter(memristor.NINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.NINDEX).setSigma(
							Double.parseDouble(textNVariation.getText()));
				} else {
					memristor.getParameter(memristor.NINDEX).setAllowVariations(false);
				}
				if (checkSfm.getSelection()) {
					memristor.getParameter(memristor.SFMINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.SFMINDEX).setSigma(
							Double.parseDouble(textSfmVariation.getText()));
				} else {
					memristor.getParameter(memristor.SFMINDEX).setAllowVariations(false);
				}
				if (checkSfo.getSelection()) {
					memristor.getParameter(memristor.SFOINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.SFOINDEX).setSigma(
							Double.parseDouble(textSfoVariation.getText()));
				} else {
					memristor.getParameter(memristor.SFOINDEX).setAllowVariations(false);
				}
				if (checkB1.getSelection()) {
					memristor.getParameter(memristor.B1INDEX).setAllowVariations(true);
					memristor.getParameter(memristor.B1INDEX).setSigma(
							Double.parseDouble(textB1Variation.getText()));
				} else {
					memristor.getParameter(memristor.B1INDEX).setAllowVariations(false);
				}
				if (checkB2.getSelection()) {
					memristor.getParameter(memristor.B2INDEX).setAllowVariations(true);
					memristor.getParameter(memristor.B2INDEX).setSigma(
							Double.parseDouble(textB2Variation.getText()));
				} else {
					memristor.getParameter(memristor.B2INDEX).setAllowVariations(false);
				}
				if (checkFon.getSelection()) {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.FONINDEX).setSigma(
							Double.parseDouble(textFonVariation.getText()));
				} else {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(false);
				}
				if (checkFoff.getSelection()) {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.FOFFINDEX).setSigma(
							Double.parseDouble(textFoffVariation.getText()));
				} else {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(false);
				}
				if (checkWMin.getSelection()) {
					memristor.getParameter(memristor.WMININDEX).setAllowVariations(true);
					memristor.getParameter(memristor.WMININDEX).setSigma(
							Double.parseDouble(textWMinVariation.getText()));
				} else {
					memristor.getParameter(memristor.WMININDEX).setAllowVariations(false);
				}
				if (checkWMax.getSelection()) {
					memristor.getParameter(memristor.WMAXINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.WMAXINDEX).setSigma(
							Double.parseDouble(textWMaxVariation.getText()));
				} else {
					memristor.getParameter(memristor.WMAXINDEX).setAllowVariations(false);
				}

				if (checkWInit.getSelection()) {
					memristor.getParameter(memristor.WINITINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.WINITINDEX).setSigma(
							Double.parseDouble(textWInitVariation.getText()));
				} else {
					memristor.getParameter(memristor.WINITINDEX).setAllowVariations(false);
				}
				if (checkP0.getSelection()) {
					memristor.getParameter(memristor.P0INDEX).setAllowVariations(true);
					memristor.getParameter(memristor.P0INDEX).setSigma(
							Double.parseDouble(textP0Variation.getText()));
				} else {
					memristor.getParameter(memristor.P0INDEX).setAllowVariations(false);
				}
				if (checkN.getSelection()) {
					memristor.getParameter(memristor.NINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.NINDEX).setSigma(
							Double.parseDouble(textNVariation.getText()));
				} else {
					memristor.getParameter(memristor.NINDEX).setAllowVariations(false);
				}

				if (checkP.getSelection()) {
					memristor.getParameter(memristor.PINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.PINDEX).setSigma(
							Double.parseDouble(textPVariation.getText()));
				} else {
					memristor.getParameter(memristor.PINDEX).setAllowVariations(false);
				}

			} else {
				// retrieve parameters
				String[] s_aux;
				double[] d_aux;
				if (checkA1.getSelection()) {
					memristor.getParameter(memristor.A1INDEX).setAllowVariations(true);
					s_aux = textA1Variation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.A1INDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.A1INDEX).setAllowVariations(false);
				}
				if (checkA2.getSelection()) {
					memristor.getParameter(memristor.A2INDEX).setAllowVariations(true);
					s_aux = textA2Variation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.A2INDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.A2INDEX).setAllowVariations(false);
				}			
				if (checkSfm.getSelection()) {
					memristor.getParameter(memristor.SFMINDEX).setAllowVariations(true);
					s_aux = textSfmVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.SFMINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.SFMINDEX).setAllowVariations(false);
				}
				if (checkSfo.getSelection()) {
					memristor.getParameter(memristor.SFOINDEX).setAllowVariations(true);
					s_aux = textSfoVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.SFOINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.SFOINDEX).setAllowVariations(false);
				}
				if (checkB1.getSelection()) {
					memristor.getParameter(memristor.B1INDEX).setAllowVariations(true);
					s_aux = textB1Variation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.B1INDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.B1INDEX).setAllowVariations(false);
				}
				if (checkB2.getSelection()) {
					memristor.getParameter(memristor.B2INDEX).setAllowVariations(true);
					s_aux = textB2Variation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.B2INDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.B2INDEX).setAllowVariations(false);
				}
				if (checkFon.getSelection()) {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(true);
					s_aux = textFonVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.FONINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.FONINDEX).setAllowVariations(false);
				}
				if (checkFoff.getSelection()) {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(true);
					s_aux = textFoffVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.FOFFINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.FOFFINDEX).setAllowVariations(false);
				}
				if (checkWMin.getSelection()) {
					memristor.getParameter(memristor.WMININDEX).setAllowVariations(true);
					s_aux = textWMinVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.WMININDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.WMININDEX).setAllowVariations(false);
				}
				if (checkWMax.getSelection()) {
					memristor.getParameter(memristor.WMAXINDEX).setAllowVariations(true);
					s_aux = textWMaxVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.WMAXINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.WMAXINDEX).setAllowVariations(false);
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
				if (checkP0.getSelection()) {
					memristor.getParameter(memristor.P0INDEX).setAllowVariations(true);
					s_aux = textP0Variation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.P0INDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.P0INDEX).setAllowVariations(false);
				}
				if (checkN.getSelection()) {
					memristor.getParameter(memristor.NINDEX).setAllowVariations(true);
					s_aux = textNVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.NINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.NINDEX).setAllowVariations(false);
				}
				if (checkP.getSelection()) {
					memristor.getParameter(memristor.PINDEX).setAllowVariations(true);
					s_aux = textPVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.PINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.PINDEX).setAllowVariations(false);
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
		compositeVariations.setText("Parameter variation, standard deviation");
		textA1Mean.setText("" + memristor.getParameter(memristor.A1INDEX).getValue());
		textA2Mean.setText("" + memristor.getParameter(memristor.A2INDEX).getValue());
		textNMean.setText("" + memristor.getParameter(memristor.NINDEX).getValue());
		textB1Mean.setText("" + memristor.getParameter(memristor.B1INDEX).getValue());
		textB2Mean.setText("" + memristor.getParameter(memristor.B2INDEX).getValue());
		textFonMean.setText("" + memristor.getParameter(memristor.FONINDEX).getValue());
		textFoffMean.setText("" + memristor.getParameter(memristor.FOFFINDEX).getValue());
		textWMinMean.setText("" + memristor.getParameter(memristor.WMININDEX).getValue());
		textWMaxMean.setText("" + memristor.getParameter(memristor.WMAXINDEX).getValue());
		textWInitVariation.setText("" + memristor.getParameter(memristor.WINITINDEX).getValue());
		textSfmMean.setText("" + memristor.getParameter(memristor.SFMINDEX).getValue());
		textSfoMean.setText("" + memristor.getParameter(memristor.SFOINDEX).getValue());
		textP0Mean.setText("" + memristor.getParameter(memristor.P0INDEX).getValue());
//		textlMean.setText("" + memristor.getParameter(memristor.LINDEX).getValue());
		textPMean.setText("" + memristor.getParameter(memristor.PINDEX).getValue());
		textCMean.setText("" + memristor.getParameter(memristor.CINDEX).getValue());

		checkA1.setText("a1, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkA2.setText("a2, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkN.setText("n, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkB1.setText("b1, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkB2.setText("b2, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkFon.setText("Fon," + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkFoff.setText("Foff, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkWMin.setText("WMin, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkWMax.setText("WMax," + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkWInit.setText("WInit," + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkSfm.setText("sfm, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkSfo.setText("sfo, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkP0.setText("p0," + ResourcesAndUsefulFunctions.SIGMA + ": ");
//		checkl.setText("l," + Resources.SIGMA + ": ");
		checkP.setText("p, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		
		textA1Variation.setText("" + memristor.getParameter(memristor.A1INDEX).getSigma());
		textA2Variation.setText("" + memristor.getParameter(memristor.A2INDEX).getSigma());
		textNVariation.setText("" + memristor.getParameter(memristor.NINDEX).getSigma());
		textB1Variation.setText("" + memristor.getParameter(memristor.B1INDEX).getSigma());
		textB2Variation.setText("" + memristor.getParameter(memristor.B2INDEX).getSigma());
		textFonVariation.setText("" + memristor.getParameter(memristor.FONINDEX).getSigma());
		textFoffVariation.setText("" + memristor.getParameter(memristor.FOFFINDEX).getSigma());
		textWMinVariation.setText("" + memristor.getParameter(memristor.WMININDEX).getSigma());
		textWMaxVariation.setText("" + memristor.getParameter(memristor.WMAXINDEX).getSigma());
		textWInitVariation.setText("" + memristor.getParameter(memristor.WINITINDEX).getSigma());
		textSfmVariation.setText("" + memristor.getParameter(memristor.SFMINDEX).getSigma());
		textSfoVariation.setText("" + memristor.getParameter(memristor.SFOINDEX).getSigma());
		textP0Variation.setText("" + memristor.getParameter(memristor.P0INDEX).getSigma());
//		textlVariation.setText("" + memristor.getParameter(memristor.LINDEX).getSigma());
		textPVariation.setText("" + memristor.getParameter(memristor.PINDEX).getSigma());
		montecarlo = true;

	}

	@Override
	public void setSpaceExploration() {
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variation, alternative value list");
		
		textA1Mean.setText("" + memristor.getParameter(memristor.A1INDEX).getValue());
		textA2Mean.setText("" + memristor.getParameter(memristor.A2INDEX).getValue());
		textNMean.setText("" + memristor.getParameter(memristor.NINDEX).getValue());
		textB1Mean.setText("" + memristor.getParameter(memristor.B1INDEX).getValue());
		textB2Mean.setText("" + memristor.getParameter(memristor.B2INDEX).getValue());
		textFonMean.setText("" + memristor.getParameter(memristor.FONINDEX).getValue());
		textFoffMean.setText("" + memristor.getParameter(memristor.FOFFINDEX).getValue());
		textWMinMean.setText("" + memristor.getParameter(memristor.WMININDEX).getValue());
		textWMaxMean.setText("" + memristor.getParameter(memristor.WMAXINDEX).getValue());
		textWInitVariation.setText("" + memristor.getParameter(memristor.WINITINDEX).getValue());
		textSfmMean.setText("" + memristor.getParameter(memristor.SFMINDEX).getValue());
		textSfoMean.setText("" + memristor.getParameter(memristor.SFOINDEX).getValue());
		textP0Mean.setText("" + memristor.getParameter(memristor.P0INDEX).getValue());
		textPMean.setText("" + memristor.getParameter(memristor.PINDEX).getValue());
		textCMean.setText("" + memristor.getParameter(memristor.CINDEX).getValue());
		
		checkA1.setText("a1: ");
		checkA2.setText("a2: ");
		checkN.setText("n: ");
		checkB1.setText("b1: ");
		checkB2.setText("b2: ");
		checkFon.setText("Fon: ");
		checkFoff.setText("Foff: ");
		checkWMin.setText("WMin: ");
		checkWMax.setText("WMax: ");
		checkSfm.setText("sfm: ");
		checkSfo.setText("sfo: ");
		checkP0.setText("p0: ");
		checkP.setText("p: ");

//		textA1Variation
//				.setText("" + memristor.getParameter(memristor.A1INDEX).getMaxVariation());
//		textA2Variation
//				.setText("" + memristor.getParameter(memristor.A2INDEX).getMaxVariation());
//		textNVariation.setText("" + memristor.getParameter(memristor.NINDEX).getMaxVariation());
//		textB1Variation
//				.setText("" + memristor.getParameter(memristor.B1INDEX).getMaxVariation());
//		textB2Variation
//				.setText("" + memristor.getParameter(memristor.B2INDEX).getMaxVariation());
//		textFonVariation.setText(""
//				+ memristor.getParameter(memristor.FONINDEX).getMaxVariation());
//		textFoffVariation.setText(""
//				+ memristor.getParameter(memristor.FOFFINDEX).getMaxVariation());
//		textWMinVariation.setText(""
//				+ memristor.getParameter(memristor.WMININDEX).getMaxVariation());
//		textWMaxVariation.setText(""
//				+ memristor.getParameter(memristor.WMAXINDEX).getMaxVariation());
//		textSfmVariation.setText(""
//				+ memristor.getParameter(memristor.SFMINDEX).getMaxVariation());
//		textSfoVariation.setText(""
//				+ memristor.getParameter(memristor.SFOINDEX).getMaxVariation());
//		textP0Variation
//				.setText("" + memristor.getParameter(memristor.P0INDEX).getMaxVariation());
//		textPVariation.setText("" + memristor.getParameter(memristor.PINDEX).getMaxVariation());

		montecarlo = false;

	}

	public void setMemristor(MemristorModel memristor) {
		this.memristor = (MemristorMIMThreshold) memristor;
	}
}
