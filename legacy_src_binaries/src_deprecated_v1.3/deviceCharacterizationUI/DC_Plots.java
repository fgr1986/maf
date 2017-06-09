package deviceCharacterizationUI;

import java.awt.Frame;
import java.awt.Panel;

import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import simulations.DC_ThreadManager;

import org.eclipse.swt.widgets.Composite;

import java.awt.BorderLayout;

import javax.swing.JRootPane;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;

public class DC_Plots {
	private DC_ThreadManager dc;
	private Panel panelVI;
	private Panel panelVIT;
	private Panel panelRT;
	private Panel panelRV;

	// private Panel panel_sv;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DC_Plots window = new DC_Plots();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DC_Plots(DC_ThreadManager dc) {
		this.dc = dc;
	}

	public DC_Plots() {
		// TODO Auto-generated constructor stub
	}

	/*public static void applyChartTheme(JFreeChart chart) {
		chart.getTitle().setFont(new Font("Arial", Font.PLAIN, 14));
	}*/


	/**
	 * Open the window.
	 */
	public void open() {
		// new ChartComposite(parent, SWT.NONE,
		// chart, true);
		Display display = Display.getDefault();
		Shell shlCharacterizationGraphs = new Shell();
		shlCharacterizationGraphs.setImage(SWTResourceManager.getImage(
				DC_UI.class, "/resources/dc.png"));

		shlCharacterizationGraphs.setToolTipText("Characterization graphs");
		shlCharacterizationGraphs.setSize(display.getClientArea().width,
				display.getClientArea().height);
		// shlCharacterizationGraphs.setMaximized(true);
		shlCharacterizationGraphs.setText("Characterization graphs");
		shlCharacterizationGraphs.setLayout(new FillLayout(SWT.HORIZONTAL));

		TabFolder tabFolder = new TabFolder(shlCharacterizationGraphs, SWT.NONE);

		TabItem tbtmVi = new TabItem(tabFolder, SWT.NONE);
		tbtmVi.setText("v(i)");

		Composite composite_vi = new Composite(tabFolder, SWT.EMBEDDED);
		tbtmVi.setControl(composite_vi);

		Frame frame = SWT_AWT.new_Frame(composite_vi);

		panelVI = new Panel();
		frame.add(panelVI);
		panelVI.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane = new JRootPane();
		panelVI.add(rootPane);

		TabItem tbtmVtIt = new TabItem(tabFolder, SWT.NONE);
		tbtmVtIt.setText("v(t), i(t)");

		Composite composite_vit = new Composite(tabFolder, SWT.EMBEDDED);
		tbtmVtIt.setControl(composite_vit);

		Frame frame_1 = SWT_AWT.new_Frame(composite_vit);

		panelVIT = new Panel();
		frame_1.add(panelVIT);
		panelVIT.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane_1 = new JRootPane();
		panelVIT.add(rootPane_1);

		TabItem tbtmRmt = new TabItem(tabFolder, SWT.NONE);
		tbtmRmt.setText("R_m(t)");

		Composite composite_rt = new Composite(tabFolder, SWT.EMBEDDED);
		tbtmRmt.setControl(composite_rt);

		Frame frame_2 = SWT_AWT.new_Frame(composite_rt);

		panelRT = new Panel();
		frame_2.add(panelRT);
		panelRT.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane_2 = new JRootPane();
		panelRT.add(rootPane_2);

		TabItem tbtmRmv = new TabItem(tabFolder, SWT.NONE);
		tbtmRmv.setText("R_m(v)");

		Composite composite_rv = new Composite(tabFolder, SWT.EMBEDDED);
		tbtmRmv.setControl(composite_rv);

		Frame frame_3 = SWT_AWT.new_Frame(composite_rv);

		panelRV = new Panel();
		frame_3.add(panelRV);
		panelRV.setLayout(new BorderLayout(0, 0));

		JRootPane rootPane_3 = new JRootPane();
		panelRV.add(rootPane_3);

		if (dc != null && dc.getSimulationResult().isCorrect()) {
			panelVI.add(new ChartPanel(dc.getVi_chart()));
			panelVIT.add(new ChartPanel(dc.getVit_chart()));
			panelRT.add(new ChartPanel(dc.getMt_chart()));
			panelRV.add(new ChartPanel(dc.getMv_chart()));
			for (JFreeChart jfc : dc.getSv_charts()) {
				TabItem newTab = new TabItem(tabFolder, SWT.NONE);
				newTab.setText(jfc.getTitle().getText());
				Composite newComposite = new Composite(tabFolder, SWT.EMBEDDED);
				newTab.setControl(newComposite);
				Frame newFrame = SWT_AWT.new_Frame(newComposite);
				Panel newPanel = new Panel();
				newFrame.add(newPanel);
				newPanel.setLayout(new BorderLayout(0, 0));
				JRootPane newJRootPane = new JRootPane();
				newPanel.add(newJRootPane);
				newPanel.add(new ChartPanel(jfc));
				newPanel.setSize(newPanel.getParent().getSize());
				// theme
			}
			panelVI.setSize(panelVI.getParent().getSize());
		}
		shlCharacterizationGraphs.open();
		shlCharacterizationGraphs.layout();
		while (!shlCharacterizationGraphs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}
}
