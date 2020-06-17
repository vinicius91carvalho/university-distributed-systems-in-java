/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Vinicius
 */
public class MessagesProperties {
    
    private static final ResourceBundle bundle;
    
    static {
        bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
    }
    
    public static String getKey(String key) {
        String response = bundle.getString(key);
        if (response != null)
            return response;
        else
            return key;
    }
}
