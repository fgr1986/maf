package es.upm.die.vlsi.memristor.math;

public class FittingCurvesResult {

    private boolean correctlyFitted;
    private int scenarioIndex;
    private double absError;
    private double minX;
    private double maxX;
    private String message;
    private String parameterValues;

    public FittingCurvesResult(boolean correctlyFitted, int scenarioIndex, double absError,
            double minX, double maxX, String message) {
        this.correctlyFitted = correctlyFitted;
        this.scenarioIndex = scenarioIndex;
        this.absError = absError;
        this.minX = minX;
        this.maxX = maxX;
        this.message = message;
        parameterValues = "";
    }

    public String getCompleteMessage() {
        if (correctlyFitted) {
            return "Correct fitting stage.\n\tBest set: " + scenarioIndex
                    + " \n\tabsError: " + absError
                    + " \n\tX considered range: [" + minX
                    + ", " + maxX + "]"
                    + " \n\tmessage: " + message
                    + " \n\tparameter values: " + parameterValues;
        } else {
            return "Error in fitting stage.\n\tBest set: " + scenarioIndex
                    + " \n\tabsError: " + absError
                    + " \n\tX considered range: [" + minX
                    + ", " + maxX + "]"
                    + " \n\tmessage: " + message
                    + " \n\tparameter values: " + parameterValues;
        }
    }

    public String getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(String parameterValues) {
        this.parameterValues = parameterValues;
    }

    public boolean isCorrectlyFitted() {
        return correctlyFitted;
    }

    public void setCorrectlyFitted(boolean correctlyFitted) {
        this.correctlyFitted = correctlyFitted;
    }

    public int getScenarioIndex() {
        return scenarioIndex;
    }

    public void setBestSet(int scenarioIndex) {
        this.scenarioIndex = scenarioIndex;
    }

    public double getAbsError() {
        return absError;
    }

    public void setAbsError(double absError) {
        this.absError = absError;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
