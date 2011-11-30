/**
 * 
 */
package br.pelommedrado.transobj.model;

/**
 * @author Andre Leite
 */
public class Semente {

	/** Endereco da semente **/
	private String endereco = null;

	/**
	 * Construtor da classe
	 * 
	 * @param endereco
	 * 		Endereco da semente.
	 */
	public Semente(String endereco) {
		super();
		
		this.endereco = endereco;
	}
	
	/**
	 * @return the endereco
	 */
	public String getEndereco() {
		return endereco;
	}

	/**
	 * @param endereco the endereco to set
	 */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
}