/**
 * 
 */
package br.pelommedrado.trans.download;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.cliente.TransFtpCliente;
import br.pelommedrado.trans.util.FileUtils;
import br.pelommedrado.trans.util.ParseChecksumCommand;

/**
 * @author Andre Leite
 */
public class FtpFileChecksum {
	/** Gerenciador de logs **/
	private static Logger logger = LoggerFactory.getLogger(FtpFileChecksum.class);

	/** Representacao do arquivo baixado **/
	private FileDownload downloadFile = null;

	public static void main(String[] args) {
		//criar cliente FTP
		final TransFtpCliente tfSemente = new TransFtpCliente();

		FTPClient ftp = null;

		try {
			//conectar ao servidor
			ftp = tfSemente.conectar("localhost", 2121, "anonymous", "");

			String fileLocal = "/home/pelom/andre.rar";
			String fileRemoto = "andre.rar";

			FtpFileChecksum fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
			fCheck.obterPacoteCorrompido(ftp);

			System.out.println(fCheck.isPacoteCorrompido());
		} catch (IOException e) {
			logger.error("Nao foi possivel conectar a semente", e);

		} finally {
			if(ftp != null && ftp.isConnected()) {
				try {
					ftp.logout();
					ftp.disconnect();

				} catch (IOException e1) {
					logger.error("erro ao desconectar da semente", e1);
				}
			}

		}
	}

	/**
	 * 
	 * @param ftp
	 */
	public FtpFileChecksum(String fileLocal, String fileRemoto) {
		super();

		this.downloadFile = new FileDownload(fileLocal, fileRemoto);
	}

	/**
	 * 
	 * @param ftp
	 * @throws IOException
	 */
	public boolean isFileCorrompido(FTPClient ftp) throws IOException {
		//a conexao nao esta ativa? 
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			throw new IOException("a conexao nao esta ativa");  
		}

		//enviar requisicao do chechsum
		ftp.doCommand(ParseChecksumCommand.CHECKSUM,
				ParseChecksumCommand.parse(downloadFile.getFileRemoto()));

		//operacao realizada com sucesso?
		if(FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

			//obter o valor retornado
			final String checksum = ftp.getReplyString().split(" ")[1].trim();

			return FileUtils.isCorrompido(downloadFile.getFileLocal(), Long.valueOf(checksum));

		} else {
			throw new IOException("nao foi possivel realizar o checksum do arquivo");

		}
	}

	/**
	 * 
	 * @param ftp
	 * @throws NumberFormatException 
	 * @throws IOException 
	 */
	public void obterPacoteCorrompido(FTPClient ftp) throws IOException {
		//a conexao nao esta ativa? 
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			throw new IOException("a conexao nao esta ativa");  
		}

		FileInputStream fileIn = null;
		try {
			//abrir o arquivo
			fileIn = new FileInputStream(downloadFile.getFileLocal());

			//buffer de leitura
			byte[] buffer = new byte[DownloadManager.MAX_BUFFER_SIZE];
			//bytes lidos
			int read = 0;
			//posicao corrente
			int off = 0;

			while ((read = fileIn.read(buffer)) != -1) {
				//enviar requisicao do chechsum
				ftp.doCommand(ParseChecksumCommand.CHECKSUM,
						ParseChecksumCommand.parse(downloadFile.getFileRemoto(), off, read));

				//operacao realizada com sucesso?
				if(FTPReply.isPositiveCompletion(ftp.getReplyCode())) {

					//obter o valor retornado
					final String checksum = ftp.getReplyString().split(" ")[1].trim();

					//os dados lidos sao menor que o buffer?
					if(read != buffer.length) {
						byte[] novo = new byte[read];
						System.arraycopy(buffer, 0, novo, 0, read);

						//bloco esta corrompido?
						if(FileUtils.isCorrompido(novo, Long.valueOf(checksum))) {
							downloadFile.add(new PacoteMal(off, read));
						}

					} else {
						//bloco esta corrompido?
						if(FileUtils.isCorrompido(buffer, Long.valueOf(checksum))) {
							downloadFile.add(new PacoteMal(off, read));
						}

					}

				} else {
					throw new IOException("Nao foi possivel termina a operacao de chechsum");

				}

				off += read;
			}

		} finally {
			if(fileIn != null) {
				fileIn.close();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPacoteCorrompido() {
		return !downloadFile.getPacotes().isEmpty();
	}

	/**
	 * @return the downloadFile
	 */
	public FileDownload getDownloadFile() {
		return downloadFile;
	}
}