/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.simulation_objects.memristor_models;

/**
 *
 * @author fgarcia
 */
public class MemristorLibrary {

    // Memristor types
    public enum Memristor_Model {

        QCONTROLLEDMODEL(0,  "Charge controlled memristor model"),
        NONLINEARDRIFTMODEL(1, "Non Linear Drift memristor model"),
        YAKOPCICEXPONENTIALDRIFTMODEL(2, "Yakopcic Exponential drift memristor model"),
        SIMMONSMODEL(3, "Abdalla & Pickett HP Labs Simmons memristor model"),
        MODIFIEDSIMMONSMODEL(4, "HP Labs MODIFIED Simmons memristor model"),
        TEAMMODEL(5, "Kvatinsky et al. TEAM Model. ThrEshold Adaptive memristor model"),
        MIMTHRESHOLDMODEL(6, "Eshraghian et al. MIM Threshold memristor model"),
        OXIDEBASEDMODEL(7, "Oxide based memristor model"),
        MICHIGANSPICEMODEL(8, "Michigan University memristor model (from SPICE code)"),        
        ASICMODEL(9, "aSiC memristor model");

        private final int index;   
        private final String title; 

        Memristor_Model(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int index() {
            return index;
        }
        
        public String title() {
            return title;
        }
    };
    
    public static final String[] MODELDESCRIPTIONS = {
        "Charge controlled memristor model",
        "Non Linear Drift memristor model",
        "Yakopcic Exponential drift memristor model",
        "Abdalla & Pickett HP Labs Simmons memristor model",
        "HP Labs MODIFIED Simmons memristor model",
        "Kvatinsky et al. TEAM Model. ThrEshold Adaptive memristor model",
        "Eshraghian et al. MIM Threshold memristor model",
        "Oxide based memristor model",
        "Michigan University memristor model from Spice code",    
        "aSiC memristor model from Spice/Verilog-AMS code"};
    public static final String[] MODELLINKS = {"",
        "Non Lineal Drift memristor model",
        "Yakopcic Exponential drift memristor model",
        "Abdalla & Pickett HP Labs Simmons memristor model",
        "HP Labs MODIFIED Simmons memristor model",
        "Kvatinsky et al. TEAM Model. ThrEshold Adaptive memristor model",
        "Eshraghian et al. MIM Threshold memristor model",
        "Oxide based memristor model",
        "Michigan University memristor model from Spice code",  
        "aSiC memristor model from Spice/Verilog-AMS code"};

    public static String GetModelDescription(int i) {
        return MODELDESCRIPTIONS[i];
    }
    
    public static String GetModelTitle(int i) {
        int count = 0;
        for(Memristor_Model m : Memristor_Model.values()){
            if(count++ == i){
                return m.title();
            }
        }
        return "Error in Memristor Library";
    }

//    public static String GetModelTitle(int i) {
//        return Memristor_Model.;
//    }

    public static MemristorModel GetNewMemristor(final Memristor_Model memristorType) {
        MemristorModel m;
        switch (memristorType) {
            case QCONTROLLEDMODEL: {
                m = new MemristorQ();
            }
            break;
            case NONLINEARDRIFTMODEL: {
                m = new MemristorNonLinearDrift();
            }
            break;
            case YAKOPCICEXPONENTIALDRIFTMODEL: {
                m = new MemristorYakopcic();
            }
            break;
            case SIMMONSMODEL: {
                m = new MemristorSimmons();
            }
            break;
            case MODIFIEDSIMMONSMODEL: {
                m = new MemristorModifiedSimmons();
            }
            break;
            case TEAMMODEL: {
                m = new MemristorTEAM();
            }
            break;
            case MIMTHRESHOLDMODEL: {
                m = new MemristorMIMThreshold();
            }
            break;
            case OXIDEBASEDMODEL: {
                m = new MemristorOxideBased();
            }
            break;
            case MICHIGANSPICEMODEL: {
                m = new MemristorMichiganSpice();
            }
            break;
            case ASICMODEL: {
                m = new MemristorASiC();
            }
            break;
            default:{
                m = null;
            }break;
        }
        return m;
    }
}
