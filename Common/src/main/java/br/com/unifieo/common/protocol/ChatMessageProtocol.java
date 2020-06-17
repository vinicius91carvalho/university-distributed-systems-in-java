/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;

/**
 *
 * @author Vinicius
 */
public class ChatMessageProtocol implements IProtocol {
    
    private IdentificationHost host;
    private SimpleMessageProtocol message;
    private UserProtocol userSender;
    private UserProtocol userReceiver;
    private byte[] file;
    private String nameFile;


    public ChatMessageProtocol(IdentificationHost host, SimpleMessageProtocol message, UserProtocol userSender, UserProtocol userReceiver, byte[] file) {
        this.host = host;
        this.message = message;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.file = file;
    }
    
    public ChatMessageProtocol(IdentificationHost host, SimpleMessageProtocol message, UserProtocol userSender, UserProtocol userReceiver, byte[] file, String nameFile) {
        this.host = host;
        this.message = message;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.file = file;
        this.nameFile = nameFile;
    }

    public IdentificationHost getHost() {
        return host;
    }

    public void setHost(IdentificationHost host) {
        this.host = host;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
    
    public SimpleMessageProtocol getMessage() {
        return message;
    }

    public void setMessage(SimpleMessageProtocol message) {
        this.message = message;
    }

    public UserProtocol getUserSender() {
        return userSender;
    }

    public void setUserSender(UserProtocol userSender) {
        this.userSender = userSender;
    }

    public UserProtocol getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(UserProtocol userReceiver) {
        this.userReceiver = userReceiver;
    }

    public Boolean isAllUsers() {
        return (getUserReceiver() == null);
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
    
    
    
    @Override
    public IdentificationHost getIdentification() {
        return host;
    }
    
}
