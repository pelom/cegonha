/**
 * 
 */
package br.pelommedrado.trans.util;

/**
 * @author Andre Leite
 */
public class ParseGetpkgCommand {

	/** Nome do comando **/
	public static final String GETPKG = "GETPKG";

	/** Arquivo **/
	protected String arquivo = null;

	/** inicio da leitura **/
	protected int off = 0;

	/** Quantidade **/
	protected int len = 0;

	protected ParseGetpkgCommand(){}

	/**
	 * 
	 * @param argumento
	 */
	public ParseGetpkgCommand(String argumento) {
		super();

		//recuperar argumentos
		final String[] args = argumento.split(" ");

		this.arquivo = args[0];
		this.off = Integer.valueOf(args[1]);
		this.len = Integer.valueOf(args[2]);
	}

	/**
	 * 
	 * @param arquivo
	 * @param tipoVerificacao
	 * @param off
	 * @param len
	 * @return
	 */
	public static String parse(String arquivo, int off, long len) {
		return arquivo  + " " + off + " " + len;
	}

	/**
	 * @return the arquivo
	 */
	public String getArquivo() {
		return arquivo;
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