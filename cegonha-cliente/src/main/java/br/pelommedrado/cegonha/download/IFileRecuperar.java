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
public interface IFileRecuperar {

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public int recuperar() throws IOException;

	/**
	 * 
	 * @param fileDownload
	 */
	public void setFileDownload(FileDownload fileDownload);

	/**
	 * 
	 * @param ftp
	 */
	public void setFtp(FTPClient ftp);
}