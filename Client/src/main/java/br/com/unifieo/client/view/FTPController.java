package br.com.unifieo.client.view;

import br.com.unifieo.client.control.FTPControl;
import br.com.unifieo.client.main.Main;
import static br.com.unifieo.client.main.Main.stage;
import br.com.unifieo.common.configuration.FTPServiceConfiguration;
import br.com.unifieo.common.encryption.CryptoUtil;
import br.com.unifieo.common.protocol.Arquivo;
import br.com.unifieo.common.protocol.FileProtocol;
import br.com.unifieo.common.protocol.IdentificationHost;
import br.com.unifieo.common.protocol.SimpleMessageProtocol;
import br.com.unifieo.common.util.MessagesProperties;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;

public class FTPController extends Controller implements Initializable {
    
    @FXML private AnchorPane pane;
    @FXML private ScrollPane scrollPane;
    @FXML private ListView<Arquivo> lstFiles;  
    
    private FTPControl control;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        initRootPane(pane);                          
                
        if (control == null) {
            control = new FTPControl();
            FileProtocol protocol = control.startConnection();
            if (protocol == null) {
                CockpitController.ftpService = null;
                goToScreen("cockpit");
            }
            if (protocol.getPaths() != null) {
                lstFiles.getItems().addAll(protocol.getPaths());
            }
            
        }        
        
        lstFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        communicateWithServer();
                    }
                }
            }
        });
        
        
        lstFiles.setCellFactory(new Callback<ListView<Arquivo>, ListCell<Arquivo>>() {

            @Override
            public ListCell<Arquivo> call(ListView<Arquivo> param) {
                final ListCell<Arquivo> cell = new ListCell<Arquivo>(){

                    @Override
                    protected void updateItem(Arquivo item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            ImageView image = null;
                            if (item.isDirectory() || item.isTopDirectory() || item.isRootDirectory()) {
                                image = new ImageView(new Image("/images/folder.png"));
                            } else {
                                image = new ImageView(new Image("/images/file.png"));
                            }
                            image.setVisible(true);
                            HBox hbox = new HBox();
                            BorderPane pane = new BorderPane();
                            pane.setCenter(new Label(item.getName()));
                            pane.setPadding(new Insets(0, 0, 0, 15));
                            hbox.getChildren().addAll(image, pane);
                            setGraphic(hbox);
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        }
                    }
                    
                };
                return cell;
            };
        });
    }
    
    @FXML
    private void refresh() {
        Arquivo path = new Arquivo();
        if (!lstFiles.getItems().isEmpty()
                    && lstFiles.getItems().get(lstFiles.getItems().size()-1).isNotTopAndRoot()) {
                path.setUri(lstFiles.getItems().get(lstFiles.getItems().size()-1).getUri());
        }
        path.setRefresh(Boolean.TRUE);
        FileProtocol file = control.sendAndReceivePaths(path);
        processResponse(file, path);
    }
    
    @FXML
    private void upload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha um arquivo para ser enviado");
        File file = fileChooser.showOpenDialog(Main.stage);
        if (file != null) {
            Arquivo path = new Arquivo();
            if (!lstFiles.getItems().isEmpty()
                    && lstFiles.getItems().get(lstFiles.getItems().size()-1).isNotTopAndRoot()) {
                path.setUri(lstFiles.getItems().get(lstFiles.getItems().size()-1).getUri());
            }
            path.setName(file.getName());
            path.setSendFile(Boolean.TRUE);
            FileProtocol protocol = new FileProtocol(IdentificationHost.CLIENT);
            protocol.setPath(path);
            CryptoUtil crypto = new CryptoUtil();
            try {
                protocol.setBytes(crypto.encrypt(Files.readAllBytes(file.toPath())));
            } catch (IOException ex) {
                Logger.getLogger(FTPController.class.getName()).log(Level.SEVERE, null, ex);
            }
            FileProtocol response = control.sendAndReceivePaths(protocol);
            Dialogs.create()
                        .owner(stage)
                        .title(MessagesProperties.getKey("msg.label.information"))
                        .masthead(MessagesProperties.getKey("msg.operation_success"))
                        .message("Upload do arquivo: "+path.getName()+" realizo com sucesso!")
                        .showInformation();
            processResponse(response, path);
        }
    }
    
    private void communicateWithServer() {
        Arquivo path = lstFiles.getSelectionModel().getSelectedItem();
        FileProtocol file = control.sendAndReceivePaths(path);
        processResponse(file, path);
    }
    
    private void processResponse(FileProtocol file, Arquivo path) {
        if (file.getBytes() != null) {
            try {
                Path pathWrite = Paths.get(FTPServiceConfiguration.DIR_CLIENT+"/"+path.getName());                
                CryptoUtil crypto = new CryptoUtil();
                Files.write(Paths.get(pathWrite.toFile().getAbsolutePath()+"_CRIPTOGRAFADO"), file.getBytes());
                Files.write(pathWrite, crypto.decrypt(file.getBytes()));
                Dialogs.create()
                        .owner(stage)
                        .title(MessagesProperties.getKey("msg.label.information"))
                        .masthead(MessagesProperties.getKey("msg.operation_success"))
                        .message("Arquivo salvo em: "+pathWrite.toString())
                        .showInformation();
                Desktop.getDesktop().open(pathWrite.toFile());
            } catch (Exception ex) {
                Logger.getLogger(FTPController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            lstFiles.getItems().clear();
            if (path != null && path.getName() != null && !path.getName().equals(".")) {
                if (file != null && file.getPaths() != null && file.getPaths().size() > 0) {
                    addNavigation(file.getPaths().get(0).getUri());
                }                
            }
            if (file.getPaths() != null) {
                lstFiles.getItems().addAll(file.getPaths());
            }
              
        }
    }
    
    private void addNavigation(URI uri) {
        Arquivo root = new Arquivo();
        root.setName(".");
        root.setUri(uri);   
        root.setDirectory(true);
        Arquivo folderTop = new Arquivo();
        folderTop.setName("..");
        folderTop.setUri(uri);
        folderTop.setDirectory(true);
        lstFiles.getItems().add(root);
        lstFiles.getItems().add(folderTop);
    }
    
    @FXML 
    private void back() {
        try {              
            control.getOutput().writeObject(new SimpleMessageProtocol("Desconectando do servico FTP", IdentificationHost.CLIENT));
        } catch (Exception ex) {}
        goToScreen("cockpit");        
    }
    
    @FXML
    private void exit() {
        System.exit(0);
    } 
       
}
