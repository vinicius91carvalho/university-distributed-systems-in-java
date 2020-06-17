/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.chat.assync;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Vinicius
 */
public class Communicate {
    
    public ObjectOutputStream output;
    public ObjectInputStream input;
    
    public Communicate(ObjectOutputStream output, ObjectInputStream input) {
        this.output = output;
        this.input = input;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
    
    
    
}
