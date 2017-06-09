
# Memristor Application Framework

*   The Memristor
*   Memristor Framework
*   How to
*   Journals & Conference Papers
*   Source Code & Collaboration
*   Binary Download & Contact
*   [RRAM Spice Compact Model](../memristor_spice_model#tabHeader_2)


## The Memristor

In 1971 Leon Chua postulated the existence of a fourth component that would complete the set of classical circuit  
elements consisting of resistors, capacitors and inductors [1].

The memristor device relates the flow and the charge by the memristor representative magnitude: the <span class="color1">memristance</span>.

After decades of study of many memristance behaviors, in 2008 the Hewlett Packard Laboratories presented a physical circuit device that responds to the equations of memristor [2], changing its internal resistance according to the current that flows through the component. Due to its reduced size memristors can be used instead of some CMOS structures, decreasing the used area, and resulting from the utilization of less transistors, brings a lower power consumption. Both area and power are critical factors in the current microelectronics, thus, the memristor receives today great attention. After the HP’s memristor fabrication numerous research groups have been able to manufacturate similar devices [3]-[5].

![memristor](http://www.die.upm.es/sites/default/files/memristor/69461704.PNG)

Related to these advances, important efforts have been made on providing different memristor models that allow their study and simulation. A wide range of models contemplates the behavior of the memristors in a more complex or simpler  
way, depending on the purpose of modeling. The high non-linearity of the memristor involves that apart from the inherited characteristics of the model, we should take into account several parameters pertaining to the simulator that can affect the computation. Therefore, a complete analysis and simulation of memristive devices is needed.


### References

<address>[1] L. O. Chua, "Memristor, The Missing Circuit Element," IEEE Transactions on Circuit Theory, vol. 18, 1971.  
[2] D. B. Strukov, G. S. Snider, D. R. Stewart, and R. S. Williams, "The missing memristor found." Nature, vol. 453, no. 7191, pp. 80-3, May 2008\. [Online]. Available: http://www.ncbi.nlm.nih.gov/pubmed/18451858  
[3] J. Borghetti, G. S. Snider, P. J. Kuekes, J. J. Yang, D. R. Stewart, and R. S. Williams, "Memristive switches enable stateful logic operations via material implication," Nature, vol. 464, no. 7290, pp. 873-876, 2010\. [Online]. Available: http://dx.doi.org/10.1038/nature08940  
[4] O. Jeong-Hoon, R. Kyung-Chang, J. Sunghun, P. Byung-Gook, and O. Kyung Seok, "An access-transistor-free resistive random access memory (RRAM) using a GST/TiO2 stack and its novel access mechanism," in Electron Devices and SolidState Circuits EDSSC 2010 IEEE International Conference of, 2010, pp. 1-4\. [Online]. Available: http://ieeexplore.ieee.org/xpls/abs all.jsp?arnumber=5713732  
[5] N. Gergel-Hackett, B. Hamadani, B. Dunlap, J. Suehle, C. Richter, C. Hacker, and D. Gundlach, "A Flexible Solution-Processed Memristor," IEEE Electron Device Letters, vol. 30, no. 7, pp. 706-708, Jul. 2009\. [Online]. Available: http://ieeexplore.ieee.org/lpdocs/epic03/wrapper.htm?arnumber=561634  
[6] W. Cai, F. Ellinger, S. Member, R. Tetzlaff, and T. Schmidt, "Abel Dynamics of Titanium Dioxide Memristor Based on Nonlinear Ionic Drift Model," arXiv:1105.2668v1, pp. 1-5, 2011.  
[7] K. Zaplatilek, "Memristor modeling in MATLAB R &Simulink R ," in Proceedings of the 5th European Computing Conference, 2011, pp. 62-67.</address>


## Memristor Framework

Memristor Framework is an automated tool for memristor model characterization and subcircuit generation, Memristor Application Framework, MAF; whose main contributions are:

1.  Implementation of the most important and referenced models in literature.
2.  Characterization of the memristor response to a given input, analysis of the simulations convergence problems and study of the device data storage speed.
3.  Given a circuit with voltage feeding and timing constrains, the framework aids the designer to choose the memristor model and parameters that fulfill the system constraints.
4.  Automated generation of subcircuit and data files. This allows the interaction with the desired circuit simulator and external mathematical software, acting as a quick validation and time saving tool.
5.  Capability of handling PVT variations, studying scenarios which affect the memristor behavior.

![](http://www.die.upm.es/sites/default/files/memristor/maf-overview.png)



This multi-platform support framework includes a static analysis tool that performs a design space exploration of the different memristor models and their parameters. Also, dynamic simulation responses are studied by the dynamic analysis module, guaranteeing the functionality and performance of a design. Moreover, this module fulfills a corner analysis of the main parameters of the memristor. That way the framework allows the exhaustive study of the binary or multilevel memristors behavior along the time and varying circuit parameters. Consequently our framework reinforces and complements the user-selected (SPICE, Spectre) main simulator acting as a quick validation and time saving tool.

![](http://www.die.upm.es/sites/default/files/memristor/main_window3.png)


### Memristor Framework Examples


![V-I analysis including parameter variations](http://www.die.upm.es/sites/default/files/memristor/dc1.png)

Device characterization Monte Carlo simulation, i(v) curve tab.


![](http://www.die.upm.es/sites/default/files/memristor/dc3.png)

Monte Carlo transient simulation. The graph shows the memristance evolution along the time, considering parameter variations.


![](http://www.die.upm.es/sites/default/files/memristor/lc2.png)


Multi-level characterization including Monte Carlo simulation. Time- to reach specific levels histogram, in an scenario with parameter variations.

Each value refers the time needed to perform a multi-level write operation.


# How To Index

*   [How To Run MAF](#run_maf)
*   [The Program](#program_maf)
*   [Example of Use](#example_maf)




# Running Memristor Application Framework

You can run MAF by executing

    * java -jar MAF_x.jar

Additionally, the program performance can be increased specifying the available amount of RAM using **-Xms** and **-Xmx**.  
-Xms specifies the minimum heapsize and -Xmx specifies the maximum size.

    * java -jar -Xmx2048m -Xms1024m MAF_x.jar

# The Program

## Main Window

[![Main Window](../Image?imageId=20&size=1)](../Image?imageId=20&size=2)

**Device Characterization** and **Level Characterization** simulators can be invoked through this window.

### Menus

As seen in the image bellow, we can select here the couple simulator-memristor model, or invoke the program help.

### Output History

A key area in the Main Window is the logging history, the place where we can analyze the program output.

## Simulator Windows

[![Simulator Window](../Image?imageId=21&size=1)](../Image?imageId=21&size=2)

### Parameter Tabs

For **Device Characterization** and **Level Characterization** simulators. As can be seen in the figure below, we control the **Timing parameters, Input signal parameters** and the **Model parameters** through the different tabs. In each one, we can select whether a parameter is fixed or it suffers from variation. Depending on the simulation mode (Monte Carlo or Corner analysis), the variation is given by a deviation from the main value or a list of values.

### Simulation Tab

As shown in the figure, in this tab we can choose the simulation title and the desired analysis (**Monte Carlo** or **Corner analysis**). If a **Monte Carlo** analysis is choosen, we can specify the number of simulations which are going to be run. Also, we can select if we want to simulate and plot the results, or only simulate and store the data for later analysis. In the **Data management** section, as users we can select to store directly the transient results in the hard-disk, or by contrast we choose a memory/hard-disk buffered mode.  
Additionally, we can reduce the amount of saved data.

## Results Windows

[![Results Window](../Image?imageId=22&size=1)](../Image?imageId=22&size=2)

Results windows are composed by several dynamic plots according the magnitudes/state variables simulated. Those plots include several tools. Using the mouse scroll/secondary buttons the user can resize, zoom, print... the represented data.

## Subcircuit Editor Window

[![Subcircuit Editor](../Image?imageId=23&size=1)](../Image?imageId=23&size=2)

### Menu

Accessed by the menu, the user can load the SPICE/Spectre subcircuit of the simulated model. Additionally, clicking the **How to** menu the user access the module help. Finally, using **Save to** entry, the user can save the loaded subcircuit.

### Subcircuit Editor

The loaded module is shown in the editor area. As an added value, the user can alter the loaded subcircuit before being saved to the hard-disk.

# Example of use

To show the framework capabilities we present a simple case of use: we will perform an 8-level writing operation in a 3x3 Resistive RAM (RRAM) crossbar array as shown in the figure.

[![RRAM Crossbar array](../Image?imageId=15&size=1)](../Image?imageId=15&size=2)

In this crossbar scheme, the writing operation involves two different pulse generators and nine cells, each composed of one memristor. Each memristor cell can be modified to store eight different resistance values, from 0 to 7, linearly separated. Initially, all cells have a value of “0”. Rows and columns are numbered from 1 to 3\. In this example, the desired operation will write a “7” in the second cell of the second row of the RRAM array (position [2, 2]). Crossbar array structures have structural problems. The main one refers to the spurious voltage which feeds the cells placed next to the selected RRAM. These voltages can modify the state variables in non- desired cells, and consequently, alter the stored values. In the circuit scheme, the target cell is filled with a striped pattern and cells that are sensitive to data alterations are filled with a dotted pattern.

The purpose of this example is twofold:

1.  Determining all the parameters to perform the full state change —including feeding voltage and timing characteristics.
2.  Analyzing the impact on the non-selected cells when the writing operation is performed.

Following the proposed methodology, first we define the design constraints. In our example we will assume that the circuit power supply limits the maximum voltage with which the memristor is fed, besides we set a limit in the duration of the writing operation, thus we have:

1.  Voltage supply should not exceed 2V .
2.  The operation should be performed in 15ns.

The next step is selecting the most appropriate memristor model. We will use Yakoptic et al. model with the default parameters because of its good trade-off between its accuracy and computational load, as well as the presence of a threshold in its behavior, which will help minimizing the cell data corruption effect. We proceed to simulate in the MAF Device Characterization module different transient simulations to set the feeding voltage, taking into account that the model must be fast enough to perform the required operation.

[![State Variable at different voltages](../Image?imageId=16&size=1)](../Image?imageId=16&size=2)

The previous figure shows simulation results. As can be seen, with 1.75V we can completely alter the state variable (bounded between “1” and “0”), and therefore, reach with this writing voltage either maximum/minimum memristance values.

Performing a Monte Carlo analysis with the MAF Device Characterization module we study how the variation of the feeding voltage and other parameters affects the maximum storage levels (_Delta w_) and the writing operation length (_Delta T_). The following figure shows an example of the evolution of the state variable w along the time. In the detailed view both _Delta T_ and _Delta w_ are presented. Evaluating those values we are able to refine the parameters of the memristor model and estimate the variability of the device.

[![evolution of the state
						variable w along the time](../Image?imageId=17&size=1)](../Image?imageId=17&size=2)

Focusing on the pulse length of the writing operation, we carry out another Monte Carlo simulation using the MAF Level Characterization module. The histogram shows the pulse lengths occurrences required to perform the multi-level storage over 250 process scenarios. Based on this information, a designer is able to determine the operation security margins.

[![MC results](../Image?imageId=18&size=1)](../Image?imageId=18&size=2)

After all the memristor variables have been settled, as well as the operation voltage and timing are chosen, using the Subcircuit Editor module we automatically generate the corresponding SPICE subcircuit and proceed to include it in the global netlist.

At this stage, we can verify in SPICE that the non-desired effects of a spurious alteration of previously stored data in adjacent cells do not appear. The last figure displays the writing operation accomplished in both the target cell and an adjacent cell. As shown, the previous value stored in the adjacent cell is almost unaltered —notice the change in the left y axis— thus the impact is negligible.

[![SPICE simulation results](../Image?imageId=19&size=1)](../Image?imageId=19&size=2)



# Journal Papers

1.  [Building Memristor Applications: From Device Model to Circuit Design.](http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6868984) Fernando García-Redondo, Marisa López-Vallejo, Pablo Ituero. IEEE Transactions on Nanotechnology. 2014\. Accepted for publication. Impact Factor 2012: 1,80.
2.  <span class="color1">[The tractability index of memristive circuits: branch-oriented and tree-based models](http://oa.upm.es/10286/1/tractmem1.pdf)</span>. García Redondo, Fernando and Riaza, Ricardo. Mathematical Methods in the Applied Sciences. 201\. Impact Factor 2012: 0.743

### Conference Papers

1.  <span class="color1">Model Validation and Simulation Framework for Novel Nanometer Devices</span>. Fernando García Redondo, Marisa López-Vallejo, Pablo Ituero. Conference on Design of Circuits and Integrated Systems 2012 (DCIS 2012) Avignon (France).
2.  <span class="color1">A CAD Framework for the Characterization and Use of Memristor Models</span>. Fernando García, Marisa López-Vallejo, and Pablo Ituero. International Conference on Synthesis, Modeling, Analysis and Simulation Methods and Applications to Circuit Design 2012 (SMACD2012) Seville, Spain. 19 - 21 September 2012.



# Source Code & Collaboration

Proven as useful tool, MAF source code is going to be opened to the research community.  
The download, execution and reproduction of this software is only allowed for academic use.

## Public Repository

[A Bitbucket repository has been set up.](https://bitbucket.org/fgr1986/maf)

We would be glad to have you working side by side with us improving and expanding this tool.

Software Requirements:

*   Java 1.6/1.7/1.8 is required.
*   However, if java command is not in the PATH, you can set it following the instructions located in [http://java.com/en/download/help/path.xml](http://java.com/en/download/help/path.xml).
*   SWT libraries installed, configured and in PATH.
*   Ant.

Skills needed:

*   Software developers.
*   Memristor model developers.
*   Memristor based circuit designers.
*   Memristor researchers: ideas and software testing.

If you are interested, please, contact with fgarcia@die.upm.es


# Memristor Framework Binary Download

Windows 64 bits specific binary, downloadable for academic purposes [from here](../maf_v1.3_winx64_swt.zip).  
The download, execution and reproduction of this software is only allowed for academic use.

Requirements:

*   Java 1.6/1.7/1.8 is required.
*   However, if java command is not in the PATH, you can set it following the instructions located in [http://java.com/en/download/help/path.xml](http://java.com/en/download/help/path.xml).
*   SWT libraries installed, configured and in PATH.

The SWT included libraries for winx64 architectures.  
For other OS, just include all the required libraries in the PATH.

To run MAF:

*   java -jar MAF_X.jar
*   Recommended: java -jar -Xmx2048m -Xms1024m MAF.jar

Password:
v1.3. To get the password, please, contact with fgarcia@die.upm.es

#License:

Copyright @ 2015 Fernando García-Redondo, Technical University of Madrid The MIT Licence Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: 1\. The users agree not to charge for the code itself but may charge for additions, extensions, or support. 2\. In any product based on the Software, the users agree to acknowledge the Research Group and/or the main author that developed the software. This acknowledgment shall appear in the product documentation. 3\. In case that any publication results from the use of the original/modified software, acknowledgement to the main author and research group shall appear in the document. 4\. The users agree to reproduce any copyright notice which appears on the software on any copy or modification of such made available to others. The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. Agreed to by Fernando García-Redondo November 13, 2015


##[RRAM Spice Compact Model](../memristor_spice_model#tabHeader_2)

RRAM Compact Model will be downloadable for academic purposes [from here](../memristor_spice_model#tabHeader_2).  
The download, execution and reproduction of this software will only be allowed for academic use.

Compatible Circuit Simulators

*   Cadence Spectre
*   LTSpice and similar
*   Verilog-A simulators
