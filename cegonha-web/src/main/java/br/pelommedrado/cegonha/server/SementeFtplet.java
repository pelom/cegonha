package br.pelommedrado.cegonha.server;

import java.io.File;
import java.io.IOException;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import br.pelommedrado.cegonha.model.Semente;
import br.pelommedrado.cegonha.util.MapaSemente;

/**
 * @author Andre Leite
 */
public class SementeFtplet extends DefaultFtplet implements InitializingBean {
	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(SementeFtplet.class);

	/** Path base **/
	private String pathBase;

	/** Mapa de sementes **/
	private MapaSemente mapaSemente = null;

	/** Numero de pessoa baixando **/
	public static int count = 0;

	/**
	 * Construtor da classe.
	 */
	public SementeFtplet() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.DefaultFtplet#onDownloadEnd(org.apache.ftpserver.ftplet.FtpSession, org.apache.ftpserver.ftplet.FtpRequest)
	 */
	@Override
	public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) 
			throws FtpException, IOException {

		final FtpletResult retVal = FtpletResult.DEFAULT;

		//obter endereco do cliente
		final String endereco = session.getClientAddress().getAddress().getHostAddress();
		//nome do arquivo que foi baixado
		final String nomeArquivo = request.getArgument();

		//criar arquivo
		final File file = new File(pathBase + nomeArquivo);

		logger.info("termino do download endereco:" + endereco + " arquivo:" + file);

		count--;

		//criar nova semente
		final Semente semente = new Semente(endereco);

		//adicionar novo semente
		mapaSemente.addSemente(file.getName(), semente);

		logger.debug("downloads ativos: " + count);

		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.DefaultFtplet#onDownloadStart(org.apache.ftpserver.ftplet.FtpSession, org.apache.ftpserver.ftplet.FtpRequest)
	 */
	@Override
	public FtpletResult onDownloadStart(FtpSession session, FtpRequest request)
			throws FtpException, IOException {

		final FtpletResult retVal = FtpletResult.DEFAULT;

		//obter endereco do cliente
		final String endereco = session.getClientAddress().getAddress().getHostAddress();
		//nome do arquivo que foi baixado
		final String nomeArquivo = request.getArgument();
		//criar arquivo
		final File file = new File(pathBase + nomeArquivo);

		logger.info("inicio do download endereco:" + endereco + " arquivo:" + file);

		count++;

		logger.debug("downloads ativos: " + count);

		return retVal;

	}

	/**
	 * 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(mapaSemente, "mapaSemente required");
	}

	/**
	 * @return the pathBase
	 */
	public String getPathBase() {
		return pathBase;
	}

	/**
	 * @param pathBase the pathBase to set
	 */
	public void setPathBase(String pathBase) {
		this.pathBase = pathBase;
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
}