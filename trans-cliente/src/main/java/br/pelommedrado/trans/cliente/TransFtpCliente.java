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

	/** Tentar recuperar o arquivo caso esteja corrompido **/
	private boolean recuperar = true;

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
	public boolean conectar() 
			throws SocketException, IOException {

		logger.info("iniciando conexao com o servidor:" + servidor + " porta:" + porta);

		//criar cliente ftp
		ftp = new FTPClient();  

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
			ftp = null;
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
	public boolean download(String fileRemoto, String fileLocal) 
			throws IOException {

		//a conexao nao esta ativa? 
		if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
			throw new IOException("a conexao nao esta ativa");  
		}

		logger.debug("baixando o arquivo...");

		//iniciar o gerenciador de download
		final DownloadManager downloadManager = 
				new DownloadManager(ftp, fileLocal, fileRemoto);

		//download completou?
		if(downloadManager.download()) {
			//criar verificador de arquivo
			final FtpFileChecksum fCheck = new FtpFileChecksum(fileLocal, fileRemoto);

			//o arquivo esta corrompido e a recuperacao esta ativa?
			if(fCheck.verificarFileCorrompido(ftp)) {
				//recuperar arquivo
				return recuperarFile(fCheck);

			} else {
				return true;

			}
		}

		return false;
	}

	/**
	 * 
	 * @param fCheck
	 * @param ftp
	 * @return
	 * @throws IOException
	 */
	private boolean recuperarFile(final FtpFileChecksum fCheck) throws IOException {
		if(!recuperar) {
			return false;
		}

		logger.debug("preparar para recuperar o arquivo corrompido");

		//scaniar os pacotes corrompidos
		fCheck.scaniarPacoteCorrompido(ftp);

		//foi possivel identificar pacotes corrompidos?
		if(fCheck.isPacoteCorrompido()) {
			//obter numero de pacotes corrompidos
			int nPkg = fCheck.getDownloadFile().getPacotes().size();

			final FtpFileRecupera fRecuperar = 
					new FtpFileRecupera(ftp, fCheck.getDownloadFile());

			//numero de pacotes recuperados e igual?
			if(fRecuperar.recuperar() != nPkg) {
				return false;
			}

			return true;

		} else {
			throw new IOException("nao foi possivel recuperar o arquivo");
		}
	}

	/**
	 * @return the recuperar
	 */
	public boolean isRecuperar() {
		return recuperar;
	}

	/**
	 * @param recuperar the recuperar to set
	 */
	public void setRecuperar(boolean recuperar) {
		this.recuperar = recuperar;
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