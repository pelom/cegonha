package br.pelommedrado.transobj.server;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.transobj.model.Semente;
import br.pelommedrado.transobj.util.MapaSemente;

/**
 * @author Andre Leite
 */
public class AppServer implements IServerListener {
	
	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(AppServer.class);;
	
	/** Servidor FTP **/
	private FtpServer ftpServer = null;

	/** Mapa de sementes **/
	private MapaSemente mapaSemente = null;
	
	/**
	 * Construtor da classe.
	 */
	public AppServer() {
		super();
		
		//criar mapa de sementes
		mapaSemente = new MapaSemente();
	}

	/**
	 * 
	 */
	@Override
	public void terminoDownLoad(String endereco, String nomeArquivo) {
		logger.info("termino do download endereco:" + endereco + " arquivo:" + nomeArquivo);
		
		//criar nova semente
		final Semente semente = new Semente(endereco);
		
		//adicionar novo semente
		mapaSemente.addSemente(nomeArquivo, semente);
	}
	
	/**
	 * Iniciar servidor FTP
	 * 
	 * @throws FtpException
	 */
	public void start() throws FtpException {
		ftpServer.start();
	}

	/**
	 * @return the ftpServer
	 */
	public FtpServer getFtpServer() {
		return ftpServer;
	}

	/**
	 * @param ftpServer the ftpServer to set
	 */
	public void setFtpServer(FtpServer ftpServer) {
		this.ftpServer = ftpServer;
	}
}