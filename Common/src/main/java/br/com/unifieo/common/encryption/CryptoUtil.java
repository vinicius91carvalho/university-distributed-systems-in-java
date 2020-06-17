/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.encryption;

import br.com.unifieo.common.configuration.ConfigurationProperties;
import br.com.unifieo.common.util.LogUtils;
import br.com.unifieo.common.util.MessagesProperties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Classe de Criptografia que utiliza o algoritmo AES (Padrão de Criptografia Avançada) evolução do DES. Conhecido com Rijndael...
 * Surgiu em um concurso promovido pelo governo dos EUA e se tornou padrão de criptografia utilizada pelo governo.
 * MD5 e SHA 1 são algoritmos de código Hash que ratificam a integridade e consistência dos dados.
 * - Características: Desempenho, Flexibilidade, Pouca memória e segurança.
 * Fonte: Wikipedia.
 * @author Vinicius
 */

public class CryptoUtil {
    
    private final String KEY_CRYPTO = ConfigurationProperties.getKey("key_crypto");
    private final String IV = "AAAAAAAAAAAAAAAA";
    private SecretKeySpec key;
    private Cipher cipher;
    
    public CryptoUtil()  {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
            key = new SecretKeySpec(KEY_CRYPTO.getBytes("UTF-8"), "AES");
        } catch (Exception e) {
            LogUtils.exception("msg.error.create_encrypt", e);
        }
    }
    
    public byte[] encrypt(byte[] input) {        
        LogUtils.msg(MessagesProperties.getKey("msg.ftp_file_crypto")+input.length);
        byte[] cypherEncrypt = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            cypherEncrypt = cipher.doFinal(input);
            LogUtils.msg(MessagesProperties.getKey("msg.ftp_file_crypto_success")+cypherEncrypt.length);
        } catch (Exception ex) {
            Logger.getLogger(CryptoUtil.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return cypherEncrypt;
    }
    
    public byte[] decrypt(byte[] input) {
        LogUtils.msg(MessagesProperties.getKey("msg.file_decrypt")+input.length);
        byte[] cypherDecrypt = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
            cypherDecrypt = cipher.doFinal(input);
            LogUtils.msg(MessagesProperties.getKey("msg.file_decrypt_sucess")+cypherDecrypt.length);
        } catch (Exception ex) {
            Logger.getLogger(CryptoUtil.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return cypherDecrypt;
        
    }    
    
}
