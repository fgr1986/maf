package memristorModels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import simulationObjects.ResourcesAndUsefulFunctions;
import simulationObjects.SimulationResult;

import com.thoughtworks.xstream.XStream;

public class MemristorIO {

	public static SimulationResult exportMemristor(MemristorModel memristor,
			String outputFile) {
		Writer out = null;
		XStream xstream = new XStream();
		String xml = "";
		SimulationResult sr = null;
		try {
			xml = xstream.toXML(new Memristor2Save(memristor));
			out = new OutputStreamWriter(new FileOutputStream(outputFile));
			out.write(xml);
			sr = new SimulationResult(true, 0, "Correctly exported");
		} catch (Exception exc) {
			System.out.println("Export error: " + exc.getLocalizedMessage());
			sr = new SimulationResult(false, 0, "Error exporting: "
					+ exc.getLocalizedMessage());
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		return sr;
	}

	public static MemristorModel importMemristor(String inputFile) {
		XStream xstream = new XStream();
		Memristor2Save memristorRead = null;
		try {
			memristorRead = (Memristor2Save) xstream
					.fromXML(new File(inputFile));
		} catch (Exception exc) {
			System.out.println("Export error: " + exc.getMessage());
		}
		if (memristorRead != null) {
			MemristorModel m = null;
			switch (memristorRead.getMemristorType()) {
			case ResourcesAndUsefulFunctions.QCONTROLLEDMODEL:
				m = new MemristorQ();
				break;
			case ResourcesAndUsefulFunctions.NONLINEARDRIFTMODEL:
				m = new MemristorNonLinearDrift();
				break;
			case ResourcesAndUsefulFunctions.YAKOPCICEXPONENTIALDRIFTMODEL:
				m = new MemristorYakopcic();
				break;
			case ResourcesAndUsefulFunctions.SIMMONSMODEL:
				m = new MemristorModifiedSimmons();
				break;
			case ResourcesAndUsefulFunctions.MODIFIEDSIMMONSMODEL:
				m = new MemristorModifiedSimmons();
				break;
			case ResourcesAndUsefulFunctions.TEAMMODEL:
				m = new MemristorTEAM();
				break;

			case ResourcesAndUsefulFunctions.MICHIGANMODEL:
				m = new MemristorMichigan();
				break;
			default:
				m = null;
				break;
			}
			return m;
		} else {
			return null;// memristor;
		}
	}
}
