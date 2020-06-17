/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
// Classe responsável por capturar as configurações de um arquivo de texto.
public class ConfigurationProperties {
    
    private static ResourceBundle bundle;
    
    static {
        String pathDirectory = System.getProperty("user.dir");
        File configuration = new File(pathDirectory+"\\configuration.properties");
        System.out.println("Procurando configurações locais");
        if (Files.exists(configuration.toPath())) {
            System.out.println("Arquivo de configurações encontrado. Carregando informações.");
            try {
                bundle = new PropertyResourceBundle(new FileInputStream(configuration));
            } catch (IOException ex) {
                Logger.getLogger(ConfigurationProperties.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } else {
            System.out.println("Arquivo de configuracoes nao encontrado. Utilizando configuracoes do JAR!");
            bundle = ResourceBundle.getBundle("configuration");
        }
        
    }
    
    public static String getKey(String key) {
        return bundle.getString(key);
    }
    
}
