/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.die.vlsi.memristor.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author fgarcia
 */
public class DialogsManager {

    public void infoDialog(String message) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        StackPane sp = new StackPane();
        VBox vbox = new VBox(2);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(new Label(message, GetIcon("/es/upm/die/vlsi/memristor/resources/icons/info_icon.png")));
        Button bOk = new Button("OK");
        bOk.setOnAction((ActionEvent e) -> {
            dialog.close();
        });
        vbox.getChildren().add(bOk);
        sp.getChildren().add(vbox);
        Scene scene = new Scene(sp, 500, 100, Color.WHITESMOKE);
        dialog.setScene(scene);
        dialog.show();
    }

    public void errorDialog(String message) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        StackPane sp = new StackPane();
        VBox vbox = new VBox(2);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(new Label(message, GetIcon( "/es/upm/die/vlsi/memristor/resources/icons/info_icon.png")));
        Button bOk = new Button("OK");
        bOk.setOnAction((ActionEvent e) -> {
            dialog.close();
        });
        vbox.getChildren().add(bOk);
        sp.getChildren().add(vbox);
        Scene scene = new Scene(sp, 500, 100, Color.WHITESMOKE);
        dialog.setScene(scene);
        dialog.show();
    }

    public ImageView GetIcon(String imagePath) {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView iconView = new ImageView(image);
        iconView.setFitWidth(30);
        iconView.setFitHeight(30);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        iconView.setCache(true);
        return iconView;
    }
}
