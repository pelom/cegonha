package br.pelommedrado.transobj.server;

import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import br.pelommedrado.trans.model.Semente;
import br.pelommedrado.trans.util.MapaSemente;

/**
 * @author Andre Leite
 */
public class ServidorFtp implements ISementeFtpletListener, InitializingBean {

	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(ServidorFtp.class);

	/** Servidor FTP **/
	private FtpServer ftpServer = null;

	/** Mapa de sementes **/
	private MapaSemente mapaSemente = null;

	/** Numero de pessoa baixando **/
	private int count = 0;

	/**
	 * Construtor da classe.
	 */
	public ServidorFtp() {
		super();
	}

	/**
	 * 
	 */
	@Override
	public void terminoDownLoad(String endereco, File arquivo) {
		logger.info("termino do download endereco:" + endereco + " arquivo:" + arquivo);

		count--;

		//criar nova semente
		final Semente semente = new Semente(endereco);

		//adicionar novo semente
		mapaSemente.addSemente(arquivo.getName(), semente);
	}

	/**
	 * 
	 */
	@Override
	public void iniciandoDownload(String endereco, File arquivo) {
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

	/**
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(ftpServer, "ftpServer required"); 
		Assert.notNull(mapaSemente, "mapaSemente required");
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
}