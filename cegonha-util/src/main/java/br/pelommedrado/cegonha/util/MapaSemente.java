/**
 * 
 */
package br.pelommedrado.cegonha.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.pelommedrado.cegonha.model.Semente;

/**
 * @author Andre Leite
 */
public class MapaSemente {

	/** Mapa com a lista de sementes **/
	private Map<Integer, Set<Semente> > mapa;

	/**
	 * Construtor da classe.
	 */
	public MapaSemente() {
		super();

		mapa = new HashMap<Integer, Set<Semente>>();
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
		Set<Semente> sementes = mapa.get(key);

		//lista de semente nao existe?
		if(sementes == null) {
			//crair e adicionar a lista 
			sementes = new HashSet<Semente>();

			mapa = Collections.synchronizedMap(mapa);
			synchronized(mapa) {
				mapa.put(key, sementes);
			}
		}

		sementes = Collections.synchronizedSet(sementes);
		synchronized(sementes) {
			//adicionar semente
			sementes.add(semente);
		}
	}

	/**
	 * 
	 * @param chave
	 * @return
	 */
	public Set<Semente> getListaSementes(String chave) {
		//gerar chave
		final Integer key =  new Integer(chave.hashCode());

		Set<Semente> sementes = mapa.get(key);
		if(sementes == null) {
			sementes = new HashSet<Semente>();
		}

		return sementes;
	}

	/**
	 * 
	 */
	public void verificarSementeAtiva() {
		mapa = Collections.synchronizedMap(mapa);

		synchronized(mapa) {
			final Set<Integer> chaves = mapa.keySet();
			//varrer as chaves
			for (Integer integer : chaves) {
				//lista de sementes
				final Set<Semente> sementes = mapa.get(integer);

				for (Semente semente : sementes) {
					semente.setAtiva(Utils.ping(semente.getEndereco()));		
				}
			}
		}
	}

	/**
	 * 
	 */
	public void removerSementeInativa() {
		mapa = Collections.synchronizedMap(mapa);

		synchronized(mapa) {
			final Set<Integer> chaves = mapa.keySet();
			//varrer as chaves
			for (Integer integer : chaves) {
				//lista de sementes
				final Set<Semente> sementes = mapa.get(integer);
				final Set<Semente> clone = new HashSet<Semente>();
				clone.addAll(sementes);
				for (Semente semente : clone) {
					if(!semente.isAtiva()) {
						sementes.remove(semente);
					}	
				}
			}
		}
	}

	/**
	 * 
	 */
	public int size() {
		final Set<Semente> sementeTemp = new HashSet<Semente>();

		mapa = Collections.synchronizedMap(mapa);

		synchronized(mapa) {
			
			final Set<Integer> chaves = mapa.keySet();
			
			//varrer as chaves
			for (Integer integer : chaves) {
				//lista de sementes
				final Set<Semente> sementes = mapa.get(integer);

				sementeTemp.addAll(sementes);
			}
		}

		return sementeTemp.size();
	}
}