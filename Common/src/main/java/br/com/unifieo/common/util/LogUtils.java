/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.util;

import br.com.unifieo.common.configuration.ConfigurationProperties;
import java.io.UnsupportedEncodingException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class LogUtils {
    
     // Log para gravação das mensagens no console ou posteriormente em arquivos de texto
    private static final Logger logger = Logger.getGlobal();    
    
    static {
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        try {
            handler.setEncoding(ConfigurationProperties.getKey("enconding_application"));
        } catch (SecurityException ex) {
            Logger.getLogger(LogUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LogUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }
    
    public static void exception(String nameMessage, Exception e) {
        logger.log(Level.SEVERE, "{0}. Descricao do erro: {1}", new Object[]{MessagesProperties.getKey(nameMessage), e.getMessage()});
    }
    
    public static void info(Level level, String nameMessage) {
        logger.log(level, MessagesProperties.getKey(nameMessage));
    }
    
    public static void info(String nameMessage) {
        logger.log(Level.INFO, MessagesProperties.getKey(nameMessage));
    }
    
    public static void msg(String msg) {
        logger.log(Level.INFO, msg);
    }
    
}
