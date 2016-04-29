/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.resources;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fgarcia
 */
public class FilesFoldersManagement {

    public static void close(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ExternalPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String children1 : children) {
                if (!deleteDir(new File(dir, children1))) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public static boolean prepareFolder(String folder) {
        boolean createFolder;
        boolean exists = (new File(folder)).exists();
        if (exists) {
            deleteDir(new File(folder));
        }
        try {
            createFolder = (new File(folder)).mkdirs();
            if (!createFolder) {
                Logger.getLogger(ResourcesMAF.loggerName).log(Level.SEVERE, "Could not create the tmp folder");
                return false;
            }
        } catch (Exception ioe) {
            Logger.getLogger(ResourcesMAF.loggerName).log(Level.SEVERE, "Could not create the tmp folder\n{0}", ioe.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static int linesNumberInFile(String fileAbsPath, boolean skipComments,
            String commentsStartWith) {
//        LineNumberReader lnr = null;
//        int linesNumber = -1;
//        try {
//            lnr = new LineNumberReader(new FileReader(new File( fileAbsPath )));
//                lnr.skip(Long.MAX_VALUE);
//            linesNumber = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
//        } catch (IOException ex) {
//            Logger.getLogger(FilesFoldersManagement.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            close(lnr);
//        }
//        return linesNumber;

        int linesNumber = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileAbsPath));
            String line;
            while ( (line=reader.readLine()) != null) {
                if( skipComments && line.startsWith(commentsStartWith) ){
                    continue;
                }
                linesNumber++;
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(FilesFoldersManagement.class.getName()).log(Level.SEVERE, null, ex);
            linesNumber = -1;
        } finally {
            close(reader);
        }
        return linesNumber;
    }
//    public static File[] filesInFolder(File folder) {
//        if (folder.isDirectory()) {
//            return folder.listFiles();
//        } else {
//            return null;
//        }
//    }
}
