package br.pelommedrado.transobj.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.util.MapaSemente;

/**
 * @author Andre Leite
 */
public class LimparSementeTask {
	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(LimparSementeTask.class);
	
	/** Mapa de semente **/
	private MapaSemente mapa = null;

	/**
	 * 
	 * @param mapa
	 */
	public LimparSementeTask(MapaSemente mapa) {
		super();
		
		this.mapa = mapa;
	}
	
	/**
	 * 
	 */
	public void limpar() {
		logger.info("iniciando o processo de limpeza");
		
		logger.debug("total de sementes:" + mapa.size());
		logger.debug("verificando sementes ativas...");
		
		mapa.verificarSementeAtiva();
		
		mapa.removerSementeInativa();
		
		logger.debug("total de sementes ativas:" + mapa.size());
	}
}