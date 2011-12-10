/**
 * 
 */
package br.pelommedrado.transobj.server.listener;

import java.io.File;

/**
 * @author Andre Leite
 */
public interface ISementeFtpletListener {

	/**
	 * Metodo executado apos a termino de um download
	 * 
	 * @param endereco
	 * 		Endereco do cliente que recebeu o arquivo no download
	 * 
	 * @param arquivo
	 * 		objeto File arquivo
	 */
	public void terminoDownLoad(String endereco, File arquivo);

	/**
	 * Metodo executado apos o inicio de um download
	 * @param endereco
	 * 		Endereco do cliente que recebeu o arquivo no download
	 * 
	 * @param arquivo
	 * 		objeto File arquivo
	 */
	public void iniciandoDownload(String endereco, File arquivo);
}