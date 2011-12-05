package br.pelommedrado.trans.cliente;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.util.DownloadManager;

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
	 * @param fileIn
	 * @param fileOut
	 * @return
	 * @throws IOException
	 */
	public boolean download(FTPClient ftp, String fileIn, String fileOut) 
			throws IOException {

		//a conexao nao esta ativa? 
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			throw new IOException("a conexao nao esta ativa");  
		}

		logger.debug("baixando o arquivo...");

		//obter as informacoes do arquivo a serem baixado
		final FTPFile[] remoteFiles = ftp.listFiles(fileIn);
		//tamanho do arquivo
		final long length = remoteFiles[0].getSize();
		//obter entrada de dados
		final InputStream in = ftp.retrieveFileStream(fileIn);

		//iniciar o gerenciador de download
		final DownloadManager downloadManager = 
				new DownloadManager(ftp, fileIn, in, fileOut, length);

		//iniciar o download gerenciado.
		downloadManager.iniciar();

		//download completado
		return ftp.completePendingCommand();
	} 
}