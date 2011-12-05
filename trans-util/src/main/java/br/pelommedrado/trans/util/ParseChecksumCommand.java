/**
 * 
 */
package br.pelommedrado.trans.util;

/**
 * @author Andre Leite
 */
public class ParseChecksumCommand {

	/** Nome do comando **/
	public static final String CHECKSUM = "CHECKSUM";

	/** Verificacao de bloco **/
	public static final int CHECKSUM_BLOCO = 0;

	/** Verificacao de arquivo **/
	public static final int CHECKSUM_ARQUIVO = 1;

	/** Arquivo **/
	private String arquivo = null;

	/** Tipo de verificacao **/
	private int tipoVericacao = CHECKSUM_BLOCO;

	/** inicio da leitura **/
	private int off = 0;

	/** Quantidade **/
	private int len = 0;

	/**
	 * 
	 * @param argumento
	 */
	public ParseChecksumCommand(String argumento) {
		super();

		//recuperar argumentos
		final String[] args = argumento.split(" ");

		this.arquivo = args[0];
		this.tipoVericacao = Integer.valueOf(args[1]);

		if(this.tipoVericacao == CHECKSUM_BLOCO) {
			this.off = Integer.valueOf(args[2]);
			this.len = Integer.valueOf(args[3]);
		}
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	public static String parse(String arquivo) {
		return arquivo + " " + CHECKSUM_ARQUIVO;
	}

	/**
	 * 
	 * @param arquivo
	 * @param tipoVerificacao
	 * @param off
	 * @param len
	 * @return
	 */
	public static String parse(String arquivo, int off, int len) {
		return arquivo + " " + CHECKSUM_BLOCO + " " + off + " " + len;
	}

	/**
	 * @return the arquivo
	 */
	public String getArquivo() {
		return arquivo;
	}

	/**
	 * @return the tipoVericacao
	 */
	public int getTipoVericacao() {
		return tipoVericacao;
	}

	/**
	 * @return the off
	 */
	public int getOff() {
		return off;
	}

	/**
	 * @return the len
	 */
	public int getLen() {
		return len;
	}	
}