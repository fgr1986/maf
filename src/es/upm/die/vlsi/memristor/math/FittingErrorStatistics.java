/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.math;

import es.upm.die.vlsi.memristor.resources.FilesFoldersManagement;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fgarcia
 */
public class FittingErrorStatistics {

    // [csvFiles.length][threadsNumber]
    private final FittingCurvesResult[][] simulationMeasureFitErrors;
    private final String[] errorFilePaths;
    private final String outputFolder;

    public FittingErrorStatistics(FittingCurvesResult[][] simulationMeasureFitErrors, String outputFolder) {
        this.simulationMeasureFitErrors = simulationMeasureFitErrors;
        this.outputFolder = outputFolder;
        errorFilePaths = new String[simulationMeasureFitErrors.length];
    }

    public void writeErrorFiles() {
        int csvCount = 0;
        for (FittingCurvesResult[] fcrs : simulationMeasureFitErrors) {
            File errorDataFile;
            errorDataFile = new File(outputFolder + "/" + ResourcesMAF.GNUPLOTSCRIPTSFOLDER + "error_csv_" + csvCount + ".gnuplot");
            FileWriter fw = null;
            try {
                fw = new FileWriter(errorDataFile);
                for (FittingCurvesResult fcr : fcrs) {
                    fw.write(fcr.getScenarioIndex() + " " + fcr.getAbsError() + "\n");
                }
                errorFilePaths[csvCount] = errorDataFile.getAbsolutePath();
            } catch (IOException ex) {
                Logger.getLogger(FittingErrorStatistics.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                FilesFoldersManagement.close(fw);
            }
            csvCount++;
        }
    }

    public String[] getErrorFilePaths() {
        return errorFilePaths;
    }

}
