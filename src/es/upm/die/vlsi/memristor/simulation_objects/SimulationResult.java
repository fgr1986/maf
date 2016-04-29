package es.upm.die.vlsi.memristor.simulation_objects;

public class SimulationResult {

    public enum states {
        CORRECT, BAD_SET_UP, SIMULATION_ERROR, SIMULATION_WARNING
    };

    private states state;
    private int finalStepComputed;
    private String message;
    private String consoleMessage;

    public SimulationResult(states state, int finalStepComputed, String message, String consoleMessage) {
        super();
        this.state = state;
        this.finalStepComputed = finalStepComputed;
        this.message = message;
        this.consoleMessage = consoleMessage;
    }

    public SimulationResult(states state, int finalStepComputed, String message) {
        super();
        this.state = state;
        this.finalStepComputed = finalStepComputed;
        this.message = message;
    }
    
    public boolean allCorrect(){
        return state==states.CORRECT || state==states.SIMULATION_WARNING ;
    }
    
    public states getState() {
        return state;
    }

    public void setState(states state) {
        this.state = state;
    }

    public int isFinalStepComputed() {
        return finalStepComputed;
    }

    public void setFinalStepComputed(int finalStepComputed) {
        this.finalStepComputed = finalStepComputed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCompleteMessage() {
        String msg;
        switch(this.state) {
            case CORRECT: {
                msg = "Correct simulation stage.\nStep: " + finalStepComputed + " \n" + message;
            }
            break;
            case BAD_SET_UP: {
                msg = "Incorrect simulation set up.\nStep:0\n" + message;
            }
            break;
            case SIMULATION_ERROR: {
                msg = "Simulation error.\nStep: " + finalStepComputed + " \n" + message;
            }
            break;
            case SIMULATION_WARNING: {
                msg = "Simulation computed correctly but with warnings:\nStep: " + finalStepComputed + " \n" + message;
            }
            break;
            default:{
                msg = "Error in simulation stage. \nStep: " + finalStepComputed
                    + " \n\n" + message + "\n\n Please, refine the parameters.";
            }
                break;
        }
        return msg;
    }

    public String getConsoleMessage() {
        return consoleMessage;
    }

    public void setConsoleMessage(String consoleMessage) {
        this.consoleMessage = consoleMessage;
    }

    public void appendConsoleMessage(String newMessage) {
        this.consoleMessage += newMessage;
    }
}
