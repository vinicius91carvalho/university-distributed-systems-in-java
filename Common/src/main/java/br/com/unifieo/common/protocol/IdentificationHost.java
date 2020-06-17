/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.unifieo.common.protocol;

import java.io.Serializable;


public enum IdentificationHost implements Serializable {
    
    CLIENT(1, "Cliente"),
    SERVER_NAME(2, "Servidor de Nomes"),
    SERVICE(3, "Servico"),
    SERVICO_SYNCRO(4, "Servico deseja obter outro No");
    
    private Integer id;
    private String description;
    
    IdentificationHost(Integer id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public static IdentificationHost getById(Integer id) {
        for (IdentificationHost type : IdentificationHost.values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
        return null;
    }    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }    
    
}
