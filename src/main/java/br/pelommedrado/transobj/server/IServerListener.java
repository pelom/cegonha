/**
 * 
 */
package br.pelommedrado.transobj.server;

/**
 * @author Andre Leite
 */
public interface IServerListener {

	/**
	 * Metodo executado apos a termino de um download
	 * 
	 * @param endereco
	 * 		Endereco do cliente que recebeu o arquivo no download
	 * 
	 * @param nomeArquivo
	 * 		Nome do arquivo
	 */
	public void terminoDownLoad(String endereco, String nomeArquivo);
}