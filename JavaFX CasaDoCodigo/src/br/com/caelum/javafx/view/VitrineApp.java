/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.view;

import br.com.caelum.javafx.controller.Carrinho;
import br.com.caelum.javafx.controller.Vitrine;
import br.com.caelum.javafx.model.Produto;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Vinicius
 */
public class VitrineApp extends Application {
    
    private AnchorPane pane;
    private TextField txtPesquisa;
    private TableView<ItemProperty> tblVitrine;
    private TableColumn<ItemProperty, String> colProduto;
    private TableColumn<ItemProperty, Double> colPreco;
    private static ObservableList<ItemProperty> listItens = FXCollections.observableArrayList();
    public static Carrinho carrinho;
    public static Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        VitrineApp.stage = stage;
        
        initItens();
        initComponents();
        initListeners();
        
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        
        stage.setResizable(false);
        stage.setTitle("Vitrine - GolFX");
        stage.show();
        
        initLayout();
    }
    
    private void initListeners() {
        txtPesquisa.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
              if (!txtPesquisa.getText().isEmpty()) {
                  tblVitrine.setItems(findItens());
              } else {
                  tblVitrine.setItems(listItens);
              }
            }
        });
    }    
    
    private void initComponents() {
        
        pane = new AnchorPane();
        pane.setPrefSize(800, 600);
        
        txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Digite o item para pesquisa");
        DropShadow shadow = new DropShadow();
        //shadow.setSpread(5);
        shadow.setColor(Color.web("#FF0000"));
        txtPesquisa.setEffect(shadow);
        
        tblVitrine = new TableView<ItemProperty>();
        tblVitrine.setPrefSize(780, 550);
        
        colProduto = new TableColumn<ItemProperty, String>();
        colProduto.setCellValueFactory(new PropertyValueFactory<ItemProperty, String>("produto"));
        
        colPreco = new TableColumn<ItemProperty, Double>();
        colPreco.setCellValueFactory(new PropertyValueFactory<ItemProperty, Double>("preco"));
        
        tblVitrine.getColumns().addAll(colProduto, colPreco);
        tblVitrine.setItems(listItens);
        
        tblVitrine.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemProperty>(){

            @Override
            public void changed(ObservableValue<? extends ItemProperty> ov, ItemProperty t, ItemProperty t1) {
                ItemApp itemApp = new ItemApp(
                        new Produto(t1.getProduto(), t1.getPreco()),
                        tblVitrine.getSelectionModel().getSelectedIndex()
                );
                try {
                    itemApp.start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(VitrineApp.class.getName()).log(Level.SEVERE, null, ex);
                }              
                
            }
            
        });
        
        pane.getChildren().addAll(txtPesquisa, tblVitrine);
        
        carrinho = new Carrinho();
        
    }
    
    private void initItens() {
        Vitrine v = new Vitrine();
        v.addProdutos(
                new Produto("Bola Topper", 15.00),
                new Produto("Camisa Esportiva", 40.00),
                new Produto("Chuteira Nike Mercurial", 199.00),
                new Produto("Caneleira Topper", 10.00));
        for (Produto p : v.getProdutos())
           listItens.add(new ItemProperty(p.getProduto(), p.getPreco())); 
    }
    
    private ObservableList<ItemProperty> findItens() {
        ObservableList<ItemProperty> itensEncontrados = 
                FXCollections.observableArrayList();
        
        for (ItemProperty item : listItens) {
            if (item.getProduto().equals(txtPesquisa.getText()))
                itensEncontrados.add(item);
        }  
        
        return itensEncontrados;
    }

    private void initLayout() {
        tblVitrine.setLayoutX(10);
        tblVitrine.setLayoutY(50);
        
        txtPesquisa.setLayoutX(pane.getWidth()-txtPesquisa.getWidth()-20);
        txtPesquisa.setLayoutY(10);
        
    }

    public class ItemProperty {

        private SimpleStringProperty produto;
        private SimpleDoubleProperty preco;

        public ItemProperty(String produto, Double preco) {
            this.produto = new SimpleStringProperty(produto);
            this.preco = new SimpleDoubleProperty(preco);
        }

        public String getProduto() {
            return produto.get();
        }

        public void setProduto(String produto) {
            this.produto.set(produto);
        }

        public double getPreco() {
            return preco.get();
        }

        public void setPreco(double preco) {
            this.preco.set(preco);
        }
    
    }
    
}
