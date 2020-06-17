package br.com.unifieo.client.view;

import br.com.unifieo.client.control.ChatControl;
import br.com.unifieo.client.main.Main;
import br.com.unifieo.common.configuration.ChatServiceConfiguration;
import br.com.unifieo.common.encryption.CryptoUtil;
import br.com.unifieo.common.util.Action;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import br.com.unifieo.common.protocol.ChatEventProtocol;
import br.com.unifieo.common.protocol.ChatMessageProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.ServiceProtocol;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.protocol.UserProtocol;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class ChatController extends Controller implements Initializable {
    
    @FXML private AnchorPane pane;
    @FXML private ScrollPane scrollPane;
    @FXML private ListView<UserProtocol> lstUsers;
    @FXML private ListView<String> lstMessages;
    @FXML private TextField txtMessage;
    @FXML private Button btnSendFile;
    @FXML private Label lblPathFile;
    
    private File file;
    
    private ChatControl control;
    
    private Map<String, Path> mapFiles = new HashMap<String, Path>();
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        initRootPane(pane);           
        
        // Recebe as mensagens do servidor de chat
        EventHandler<Event> handlerMessage = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Platform.runLater(() -> {
                    ChatMessageProtocol messageProtocol = (ChatMessageProtocol) event.getSource();
                    receivedMessage(messageProtocol);                                
                });
            }            
        };
        
        // Recebe eventos do servidor de nomes
        EventHandler<Event> handlerEvent = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Platform.runLater(() -> {
                    ChatEventProtocol eventProtocol = (ChatEventProtocol) event.getSource();
                    receivedEvent(eventProtocol);                                    
                });
            }            
        };
        
        btnSendFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(MessagesProperties.getKey("msg.choose_file_send"));
                file = fileChooser.showOpenDialog(Main.stage);
                if (file != null) {
                    lblPathFile.setText(file.getAbsolutePath());
                }
            }
        });
        
        lstMessages.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        String item = lstMessages.getSelectionModel().getSelectedItem();
                        if (item == null || item.isEmpty()) {
                            return;
                        }
                        Path path = mapFiles.get(item);
                        if (path != null) {
                            try {
                                Desktop.getDesktop().open(path.toFile());
                            } catch (IOException ex) {
                                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }        
            }
        });
        
        lstMessages.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>(){

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (item.contains("Você recebeu o arquivo:")) {
                                    getStyleClass().add("item-basic-file");
                                    getStyleClass().add("title-principal");
                            } else if (item.contains("diz para "+ChatControl.getUserProtocol().getLogin())) {
                                getStyleClass().add("item-basic-simple");                                                                                                          
                                getStyleClass().add("title-principal");
                            }                                
                            setText(item);
                            setContentDisplay(ContentDisplay.TEXT_ONLY);
                        }
                    }
                    
                };
                return cell;
            };
        });
                
        txtMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    String msg = txtMessage.getText();
                    txtMessage.clear();
                    UserProtocol userReceived = lstUsers.getSelectionModel().getSelectedItem();
                    if (userReceived != null && userReceived.getLogin().equals(MessagesProperties.getKey("msg.label.all"))) {
                        userReceived = null;
                    }
                    byte[] fileBytes = null;
                    String nameFile = null;
                    if (file != null) {
                        lblPathFile.setText(MessagesProperties.getKey("msg.click_to_send_file"));
                        try {
                            nameFile = file.getName();
                            fileBytes = Files.readAllBytes(file.toPath());
                            CryptoUtil crypto = new CryptoUtil();
                            fileBytes = crypto.encrypt(fileBytes);
                        } catch (IOException ex) {
                            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        file = null;
                    }                                        
                    
                    ChatMessageProtocol message = new ChatMessageProtocol(
                            IdentificationHost.CLIENT, 
                            new SimpleMessageProtocol(msg, IdentificationHost.CLIENT), 
                            ChatControl.getUserProtocol(), 
                            userReceived, 
                            fileBytes, 
                            nameFile);
                    control.getClientSend().sendMessage(message);
                }
            }
        });
                
        if (control == null) {
            control = new ChatControl(handlerMessage, handlerEvent);
            ServiceProtocol protocol = control.startConnection();            
            if (protocol == null) {
                CockpitController.chatService = null;
                goToScreen("cockpit");
            }
            
            lstMessages.getItems().add(MessagesProperties.getKey("msg.welcome_chat_service"));
            lstUsers.getItems().addAll(control.getListUsers());       
        }
        
        
    }
    
    private void receivedMessage(ChatMessageProtocol messageProtocol) {
        StringBuilder builder = new StringBuilder();
                builder.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
                .append(" ").append(messageProtocol.getUserSender().getLogin())
                .append(" diz para ");
        if (!messageProtocol.isAllUsers()) 
            builder.append(messageProtocol.getUserReceiver()).append(": ");
        else
            builder.append("Todos: ");
        builder.append(messageProtocol.getMessage());
        LogUtils.msg(MessagesProperties.getKey("msg.msg_received")+messageProtocol.getMessage());
        lstMessages.getItems().add(builder.toString());
        
        if (messageProtocol.getFile() != null) {            
            Path pathWrite = Paths.get(ChatServiceConfiguration.DIR_DOWNLOADS+"/"+messageProtocol.getNameFile());                
            CryptoUtil crypto = new CryptoUtil();
            try {                
                Files.write(pathWrite, crypto.decrypt(messageProtocol.getFile()));
                StringBuilder msgFile = new StringBuilder();
                msgFile.append("Você recebeu o arquivo: ")
                    .append(messageProtocol.getNameFile())
                    .append(" de ")
                    .append(messageProtocol.getUserSender().getLogin())
                    .append(" clique aqui para abri-lo");
                lstMessages.getItems().add(msgFile.toString());
                mapFiles.put(msgFile.toString(), pathWrite);
            } catch (Exception ex) {
                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    private void receivedEvent(ChatEventProtocol eventProtocol) {
        StringBuilder builder = new StringBuilder();
                builder.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
                .append(" ").append(eventProtocol.getUser().getLogin());
                
         if (eventProtocol.getAction().equals(Action.ADD)) {
             builder.append(" entrou no bate papo.");
             lstUsers.getItems().add(eventProtocol.getUser());             
         } else {
             builder.append(" saiu do bate papo.");
             lstUsers.getItems().remove(eventProtocol.getUser());
         }
         lstMessages.getItems().add(builder.toString());
    }
    
    @FXML 
    private void back() {
        goToScreen("cockpit");
        control.getClientSend().sendEvent(new ChatEventProtocol(IdentificationHost.CLIENT, Action.REMOVE, ChatControl.getUserProtocol()));
    }
       
}
