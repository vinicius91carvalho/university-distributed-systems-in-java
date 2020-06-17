package br.com.unifieo.client.view;

import br.com.unifieo.client.control.ChatControl;
import br.com.unifieo.client.control.FTPControl;
import br.com.unifieo.common.util.ConnectionUtil;
import br.com.unifieo.common.protocol.ServiceProtocol;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class CockpitController extends Controller implements Initializable {
    
    @FXML private AnchorPane pane;
    @FXML private Pane pnlClient;
    @FXML private Pane pnlFTP;
    public static ServiceProtocol chatService;
    public static ServiceProtocol ftpService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        initRootPane(pane);       
        addScaleTransitions(pnlClient, 1.2);
        addScaleTransitions(pnlFTP, 1.2);
        
        pnlClient.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                goToScreen("chat");
            }            
        });
        
        pnlFTP.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                goToScreen("ftp");
            }            
        });                
        
        
        if (chatService == null) {
            chatService = ConnectionUtil.captureService("CHAT");
            ChatControl.setChatService(chatService);
            if (chatService == null) {
                pnlClient.disableProperty().setValue(true);
                pnlClient.getStyleClass().add("cockpit-disabled");                
            }
        }
        
        if (ftpService == null) {
            ftpService = ConnectionUtil.captureService("FTP");
            FTPControl.setFtpService(ftpService);
            if (ftpService == null) {
                pnlFTP.disableProperty().setValue(true);
                pnlFTP.getStyleClass().add("cockpit-disabled");
            }            
        }                      
        
    }          
    
    @FXML
    private void exit() {
        System.exit(0);
    }  
       
}
