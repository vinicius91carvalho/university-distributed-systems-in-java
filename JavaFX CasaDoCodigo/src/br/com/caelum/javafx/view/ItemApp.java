/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.view;

import br.com.caelum.javafx.model.Produto;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Vinicius
 */
public class ItemApp extends Application {


    private AnchorPane pane;
    public static Stage stage;
    private ImageView imgItem;
    private Label lblPreco, lblDescricao;
    private Button btnAddCarrinho;
    private Produto produto;

    public ItemApp(Produto produto, int index) {
        this.produto = produto;
        this.index = index;
    }
    
    private int index;
    private static String[] images = {
        "http://www.sportcenterlopes.com.br/images/250_topper_campo_2009replic.jpg",
        "http://1.bp.blogspot.com/_H8uGs8K8kaY/TLZTXR8nIgI/AAAAAAAAF_0/BvpxdqGF4PE/s1600/luva_umbro.png",
        "http://bimg2.mlstatic.com/camisa-nike-active-importada-manga-longa-esportiva-vermelha_MLB-F-199843960_1391.jpg",
        "http://www.showtenis.com.br/images/_product/979/979112/chuteira-nike-mercurial-glide-3-fg-campo--199fd9.jpg",
        "http://www.katy.com.br/imagens/produtos/original/caneleira-topper-trainning-difusion-13340619502673137.jpg"};
    
    @Override
    public void start(Stage stage) throws Exception {
        ItemApp.stage = stage;
        
        initComponents();
        initListeners();
        initTransitions();
        initTimeline();
        
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        
        initLayout();
    }
    
    private void initComponents() {
        pane = new AnchorPane();
        pane.setPrefSize(600, 400);
        
        lblDescricao = new Label(getProduto().getProduto());
        lblPreco = new Label(String.valueOf(getProduto().getPreco()));
        
        imgItem = new ImageView(new Image(images[getIndex()]));
        imgItem.setFitHeight(300);
        imgItem.setFitWidth(300);
        
        btnAddCarrinho = new Button("Adicionar");
        
        pane.getChildren().addAll(lblDescricao, lblPreco, imgItem, btnAddCarrinho);
        
    }    
    
    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    private void initLayout() {
        lblDescricao.setLayoutX(10);
        lblDescricao.setLayoutY(10);
        
        lblPreco.setLayoutX(10);
        lblPreco.setLayoutY(30);
        
        imgItem.setLayoutX(10);
        imgItem.setLayoutY(50);  
        imgItem.setEffect(new Reflection());
        
        btnAddCarrinho.setLayoutX(pane.getWidth()-btnAddCarrinho.getWidth()-20);
        btnAddCarrinho.setLayoutY(10);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void initListeners() {
        btnAddCarrinho.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                VitrineApp.carrinho.addProdutos(produto);
                try {
                    new CarrinhoApp().start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(ItemApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        /*btnAddCarrinho.setOnMouseEntered(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(2000), btnAddCarrinho);
                scale.setToX(1.5);
                scale.setToY(1.5);
                scale.play();
            }
            
        });
        
        btnAddCarrinho.setOnMouseExited(new EventHandler() {

            @Override
            public void handle(Event t) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(2000), btnAddCarrinho);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        }); */      
        
    }
    
    private void initTimeline() {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(imgItem.opacityProperty(), 0.0);
        KeyFrame kf = new KeyFrame(Duration.millis(2000), kv);
        timeline.getKeyFrames().addAll(kf);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    private void initTransitions() {
        /*FadeTransition fade = new FadeTransition(Duration.millis(3000), imgItem);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        ScaleTransition scale = new ScaleTransition(Duration.millis(2000), btnAddCarrinho);
        scale.setToX(1.5);
        scale.setToY(1.5);
        ParallelTransition parallel = new ParallelTransition();
        parallel.getChildren().addAll(fade, scale);
        parallel.play();
        /*SequentialTransition sequential = new SequentialTransition();
        sequential.getChildren().addAll(fade, scale);
        sequential.play();*/
    }
    
}
