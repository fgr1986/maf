/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import es.upm.die.vlsi.memristor.resources.ResourcesMAF;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;
import es.upm.die.vlsi.memristor.simulation_objects.timing.Timing;

/**
 *
 * @author fgarcia
 */
public class TimingParametersUI extends StackPane {
    
    private final TextField tfMinTimeStep;
    private final TextField tfMaxTimeStep;
    private final TextField textFieldTransientLength;

    public TimingParametersUI() {

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(12);
        gridPane.setAlignment(Pos.CENTER);
        
        Label lbTiming = new Label("Timing Parameters:");
        lbTiming.setFont(ResourcesMAF.TITLEFONT );
        gridPane.add(lbTiming, 0, 0);
        
        // Transient
        Label labelTransientLength = new Label("Transient Lengh [s]:");
        ResizeLabel(labelTransientLength);
        gridPane.add(labelTransientLength, 0, 1);
        textFieldTransientLength = new TextField("1e0");
        ResizeTextField(textFieldTransientLength);
        gridPane.add(textFieldTransientLength, 1, 1);

        // Time step
        Label labelMinTimeStep = new Label("Min time step [s]:");
        ResizeLabel(labelMinTimeStep);
        tfMinTimeStep = new TextField("1e-8");
        ResizeTextField(tfMinTimeStep);
        Label labelMaxTimeStep = new Label("Max time step [s]:");
        ResizeLabel(labelMaxTimeStep);
        tfMaxTimeStep = new TextField("1e-5");
        ResizeTextField(tfMaxTimeStep);

        gridPane.add(labelMinTimeStep, 0, 2);
        gridPane.add(tfMinTimeStep, 1, 2);
        gridPane.add(labelMaxTimeStep, 2, 2);
        gridPane.add(tfMaxTimeStep, 3, 2);

        ScrollPane sp = new ScrollPane();
        sp.setContent(gridPane);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        this.getChildren().add(sp);
    }

    private void ResizeLabel(Label lb) {
        lb.setMinWidth(150.0);
//        lb.setPrefWidth(80.0);
//        lb.setMaxWidth(80.0);
    }

    private void ResizeTextField(TextField tf) {
        tf.setMinWidth(80.0);
        tf.setPrefWidth(80.0);
        tf.setMaxWidth(80.0);
    }

    public Timing getTiming() {
        Timing timing;
        try {
            // retrieve timing
            timing = new Timing( 0,
                    Double.parseDouble(textFieldTransientLength.getText()),
                    Double.parseDouble(tfMinTimeStep.getText()),
                    Double.parseDouble(tfMaxTimeStep.getText()) );
            if (timing.gettStep() >= timing.getTf()) {
                ErrorDialog("Timing error: define the time range of operation!");
                return null;
            }
        } catch (Exception exc) {
            ErrorDialog("Timing error!" + exc.getLocalizedMessage());
            return null;
        }
        return timing;
    }

    private void ErrorDialog(String message) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(new Group(new Text(25, 25, message)));
        dialog.setScene(scene);
        dialog.show();
    }

}
