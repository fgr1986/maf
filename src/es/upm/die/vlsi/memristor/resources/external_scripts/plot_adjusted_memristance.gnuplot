

#pdf
# set term pdfcairo size 20cm,15cm font "Times-New Roman,18"
# set output "n_tx_sizes.pdf"


# wxt
# set terminal wxt size 410,250 enhanced font 'Verdana,9' persist
# png
# set terminal pngcairo size 1200,800 enhanced font 'Verdana,9'
# set output "output.png"

# gnuplot -e "filename='foo.data'" foo.plg
# measuresFile = '/home/fgarcia/local_workspace/memristor/measures/D-SiCCu-W-10-01-06-03-80um-02_6_9.txt'
# simulationFile = '/home/fgarcia/local_workspace/memristor/simulations/dc_montecarlo_Mon_Feb_23_15.51.32_GMT_2015_Non_Linear_Drift_memristor_model/thread_0.csv'

set term svg size 1200,900 fname 'Times' fsize 18
set output outputFile
set grid
set format x "%g"
set format y "%g"
set xlabel "Voltage [V]"
set ylabel "Current [A]"
set title  myTitle
set style line 11 lc rgb '#808080' lt 1
set tics nomirror

set style line 1 lc rgb '#a2142f' lt 1 lw 2 pt 6 # red
set style line 2 lc rgb '#7e2f8e' lt 2 lw 2 pt 0# purple

set palette defined ( 0 "#4d69ab", 50 "#ffd35a", 100 "#ed2c29")
set object 1 rect from screen 0.0, 0.0, 0 to screen 1, 1, 0 behind 
set object 1 rect fc rgb "white" fillstyle solid 1.0 noborder
set key top left
unset colorbox
# margins and so on
set autoscale  
set offset graph 0.1, graph 0.1, graph 0.1, graph 0.1

# rectanges without border
set style rect fc lt -1 fs solid 0.10 noborder
# shadow graph
set obj 1 rect from graph 0, graph 0 to graph 1, graph 1 fs solid 0.15 fc rgb "#ececec" behind
# shadow non-fitting region
set obj rect from graph 0, graph 0 to minX, graph 1 fs solid 1 fc rgb "#aaaaaa" behind
set obj rect from maxX, graph 0 to  graph 1, graph 1 fs solid 1 fc rgb "#aaaaaa" behind

# set style rect fc lt -1 fs solid 0.10 noborder
# set obj rect from minX, graph 0 to maxX, graph 1

set autoscale  y

plot measuresFile   using 1:($1/$2) every ::1 w lp ls 1  title 'Measures (Memristance)', \
     simulationFile using 2:(1e3*$2/$3) every ::1 w lp ls 2  title 'Simulation (Memristance)'

set output logOutputFile
plot measuresFile   using 1:(abs($1/$2)) every ::1 w lp ls 1  title 'Measures (Memristance)', \
     simulationFile using 2:(abs(1e3*$2/$3)) every ::1 w lp ls 2  title 'Simulation (Memristance)'

unset output
quit