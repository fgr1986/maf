# Memristor Application Framework

# Version
> v1.8.6, 05/09/2017

# Author:
* [Fernando García-Redondo](http://www.fernandeando.com/profile.html)
* [fgarcia@die.upm.es](mailto:fgarcia@die.upm.es)

# License:
## **Creative Commons Attribution-NonComercial-ShareAlike**
* Copy and distribution allowed for academical/research purposes.
* Modification allowed prior author authorization.

# Documentation and Downloads
[http://vlsi.die.upm.es/memristor](http://vlsi.die.upm.es/memristor)

# Academic Papers
## Journal Papers
* **[Building Memristor Applications: From Device Model to Circuit Design](http://dx.doi.org/10.1109/TNANO.2014.2345093).** Fernando García-Redondo, Marisa López-Vallejo, Pablo Ituero. IEEE Transactions on Nanotechnology. 2014. Accepted for publication. Impact Factor 2012: 1,80.
* **[The tractability index of memristive circuits: branch-oriented and tree-based models](http://oa.upm.es/10286/1/tractmem1.pdf).** García Redondo, Fernando and Riaza, Ricardo. Mathematical Methods in the Applied Sciences. 201. Impact Factor 2012: 0.743

## Conference Papers
* **Model Validation and Simulation Framework for Novel Nanometer Devices.** Fernando García Redondo, Marisa López-Vallejo, Pablo Ituero. Conference on Design of Circuits and Integrated Systems 2012 (DCIS 2012) Avignon (France).
* **[A CAD Framework for the Characterization and Use of Memristor Models](http://dx.doi.org/10.1109/SMACD.2012.6339408).** Fernando García, Marisa López-Vallejo, and Pablo Ituero. International Conference on Synthesis, Modeling, Analysis and Simulation Methods and Applications to Circuit Design 2012 (SMACD2012) Seville, Spain. 19 - 21 September 2012.

# Changelog:
[https://github.com/fgr1986/maf](https://github.com/fgr1986/maf)

# Repository
[https://github.com/fgr1986/maf](https://github.com/fgr1986/maf)


# Requirements
* Java 8
* JavaFx (already included in Java 8)
* GnuPlot 4.x
* [Optional] Jfreechart libraries installed and configured
* [Optional] Ant/Maven

## Requirements installation: Linux.
* Download OpenJDK-8. In Ubuntu/Debian/Mint
```
	sudo apt install openjdk-8-jdk openjdk-8-doc
```
* Download open-javafx. In Ubuntu/Debian/Mint
```
sudo apt install openjfx
```

## Requirements installation: Windows.
* Download java 8 JDK (development) JRE (run) from Oracle web site
* Download javafx from Oracle web site

# How to build
* **Option 1)** Import the project in Eclipse. This will use:
	* *.project* file
	* *.classpath, build.xml and build.fxbuild* files
* **Option 2)** Configure and import the ant build file *build.xml*
* **Option 3)** Netbeans/Eclipse IDE
	* Create a new JavaFx Project
	* Add the sources *src*
	* Configure the **BUILDPATH** and **PATH**: add the libraries in the *lib* folder to the PATH

# Project Structure

```
maf.........................[root]
    * src...................[sources ]
    * lib...................[JAR libraries]
    * dist..................[executable jars]
    * measures..............[examples of measures]
    * conf_files............[examples of configuration profiles for models]
    * icons.................[icons]
    * legacy_src_binaries...[deprecated MAF sources and binaries]
    * .project..............[eclipse project file]
    * .classpath
    * build.xml
    * build.fxbuild
    * changelog
    * LICENSE
    * README
```

# How to run
substitute 'X' with the required version

		java -jar MAF_X.jar

## Increase RAM usage (improves performance)
-Xms specifies the minimum heapsize and -Xmx specifies the maximum size.

		java -jar -Xmx2048m -Xms1024m MAF_X.jar
