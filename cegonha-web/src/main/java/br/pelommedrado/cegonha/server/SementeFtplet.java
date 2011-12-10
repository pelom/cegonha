package br.pelommedrado.cegonha.server;

import java.io.File;
import java.io.IOException;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

import br.pelommedrado.cegonha.server.listener.ISementeFtpletListener;

/**
 * @author Andre Leite
 */
public class SementeFtplet extends DefaultFtplet {

	/** Ouvinte de eventos do servidor **/
	private ISementeFtpletListener listener; 

	/** Path base **/
	private String pathBase;

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

		FtpletResult retVal = FtpletResult.DEFAULT;

		//obter endereco do cliente
		final String endereco = session.getClientAddress().getAddress().getHostAddress();
		//nome do arquivo que foi baixado
		final String nomeArquivo = request.getArgument();
		final File file = new File(pathBase + nomeArquivo);
		synchronized (listener) {
			//notificar ouvinte
			listener.terminoDownLoad(endereco, file);
		}

		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.apache.ftpserver.ftplet.DefaultFtplet#onDownloadStart(org.apache.ftpserver.ftplet.FtpSession, org.apache.ftpserver.ftplet.FtpRequest)
	 */
	@Override
	public FtpletResult onDownloadStart(FtpSession session, FtpRequest request)
			throws FtpException, IOException {

		//obter endereco do cliente
		final String endereco = session.getClientAddress().getAddress().getHostAddress();
		//nome do arquivo que foi baixado
		final String nomeArquivo = request.getArgument();

		synchronized (listener) {
			//notificar ouvinte
			listener.iniciandoDownload(endereco, new File(nomeArquivo));
		}

		return super.onDownloadStart(session, request);
	}

	/**
	 * @return the listener
	 */
	public ISementeFtpletListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(ISementeFtpletListener listener) {
		this.listener = listener;
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
}