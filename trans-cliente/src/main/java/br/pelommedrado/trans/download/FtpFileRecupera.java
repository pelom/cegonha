/**
 * 
 */
package br.pelommedrado.trans.download;

import java.util.List;

/**
 * @author Andre Leite
 */
public class FtpFileRecupera {

	/** Lista com os pacotes corrompidos **/
	private List<PacoteMal> pacotes;

	/**
	 * 
	 * @param pacotes
	 */
	private FtpFileRecupera(List<PacoteMal> pacotes) {
		super();

		this.pacotes = pacotes;
	}

	public void recuperar() {

	}
}