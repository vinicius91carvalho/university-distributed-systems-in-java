package br.com.unifieo.nameserver.util;

import br.com.unifieo.common.util.LogUtils;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class EntityListener {
	
	@PrePersist
	public void quandoPersistir(Object objeto) {		
		    LogUtils.msg(new StringBuilder()
			.append("Persistindo objeto: ")
			.append(objeto.toString())
			.append(" no banco")
			.toString());
	}	
	
	@PreRemove
	public void quandoRemover(Object objeto) {
		LogUtils.msg(new StringBuilder()
		.append("Removendo objeto: ")
		.append(objeto.toString())
		.append(" no banco")
		.toString());	
	}
	
	@PreUpdate
	public void quandoAlterar(Object objeto) {
		LogUtils.msg(new StringBuilder()
		.append("Atualizando objeto: ")
		.append(objeto.toString())
		.append(" no banco")
		.toString());
	}
	
	@PostPersist
	public void quandoPersistido(Object objeto) {
                LogUtils.msg(new StringBuilder()
		.append(objeto.getClass().getSimpleName())
                .append(": ")
		.append(objeto.toString())
		.append(" cadastrado no banco com sucesso!")
		.toString());
	}
	
	@PostUpdate
	public void quandoAlterado(Object objeto) {
		LogUtils.msg(new StringBuilder()
		.append(objeto.getClass().getSimpleName())
                .append(": ")
		.append(objeto.toString())
		.append(" alterado no banco com sucesso!")
		.toString());
	}
	
	@PostRemove
	public void quandoRemovido(Object objeto) {
                LogUtils.msg(new StringBuilder()
		.append(objeto.getClass().getSimpleName())
                .append(": ")
		.append(objeto.toString())
		.append(" removido do banco com sucesso!")
		.toString());
	}
}
