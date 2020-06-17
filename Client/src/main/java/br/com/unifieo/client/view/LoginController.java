package br.com.unifieo.client.view;

import br.com.unifieo.client.control.LoginControl;
import static br.com.unifieo.client.main.Main.stage;
import br.com.unifieo.client.model.UserModel;
import br.com.unifieo.common.util.MessagesProperties;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.dialog.Dialogs;

public class LoginController extends Controller implements Initializable {
    
    @FXML private AnchorPane pane;
    @FXML private TextField txtLogin;
    @FXML private PasswordField pwdPassword;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        initRootPane(pane);
        // Inicialização dos campos
        txtLogin.setPromptText(MessagesProperties.getKey("msg.login"));
        pwdPassword.setPromptText(MessagesProperties.getKey("msg.password"));
        
        txtLogin.focusedProperty().addListener((observable, oldValue, newValue) -> {focusValidate(txtLogin, newValue);});
        pwdPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {focusValidate(pwdPassword, newValue);});    
                
    }
     
     
    @FXML
    private void login() {
        // Valida se os campos foram preenchidos
        if (txtLogin.getText().isEmpty() || pwdPassword.getText().isEmpty()) {
            return;
        }        
        UserModel user = new UserModel(txtLogin.getText(), pwdPassword.getText());        
        LoginControl login = new LoginControl();
        if (login.executeLogin(user)) {
            goToScreen("cockpit");
        } else {
            Dialogs.create()
                    .owner(stage)
                    .title(MessagesProperties.getKey("msg.label.attention"))
                    .masthead(MessagesProperties.getKey("msg.data_incorrect"))
                    .message(MessagesProperties.getKey("msg.error.login_incorrect"))
                    .showWarning();
        }
        
    }
    
    @FXML
    private void register() {     
        goToScreen("register");
    }
    
    @FXML
    private void exit() {
        System.exit(0);
    }
       
}
