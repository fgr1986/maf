/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.resources;

import es.upm.die.vlsi.memristor.simulations.DCMonteCarloManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fgarcia
 */
public class ExternalPlotter {

    public enum PLOTTER {

        GNUPLOT("/es/upm/die/vlsi/memristor/resources/external_scripts/plot_adjusted_measures.gnuplot",
                "/es/upm/die/vlsi/memristor/resources/external_scripts/plot_simulation.gnuplot",
                "/es/upm/die/vlsi/memristor/resources/external_scripts/plot_simulations.gnuplot", 
                "/es/upm/die/vlsi/memristor/resources/external_scripts/plot_adjusted_error.gnuplot",
                "/es/upm/die/vlsi/memristor/resources/external_scripts/plot_adjusted_memristance.gnuplot");
        private String[] tempScriptPaths;
        private String[] tempScriptAbsolutePaths;
        private static final int ADJUSTED_MEASURE_INDEX = 0;
        private static final int SIMULATION_INDEX = 1;
        private static final int SIMULATIONS_INDEX = 2;
        private static final int ERROR_INDEX = 3;
        private static final int MEMRISTANCE_INDEX = 4;
        

        PLOTTER(String plotAdjustedMeasuresScriptPath, String plotSimulationScriptPath,
                String plotSimulationsScriptPath, String plotAdjustedErrorScriptPath,
                String plotAdjustedMemristanceScriptPath ) {
            // prepare folders
            tempScriptPaths = new String[5];
            tempScriptAbsolutePaths = new String[5];
            tempScriptPaths[ADJUSTED_MEASURE_INDEX] = plotAdjustedMeasuresScriptPath;
            tempScriptPaths[SIMULATION_INDEX] = plotSimulationScriptPath;
            tempScriptPaths[SIMULATIONS_INDEX] = plotSimulationsScriptPath;
            tempScriptPaths[ERROR_INDEX] = plotAdjustedErrorScriptPath;
            tempScriptPaths[MEMRISTANCE_INDEX] = plotAdjustedMemristanceScriptPath;
            // Create temp scripts
            int scriptCount = 0;
            for (String ssp : tempScriptPaths) {
                InputStream scriptIS = getClass().getResourceAsStream(ssp);
                File tempScript;
                try {
                    tempScript = File.createTempFile("temp_maf_" + scriptCount + ".gnuplot", ".tmp");
                    tempScriptAbsolutePaths[scriptCount++] = tempScript.getAbsolutePath();
                    BufferedReader fr = null;
                    FileWriter fw = null;
                    try {
                        fr = new BufferedReader(new InputStreamReader(scriptIS));
                        fw = new FileWriter(tempScript);
                        int c = fr.read();
                        while (c != -1) {
                            fw.write(c);
                            c = fr.read();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        FilesFoldersManagement.close(fr);
                        FilesFoldersManagement.close(fw);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public String getAdjustedMeasureScriptAbsolutePath() {
            return tempScriptAbsolutePaths[ADJUSTED_MEASURE_INDEX];
        }

        public String getAdjustedMemristanceScriptAbsolutePath() {
            return tempScriptAbsolutePaths[MEMRISTANCE_INDEX];
        }

        public String getSimulationScriptAbsolutePath() {
            return tempScriptAbsolutePaths[SIMULATION_INDEX];
        }

        public String getSimulationsScriptAbsolutePath() {
            return tempScriptAbsolutePaths[SIMULATIONS_INDEX];
        }
        
        public String getAdjustedErrorScriptAbsolutePath(){
            return tempScriptAbsolutePaths[ERROR_INDEX];
        }

    };

    private final PLOTTER plotter;
    private String scriptsFolder;

    public ExternalPlotter(PLOTTER plotter) {
        this.plotter = plotter;
        this.scriptsFolder = ResourcesMAF.SIMULATIONSFOLDER + "/" + ResourcesMAF.GNUPLOTSCRIPTSFOLDER;
    }

    public ExternalPlotter() {
        this.plotter = PLOTTER.GNUPLOT;
        this.scriptsFolder = ResourcesMAF.SIMULATIONSFOLDER + "/" + ResourcesMAF.GNUPLOTSCRIPTSFOLDER;
    }

    public void prepareScriptsFolder(String simulationsFolder) {
        this.scriptsFolder = simulationsFolder + "/" + ResourcesMAF.GNUPLOTSCRIPTSFOLDER;
        if (!FilesFoldersManagement.prepareFolder(scriptsFolder)) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, scriptsFolder + " not created.");
        }
    }    
    
    public boolean plotAdjustedError(String outputFolder, String errorPath, int measuresId ) {
        boolean result = true;
        InputStream scriptIS;
        try {
            scriptIS = new FileInputStream(plotter.getAdjustedErrorScriptAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        String gsPath = scriptsFolder + "/adjusted_error_csv_" + measuresId + ".gnuplot";
        File gnuPlotScript = new File(gsPath);
        BufferedReader fr = null;
        Writer fw = null;
        try {
            fr = new BufferedReader(new InputStreamReader(scriptIS));
//            fw = new FileWriter(gnuPlotScript);
            fw = new OutputStreamWriter(new FileOutputStream(gnuPlotScript));
            fw.write("errorFile='" + errorPath + "'"
                    + ";\noutputFile='" + outputFolder + "/" + measuresId + "_adjusted_error.svg'"           
                    + ";\nmyTitle='Voltage-Current error fitting for measure #" + measuresId + "'");
            fw.write("\n\n");
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } finally {
            FilesFoldersManagement.close(fr);
            FilesFoldersManagement.close(fw);
        }
        if (gnuPlotScript.exists()) {
            executeCommand("gnuplot " + gnuPlotScript.getAbsolutePath());
        }
        return result;
    }

    public boolean plotAdjustedCycleMeasures(String outputFolder, String scatterPath, String simulationPath,
            int measuresId, int simulationId, double minX, double maxX) {
        boolean result = true;
        InputStream scriptIS;
        try {
            scriptIS = new FileInputStream(plotter.getAdjustedMeasureScriptAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        String gsPath = scriptsFolder + "/adjusted_cycle_" + measuresId + "_sim_" + simulationId + ".gnuplot";
        File gnuPlotScript = new File(gsPath);
        BufferedReader fr = null;
        Writer fw = null;
        try {
            fr = new BufferedReader(new InputStreamReader(scriptIS));
//            fw = new FileWriter(gnuPlotScript);
            fw = new OutputStreamWriter(new FileOutputStream(gnuPlotScript));
            fw.write("measuresFile='" + scatterPath + "'"
                    + ";simulationFile='" + simulationPath + "'"
                    + ";outputFile='" + outputFolder + "/" + measuresId + "_adjusted.svg'"
                    + ";logOutputFile='" + outputFolder + "/log_" + measuresId + "_adjusted.svg'"
                    + ";minX='" + minX + "'"
                    + ";maxX='" + maxX + "'"              
                    + ";myTitle='Voltage-Current best fitting for measures #" + measuresId + " simulation #" + simulationId + "'");
            fw.write("\n\n");
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } finally {
            FilesFoldersManagement.close(fr);
            FilesFoldersManagement.close(fw);
        }
        if (gnuPlotScript.exists()) {
            executeCommand("gnuplot " + gnuPlotScript.getAbsolutePath());
        }
        return result;
    }
    
    public boolean plotAdjustedMemristance(String outputFolder, String scatterPath, String simulationPath,
            int measuresId, int simulationId, double minX, double maxX) {
        boolean result = true;
        InputStream scriptIS;
        try {
            scriptIS = new FileInputStream(plotter.getAdjustedMemristanceScriptAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        String gsPath = scriptsFolder + "/adjusted_memristance_cycle_" + measuresId + "_sim_" + simulationId + ".gnuplot";
        File gnuPlotScript = new File(gsPath);
        BufferedReader fr = null;
        Writer fw = null;
        try {
            fr = new BufferedReader(new InputStreamReader(scriptIS));
//            fw = new FileWriter(gnuPlotScript);
            fw = new OutputStreamWriter(new FileOutputStream(gnuPlotScript));
            fw.write("measuresFile='" + scatterPath + "'"
                    + ";simulationFile='" + simulationPath + "'"
                    + ";outputFile='" + outputFolder + "/" + measuresId + "_memristance_adjusted.svg'"
                    + ";logOutputFile='" + outputFolder + "/log_" + measuresId + "_memristance_adjusted.svg'"
                    + ";minX='" + minX + "'"
                    + ";maxX='" + maxX + "'"                 
                    + ";myTitle='Memristance-Voltage for measures #" + measuresId + " simulation #" + simulationId + "'");
            fw.write("\n\n");
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } finally {
            FilesFoldersManagement.close(fr);
            FilesFoldersManagement.close(fw);
        }
        if (gnuPlotScript.exists()) {
            executeCommand("gnuplot " + gnuPlotScript.getAbsolutePath());
        }
        return result;
    }

    public boolean plotSimulation(String outputFolder, String simulationPath, int simulationId) {
        boolean result = true;
        InputStream scriptIS;
        try {
            scriptIS = new FileInputStream(plotter.getSimulationScriptAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        String gsPath = scriptsFolder + "/simulation_" + simulationId + ".gnuplot";
        File gnuPlotScript = new File(gsPath);
        BufferedReader fr = null;
        Writer fw = null;
        try {
            fr = new BufferedReader(new InputStreamReader(scriptIS));
            fw = new OutputStreamWriter(new FileOutputStream(gnuPlotScript.getAbsolutePath()));
            fw.write("simulationFile='" + simulationPath + "'"
                    + ";outputFile='" + outputFolder + "/simulation_" + simulationId + ".svg'"
                    + ";logOutputFile='" + outputFolder + "/simulation_log_" + simulationId + ".svg'"
                    + ";myTitle='Voltage-Current simulation #" + simulationId + "'");
            fw.write("\n\n");
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } finally {
            FilesFoldersManagement.close(fr);
            FilesFoldersManagement.close(fw);
        }
        if (gnuPlotScript.exists()) {
            executeCommand("gnuplot " + gnuPlotScript.getAbsolutePath());
        }
        return result;
    }

    public boolean plotSimulations(String outputFolder, String simulationPaths ) {
        boolean result = true;
        InputStream scriptIS;
        try {
            scriptIS = new FileInputStream(plotter.getSimulationsScriptAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        String gsPath = scriptsFolder + "/simulations.gnuplot";
        File gnuPlotScript = new File(gsPath);
        BufferedReader fr = null;
        Writer fw = null;
        try {
            fr = new BufferedReader(new InputStreamReader(scriptIS));
            fw = new OutputStreamWriter(new FileOutputStream(gnuPlotScript.getAbsolutePath()));
            // plot for [i=1:1000] 'data'.i.'.txt' using 1:2 title 'Flow '.i            
            // filenames = "first second third fourth fifth"
            // plot for [file in filenames] file."dat" using 1:2 with lines
            fw.write("filenames='" + simulationPaths + "'"
                    + ";outputFile='" + outputFolder + "/simulations.svg'"
                    + ";logOutputFile='" + outputFolder + "/simulations_log.svg'"
                    + ";myTitle='Voltage-Current simulations'");
            fw.write("\n\n");
            int c = fr.read();
            while (c != -1) {
                fw.write(c);
                c = fr.read();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } finally {
            FilesFoldersManagement.close(fr);
            FilesFoldersManagement.close(fw);
        }
        if (gnuPlotScript.exists()) {
            executeCommand("gnuplot " + gnuPlotScript.getAbsolutePath());
        }
        return result;
    }

    private void executeCommand(String command) {
        String s;
        try {
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec(command);
//            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//            }
            //   read any errors from the attempted command
            if ((s = stdError.readLine()) != null) {
                System.out.println("Here is the standard error:\n");
                System.out.println(s);
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            }
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            Logger.getLogger(DCMonteCarloManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
