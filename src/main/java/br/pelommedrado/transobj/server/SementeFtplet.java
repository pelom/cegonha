package br.pelommedrado.transobj.server;

import java.io.IOException;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

/**
 * @author Andre Leite
 */
public class SementeFtplet extends DefaultFtplet {

	/** Ouvinte de eventos do servidor **/
	private IServerListener listener; 
	
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
	public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		
		//obter endereco do cliente
		final String endereco = session.getClientAddress().getAddress().getHostAddress();
		//nome do arquivo que foi baixado
		final String nomeArquivo = request.getArgument();
		
		synchronized (listener) {
			//notificar ouvinte
			listener.terminoDownLoad(endereco, nomeArquivo);
		}
		
		return super.onDownloadEnd(session, request);
	}

	/**
	 * @return the listener
	 */
	public IServerListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(IServerListener listener) {
		this.listener = listener;
	}
}