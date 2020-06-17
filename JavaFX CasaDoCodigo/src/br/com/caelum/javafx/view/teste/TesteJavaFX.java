/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.view.teste;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Vinicius
 */
public class TesteJavaFX extends Application {

    private Label label;
    private AnchorPane pane;
    private Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        initComponents();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    private void initComponents() {
        pane = new AnchorPane();
        label = new Label("Casa do Código!");
        pane.getChildren().add(label);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
