/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.client.control;

import br.com.unifieo.client.dao.UserDAO;
import br.com.unifieo.client.model.UserModel;
import br.com.unifieo.client.util.ConvertUtil;
import br.com.unifieo.common.util.MessagesProperties;

/**
 *
 * @author Vinicius
 */
public class LoginControl {
    
    private UserDAO dao;
    
    public LoginControl() {
        dao = new UserDAO();
    }    
    
    public Boolean executeLogin(UserModel user) {
        
        if (dao.login(user)) {
            ChatControl.setUserProtocol(ConvertUtil.convertUserToProtocol(user));
            FTPControl.setUserProtocol(ConvertUtil.convertUserToProtocol(user));
            return true;
        }
        return false;
    }
    
    public Boolean checkUserExist(String login) {
        if (dao.findById(new UserModel(login, null)) != null)
            return true;
        else 
            return false;
    }
    
    public Boolean registerUser(String login, String password, String confirmPassword) throws Exception {
        if (password.equals(confirmPassword)) {
                if (!checkUserExist(login)) {                    
                    // Caso o usuário digitou todas as informações corretamente, cadastre-o
                    UserModel userModel = new UserModel();
                    userModel.setLogin(login);
                    userModel.setPassword(password);
                    
                    dao.add(userModel);                    
                    return true;
                } else {
                    throw new Exception(MessagesProperties.getKey("msg.error.user_exists"));
                }
        } else {
            throw new Exception(MessagesProperties.getKey("msg.error.password_confirmation_fail"));
        }       
        
    }        
    
}
