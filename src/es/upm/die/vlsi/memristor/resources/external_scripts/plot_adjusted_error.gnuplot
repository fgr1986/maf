
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
set style line 1 lc rgb '#0060ad' lt 1 lw 2 pt 6 
set style line 2 lc rgb '#5e9c36' lt 2 lw 2 pt 0

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

# colors
# set style fill transparent solid 0.8 border lc rgb '#666666'
set style fill transparent solid 0.8 border
set boxwidth 0.38 relative
offset=0.38

unset xtics
set logscale y
#plot
#plot errorFile u 1:2:xtic(1) w boxes lc rgb"#5e9c36"  title'Fitting Error at each Scenario'
plot errorFile u 1:2:xtic(1) w lp ls 2  title'Fitting Error at each Scenario'

unset output
quit
