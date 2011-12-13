/**
 * 
 */
package br.pelommedrado.cegonha.download.util;

import java.util.ArrayList;
import java.util.List;

import br.pelommedrado.cegonha.download.impl.DownloadManager;

/**
 * @author Andre Leite
 */
public class FileDownload {

	/** Arquivo local **/
	private String fileLocal = null;

	/** Arquivo remoto **/
	private String fileRemoto = null;

	/** Pacotes corrompidos **/
	private List<FilePacote> pacotes;

	/** Tamanho do arquivo **/
	private long len = -1;

	/** Indica se o arquivo deve ser recuperado **/
	private boolean recuperar = true;

	/** Maximo de porcentual de perda para a recuperar **/ 
	private int porcentualMax = 10;
	
	/**
	 * 
	 * @param fileLocal
	 * @param fileRemoto
	 */
	public FileDownload(String fileLocal, String fileRemoto) {
		super();

		this.fileLocal = fileLocal;
		this.fileRemoto = fileRemoto;
		this.pacotes = new ArrayList<FilePacote>();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPodeRecuperar() {
		return calcPorcentualPerdaPacote() <= porcentualMax;
	}
	
	/**
	 * 
	 * @param len
	 * @param loss
	 * @param buf
	 * @return
	 */
	public int calcPorcentualPerdaPacote() {
		final int loss = getNumPacoteCorrompido();
		final double tPck = Math.ceil( ((double) getLength() / DownloadManager.MAX_BUFFER_SIZE) );
		final double perda =  ((double) loss / tPck);

		return (int) Math.ceil(perda * 100);
	}
	
	/**
	 * 
	 * @param pacote
	 */
	public void add(FilePacote pacote) {
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
	public List<FilePacote> getPacotes() {
		return pacotes;
	}

	/**
	 * 
	 * @return
	 */
	public int getNumPacoteCorrompido() {
		return pacotes.size();
	}
	
	/**
	 * @return the size
	 */
	public long getLength() {
		return len;
	}

	/**
	 * @return the recuperar
	 */
	public boolean isRecuperar() {
		return recuperar;
	}
	
	/**
	 * @param recuperar the recuperar to set
	 */
	public void setRecuperar(boolean recuperar) {
		this.recuperar = recuperar;
	}

	/**
	 * @param porcentualMax the porcentualMax to set
	 */
	public void setPorcentualMax(int porcentualMax) {
		this.porcentualMax = porcentualMax;
	}

	/**
	 * @param len the len to set
	 */
	public void setLen(long len) {
		this.len = len;
	}
}