/**
 * 
 */
package br.pelommedrado.cegonha.download;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import br.pelommedrado.cegonha.download.util.FileDownload;

/**
 * @author Andre Leite
 */
public interface IFileChecksum {

	/**
	 * 
	 * @param ftp
	 * @return
	 * @throws IOException
	 */
	public boolean verificarFileCorrompido(FTPClient ftp) 
			throws IOException;

	/**
	 * 
	 * @param ftp
	 * @throws IOException
	 */
	public void verificarPacoteCorrompido(FTPClient ftp) 
			throws IOException;

	/**
	 * 
	 * @return
	 */
	public boolean isPacoteCorrompido();

	/**
	 * 
	 * @return
	 */
	public FileDownload getDownloadFile();
}