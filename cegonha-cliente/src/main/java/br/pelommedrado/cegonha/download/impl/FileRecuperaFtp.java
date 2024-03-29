/**
 * 
 */
package br.pelommedrado.cegonha.download.impl;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.util.Base64;

import br.pelommedrado.cegonha.download.IFileRecuperar;
import br.pelommedrado.cegonha.download.util.FileDownload;
import br.pelommedrado.cegonha.download.util.FilePacote;
import br.pelommedrado.cegonha.parse.ParseGetpkgCommand;

/**
 * @author Andre Leite
 */
public class FileRecuperaFtp implements IFileRecuperar {

	/** Arquivo a ser recuperado **/
	private FileDownload fileDownload = null;

	/** Cliente FTP **/
	private FTPClient ftp;

	/**
	 * Construtor da classe.
	 */
	public FileRecuperaFtp() {
		super();
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public int recuperar() throws IOException {
		//o arquivo nao pode ser recuperado?
		if(!fileDownload.isPodeRecuperar()) {
			throw new IOException("nao foi possivel recuperar o arquivo " + fileDownload.getFileLocal() + 
					" o porcentual de perda ultrapassou a faixa permitida " + fileDownload.calcPorcentualPerdaPacote());
		}
		
		//a conexao nao esta ativa? 
		if(!ftp.isConnected()) {
			throw new IOException("a conexao nao esta ativa");  
		}

		//abrir o arquivo
		final RandomAccessFile fileOut = new 
				RandomAccessFile(fileDownload.getFileLocal(), "rw");

		int n = 0;

		try {
			//varrer os pacotes corrompidos
			for (FilePacote pc : fileDownload.getPacotes()) {

				//configurar posicao de escrita
				fileOut.seek(pc.getOff());

				//enviar requisicao do chechsum
				ftp.doCommand(ParseGetpkgCommand.GETPKG,
						ParseGetpkgCommand.parse(fileDownload.getFileRemoto(), 
								pc.getOff(), pc.getLen()));

				//operacao realizada com sucesso?
				if(FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

					//obter o valor retornado
					final String stgBytes = ftp.getReplyString().split(" ")[1].trim();
					final byte[] bytes = Base64.decodeBase64(stgBytes);

					//recuperar dados
					fileOut.write(bytes);

					n++;

				} else {
					throw new IOException("nao foi possivel realizar o checksum do arquivo");

				}
			}

		} finally {
			// Close file.
			if (fileOut != null) {
				fileOut.close();
			}
		}

		//limpar os pacotes
		fileDownload.getPacotes().clear();
		
		return n;
	}
	
	/**
	 * @param fileDownload the fileDownload to set
	 */
	public void setFileDownload(FileDownload fileDownload) {
		this.fileDownload = fileDownload;
	}

	/**
	 * @param ftp the ftp to set
	 */
	public void setFtp(FTPClient ftp) {
		this.ftp = ftp;
	}
}