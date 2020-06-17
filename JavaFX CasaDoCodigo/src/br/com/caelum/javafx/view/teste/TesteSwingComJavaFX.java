/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.view.teste;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Vinicius
 */
public class TesteSwingComJavaFX extends JFrame {

    private static void initComponents() {
        JFrame frame = new JFrame("Tela com Swing + JavaFX");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(300,200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                initFX(fxPanel);
            }
            
        });        
    }
    
    private static void initFX(JFXPanel fxPanel) {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initComponents();
            }
        });
    }

    private static Scene createScene() {
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root);
        root.getChildren().add(new Label("Casa do Código!"));
        return scene;
        
    }
}
