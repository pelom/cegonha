/**
 * 
 */
package br.pelommedrado.trans.util;

import org.springframework.expression.ParseException;

/**
 * @author Andre Leite
 */
public class ParseChecksumCommand extends ParseGetpkgCommand {

	/** Nome do comando **/
	public static final String CHECKSUM = "CHECKSUM";

	/** Verificacao de bloco **/
	public static final int CHECKSUM_BLOCO = 0;

	/** Verificacao de arquivo **/
	public static final int CHECKSUM_ARQUIVO = 1;

	/** Tipo de verificacao **/
	private int tipoVericacao = CHECKSUM_BLOCO;

	/**
	 * 
	 * @param argumento
	 */
	public ParseChecksumCommand(String argumento) {
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
	 * @param reply
	 * @return
	 */
	public static long parseChecksum(String reply) {
		long checksum = -1;

		try {
			checksum = Long.valueOf(reply.split(" ")[1].trim());

		} catch (Exception e) {
			throw new ParseException(1, reply);
		}

		return checksum;
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
	 * @return the tipoVericacao
	 */
	public int getTipoVericacao() {
		return tipoVericacao;
	}	
}