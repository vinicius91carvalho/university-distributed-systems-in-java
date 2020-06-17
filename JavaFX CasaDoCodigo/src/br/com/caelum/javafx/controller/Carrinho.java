/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.controller;

import br.com.caelum.javafx.model.Produto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Vinicius
 */
public class Carrinho {

    private List<Produto> produtos = new ArrayList<Produto>();
    
    public void addProdutos(Produto... produtos) {
        for (Produto produto : produtos)
            this.produtos.add(produto);
    }
    
    public void removeProduto(Produto p) {
        Iterator<Produto> itProduto = produtos.iterator();
        while (itProduto.hasNext()) {
            Produto produto = itProduto.next();
            if (produto.equals(p)) {
                produtos.remove(p);
                break;
            }    
        }
    }    

    public List<Produto> getProdutos() {
        return produtos;
    }   
    
}
