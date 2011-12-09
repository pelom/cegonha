/**
 * 
 */
package br.pelommedrado.trans.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.pelommedrado.trans.model.Semente;

/**
 * @author Andre Leite
 */
public class MapaSemente {

	/** Mapa com a lista de sementes **/
	private Map<Integer, List<Semente> > mapa;

	/**
	 * Construtor da classe.
	 */
	public MapaSemente() {
		super();

		mapa = new HashMap<Integer, List<Semente>>();
	}

	/**
	 * Adicionar semente no mapa de sementes
	 * 
	 * @param chave
	 * @param semente
	 */
	public void addSemente(String chave, Semente semente) {
		//gerar chave
		final Integer key =  new Integer(chave.hashCode());

		//obter lista de sementes
		List<Semente> sementes = mapa.get(key);

		//lista de semente nao existe?
		if(sementes == null) {
			//crair e adicionar a lista 
			sementes = new Vector<Semente>();
			mapa.put(key, sementes);
		}

		//adicionar semente
		sementes.add(semente);
	}

	/**
	 * 
	 * @param chave
	 * @return
	 */
	public List<Semente> getListaSementes(String chave) {
		//gerar chave
		final Integer key =  new Integer(chave.hashCode());

		List<Semente> sementes = mapa.get(key);
		if(sementes == null) {
			sementes = new Vector<Semente>();
		}

		return sementes;
	}
}