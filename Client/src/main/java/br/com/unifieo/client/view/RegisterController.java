/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.client.view;

import br.com.unifieo.client.control.LoginControl;
import static br.com.unifieo.client.main.Main.stage;
import br.com.unifieo.common.util.MessagesProperties;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Vinicius
 */
public class RegisterController extends Controller implements Initializable {

    @FXML private AnchorPane pane;
    @FXML private TextField txtLogin;
    @FXML private PasswordField pwdPassword;
    @FXML private PasswordField pwdConfirmPassword;
    
    private LoginControl loginControl;
    
    public RegisterController() {
        loginControl = new LoginControl();
    }    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initRootPane(pane);
        txtLogin.setPromptText(MessagesProperties.getKey("msg.login"));
        pwdPassword.setPromptText(MessagesProperties.getKey("msg.password"));
        pwdConfirmPassword.setPromptText(MessagesProperties.getKey("msg.cofirm_passowrd"));
        
        txtLogin.focusedProperty().addListener((observable, oldValue, newValue) -> {focusValidate(txtLogin, newValue);});
        pwdPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {focusValidate(pwdPassword, newValue);});
        pwdConfirmPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {focusValidate(pwdConfirmPassword, newValue);});
    }
    
    @FXML
    private void register() {
        if (!(txtLogin.getText().isEmpty() || pwdPassword.getText().isEmpty() || pwdConfirmPassword.getText().isEmpty())) {
            String password = pwdPassword.getText();
            String confirmPassword = pwdConfirmPassword.getText();
            String login = txtLogin.getText();
            
            try {
                if (loginControl.registerUser(login, password, confirmPassword)) {
                    Dialogs.create()
                        .owner(stage)
                        .title(MessagesProperties.getKey("msg.label.information"))
                        .masthead(MessagesProperties.getKey("msg.operation_success"))
                        .message(MessagesProperties.getKey("msg.data_register"))
                        .showInformation();
                    goToScreen("login");
                }
            } catch (Exception ex) {
                Dialogs.create()
                    .owner(stage)
                    .title(MessagesProperties.getKey("msg.label.attention"))
                    .masthead(MessagesProperties.getKey("msg.data_incorrect"))
                    .message(ex.getMessage())
                    .showError();
            }
            
        }
    }    
    
    @FXML 
    private void back() {
        goToScreen("login");
    }
    
    @FXML
    private void exit() {
        System.exit(0);
    }
    
}
