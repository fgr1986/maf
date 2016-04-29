package es.upm.die.vlsi.memristor.ui;

import es.upm.die.vlsi.memristor.simulations.DCThreadManager;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class DCPlots {

    private final DCThreadManager dc;

    public DCPlots(DCThreadManager dc) {
        this.dc = dc;
        // tab pane
        final TabPane tabPane = new TabPane();
        tabPane.setSide(Side.TOP);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // charts
        for (JFreeChart lc : this.dc.getPlotter().getAllCharts()) {
            final Tab lcTab = new Tab();
            final StackPane lcSpane = new StackPane();
            lcTab.setText( lc.getTitle().getText() );
            lcTab.setContent(lcSpane);
            // Using java2d in javafx
            // Bind canvas size to stack pane size. 
//            ChartCanvas canvas = new ChartCanvas(lc);
            final SwingNode chartSwingNode = new SwingNode();
            chartSwingNode.setContent(  new ChartPanel(lc) );
            lcSpane.getChildren().add(chartSwingNode);
//            canvas.widthProperty().bind(lcSpane.widthProperty());
//            canvas.heightProperty().bind(lcSpane.heightProperty());
            tabPane.getTabs().add(lcTab);
        }

        final Stage stage = new Stage();
        stage.setTitle("Results");
        //create scene with set width, height and color
        Scene scene = new Scene(tabPane, 1000, 600, Color.WHITESMOKE);
        //set scene to stage
        stage.setScene(scene);
        //center stage on screen
        stage.centerOnScreen();
        //show the stage
        stage.show();
    }

}
