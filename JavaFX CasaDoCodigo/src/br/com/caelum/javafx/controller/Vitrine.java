/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.caelum.javafx.controller;

import br.com.caelum.javafx.model.Produto;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vinicius
 */
public class Vitrine {

    private static List<Produto> produtos = new ArrayList<Produto>();
    
    public void addProdutos(Produto... produtos) {
        for (Produto produto : produtos)
            Vitrine.produtos.add(produto);
    }

    public static List<Produto> getProdutos() {
        return produtos;
    }   
    
}
