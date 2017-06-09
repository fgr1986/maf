package memristorModelsUI;

import memristorModels.MemristorModel;
import memristorModels.MemristorQ;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

import simulationObjects.ResourcesAndUsefulFunctions;

public class MemristorMenuQControlled extends Composite implements
		MemristorModelMenu {
	private Text textAVariation;
	private Text textDVariation;
	private Text textCVariation;
	private Text textBVariation;
	private Button checkA;
	private Button checkD;
	private Button checkC;
	private Button checkB;
	private MemristorQ memristor;
	private boolean montecarlo;
	private Group grpModelParameterMean;
	private Text textAMean;
	private Text textDMean;
	private Text textCMean;
	private Text textBMean;
	private Button buttonAll;
	private Group compositeVariations;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MemristorMenuQControlled(Composite parent, int style) {
		super(parent, style);
		this.memristor = new MemristorQ();
		compositeVariations = new Group(this, SWT.SHADOW_ETCHED_IN);
		compositeVariations.setText("Model parameter variation values");
		compositeVariations.setLayout(null);
		compositeVariations.setBounds(10, 127, 696, 111);

		checkA = new Button(compositeVariations, SWT.CHECK);
		checkA.setSelection(true);
		checkA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textAVariation.setEnabled(checkA.getSelection());
			}
		});
		checkA.setText("a:");
		checkA.setBounds(10, 31, 151, 18);

		textAVariation = new Text(compositeVariations, SWT.BORDER);
		textAVariation.setBounds(182, 31, 85, 26);

		textDVariation = new Text(compositeVariations, SWT.BORDER);
		textDVariation.setBounds(445, 67, 85, 30);

		checkD = new Button(compositeVariations, SWT.CHECK);
		checkD.setSelection(true);
		checkD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textDVariation.setEnabled(checkD.getSelection());
			}
		});
		checkD.setText("p:");
		checkD.setBounds(273, 71, 151, 18);

		textCVariation = new Text(compositeVariations, SWT.BORDER);
		textCVariation.setBounds(445, 31, 85, 30);

		checkC = new Button(compositeVariations, SWT.CHECK);
		checkC.setSelection(true);
		checkC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textCVariation.setEnabled(checkC.getSelection());
			}
		});
		checkC.setText("uv(m²s⁻¹V⁻¹):");
		checkC.setBounds(273, 35, 151, 18);

		checkB = new Button(compositeVariations, SWT.CHECK);
		checkB.setSelection(true);
		checkB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textBVariation.setEnabled(checkB.getSelection());
			}
		});
		checkB.setText("b:");
		checkB.setBounds(10, 63, 151, 18);

		textBVariation = new Text(compositeVariations, SWT.BORDER);
		textBVariation.setBounds(182, 63, 85, 26);

		textAVariation.setText("" + this.memristor.getParameter(memristor.AINDEX).getValue());
		textBVariation.setText("" + this.memristor.getParameter(memristor.BINDEX).getValue());
		textCVariation.setText("" + this.memristor.getParameter(memristor.CINDEX).getValue());
		textDVariation.setText("" + this.memristor.getParameter(memristor.DINDEX).getValue());
		
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
		buttonAll.setBounds(562, 75, 124, 26);
		
		grpModelParameterMean = new Group(this, SWT.SHADOW_ETCHED_IN);
		grpModelParameterMean.setLayout(null);
		grpModelParameterMean.setText("Model parameter mean values");
		grpModelParameterMean.setBounds(10, 10, 696, 111);
		
		Label lblA = new Label(grpModelParameterMean, SWT.NONE);
		lblA.setText("a:");
		lblA.setBounds(10, 31, 83, 18);
		
		textAMean = new Text(grpModelParameterMean, SWT.BORDER);
		textAMean.setText("0.0");
		textAMean.setBounds(99, 31, 85, 26);
		
		textDMean = new Text(grpModelParameterMean, SWT.BORDER);
		textDMean.setText("0.0");
		textDMean.setBounds(371, 67, 85, 30);
		
		Label lblD = new Label(grpModelParameterMean, SWT.NONE);
		lblD.setText("d:");
		lblD.setBounds(273, 71, 92, 18);
		
		textCMean = new Text(grpModelParameterMean, SWT.BORDER);
		textCMean.setText("0.0");
		textCMean.setBounds(371, 31, 85, 30);
		
		Label lblC = new Label(grpModelParameterMean, SWT.NONE);
		lblC.setText("c:");
		lblC.setBounds(273, 35, 92, 18);
		
		Label lblB = new Label(grpModelParameterMean, SWT.NONE);
		lblB.setText("b:");
		lblB.setBounds(10, 63, 83, 18);
		
		textBMean = new Text(grpModelParameterMean, SWT.BORDER);
		textBMean.setText("0.0");
		textBMean.setBounds(99, 63, 85, 26);
		

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
			memristor.getParameter(memristor.AINDEX).setValue(Double.parseDouble(textAMean.getText()));
			memristor.getParameter(memristor.BINDEX).setValue(
					Double.parseDouble(textBMean.getText()));
			memristor.getParameter(memristor.CINDEX).setValue(
					Double.parseDouble(textCMean.getText()));
			memristor.getParameter(memristor.DINDEX)
					.setValue(Double.parseDouble(textDMean.getText()));
			
			if (montecarlo) {
				if (checkA.getSelection()) {
					memristor.getParameter(memristor.AINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.AINDEX).setSigma(
							Double.parseDouble(textAVariation.getText()));
				} else {
					memristor.getParameter(memristor.AINDEX).setAllowVariations(false);
				}
				if (checkB.getSelection()) {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.BINDEX).setSigma(
							Double.parseDouble(textBVariation.getText()));
				} else {
					memristor.getParameter(memristor.BINDEX).setAllowVariations(false);
				}
				if (checkC.getSelection()) {
					memristor.getParameter(memristor.CINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.CINDEX).setSigma(
							Double.parseDouble(textCVariation.getText()));
				} else {
					memristor.getParameter(memristor.CINDEX).setAllowVariations(false);
				}
				if (checkD.getSelection()) {
					memristor.getParameter(memristor.DINDEX).setAllowVariations(true);
					memristor.getParameter(memristor.DINDEX).setSigma(
							Double.parseDouble(textDVariation.getText()));
				} else {
					memristor.getParameter(memristor.DINDEX).setAllowVariations(false);
				}
			} else {
				String[] s_aux;
				double[] d_aux; 	 	
				if (checkA.getSelection()) {
					memristor.getParameter(memristor.AINDEX).setAllowVariations(true);
					s_aux = textAVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.AINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.AINDEX).setAllowVariations(false);
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
				if (checkC.getSelection()) {
					memristor.getParameter(memristor.CINDEX).setAllowVariations(true);
					s_aux = textCVariation.getText().split(ResourcesAndUsefulFunctions.VALUESPLITTER);
					d_aux = new double[s_aux.length];
					for(int i=0;i< d_aux.length;i++){
						d_aux[i] = Double.parseDouble(s_aux[i]);
					}
					memristor.getParameter(memristor.CINDEX).setValueList(d_aux);
				} else {
					memristor.getParameter(memristor.CINDEX).setAllowVariations(false);
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
		checkB.setText("b: ");
		checkA.setText("a: ");
		checkD.setText("d: ");
		checkC.setText("c: ");

		textBMean.setText("" + memristor.getParameter(memristor.BINDEX).getSigma());
		textAMean.setText("" + memristor.getParameter(memristor.AINDEX).getValue());
		textDMean.setText("" + memristor.getParameter(memristor.DINDEX).getValue());
		textCMean.setText("" + memristor.getParameter(memristor.CINDEX).getValue());
		
		textBVariation.setText("" + memristor.getParameter(memristor.BINDEX).getSigma());
		textAVariation.setText("" + memristor.getParameter(memristor.AINDEX).getSigma());
		textDVariation.setText("" + memristor.getParameter(memristor.DINDEX).getSigma());
		textCVariation.setText("" + memristor.getParameter(memristor.CINDEX).getSigma());
		montecarlo = true;

	}

	@Override
	public void setSpaceExploration() {
		// TODO Auto-generated method stub
		compositeVariations.setText("Parameter variation, alternative value list");
		checkB.setText("b: ");
		checkA.setText("a: ");
		checkD.setText("d: ");
		checkC.setText("c: ");

//		textBMean.setText("" + memristor.getParameter(memristor.BINDEX).getValue());
//		textAMean.setText("" + memristor.getParameter(memristor.AINDEX).getValue());
//		textDMean.setText("" + memristor.getParameter(memristor.DINDEX).getValue());
//		textCMean.setText("" + memristor.getParameter(memristor.CINDEX).getValue());
//		textBVariation.setText("" + memristor.getParameter(memristor.BINDEX).getMaxVariation());
//		textAVariation.setText("" + memristor.getParameter(memristor.AINDEX).getMaxVariation());
//		textDVariation.setText("" + memristor.getParameter(memristor.DINDEX).getMaxVariation());
//		textCVariation.setText("" + memristor.getParameter(memristor.CINDEX).getMaxVariation());
		
		montecarlo = false;
	}
}
