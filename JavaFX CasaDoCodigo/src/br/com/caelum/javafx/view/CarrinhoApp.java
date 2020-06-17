/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.view;

import br.com.caelum.javafx.model.Produto;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author Vinicius
 */
public class CarrinhoApp extends Application {

    private AnchorPane pane;
    private TableView<ItemProperty> tblCarrinho;
    private TableColumn<ItemProperty, String> colProduto;
    private TableColumn<ItemProperty, Double> colPreco;
    private Button btnExcluirItem, btnVoltarVitrine, btnConfirmarCompra;
    private ObservableList<ItemProperty> listItens = FXCollections.observableArrayList();
    public static Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        CarrinhoApp.stage = stage;
        
        initItens();
        initComponents();
        
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        initListeners();
        initLayout();
        
        
    }

    private void initComponents() {
        pane = new AnchorPane();   
        pane.setPrefSize(800, 600);
        
        tblCarrinho = new TableView<ItemProperty>();
        
        colProduto = new TableColumn<ItemProperty, String>();
        colProduto.setText("Produto");
        colProduto.setCellValueFactory(new PropertyValueFactory<ItemProperty, String>("produto"));
        
        colPreco = new TableColumn<ItemProperty, Double>();
        colPreco.setText("Preco");
        colPreco.setCellValueFactory(new PropertyValueFactory<ItemProperty, Double>("preco"));
        
        tblCarrinho.getColumns().addAll(colProduto, colPreco);
        
        btnExcluirItem = new Button("Excluir item");
        btnVoltarVitrine = new Button("Voltar vitrine");
        btnConfirmarCompra = new Button("Confirmar compra");
        
        tblCarrinho.setItems(listItens);
        
        pane.getChildren().addAll(tblCarrinho, btnConfirmarCompra, btnExcluirItem, btnVoltarVitrine);
        
    }
    
    private void initItens() {
        for (Produto p : VitrineApp.carrinho.getProdutos()) {
            listItens.add(new ItemProperty(p.getProduto(), p.getPreco()));
        }
    }

    private void initLayout() {
        
        tblCarrinho.setPrefWidth(400);
        tblCarrinho.setLayoutX(10);
        tblCarrinho.setLayoutY(10);
        
        btnExcluirItem.setPrefWidth(100);
        btnExcluirItem.setLayoutX(pane.getWidth() - 120);
        btnExcluirItem.setLayoutY(10);
        
        btnConfirmarCompra.setPrefWidth(100);        
        btnConfirmarCompra.setLayoutX(pane.getWidth() - 120);
        btnConfirmarCompra.setLayoutY(50);
        
        btnVoltarVitrine.setPrefWidth(100);
        btnVoltarVitrine.setLayoutX(pane.getWidth() - 120);
        btnVoltarVitrine.setLayoutY(90);
    }

    private void initListeners() {
        
        btnExcluirItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                ItemProperty itemProp = tblCarrinho.getSelectionModel().getSelectedItem();
                Produto p = new Produto(itemProp.getProduto(), itemProp.getPreco());
                VitrineApp.carrinho.removeProduto(p);
                tblCarrinho.getItems().remove(tblCarrinho.getSelectionModel().getSelectedItem());
            }
        });
        
        btnVoltarVitrine.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                ItemApp.stage.close();
                CarrinhoApp.stage.close();
            }
        });
        
        btnConfirmarCompra.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CarrinhoApp.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        JOptionPane.showMessageDialog(null, "Compra realizada com sucesso!");
                        
                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                CarrinhoApp.stage.close();
                                ItemApp.stage.close();
                            }
                        });
                    }
                });
                thread.start();
            }     
        
        });
                
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