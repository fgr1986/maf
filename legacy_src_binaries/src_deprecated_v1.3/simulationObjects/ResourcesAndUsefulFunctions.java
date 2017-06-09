package simulationObjects;

import java.awt.Color;
import java.text.DecimalFormat;

public class ResourcesAndUsefulFunctions {
	//app info
	public static final String APPTITLE = "Memristor Analysis Framework";
	public static final String APPVERSION = "v1.2";
	// Constants
	public static final double e = 1.6021e-19; // Electron charge
	public static final double effMSi = 0.31 * 9.1e-31; // Effective mass in
														// a-Si
	public static final double PLANK = 6.62606e-34; // Plank constant
	public static final double e0 = 8.854e-12; // Vacuum permitibity
	public static final double m = 9.1e-31; // electron mass
	public static final double reducedPLANK = PLANK / (2 * Math.PI); // Plank
																		// constant
	public static final double k = 1.38e-23; // Boltzmann's constant
	//Plot background color
	public static final Color PLOTBACKGROUND = Color.BLACK;
	// Logg
	public static final String loggerName = "MAFLogger";
	//Corner values splitter
	public static final String VALUESPLITTER = ";";
	// Memristor's inherited constants
	public static final double constMinI = 1e-12;
	public static final double constMinV = 1e-12;
	public static final double constMinW = 1e-18;
	//security
	public static final int MAXRECOMMENDEDTHREADS = 10000;
	//mAGNITUDES SIZES:
	public static final int MAGNITUDEVALUESBUFFERSIZE = 4096;
	public static final int MINBUFFER = 128;
	public static final int MAXBUFFER = 20480;
	// Memristor types
	public static final int QCONTROLLEDMODEL = 0;
	public static final int NONLINEARDRIFTMODEL = 1;
	public static final int YAKOPCICEXPONENTIALDRIFTMODEL = 2;
	public static final int SIMMONSMODEL = 3;
	public static final int MODIFIEDSIMMONSMODEL = 4;
	public static final int TEAMMODEL = 5;
	public static final int MIMTHRESHOLDMODEL = 6;
	public static final int MICHIGANMODEL = 7;
	public static final String[] MODELFILENAMES = { "qControlled", "nonLinear",
			"yakopcic", "simmons", "simmonsModified", "team", "mimTH",
			"michigan" };
	public static final String[] MODELTITLES = {
			"Charge controlled memristor model",
			"Non Linear Drift memristor model",
			"Yakopcic Exponential drift memristor model",
			"Abdalla & Pickett HP Labs Simmons memristor model",
			"HP Labs MODIFIED Simmons memristor model",
			"Kvatinsky et al. TEAM Model. ThrEshold Adaptive memristor model",
			"Eshraghian et al. MIM Threshold memristor model",
			"Michigan University memristor model" };
	public static final String[] MODELDESCRIPTIONS = {
			"Charge controlled memristor model",
			"Non Linear Drift memristor model",
			"Yakopcic Exponential drift memristor model",
			"Abdalla & Pickett HP Labs Simmons memristor model",
			"HP Labs MODIFIED Simmons memristor model",
			"Kvatinsky et al. TEAM Model. ThrEshold Adaptive memristor model",
			"Eshraghian et al. MIM Threshold memristor model",
			"Michigan University memristor model" };
	public static final String[] MODELLINKS = { "",
			"Non Lineal Drift memristor model",
			"Yakopcic Exponential drift memristor model",
			"Abdalla & Pickett HP Labs Simmons memristor model",
			"HP Labs MODIFIED Simmons memristor model",
			"Kvatinsky et al. TEAM Model. ThrEshold Adaptive memristor model",
			"Eshraghian et al. MIM Threshold memristor model",
			"Michigan University memristor model" };
	public static final String VARIATIONS = "Model parameters and max variations:";
	public static final String DEVIATIONS = "Model parameters and deviations:";
	// Simulation constants
	public static final int DEFAULTSTEPS = (int) 1e5;
	public static final int DEFAULTSTEPSPLOTTEDMULTI = (int) 2e5;
//	public static final double MINSTEPDC = 1e-6;
//	public static final double MAXSTEPDC = 1e-3;
//	public static final double MINSTEPLC = 1e-15;
//	public static final double MAXSTEPLC = 1e-8;
	public static final double MINSTEPDC = 2e-12;
	public static final double MAXSTEPDC = 5e-10;
	public static final double MINSTEPLC = 2e-12;
	public static final double MAXSTEPLC = 5e-10;
	public static final double MAXDURATIONLC = 1e-3;
	public static final boolean REDUCEPLOTTEDSAMPLES = true;
	public static final boolean REDUCESTOREDSAMPLES = true;
	public static final int STOREFREQ = (int) 1e4;
	// file settings
	public static final String RESOURCESFOLDER = "/resources/";
	public static final String LTSPICEEXTENSION = ".sub";
	public static final String SPECTREEXTENSION = ".scs";
	public static final String SPECTREFOLDER = "spectre";
	public static final String LTSPICEFOLDER = "ltspice";
	public static final String CLASSICFOLDER = "classic";
	public static final String LSIFOLDER = "lsi";
	public static final String DEVICEMONTECARLOOUTPUT = "dc_montecarlo";
	public static final String DEVICESPACEEXPLORATIONOUTPUT = "dc_space_exploration";
	public static final String LEVELMONTECARLOOUTPUT = "lc_montecarlo";
	public static final String LEVELSPACEEXPLORATIONOUTPUT = "lc_space_exploration";
	public static final String OUTPUTEXTENSION = "csv";
	public static final String OUTPUTEXTENSIONXML = "xml";
	public static final String SIMULATIONSFOLDER = "simulations";
	// Voltage Types
	public static final int VSINE = 0;
	public static final int VPULSE = 1;
	public static final int VTRIANGLE = 2;
	public static final int VCONSTANT = 3;
	public static final int VCONSTANTRANGE = 4;
	// Special characters
	public static final String OHM1 = "Ω";
	public static final String OHM2 = "(Ω)";
	public static final String SIGMA = "σ";
	public static final String DELTA = "Δ";

	public static String getFormatTime(double d) {
		if (d > 1) {
			String finalResult = "";
			double auxCount = d;
			double dividendo = 0;
			dividendo = auxCount / 3600;
			if (dividendo > 1) {
				finalResult += Math.floor(dividendo) + " hours, ";
				auxCount -= Math.floor(dividendo) * 3600;
			}
			dividendo = auxCount / 60;
			if (dividendo > 1) {
				finalResult += Math.floor(dividendo) + " minutes, ";
				auxCount -= Math.floor(dividendo) * 60;
			}
			finalResult += Math.floor(auxCount*100)/100 + " secs.";
			return finalResult;
		} else {
			return "less than 1 second";
		}
	}
	
	public static String getFormatTimeTyneValues(double d) {
		if (d >= 0) {
			return (new DecimalFormat("0.###E0").format(d));
		} else {
			return "NaN";
		}
	}
	
}
