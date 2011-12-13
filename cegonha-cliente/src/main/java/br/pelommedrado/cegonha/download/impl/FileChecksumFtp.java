/**
 * 
 */
package br.pelommedrado.cegonha.download.impl;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.cegonha.download.IFileChecksum;
import br.pelommedrado.cegonha.download.util.FileDownload;
import br.pelommedrado.cegonha.download.util.FilePacote;
import br.pelommedrado.cegonha.parse.ParseChecksumCommand;
import br.pelommedrado.cegonha.util.FileUtils;

/**
 * @author Andre Leite
 */
public class FileChecksumFtp implements IFileChecksum {
	/** Gerenciador de logs **/
	private static Logger logger = LoggerFactory.getLogger(FileChecksumFtp.class);

	/** Representacao do arquivo baixado **/
	private FileDownload fileDownload = null;

	/**
	 * Construtor da classe.
	 */
	public FileChecksumFtp() {
		super();
	}

	/**
	 * 
	 */
	public boolean verificarFileCorrompido(FTPClient ftp) throws IOException {
		//a conexao nao esta ativa? 
		if(!ftp.isConnected()) {
			throw new IOException("a conexao nao esta ativa");  
		}

		//enviar requisicao do chechsum
		ftp.doCommand(ParseChecksumCommand.CHECKSUM,
				ParseChecksumCommand.parse(fileDownload.getFileRemoto()));

		//operacao realizada com sucesso?
		if(FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

			//obter o valor retornado
			final long checksum = ParseChecksumCommand.parseChecksum(ftp.getReplyString());

			return FileUtils.isCorrompido(fileDownload.getFileLocal(), checksum);

		} else {
			throw new IOException("nao foi possivel realizar o checksum do arquivo");

		}
	}

	/**
	 * 
	 */
	public int verificarPacoteCorrompido(FTPClient ftp) throws IOException {
		logger.info("scaniar pacotes corrompidos");

		//a conexao nao esta ativa? 
		if(!ftp.isConnected()) {
			throw new IOException("a conexao nao esta ativa");  
		}

		FileInputStream fileIn = null;
		//buffer de leitura
		byte[] buffer = new byte[DownloadManager.MAX_BUFFER_SIZE];
		//bytes lidos
		int read = 0;
		//posicao corrente
		int off = 0;

		try {
			//abrir o arquivo
			fileIn = new FileInputStream(fileDownload.getFileLocal());

			while ((read = fileIn.read(buffer)) != -1) {
				//enviar requisicao do chechsum
				ftp.doCommand(ParseChecksumCommand.CHECKSUM,
						ParseChecksumCommand.parse(fileDownload.getFileRemoto(), off, read));

				//operacao realizada com sucesso?
				if(FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

					//obter o valor retornado
					final long checksum = ParseChecksumCommand.parseChecksum(ftp.getReplyString());

					//os dados lidos sao menor que o buffer?
					if(read != buffer.length) {
						byte[] novo = new byte[read];
						System.arraycopy(buffer, 0, novo, 0, read);

						//bloco esta corrompido?
						if(FileUtils.isCorrompido(novo, Long.valueOf(checksum))) {
							fileDownload.add(new FilePacote(off, read));
						}

					} else {
						//bloco esta corrompido?
						if(FileUtils.isCorrompido(buffer, Long.valueOf(checksum))) {
							fileDownload.add(new FilePacote(off, read));
						}

					}

				} else {
					throw new IOException("Nao foi possivel termina a operacao de chechsum");

				}

				off += read;
			}

		} finally {
			logger.debug(fileDownload.getPacotes().size() + " pacotes encontrados");

			if(fileIn != null) {
				fileIn.close();
			}
		}
		
		return fileDownload.getNumPacoteCorrompido();
	}

	/**
	 * 
	 */
	public boolean isPacoteCorrompido() {
		return !fileDownload.getPacotes().isEmpty();
	}

	/**
	 * 
	 */
	public FileDownload getFileDownload() {
		return fileDownload;
	}
	
	/**
	 * 
	 * @param fileDownload
	 */
	public void setFileDownload(FileDownload fileDownload) {
		this.fileDownload = fileDownload;
	}
}