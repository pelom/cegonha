/**
 * 
 */
package br.pelommedrado.cegonha.download;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.util.Base64;

import br.pelommedrado.cegonha.parse.ParseGetpkgCommand;

/**
 * @author Andre Leite
 */
public class FtpFileRecupera {

	/** Arquivo a ser recuperado **/
	private FileDownload fileDownload = null;
	
	/** Cliente FTP **/
	private FTPClient ftp;

	/**
	 * Construtor da classe.
	 * 
	 * @param ftp
	 * 		Cliente FTP
	 * 
	 * @param fileDownload
	 * 		Arquivo para ser recuperado
	 */
	public FtpFileRecupera(FTPClient ftp, FileDownload fileDownload) {
		super();

		this.ftp = ftp;
		this.fileDownload = fileDownload;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public int recuperar() throws IOException {
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
			for (PacoteMal pc : fileDownload.getPacotes()) {

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
				try {
					fileOut.close();
				} catch (Exception e) {}
			}
		}

		return n;
	}
}