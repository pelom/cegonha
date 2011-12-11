/**
 * 
 */
package br.pelommedrado.cegonha.download;

import java.io.IOException;

/**
 * @author Andre Leite
 */
public interface IDownloadManager {

	/**
	 * 
	 * @param fileChecksum
	 */
	public void setFileChecksum(IFileChecksum fileChecksum);

	/**
	 * 
	 * @param fileRecupera
	 */
	public void setFileRecupera(IFileRecuperar fileRecuperar);

	/**
	 * 
	 * @param recuperar
	 */
	public void setRecuperar(boolean recuperar);
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean download() throws IOException;
}