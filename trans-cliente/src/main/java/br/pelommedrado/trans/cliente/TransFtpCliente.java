package br.pelommedrado.trans.cliente;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * @param dirIn
	 * @param dirOut
	 * @param file
	 * @throws IOException
	 */
	public void download(FTPClient ftp, String dirIn, String dirOut, String file) 
			throws IOException {

		//a conexao nao esta ativa? 
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) { 
			throw new IOException("a conexao nao esta ativa");  
		}

		//arquivo remoto
		String remoteFile = dirIn + "/" + file;

		//write the contents of the remote file to a FileOutputStream
		final OutputStream outStream = 
				new FileOutputStream( dirOut + "/" + file);

		ftp.retrieveFile(remoteFile, outStream);

		IOUtils.closeQuietly( outStream );

		ftp.disconnect();
	} 
}