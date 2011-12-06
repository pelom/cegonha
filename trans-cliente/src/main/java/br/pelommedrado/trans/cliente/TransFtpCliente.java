package br.pelommedrado.trans.cliente;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.download.DownloadManager;
import br.pelommedrado.trans.download.FtpFileChecksum;
import br.pelommedrado.trans.download.FtpFileRecupera;

/**
 * @author Andre Leite
 */
public class TransFtpCliente {
	/** Gerenciador de logs **/
	private static Logger logger = LoggerFactory.getLogger(TransFtpCliente.class);

	/**
	 * 
	 */
	public TransFtpCliente() {
		super();
	}

	/**
	 * 
	 * @param servidor
	 * @param porta
	 * @param usuario
	 * @param senha
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	public FTPClient conectar(String servidor, int porta, String usuario, String senha) 
			throws SocketException, IOException {

		logger.info("iniciando a conexao com o servidor:"+ servidor + " porta:" + porta);

		final FTPClient ftp = new FTPClient();  

		//estabelecar conexao
		ftp.connect(servidor, porta);  

		// verifica se conectou com sucesso!  
		if(FTPReply.isPositiveCompletion(ftp.getReplyCode()))  {
			logger.debug("conexao estabelecida com sucesso, realizando login com o usuario:" + usuario);

			//realizar login
			ftp.login(usuario, senha);  

			return ftp;

		} else {
			logger.debug("nao foi possivel realizar a conexao ela foi recusada");

			//erro ao se conectar  
			ftp.disconnect();

			return null;  
		}
	}

	/**
	 * 
	 * @param ftp
	 * @throws IOException
	 */
	public void desconectar(FTPClient ftp) throws IOException {
		if(ftp != null && ftp.isConnected()) {
			ftp.logout();
			ftp.disconnect();
		}
	}
	
	/**
	 * 
	 * @param ftp
	 * @param fileRemoto
	 * @param fileLocal
	 * @return
	 * @throws IOException
	 */
	public boolean download(FTPClient ftp, String fileRemoto, String fileLocal) 
			throws IOException {

		//a conexao nao esta ativa? 
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			throw new IOException("a conexao nao esta ativa");  
		}

		logger.debug("baixando o arquivo...");

		//ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

		//iniciar o gerenciador de download
		final DownloadManager downloadManager = 
				new DownloadManager(ftp.retrieveFileStream(fileRemoto), fileLocal);

		//iniciar o download gerenciado.
		downloadManager.download();

		//download completou?
		if(ftp.completePendingCommand()) {
			//criar verificador de arquivo
			final FtpFileChecksum fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
			
			//o arquivo esta corrompido?
			if(fCheck.isFileCorrompido(ftp)) {
				
				//obter pacotes corrompidos
				fCheck.scaniarPacoteCorrompido(ftp);
				
				//foi possivel identificar pacotes corrompidos?
				if(fCheck.isPacoteCorrompido()) {
					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					
					final FtpFileRecupera fRecuperar = 
							new FtpFileRecupera(fCheck.getDownloadFile());
					
					fRecuperar.recuperar(ftp.retrieveFileStream(fileRemoto));
					
				} else {
					throw new IOException("nao e possivel recuperar o arquivo");
				}

			} else {
				return true;

			}

		}

		return false;
	} 
}