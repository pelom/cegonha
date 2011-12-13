package br.pelommedrado.cegonha.cliente;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.cegonha.download.impl.DownloadManager;
import br.pelommedrado.cegonha.download.util.FileDownload;

/**
 * @author Andre Leite
 */
public class FtpCliente {
	/** Gerenciador de logs **/
	private static Logger logger = LoggerFactory.getLogger(FtpCliente.class);

	/** Servidor **/
	private String servidor = null;

	/** Numero da porta **/
	private int porta= -1;

	/** Usuario do ftp **/
	private String usuario = null;

	/** Senha para logar no ftp **/
	private String senha = null;

	/** cliente ftp **/
	private FTPClient ftp = null;

	/**
	 * Construtor da classe.
	 */
	public FtpCliente() {
		super();

		//criar cliente ftp
		ftp = new FTPClient();  
	}

	/**
	 * 
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	public boolean conectar() throws SocketException, IOException {
		logger.info("iniciando conexao com o servidor:" + servidor + " porta:" + porta);

		//a conexao esta ativa? 
		if(ftp.isConnected()) {
			throw new IOException("a conexao esta ativa");
		}

		//estabelecar conexao
		ftp.connect(servidor, porta);  

		// verifica se conectou com sucesso!  
		if(FTPReply.isPositiveCompletion(ftp.getReplyCode()))  {
			logger.debug("conexao estabelecida com sucesso, realizando login com o usuario:" + usuario);

			//realizar login
			ftp.login(usuario, senha);  

			return true;

		} else {
			logger.debug("nao foi possivel realizar a conexao ela foi recusada");

			//erro ao se conectar  
			ftp.disconnect();

			return false;  
		}
	}

	/**
	 * 
	 * @param ftp
	 * @throws IOException
	 */
	public void desconectar() throws IOException {
		if(ftp != null && ftp.isConnected()) {
			ftp.logout();
			ftp.disconnect();
		}
	}

	/**
	 * 
	 * @param fileRemoto
	 * @param fileLocal
	 * @param recuperar
	 * @return
	 * @throws IOException
	 */
	public boolean download(FileDownload fileDownload) throws IOException {
		//a conexao nao esta ativa? 
		if(!ftp.isConnected()) {
			throw new IOException("a conexao nao esta ativa");  
		}

		//obter informacoes do arquivo a ser baixado
		final FTPFile ftpFile = ftp.listFiles(fileDownload.getFileRemoto())[0];
		fileDownload.setLen(ftpFile.getSize());
		
		logger.debug("baixando o arquivo...");

		//iniciar o gerenciador de download
		final DownloadManager downloadManager = 
				new DownloadManager(ftp, fileDownload);

		return downloadManager.download();
	}

	/**
	 * @return the servidor
	 */
	public String getServidor() {
		return servidor;
	}

	/**
	 * @return the porta
	 */
	public int getPorta() {
		return porta;
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @return the senha
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * @param servidor the servidor to set
	 */
	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	/**
	 * @param porta the porta to set
	 */
	public void setPorta(int porta) {
		this.porta = porta;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * @param senha the senha to set
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * @return the ftp
	 */
	public FTPClient getFtp() {
		return ftp;
	}

	/**
	 * @param ftp the ftp to set
	 */
	public void setFtp(FTPClient ftp) {
		this.ftp = ftp;
	} 
}