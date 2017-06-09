package memristorModelsUI;

import memristorModels.MemristorYakopcic;
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
import org.eclipse.wb.swt.SWTResourceManager;

import simulationObjects.ResourcesAndUsefulFunctions;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

public class MemristorMenuYakopcic extends Composite implements
		MemristorModelMenu {
	private Button checkB;
	private Button checkRinit;
	private Button checkA2;
	private Button checkA1;
	private Button checkAlphaP;
	private Button checkAlphaN;
	private Button checkVp;
	private Button checkVn;
	private Button checkXp;
	private Button checkXn;
	private Button checkXinit;
	private Button checkAp;
	private Button checkAn;

	private Text textBVariation;
	private Text textRinitVariation;
	private Text textA2Variation;
	private Text textA1Variation;
	private Text textAlphaPVariation;
	private Text textAlphaNVariation;
	private Text textVpVariation;
	private Text textVnVariation;
	private Text textXpVariation;
	private Text textXnVariation;
	private Text textXinitVariation;
	private Text textApVariation;
	private Text textAnVariation;
	private Text textBMean;
	private Text textRinitMean;
	private Text textA2Mean;
	private Text textA1Mean;
	private Text textAlphaPMean;
	private Text textAlphaNMean;
	private Text textVpMean;
	private Text textVnMean;
	private Text textXpMean;
	private Text textXnMean;
	private Text textXinitMean;
	private Text textApMean;
	private Text textAnMean;
	private Combo comboPresets;
	private Combo comboWindowFunctions;
	private MemristorYakopcic memristor;
	private boolean montecarlo;

	private Group compositeVariations;
	private Button buttonAll;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MemristorMenuYakopcic(Composite parent, int style) {
		super(parent, style);
		memristor = new MemristorYakopcic();
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLocation(10, 10);
		group.setSize(700, 187);
		group.setText("Model parameters");

		Label label_1 = new Label(group, SWT.NONE);
		label_1.setText("b:");
		label_1.setBounds(10, 88, 54, 26);

		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("R init:");
		label_2.setBounds(10, 120, 64, 22);

		textBMean = new Text(group, SWT.BORDER);
		textBMean.setText("1.0");
		textBMean.setBounds(99, 84, 54, 26);

		textRinitMean = new Text(group, SWT.BORDER);
		textRinitMean.setText("215.0");
		textRinitMean.setBounds(99, 116, 54, 26);

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

		Label label_5 = new Label(group, SWT.NONE);
		label_5.setText("alpha P:");
		label_5.setBounds(187, 20, 64, 26);

		textAlphaPMean = new Text(group, SWT.BORDER);
		textAlphaPMean.setText("4.0");
		textAlphaPMean.setBounds(257, 20, 46, 26);

		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("alpha N:");
		label_6.setBounds(187, 56, 64, 26);

		textAlphaNMean = new Text(group, SWT.BORDER);
		textAlphaNMean.setText("24.0");
		textAlphaNMean.setBounds(257, 52, 46, 26);

		Label label_7 = new Label(group, SWT.NONE);
		label_7.setText("Vp (V):");
		label_7.setBounds(187, 88, 64, 26);

		textVpMean = new Text(group, SWT.BORDER);
		textVpMean.setText("1.2");
		textVpMean.setBounds(257, 84, 46, 26);

		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("Vn (V):");
		label_8.setBounds(187, 120, 64, 22);

		textVnMean = new Text(group, SWT.BORDER);
		textVnMean.setText("0.6");
		textVnMean.setBounds(257, 116, 46, 26);

		Label label_9 = new Label(group, SWT.NONE);
		label_9.setText("xp:");
		label_9.setBounds(314, 20, 40, 16);

		textXpMean = new Text(group, SWT.BORDER);
		textXpMean.setText("0.7");
		textXpMean.setBounds(376, 21, 54, 26);

		Label label_10 = new Label(group, SWT.NONE);
		label_10.setText("xn:");
		label_10.setBounds(314, 56, 40, 26);

		textXnMean = new Text(group, SWT.BORDER);
		textXnMean.setText("0.8");
		textXnMean.setBounds(376, 53, 54, 26);

		Label label_11 = new Label(group, SWT.NONE);
		label_11.setText("x_init:");
		label_11.setBounds(454, 22, 54, 22);

		textXinitMean = new Text(group, SWT.BORDER);
		textXinitMean.setText("0.02");
		textXinitMean.setBounds(508, 20, 63, 26);

		Label label_12 = new Label(group, SWT.NONE);
		label_12.setText("Ap:");
		label_12.setBounds(455, 56, 40, 22);

		textApMean = new Text(group, SWT.BORDER);
		textApMean.setText("5.0");
		textApMean.setBounds(517, 52, 54, 26);

		Label label_13 = new Label(group, SWT.NONE);
		label_13.setText("An:");
		label_13.setBounds(455, 88, 40, 22);

		textAnMean = new Text(group, SWT.BORDER);
		textAnMean.setText("30.0");
		textAnMean.setBounds(517, 84, 54, 26);

		Label label_14 = new Label(group, SWT.NONE);
		label_14.setText("Presets:");
		label_14.setFont(SWTResourceManager.getFont("Sans", 10, SWT.BOLD));
		label_14.setBounds(10, 156, 75, 16);

		comboPresets = new Combo(group, SWT.NONE);
		comboPresets.setBounds(99, 152, 481, 27);
		for (String w : this.memristor.getPresets()) {
			comboPresets.add(w);
		}
		comboPresets.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				memristor.setPresetIndex(comboPresets.getSelectionIndex());
				textA1Mean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultA1()[comboPresets
								.getSelectionIndex()]);
				textA2Mean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultA2()[comboPresets
								.getSelectionIndex()]);
				textBMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultB()[comboPresets.getSelectionIndex()]);
				textApMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultAP()[comboPresets
								.getSelectionIndex()]);
				textAnMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultAN()[comboPresets
								.getSelectionIndex()]);
				textVpMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultVP()[comboPresets
								.getSelectionIndex()]);
				textVnMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultVN()[comboPresets
								.getSelectionIndex()]);
				textXpMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultXP()[comboPresets
								.getSelectionIndex()]);
				textXnMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultXN()[comboPresets
								.getSelectionIndex()]);
				textAlphaPMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultAlphaP()[comboPresets
								.getSelectionIndex()]);
				textAlphaNMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultAlphaN()[comboPresets
								.getSelectionIndex()]);
				textRinitMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultRInit()[comboPresets
								.getSelectionIndex()]);
				textXinitMean.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultXInit()[comboPresets
								.getSelectionIndex()]);

			}
		});
		memristor.setPresetIndex(0);
		comboPresets.select(0);

		comboWindowFunctions = new Combo(group, SWT.NONE);
		comboWindowFunctions.setBounds(431, 115, 149, 27);

		for (String w : this.memristor.windowPresets) {
			comboWindowFunctions.add(w);
		}
		comboWindowFunctions.select(0);

		Label label_15 = new Label(group, SWT.NONE);
		label_15.setText("window function:");
		label_15.setBounds(314, 124, 111, 18);
		compositeVariations = new Group(this, SWT.SHADOW_ETCHED_IN);
		compositeVariations.setLocation(10, 203);
		compositeVariations.setSize(700, 251);
		compositeVariations.setText("Model parameters");

		checkB = new Button(compositeVariations, SWT.CHECK);
		checkB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textBVariation.setEnabled(checkB.getSelection());
			}
		});
		checkB.setSelection(true);
		checkB.setText("b:");
		checkB.setBounds(10, 89, 174, 26);

		checkRinit = new Button(compositeVariations, SWT.CHECK);
		checkRinit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRinitVariation.setEnabled(checkRinit.getSelection());
			}
		});
		checkRinit.setSelection(true);
		checkRinit.setText("R init:");
		checkRinit.setBounds(251, 221, 270, 22);

		textBVariation = new Text(compositeVariations, SWT.BORDER);
		textBVariation.setBounds(190, 85, 54, 26);

		textRinitVariation = new Text(compositeVariations, SWT.BORDER);
		textRinitVariation.setBounds(549, 214, 54, 26);

		checkA2 = new Button(compositeVariations, SWT.CHECK);
		checkA2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textA2Variation.setEnabled(checkA2.getSelection());
			}
		});
		checkA2.setSelection(true);
		checkA2.setText("a2:");
		checkA2.setBounds(10, 57, 174, 26);

		textA2Variation = new Text(compositeVariations, SWT.BORDER);
		textA2Variation.setBounds(190, 53, 54, 26);

		textA1Variation = new Text(compositeVariations, SWT.BORDER);
		textA1Variation.setBounds(190, 21, 54, 26);

		checkA1 = new Button(compositeVariations, SWT.CHECK);
		checkA1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textA1Variation.setEnabled(checkA1.getSelection());
			}
		});
		checkA1.setSelection(true);
		checkA1.setText("a1:");
		checkA1.setBounds(10, 25, 174, 26);

		checkAlphaP = new Button(compositeVariations, SWT.CHECK);
		checkAlphaP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAlphaPVariation.setEnabled(checkAlphaP.getSelection());
			}
		});
		checkAlphaP.setSelection(true);
		checkAlphaP.setText("alpha P:");
		checkAlphaP.setBounds(10, 121, 174, 26);

		textAlphaPVariation = new Text(compositeVariations, SWT.BORDER);
		textAlphaPVariation.setBounds(190, 121, 54, 26);

		checkAlphaN = new Button(compositeVariations, SWT.CHECK);
		checkAlphaN.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAlphaNVariation.setEnabled(checkAlphaN.getSelection());
			}
		});
		checkAlphaN.setSelection(true);
		checkAlphaN.setText("alpha N:");
		checkAlphaN.setBounds(10, 157, 174, 26);

		textAlphaNVariation = new Text(compositeVariations, SWT.BORDER);
		textAlphaNVariation.setBounds(190, 153, 54, 26);

		checkVp = new Button(compositeVariations, SWT.CHECK);
		checkVp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textVpVariation.setEnabled(checkVp.getSelection());
			}
		});
		checkVp.setSelection(true);
		checkVp.setText("Vp (V):");
		checkVp.setBounds(10, 189, 174, 26);

		textVpVariation = new Text(compositeVariations, SWT.BORDER);
		textVpVariation.setBounds(190, 185, 54, 26);

		checkVn = new Button(compositeVariations, SWT.CHECK);
		checkVn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textVnVariation.setEnabled(checkVn.getSelection());
			}
		});
		checkVn.setSelection(true);
		checkVn.setText("Vn (V):");
		checkVn.setBounds(10, 221, 174, 22);

		textVnVariation = new Text(compositeVariations, SWT.BORDER);
		textVnVariation.setBounds(190, 217, 54, 26);

		checkXp = new Button(compositeVariations, SWT.CHECK);
		checkXp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textXpVariation.setEnabled(checkXp.getSelection());
			}
		});
		checkXp.setSelection(true);
		checkXp.setText("xp:");
		checkXp.setBounds(251, 60, 270, 16);

		textXpVariation = new Text(compositeVariations, SWT.BORDER);
		textXpVariation.setBounds(549, 57, 54, 26);

		checkXn = new Button(compositeVariations, SWT.CHECK);
		checkXn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textXnVariation.setEnabled(checkXn.getSelection());
			}
		});
		checkXn.setSelection(true);
		checkXn.setText("xn:");
		checkXn.setBounds(251, 89, 270, 26);

		textXnVariation = new Text(compositeVariations, SWT.BORDER);
		textXnVariation.setBounds(549, 86, 54, 26);

		checkXinit = new Button(compositeVariations, SWT.CHECK);
		checkXinit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textXinitVariation.setEnabled(checkXinit.getSelection());
			}
		});
		checkXinit.setSelection(true);
		checkXinit.setText("x_init:");
		checkXinit.setBounds(251, 189, 292, 22);

		textXinitVariation = new Text(compositeVariations, SWT.BORDER);
		textXinitVariation.setBounds(549, 185, 54, 26);

		checkAp = new Button(compositeVariations, SWT.CHECK);
		checkAp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textApVariation.setEnabled(checkAp.getSelection());
			}
		});
		checkAp.setSelection(true);
		checkAp.setText("Ap:");
		checkAp.setBounds(251, 121, 270, 22);

		textApVariation = new Text(compositeVariations, SWT.BORDER);
		textApVariation.setBounds(549, 118, 54, 26);

		checkAn = new Button(compositeVariations, SWT.CHECK);
		checkAn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAnVariation.setEnabled(checkAn.getSelection());
			}
		});
		checkAn.setSelection(true);
		checkAn.setText("An:");
		checkAn.setBounds(251, 160, 270, 22);

		textAnVariation = new Text(compositeVariations, SWT.BORDER);
		textAnVariation.setBounds(549, 153, 54, 26);

		textA1Variation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultA1()[0]);
		textA2Variation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultA2()[0]);
		textBVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultB()[0]);
		textApVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultAP()[0]);
		textAnVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultAN()[0]);
		textVpVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultVP()[0]);
		textVnVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultVN()[0]);
		textXpVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultXP()[0]);
		textXnVariation
				.setText(""
						+ ((MemristorYakopcic) memristor)
								.getDefaultXN()[0]);
		textAlphaPVariation.setText(""
				+ ((MemristorYakopcic) memristor)
						.getDefaultAlphaP()[0]);
		textAlphaNVariation.setText(""
				+ ((MemristorYakopcic) memristor)
						.getDefaultAlphaN()[0]);
		textRinitVariation.setText(""
				+ ((MemristorYakopcic) memristor)
						.getDefaultRInit()[0]);
		textXinitVariation.setText(""
				+ ((MemristorYakopcic) memristor)
						.getDefaultXInit()[0]);
		
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
		buttonAll.setBounds(566, 21, 124, 26);
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
			memristor.getParameter(memristor.WINDOWINDEX).setValue(
					comboWindowFunctions.getSelectionIndex());
			// retrieve parameters
			memristor.getParameter(memristor.A1INDEX)
					.setValue(Double.parseDouble(textA1Mean.getText()));
			memristor.getParameter(memristor.A2INDEX)
					.setValue(Double.parseDouble(textA1Mean.getText()));
			memristor.getParameter(memristor.BINDEX).setValue(Double.parseDouble(textBMean.getText()));
			memristor.getParameter(memristor.APINDEX)
					.setValue(Double.parseDouble(textApMean.getText()));
			memristor.getParameter(memristor.ANINDEX)
					.setValue(Double.parseDouble(textAnMean.getText()));
			memristor.getParameter(memristor.ALPHAPINDEX).setValue(
					Double.parseDouble(textAlphaPMean.getText()));
			memristor.getParameter(memristor.ALPHANINDEX).setValue(
					Double.parseDouble(textAlphaNMean.getText()));
			memristor.getParameter(memristor.VPINDEX)
					.setValue(Double.parseDouble(textVpMean.getText()));
			memristor.getParameter(memristor.VNINDEX)
					.setValue(Double.parseDouble(textVnMean.getText()));
			memristor.getParameter(memristor.XPINDEX)
					.setValue(Double.parseDouble(textXpMean.getText()));
			memristor.getParameter(memristor.XNINDEX)
					.setValue(Double.parseDouble(textXnMean.getText()));
			memristor.getParameter(memristor.RINITINDEX).setValue(
					Double.parseDouble(textRinitMean.getText()));
			memristor.getParameter(memristor.XINITINDEX).setValue(
					Double.parseDouble(textXinitMean.getText()));
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
				if (checkB.getSelection()) {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.BINDEX).setSigma(
							Double.parseDouble(textBVariation.getText()));
				} else {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(false);
				}
				if (checkAp.getSelection()) {
					memristor.getParameter(memristor.APINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.APINDEX).setSigma(
							Double.parseDouble(textApVariation.getText()));
				} else {
					memristor.getParameter(memristor.APINDEX).setAllowVariations(false);
				}
				if (checkAn.getSelection()) {
					memristor.getParameter(memristor.ANINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.ANINDEX).setSigma(
							Double.parseDouble(textAnVariation.getText()));
				} else {
					memristor.getParameter(memristor.ANINDEX).setAllowVariations(false);
				}
				if (checkAlphaP.getSelection()) {
					memristor.getParameter(memristor.ALPHAPINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.ALPHAPINDEX).setSigma(
							Double.parseDouble(textAlphaPVariation.getText()));
				} else {
					memristor.getParameter(memristor.ALPHAPINDEX).setAllowVariations(false);
				}
				if (checkAlphaN.getSelection()) {
					memristor.getParameter(memristor.ALPHANINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.ALPHANINDEX).setSigma(
							Double.parseDouble(textAlphaNVariation.getText()));
				} else {
					memristor.getParameter(memristor.ALPHANINDEX).setAllowVariations(false);
				}
				if (checkVp.getSelection()) {
					memristor.getParameter(memristor.VPINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.VPINDEX).setSigma(
							Double.parseDouble(textVpVariation.getText()));
				} else {
					memristor.getParameter(memristor.VPINDEX).setAllowVariations(false);
				}
				if (checkVn.getSelection()) {
					memristor.getParameter(memristor.VNINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.VNINDEX).setSigma(
							Double.parseDouble(textVnVariation.getText()));
				} else {
					memristor.getParameter(memristor.VNINDEX).setAllowVariations(false);
				}
				if (checkXp.getSelection()) {
					memristor.getParameter(memristor.XPINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.XPINDEX).setSigma(
							Double.parseDouble(textXpVariation.getText()));
				} else {
					memristor.getParameter(memristor.XPINDEX).setAllowVariations(false);
				}
				if (checkXn.getSelection()) {
					memristor.getParameter(memristor.XNINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.XNINDEX).setSigma(
							Double.parseDouble(textXnVariation.getText()));
				} else {
					memristor.getParameter(memristor.XNINDEX).setAllowVariations(false);
				}
				if (checkRinit.getSelection()) {
					memristor.getParameter(memristor.RINITINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.RINITINDEX).setSigma(
							Double.parseDouble(textRinitVariation.getText()));
				} else {
					memristor.getParameter(memristor.RINITINDEX).setAllowVariations(false);
				}
				if (checkXinit.getSelection()) {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.XINITINDEX).setSigma(
							Double.parseDouble(textXinitVariation.getText()));
				} else {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(false);
				}
			} else {
				String[] s_aux;
				double[] d_aux;
				// retrieve parameters
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
				if (checkAp.getSelection()) {
					memristor.getParameter(memristor.APINDEX).setAllowVariations(true);
					s_aux = textApVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.APINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.APINDEX).setAllowVariations(false);
				}
				if (checkAn.getSelection()) {
					memristor.getParameter(memristor.ANINDEX).setAllowVariations(true);
					s_aux = textAnVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.ANINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.ANINDEX).setAllowVariations(false);
				}
				if (checkAlphaP.getSelection()) {
					memristor.getParameter(memristor.ALPHAPINDEX).setAllowVariations(true);
					s_aux = textAlphaPVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.ALPHAPINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.ALPHAPINDEX).setAllowVariations(false);
				}
				if (checkAlphaN.getSelection()) {
					memristor.getParameter(memristor.ALPHANINDEX).setAllowVariations(true);
					s_aux = textAlphaNVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.ALPHANINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.ALPHANINDEX).setAllowVariations(false);
				}
				if (checkVp.getSelection()) {
					memristor.getParameter(memristor.VPINDEX).setAllowVariations(true);
					s_aux = textVpVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.VPINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.VPINDEX).setAllowVariations(false);
				}
				if (checkVn.getSelection()) {
					memristor.getParameter(memristor.VNINDEX).setAllowVariations(true);
					s_aux = textVnVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.VNINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.VNINDEX).setAllowVariations(false);
				}
				if (checkXp.getSelection()) {
					memristor.getParameter(memristor.XPINDEX).setAllowVariations(true);
					s_aux = textXpVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.XPINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.XPINDEX).setAllowVariations(false);
				}
				if (checkXn.getSelection()) {
					memristor.getParameter(memristor.XNINDEX).setAllowVariations(true);
					s_aux = textXnVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.XNINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.XNINDEX).setAllowVariations(false);
				}
				if (checkRinit.getSelection()) {
					memristor.getParameter(memristor.RINITINDEX).setAllowVariations(true);
					s_aux = textRinitVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.RINITINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.RINITINDEX).setAllowVariations(false);
				}
				if (checkXinit.getSelection()) {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(true);
					s_aux = textXinitVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.XINITINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.XINITINDEX).setAllowVariations(false);
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

		checkA1.setText("a1, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkA2.setText("a2, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkB.setText("b, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAlphaP.setText("alphaP, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAlphaN.setText("alphaN, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkVp.setText("Vp," + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkVn.setText("Vn, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkXp.setText("Xp, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkXn.setText("Xn," + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAp.setText("Ap, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkAn.setText("An, " + ResourcesAndUsefulFunctions.SIGMA + ": ");
		checkXinit.setText("x_init simulator determined," + ResourcesAndUsefulFunctions.SIGMA
				+ ": ");
		checkRinit.setText("R_init," + "," + ResourcesAndUsefulFunctions.SIGMA + ": ");
		textA1Variation.setText("" + memristor.getParameter(memristor.A1INDEX).getSigma());
		textA2Variation.setText("" + memristor.getParameter(memristor.A2INDEX).getSigma());
		textBVariation.setText("" + memristor.getParameter(memristor.BINDEX).getSigma());
		textAlphaPVariation.setText("" + memristor.getParameter(memristor.ALPHAPINDEX).getSigma());
		textAlphaNVariation.setText("" + memristor.getParameter(memristor.ALPHANINDEX).getSigma());
		textVpVariation.setText("" + memristor.getParameter(memristor.VPINDEX).getSigma());
		textVnVariation.setText("" + memristor.getParameter(memristor.VNINDEX).getSigma());
		textXpVariation.setText("" + memristor.getParameter(memristor.XPINDEX).getSigma());
		textXnVariation.setText("" + memristor.getParameter(memristor.XNINDEX).getSigma());
		textApVariation.setText("" + memristor.getParameter(memristor.APINDEX).getSigma());
		textAnVariation.setText("" + memristor.getParameter(memristor.ANINDEX).getSigma());
		textXinitVariation.setText("" + memristor.getParameter(memristor.XINITINDEX).getSigma());
		textRinitVariation.setText("" + memristor.getParameter(memristor.RINITINDEX).getSigma());
		montecarlo = true;

	}

	@Override
	public void setSpaceExploration() {
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variation, alternative value list");
		checkA1.setText("a1: ");
		checkA2.setText("a2: ");
		checkB.setText("b: ");
		checkAlphaP.setText("alphaP: ");
		checkAlphaN.setText("alphaN: ");
		checkVp.setText("Vp: ");
		checkVn.setText("Vn: ");
		checkXp.setText("Xp: ");
		checkXn.setText("Xn: ");
		checkAp.setText("Ap: ");
		checkAn.setText("An: ");
		checkXinit.setText("x_init simulator determined" + ", "
				+ ResourcesAndUsefulFunctions.DELTA + ": ");
		checkRinit.setText("R_init: ");

//		textA1Variation
//				.setText("" + memristor.getParameter(memristor.A1INDEX).getMaxVariation());
//		textA2Variation
//				.setText("" + memristor.getParameter(memristor.A2INDEX).getMaxVariation());
//		textBVariation.setText("" + memristor.getParameter(memristor.BINDEX).getMaxVariation());
//		textAlphaPVariation.setText(""
//				+ memristor.getParameter(memristor.ALPHAPINDEX).getMaxVariation());
//		textAlphaNVariation.setText(""
//				+ memristor.getParameter(memristor.ALPHANINDEX).getMaxVariation());
//		textVpVariation
//				.setText("" + memristor.getParameter(memristor.VPINDEX).getMaxVariation());
//		textVnVariation
//				.setText("" + memristor.getParameter(memristor.VNINDEX).getMaxVariation());
//		textXpVariation
//				.setText("" + memristor.getParameter(memristor.XPINDEX).getMaxVariation());
//		textXnVariation
//				.setText("" + memristor.getParameter(memristor.XNINDEX).getMaxVariation());
//		textApVariation
//				.setText("" + memristor.getParameter(memristor.APINDEX).getMaxVariation());
//		textAnVariation
//				.setText("" + memristor.getParameter(memristor.ANINDEX).getMaxVariation());
//		textXinitVariation.setText(""
//				+ memristor.getParameter(memristor.XINITINDEX).getMaxVariation());
//		textRinitVariation.setText(""
//				+ memristor.getParameter(memristor.RINITINDEX).getMaxVariation());
		
		montecarlo = false;

	}

	public void setMemristor(MemristorModel memristor) {
		this.memristor = (MemristorYakopcic) memristor;
	}
}
