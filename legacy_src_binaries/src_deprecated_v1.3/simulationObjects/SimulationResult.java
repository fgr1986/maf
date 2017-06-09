package simulationObjects;

public class SimulationResult {
	private boolean correct;
	private int finalStepComputed;
	private String message;
	private String consoleMessage;

	public SimulationResult(boolean correct, int finalStepComputed,
			String message, String consoleMessage) {
		super();
		this.correct = correct;
		this.finalStepComputed = finalStepComputed;
		this.message = message;
		this.consoleMessage = consoleMessage;
	}

	public SimulationResult(boolean correct, int finalStepComputed,
			String message) {
		super();
		this.correct = correct;
		this.finalStepComputed = finalStepComputed;
		this.message = message;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
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
		if (correct) {
			return "Correct simulation stage.\nStep: " + finalStepComputed
					+ " \n" + message;
		} else {
			return "Error in simulation stage. \nStep: " + finalStepComputed
					+ " \n\n" + message + "\n\n Please, refine the parameters.";
		}
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
