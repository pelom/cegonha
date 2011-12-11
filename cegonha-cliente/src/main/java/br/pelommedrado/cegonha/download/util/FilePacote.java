package br.pelommedrado.cegonha.download.util;

/**
 * @author Andre Leite
 */
public class FilePacote {

	/** Posicao inical **/
	private int off;

	/** Numero de bytes **/
	private int len;

	/**
	 * Construtor da classe.
	 * 
	 * @param off
	 * 		Posicao inicial
	 * 
	 * @param len
	 * 		Numero de bytes
	 */
	public FilePacote(int off, int len) {
		super();

		this.off = off;
		this.len = len;
	}

	/**
	 * @return the off
	 */
	public int getOff() {
		return off;
	}

	/**
	 * @return the len
	 */
	public int getLen() {
		return len;
	}
}