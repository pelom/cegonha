/**
 * 
 */
package br.pelommedrado.cegonha.download;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre Leite
 */
public class FileDownload {

	/** Arquivo local **/
	private String fileLocal = null;

	/** Arquivo remoto **/
	private String fileRemoto = null;

	/** Pacotes corrompidos **/
	private List<FileFtpPacote> pacotes;

	/**
	 * 
	 * @param fileLocal
	 * @param fileRemoto
	 */
	public FileDownload(String fileLocal, String fileRemoto) {
		super();

		this.fileLocal = fileLocal;
		this.fileRemoto = fileRemoto;
		this.pacotes = new ArrayList<FileFtpPacote>();
	}
	
	/**
	 * 
	 * @param pacote
	 */
	public void add(FileFtpPacote pacote) {
		this.pacotes.add(pacote);
	}

	/**
	 * @return the fileLocal
	 */
	public String getFileLocal() {
		return fileLocal;
	}

	/**
	 * @return the fileRemoto
	 */
	public String getFileRemoto() {
		return fileRemoto;
	}

	/**
	 * @return the pacotes
	 */
	public List<FileFtpPacote> getPacotes() {
		return pacotes;
	}
}