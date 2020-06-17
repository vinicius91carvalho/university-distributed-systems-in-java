/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.view;

import br.com.caelum.javafx.controller.Vitrine;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author Vinicius
 */
public class LoginApp extends Application {

    private AnchorPane anchorPane;
    private TextField txtField;
    private PasswordField pwdField;
    private Button btnEntrar;
    private Button btnSair;
    private static Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {        
        LoginApp.stage = stage;
        
        initComponents();
        initListeners();
        
        Scene scene = new Scene(anchorPane);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Login - GolFX");
        stage.show();
        
        initLayout();
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void initComponents() {
        anchorPane = new AnchorPane();
        anchorPane.setPrefSize(400, 300);        
        
        txtField = new TextField();
        txtField.setPromptText("Digite aqui o seu login");              
        
        pwdField = new PasswordField();
        pwdField.setPromptText("Digite aqui a sua senha");
        
        btnEntrar = new Button("Entrar");
        btnSair = new Button("Sair");
        
        anchorPane.getChildren().addAll(txtField, pwdField, btnEntrar, btnSair);             
    }
    
    private void initLayout() {
        anchorPane.getStyleClass().add("pane");
        
        txtField.setLayoutX((anchorPane.getWidth() - txtField.getWidth()) / 2);
        txtField.setLayoutY(50);
        pwdField.setLayoutX((anchorPane.getWidth() - pwdField.getWidth()) / 2);

        pwdField.setLayoutY(100);
        
        btnEntrar.setLayoutX((anchorPane.getWidth() - btnEntrar.getWidth()) / 2);
        btnEntrar.setLayoutY(150);
        btnEntrar.getStyleClass().add("btnLogin");
        btnEntrar.setEffect(new InnerShadow());
        
        btnSair.setLayoutX((anchorPane.getWidth() - btnSair.getWidth()) / 2);
        btnSair.setLayoutY(200);
        btnSair.getStyleClass().add("btnLogin");
        
        //anchorPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, white 0%, silver 100%);");
    }
    
    private void initListeners() {
        btnSair.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                fecharAplicacao();
            }
            
        });
        
        btnEntrar.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                logar();
            }

        });
    }
    
    private void logar() {
        if (txtField.getText().equals("admin") &&
                pwdField.getText().equals("casadocodigo")) {
            try {
                new VitrineApp().start(new Stage());
                LoginApp.getStage().close();
            } catch (Exception ex) {
                Logger.getLogger(LoginApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else {
            JOptionPane.showMessageDialog(null, "Login e/ou senha invalidos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void fecharAplicacao() {
        System.exit(0);
    }
    
    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        LoginApp.stage = stage;
    }
}
