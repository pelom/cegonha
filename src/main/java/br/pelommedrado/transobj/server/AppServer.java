package br.pelommedrado.transobj.server;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import br.pelommedrado.transobj.model.Semente;
import br.pelommedrado.transobj.util.MapaSemente;

/**
 * @author Andre Leite
 */
public class AppServer implements IServerListener, InitializingBean {

	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(AppServer.class);;

	/** Servidor FTP **/
	private FtpServer ftpServer = null;

	/** Mapa de sementes **/
	private MapaSemente mapaSemente = null;

	/** Numero de pessoa baixando **/
	private int count = 0;
	
	/**
	 * Construtor da classe.
	 */
	public AppServer() {
		super();
	}

	/**
	 * 
	 */
	@Override
	public void terminoDownLoad(String endereco, String nomeArquivo) {
		logger.info("termino do download endereco:" + endereco + " arquivo:" + nomeArquivo);

		count--;
		
		//criar nova semente
		final Semente semente = new Semente(endereco);

		//adicionar novo semente
		mapaSemente.addSemente(nomeArquivo, semente);
	}

	/**
	 * 
	 */
	@Override
	public void iniciandoDownload(String endereco, String nomeArquivo) {
		// TODO Auto-generated method stub
		count++;
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

	/**
	 * @return the mapaSemente
	 */
	public MapaSemente getMapaSemente() {
		return mapaSemente;
	}

	/**
	 * @param mapaSemente the mapaSemente to set
	 */
	public void setMapaSemente(MapaSemente mapaSemente) {
		this.mapaSemente = mapaSemente;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(ftpServer, "ftpServer required"); 
		Assert.notNull(mapaSemente, "mapaSemente required");
		
		start();
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
}