package memristorModelsUI;

import memristorModels.MemristorNonLinearDrift;
import memristorModels.MemristorModel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import simulationObjects.ResourcesAndUsefulFunctions;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

public class MemristorMenuNonLineal extends Composite implements
		MemristorModelMenu {
	private Text textRoffVariation;
	private Text textPVariation;
	private Text textUvVariation;
	private Text textRonVariation;
	private Text textXinitVariation;
	private Text textDVariation;
	private Button checkRoff;
	private Button checkP;
	private Button checkUv;
	private Button checkRon;
	private Button checkXinit;
	private Button checkD;
	private MemristorNonLinearDrift memristor;
	private boolean montecarlo;
	private Combo combo;
	private Button checkJ;
	private Text textJVariation;
	private Group group;
	private Text textRoffMean;
	private Text textPMean;
	private Text textUvMean;
	private Text textRonMean;
	private Text textXinitMean;
	private Text textDMean;
	private Text textJMean;
	private Label label_w;

private Group compositeVariations;
private Button buttonAll;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MemristorMenuNonLineal(Composite parent, int style) {
		super(parent, style);
		this.memristor = new MemristorNonLinearDrift();
		compositeVariations = new Group(this, SWT.SHADOW_ETCHED_IN);
		compositeVariations.setText("Model parameter variation values");
		compositeVariations.setLayout(null);
		compositeVariations.setBounds(10, 220, 696, 168);

		checkRoff = new Button(compositeVariations, SWT.CHECK);
		checkRoff.setSelection(true);
		checkRoff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRoffVariation.setEnabled(checkRoff.getSelection());
			}
		});
		checkRoff.setText("Roff(Ω): ");
		checkRoff.setBounds(10, 31, 151, 18);

		textRoffVariation = new Text(compositeVariations, SWT.BORDER);
		textRoffVariation.setBounds(182, 31, 85, 26);

		textPVariation = new Text(compositeVariations, SWT.BORDER);
		textPVariation.setBounds(445, 67, 85, 30);

		checkP = new Button(compositeVariations, SWT.CHECK);
		checkP.setSelection(true);
		checkP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textPVariation.setEnabled(checkP.getSelection());
			}
		});
		checkP.setText("p:");
		checkP.setBounds(273, 71, 151, 18);

		textUvVariation = new Text(compositeVariations, SWT.BORDER);
		textUvVariation.setBounds(445, 31, 85, 30);

		checkUv = new Button(compositeVariations, SWT.CHECK);
		checkUv.setSelection(true);
		checkUv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textUvVariation.setEnabled(checkUv.getSelection());
			}
		});
		checkUv.setText("uv(m²s⁻¹V⁻¹):");
		checkUv.setBounds(273, 35, 151, 18);

		checkRon = new Button(compositeVariations, SWT.CHECK);
		checkRon.setSelection(true);
		checkRon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textRonVariation.setEnabled(checkRon.getSelection());
			}
		});
		checkRon.setText("Ron, mean: ");
		checkRon.setBounds(10, 63, 151, 18);

		textRonVariation = new Text(compositeVariations, SWT.BORDER);
		textRonVariation.setBounds(182, 63, 85, 26);

		checkXinit = new Button(compositeVariations, SWT.CHECK);
		checkXinit.setSelection(true);
		checkXinit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textXinitVariation.setEnabled(checkXinit.getSelection());
			}
		});
		checkXinit.setText("x_init [0,1]: ");
		checkXinit.setBounds(10, 95, 151, 18);

		textXinitVariation = new Text(compositeVariations, SWT.BORDER);
		textXinitVariation.setBounds(182, 95, 85, 26);

		checkD = new Button(compositeVariations, SWT.CHECK);
		checkD.setSelection(true);
		checkD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textDVariation.setEnabled(checkD.getSelection());
			}
		});
		checkD.setText("D(m): ");
		checkD.setBounds(10, 131, 151, 18);

		textDVariation = new Text(compositeVariations, SWT.BORDER);
		textDVariation.setText("10e-9");
		textDVariation.setBounds(182, 127, 85, 30);

		textRoffVariation.setText("" + this.memristor.getParameter(memristor.ROFFINDEX).getValue());
		textRonVariation.setText("" + this.memristor.getParameter(memristor.RONINDEX).getValue());
		textDVariation.setText("" + this.memristor.getParameter(memristor.DINDEX).getValue());
		textXinitVariation.setText("" + this.memristor.getParameter(memristor.XINITINDEX).getValue());
		textUvVariation.setText("" + this.memristor.getParameter(memristor.UVINDEX).getValue());
		textPVariation.setText("" + this.memristor.getParameter(memristor.PINDEX).getValue());

		checkJ = new Button(compositeVariations, SWT.CHECK);
		checkJ.setText("p:");
		checkJ.setSelection(true);
		checkJ.setBounds(273, 107, 151, 18);

		textJVariation = new Text(compositeVariations, SWT.BORDER);
		textJVariation.setEnabled(false);
		textJVariation.setText("0.0");
		textJVariation.setBounds(445, 103, 85, 30);
		
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
		buttonAll.setBounds(562, 131, 124, 26);

		group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(null);
		group.setText("Model parameter variation values");
		group.setBounds(10, 10, 696, 204);

		Label label = new Label(group, SWT.NONE);
		label.setText("Roff(Ω): ");
		label.setBounds(10, 31, 151, 18);

		textRoffMean = new Text(group, SWT.BORDER);
		textRoffMean.setText("0.0");
		textRoffMean.setBounds(182, 31, 85, 26);

		textPMean = new Text(group, SWT.BORDER);
		textPMean.setText("0.0");
		textPMean.setBounds(445, 67, 85, 30);

		Label label_1 = new Label(group, SWT.NONE);
		label_1.setText("p:");
		label_1.setBounds(273, 71, 151, 18);

		textUvMean = new Text(group, SWT.BORDER);
		textUvMean.setText("0.0");
		textUvMean.setBounds(445, 31, 85, 30);

		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("uv(m²s⁻¹V⁻¹):");
		label_2.setBounds(273, 35, 151, 18);

		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("Ron, mean: ");
		label_3.setBounds(10, 63, 151, 18);

		textRonMean = new Text(group, SWT.BORDER);
		textRonMean.setText("0.0");
		textRonMean.setBounds(182, 63, 85, 26);

		Label label_4 = new Label(group, SWT.NONE);
		label_4.setText("x_init [0,1]: ");
		label_4.setBounds(10, 95, 151, 18);

		textXinitMean = new Text(group, SWT.BORDER);
		textXinitMean.setText("0.0");
		textXinitMean.setBounds(182, 95, 85, 26);

		Label label_5 = new Label(group, SWT.NONE);
		label_5.setText("D(m): ");
		label_5.setBounds(10, 131, 151, 18);

		textDMean = new Text(group, SWT.BORDER);
		textDMean.setText("10e-9");
		textDMean.setBounds(182, 127, 85, 30);

		combo = new Combo(group, SWT.NONE);
		combo.setBounds(150, 163, 187, 27);
		for (String w : this.memristor.windowPresets) {
			combo.add(w);
		}
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textJVariation.setEnabled(combo.getSelectionIndex() == 1);
				textJMean.setEnabled(combo.getSelectionIndex() == 1);
			}
		});
		combo.select(0);

		label_w = new Label(group, SWT.NONE);
		label_w.setText("window function:");
		label_w.setBounds(10, 172, 134, 18);

		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("j:");
		label_6.setBounds(273, 107, 151, 18);

		textJMean = new Text(group, SWT.BORDER);
		textJMean.setText("0.0");
		textJMean.setEnabled(false);
		textJMean.setBounds(445, 103, 85, 30);

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
			memristor.getParameter(memristor.WINDOWINDEX).setValue(combo.getSelectionIndex());
			// retrieve parameters
			memristor.getParameter(memristor.JINDEX).setValue(Double.parseDouble(textJMean.getText()));
			memristor.getParameter(memristor.RONINDEX).setValue(
					Double.parseDouble(textRonMean.getText()));
			memristor.getParameter(memristor.ROFFINDEX).setValue(
					Double.parseDouble(textRoffMean.getText()));
			memristor.getParameter(memristor.UVINDEX)
					.setValue(Double.parseDouble(textUvMean.getText()));
			memristor.getParameter(memristor.PINDEX).setValue(Double.parseDouble(textPMean.getText()));
			memristor.getParameter(memristor.DINDEX).setValue(Double.parseDouble(textDMean.getText()));
			memristor.getParameter(memristor.XINITINDEX).setValue(
					Double.parseDouble(textXinitMean.getText()));

			if (montecarlo) {
				if (checkJ.getSelection()) {
					memristor.getParameter(memristor.JINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.JINDEX).setSigma(
							Double.parseDouble(textJVariation.getText()));
				} else {
					memristor.getParameter(memristor.JINDEX).setAllowVariations(false);
				}
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
				if (checkUv.getSelection()) {
					memristor.getParameter(memristor.UVINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.UVINDEX).setSigma(
							Double.parseDouble(textUvVariation.getText()));
				} else {
					memristor.getParameter(memristor.UVINDEX).setAllowVariations(false);
				}
				if (checkP.getSelection()) {
					memristor.getParameter(memristor.PINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.PINDEX).setSigma(
							Double.parseDouble(textPVariation.getText()));
				} else {
					memristor.getParameter(memristor.PINDEX).setAllowVariations(false);
				}
				if (checkD.getSelection()) {
					memristor.getParameter(memristor.DINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.DINDEX).setSigma(
							Double.parseDouble(textDVariation.getText()));
				} else {
					memristor.getParameter(memristor.DINDEX).setAllowVariations(false);
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
				if (checkJ.getSelection()) {
					memristor.getParameter(memristor.JINDEX).setAllowVariations(true);
					s_aux = textJVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.JINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.JINDEX).setAllowVariations(false);
				}
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
				if (checkUv.getSelection()) {
					memristor.getParameter(memristor.UVINDEX).setAllowVariations(true);
					s_aux = textUvVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.UVINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.UVINDEX).setAllowVariations(false);
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
				if (checkD.getSelection()) {
					memristor.getParameter(memristor.DINDEX).setAllowVariations(true);
					s_aux = textDVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.DINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.DINDEX).setAllowVariations(false);
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
		checkRon.setText("Ron, " + ResourcesAndUsefulFunctions.SIGMA + ":");
		checkRoff.setText("Roff, " + ResourcesAndUsefulFunctions.SIGMA + ":");
		checkP.setText("p, " + ResourcesAndUsefulFunctions.SIGMA + ":");
		checkJ.setText("j, " + ResourcesAndUsefulFunctions.SIGMA + ":");
		checkUv.setText("uv, " + ", " + ResourcesAndUsefulFunctions.SIGMA + ":");
		checkD.setText("d, " + ResourcesAndUsefulFunctions.SIGMA + ":");
		checkXinit.setText("x_init, " + ResourcesAndUsefulFunctions.SIGMA + ":");

		textRonMean.setText("" + memristor.getParameter(memristor.RONINDEX).getValue());
		textRoffMean.setText("" + memristor.getParameter(memristor.ROFFINDEX).getValue());
		textPMean.setText("" + memristor.getParameter(memristor.PINDEX).getValue());
		textJMean.setText("" + memristor.getParameter(memristor.JINDEX).getValue());
		textUvMean.setText("" + memristor.getParameter(memristor.UVINDEX).getValue());
		textDMean.setText("" + memristor.getParameter(memristor.DINDEX).getValue());
		textXinitMean.setText("" + memristor.getParameter(memristor.XINITINDEX).getValue());

		textRonVariation.setText("" + memristor.getParameter(memristor.RONINDEX).getSigma());
		textRoffVariation.setText("" + memristor.getParameter(memristor.ROFFINDEX).getSigma());
		textPVariation.setText("" + memristor.getParameter(memristor.PINDEX).getSigma());
		textJVariation.setText("" + memristor.getParameter(memristor.JINDEX).getSigma());
		textUvVariation.setText("" + memristor.getParameter(memristor.UVINDEX).getSigma());
		textDVariation.setText("" + memristor.getParameter(memristor.DINDEX).getSigma());
		textXinitVariation.setText("" + memristor.getParameter(memristor.XINITINDEX).getSigma());
		montecarlo = true;

	}

	@Override
	public void setSpaceExploration() {
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variation, alternative value list");
		
		checkRon.setText("Ron:");
		checkRoff.setText("Roff:");
		checkP.setText("p:");
		checkJ.setText("j:");
		checkUv.setText("uv, " + ":");
		checkD.setText("d:");
		checkXinit.setText("x_init:");

//		textRonMean.setText("" + memristor.getParameter(memristor.RONINDEX).getValue());
//		textRoffMean.setText("" + memristor.getParameter(memristor.ROFFINDEX).getValue());
//		textPMean.setText("" + memristor.getParameter(memristor.PINDEX).getValue());
//		textJMean.setText("" + memristor.getParameter(memristor.JINDEX).getValue());
//		textUvMean.setText("" + memristor.getParameter(memristor.UVINDEX).getValue());
//		textDMean.setText("" + memristor.getParameter(memristor.DINDEX).getValue());
//		textXinitMean.setText("" + memristor.getParameter(memristor.XINITINDEX).getValue());
//		textRonVariation.setText(""
//				+ memristor.getParameter(memristor.RONINDEX).getMaxVariation());
//		textRoffVariation.setText(""
//				+ memristor.getParameter(memristor.ROFFINDEX).getMaxVariation());
//		textPVariation.setText("" + memristor.getParameter(memristor.PINDEX).getMaxVariation());
//		textJVariation.setText("" + memristor.getParameter(memristor.JINDEX).getMaxVariation());
//		textUvVariation
//				.setText("" + memristor.getParameter(memristor.UVINDEX).getMaxVariation());
//		textDVariation.setText("" + memristor.getParameter(memristor.DINDEX).getMaxVariation());
//		textXinitVariation.setText(""
//				+ memristor.getParameter(memristor.XINITINDEX).getMaxVariation());
		
		montecarlo = false;
	}
}
