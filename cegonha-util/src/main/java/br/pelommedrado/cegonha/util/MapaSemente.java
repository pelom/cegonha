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
	 * @param chave
	 * @return
	 */
	public Set<Semente> getListaSementesAtiva(String chave) {
		Set<Semente> sementes = getListaSementes(chave);

		sementes = Collections.synchronizedSet(sementes);

		synchronized(sementes) {
			if(sementes.isEmpty()) {
				return sementes;
			}

			Set<Semente> sementeAtiva = new HashSet<Semente>();

			for (Semente semente : sementes) {
				if(semente.isAtiva()) {
					sementeAtiva.add(semente);
				}
			}

			return sementeAtiva;
		}
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
		int size = 0;
		
		mapa = Collections.synchronizedMap(mapa);

		synchronized(mapa) {
			final Set<Integer> chaves = mapa.keySet();
			//varrer as chaves
			for (Integer integer : chaves) {
				//lista de sementes
				final Set<Semente> sementes = mapa.get(integer);
			
				size += sementes.size();
			}
		}
		
		return size;
	}
}