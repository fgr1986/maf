

#pdf
# set term pdfcairo size 20cm,15cm font "Times-New Roman,18"
# set output "n_tx_sizes.pdf"


# wxt
# set terminal wxt size 410,250 enhanced font 'Verdana,9' persist
# png
# set terminal pngcairo size 1200,800 enhanced font 'Verdana,9'
# set output "output.png"

# gnuplot -e "filename='foo.data'" foo.plg

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
#set style line 1 lc rgb '#0060ad' lt 1 lw 2 pt 6 
set style line 1 lc rgb '#0060ad' lt 1 lw 1 pt 0 

set palette defined ( 0 "#4d69ab", 50 "#ffd35a", 100 "#ed2c29")
set object 1 rect from screen 0.0, 0.0, 0 to screen 1, 1, 0 behind 
set object 1 rect fc rgb "white" fillstyle solid 1.0 noborder
set key top left
# margins and so on
set autoscale  
set offset graph 0.1, graph 0.1, graph 0.1, graph 0.1

# rectanges without border
set style rect fc lt -1 fs solid 0.10 noborder
# shadow graph
set obj 1 rect from graph 0, graph 0 to graph 1, graph 1 fs solid 0.15 fc rgb "#ececec" behind

unset colorbox
unset key
plot for [file in filenames] file using 2:3 every ::1 w lp ls 1

set output logOutputFile
set logscale y

# plot for [i=1:FILES] FILE(i)
plot for [file in filenames] file using 2:(abs($3)) every ::1 w lp ls 1
unset output
quit