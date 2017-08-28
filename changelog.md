# Changelog from version v1.8.5
    * The Repository has been cleaned
    * Will have Eclipse (prior JavaFX installation) import scripts/project files

# Version 1.x.y


* **TO DO IN NEXT VERSION** order by priority: v2.0
    * **Set instance going ON/OFF dependent on control variable?**
    * ENUM in voltage stages
    * Complex csv files import
    * Poisson distributions


## Version 1.8.y

    * Changelog from version v1.8.5
        * The Repository has been cleaned
        * Will have Eclipse (prior JavaFX installation) import scripts/project files

    * Changelog from version v1.8.2-1.8.4b
        * **aSiC model: aSiC device correctly fitted**

    * Changelog from version v1.8.1
        * **To do** FIT HRS with log, LRS linearly
        * **aSiC model: under development**

    * Changelog from version v1.8.0
        * **To do** negative pulse?
        * **aSiC model: under development**

## Version 1.7.y


* Changelog from version v1.7.7b
    * **To do** negative pulse?
    * **fgarcia model: under development**
    * PulseVoltage Class minor bug has been fixed.

* Changelog from version v1.7.7
    * **To do** negative pulse?
    * **fgarcia model: under development**

* Changelog from version v1.7.6
    * **Oxide-based model: working**
    * JFreecharts plotting improvements

* Changelog from version v1.7.5
    * **Oxide-based model: SET is working**
    * problems with RESET operation


* Changelog from version v1.7.4
    **Oxide-based model: working without kinetics**

* Changelog from version v1.7.3
    * Windows gnuplot
    * Montecarlo compliance current bug solved

* Changelog from version v1.7.2
    * Save log to file, not only show the messages in the main window.
    **Yakopcic model: b-> b_1 and b_2**


* Changelog from version v1.7.1
    * Revert last steps (**recover mechanishm**):
        * **Can increase?? review if not coming from recovering state**
        * Memristor: each magnitude has a tempBuffer.
        * InputVoltage: partialCount and currentVoltage have a tempBuffer.
        * Timing: tStep, timeCounter and stepCount have a tempBuffer.
    * **Rm(v) graphs in gnuplot**.
    * Bug fixes

* Changelog from version v1.7.0
    * **Last always saved**
    * CSVReader bug fixes
    * All InputVoltage classes have been adapted to the new estimation method.
    * Fixed pulse ram voltage
    * **For each csv, plot error with all threads**
    * **Fitting error defined as quadratic error**
    * XYSegment class has been added to handle monotony changes in the magnitudes.
    * Improved fitting due to XYSegment class.
    * **A step is accepted in MemristorModel Class, not in subclass, and it is defined by a control variable**
    * Full support **Implement better integration algorithms.**
    * Full support **Optimize models, creating auxiliary variables for magnitudes.**

## Version 1.6.y

* Changelog from version v1.6.1
    * **Better integration algorithms:**
        * Improved system, using jdk-8 lamda functionalities.
        * RungeKutta algorithm included.
    * Thread blocks are on the fly created as the simulations run, reducing the RAM used.
    * First attempt of using auxiliary Magnitudes/Parameter variables:
        * **Yakopcic**
        * **Non-Linear drift**
    * Improve how threads are cleaned from memory.

* Changelog from version v1.6.0
    * Stores always v(t=0).
    * Plotting functionality separated from thread manager.
    * While fitting, curves are divided in different segments.
    * Yakopcic model improved.

## Version 1.5.y

* Changelog from version v1.5.9
    * Memristor variations creation improved.
    * InputVoltage Class do not require from that.
    * **Yakopcic's model improved**
    * Imports one simulation results at a time while fitting.
    * Improved fitting algorithm. **Needs to be improved also, see to do list**

* Changelog from version v1.5.8
    * No need of altering memristor models changing arraylist by arrays.
    * Threads and magnitudes take this into account.
    * plot scenarios that met the measurements.
    * Revised error calculus.
    * Improvement in File I/O methods.
    * Improvements in thread managers

* Changelog from version v1.5.7c
    * Load Corners from file
    * Logging improvements

* Changelog from version v1.5.6
    * Fitting improvements
    * Fitting export scenario
    * Corners automated generation
    * GnuPlot plotter improvements
    * Allow avoiding simulation plots, plotting only the fitted curves

* Changelog from version v1.5.5
    * Bug fixes
    * I/O improvements
    * Error in %

* Changelog from version v1.5.4
    * Better write to disk buffer system
    * Complete GnuPlot support
    * Bug fixes

* Changelog from version v1.5.3
    * Full GnuPlot support

* Changelog from version v1.5.2
    * Threads are computed in blocks of 1024 concurrent simulations.
    * Bug fixes.
    * Using Enums instead single ints
    * GnuPlot script to plot data fitting measures
    * Warn about tabs

* Changelog from version v1.5.1
    * Bug fixes.
    * Value list generation.
    * Fit to individual cycles

* Changelog from version v1.5.0
    * Complain current during simulations

## Version 1.4.y

* Changelog from version v1.4.8
    * First best simulation-measures fit implementation.
    * Thread Managers improved

* Changelog from version v1.4.7
    * Thread Managers improved
    * Complex csv files import

* Changelog from version v1.4.6
    * Timing management improvements
    * Corners analysis

* Changelog from version v1.4.5
    * Pulses ramp voltage incorporated

* Changelog from version v1.4.4
    * Imports simple CSV v-i measurements files

* Changelog from version v1.4.3
    * Memristor Model Parameter Presets support

* Changelog from version v1.4.2
	* JFreeChart and JavaFX integration

* Changelog from version v1.4.1
	* Starting GUI migration to JavaFx, Java8 required

* Changelog from version v1.4.0
	* Starting GUI migration to JavaFx, Java8 required
