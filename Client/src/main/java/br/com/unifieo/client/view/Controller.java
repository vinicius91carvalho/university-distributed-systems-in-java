/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.client.view;

import br.com.unifieo.client.main.Main;
import br.com.unifieo.common.util.LogUtils;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author Vinicius
 */
public class Controller {
    
    // Posições iniciais do painel
    private Double initX;
    private Double initY;
    
    /**
     * Método responsável por inicializar as operações do Painel que contém todos os componentes
     */
    protected void initRootPane(Pane pane) {
        
        // Adiciona evento de transição aos botões
        for (Node node : pane.getChildren()) {
            if (node instanceof Button) {
                addScaleTransitions(node, 1.050);
                ((Button) node).defaultButtonProperty().bind(((Button) node).focusedProperty());
            }
        }
        
        // Salva o estado atual do AnchorPane quando o mouse é pressionado
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                initX = event.getScreenX() - Main.stage.getX();
                initY = event.getScreenY() - Main.stage.getY();
            }            
        });
        
       // Quando o mouse é movido, seta as posições do painel dinamicamente
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.stage.setX(event.getScreenX() - initX);
                Main.stage.setY(event.getScreenY() - initY);
            }            
        });
        
    }
    
    public void goToScreen(String namePage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/"+namePage+".fxml"));
            Main.stage.setScene(new Scene(root, Color.TRANSPARENT));
        } catch (Exception ex) {
            LogUtils.exception("msg.error.change_screen", ex);
        }
    }
    
    public Boolean validateNodesByStyleClass(String styleClass, Node... nodes) {
        for (Node node : nodes) {
            if (node.getStyleClass().contains(styleClass)) {
                return false;
            }    
        }
        return true;
    }
    
    public void focusValidate(TextField textField, Boolean newValue) {
        if (newValue) {
            if (textField.getStyleClass().contains("not-empty"))
                textField.getStyleClass().remove("not-empty");
        } else {
            if (textField.getText().isEmpty() && !textField.getStyleClass().contains("not-empty")) {
                textField.getStyleClass().add("not-empty");
            }
        }
        
    }
    
    public void addScaleTransitions(Node node, Double dblScale) {
        node.setOnMouseEntered(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(350), node);
                scale.setToX(dblScale);
                scale.setToY(dblScale);
                scale.play();
            }
            
        });
        
        node.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(500), node);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });                
        
    }
        
    
}
