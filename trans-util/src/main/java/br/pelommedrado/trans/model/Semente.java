/**
 * 
 */
package br.pelommedrado.trans.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Andre Leite
 */
@XmlRootElement(name="semente")
public class Semente implements Serializable {
	private static final long serialVersionUID = 2485994588577555516L;

	/** Endereco da semente **/
	private String endereco = null;

	/**
	 * Construtor da classe
	 */
	public Semente() {
		super();
	}

	/**
	 * Construtor da classe
	 * 
	 * @param endereco
	 * 		Endereco da semente.
	 */
	public Semente(String endereco) {
		this();

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endereco == null) ? 0 : endereco.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Semente other = (Semente) obj;
		if (endereco == null) {
			if (other.endereco != null)
				return false;
		} else if (!endereco.equals(other.endereco))
			return false;
		return true;
	}
}