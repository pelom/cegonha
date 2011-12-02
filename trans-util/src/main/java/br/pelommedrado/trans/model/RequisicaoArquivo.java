/**
 * 
 */
package br.pelommedrado.trans.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Andre Leite
 */
@XmlRootElement(name="requisicaoArquivo")
public class RequisicaoArquivo implements Serializable {
	private static final long serialVersionUID = 3621395144809028862L;

	/** Indica se a semente tem o arquivo **/
	private boolean temArquivo = false;

	/** Tem disponiblidade **/
	private boolean disponivel = false;

	/**
	 * Construtor da classe.
	 */
	public RequisicaoArquivo() {
		super();
	}

	/**
	 * @return the temArquivo
	 */
	public boolean isTemArquivo() {
		return temArquivo;
	}

	/**
	 * @return the disponivel
	 */
	public boolean isDisponivel() {
		return disponivel;
	}

	/**
	 * @param temArquivo the temArquivo to set
	 */
	public void setTemArquivo(boolean temArquivo) {
		this.temArquivo = temArquivo;
	}

	/**
	 * @param disponivel the disponivel to set
	 */
	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
}