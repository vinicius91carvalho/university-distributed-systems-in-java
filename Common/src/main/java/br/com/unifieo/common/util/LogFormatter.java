/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author Vinicius
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder
                .append(new SimpleDateFormat("dd/MM/yyyy hh:MM:ss").format(new Date()))
                .append(" - [").append(record.getLevel()).append("] - ")
                .append(formatMessage(record))
                .append("\n");
        return builder.toString();
                    
    }

    @Override
    public String getHead(Handler h) {
        return super.getHead(h); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTail(Handler h) {
        return super.getTail(h); //To change body of generated methods, choose Tools | Templates.
    }
}
