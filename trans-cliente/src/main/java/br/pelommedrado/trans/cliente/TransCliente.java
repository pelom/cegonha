/**
 * 
 */
package br.pelommedrado.trans.cliente;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.model.RequisicaoArquivo;
import br.pelommedrado.trans.model.Semente;

/**
 * @author Andre Leite
 */
public class TransCliente {

	/** Gerenciador de logs **/
	private static Logger logger = LoggerFactory.getLogger(TransFtpCliente.class);

	/** Cliente Web service **/
	private TransWebServiceCliente wsServidor = null;

	/** Cliente Web service local **/
	private TransWebServiceCliente wsLocal = null;

	/** usuario **/
	private String usuarioFtp = "";

	/** senha **/
	private String senhaFtp = "";

	/** Servidor **/
	private String servidorFtp = null;

	/** Numero de porta **/
	private int portaFtp;

	/** Diretorio de saida **/
	private String dirOut = null;

	/** Diretorio remoto **/
	private String dirRemoto = null;

	/**
	 * Construtor da classe.
	 */
	public TransCliente() {
		super();
	}

	/**
	 * 
	 * @param arquivos
	 * @return
	 * @throws IOException 
	 */
	public void obterArquivos(List<String> arquivos) throws IOException {
		logger.info("obter lista de arquivos");

		//varrer os arquivos
		for (String arquivo : arquivos) {
			//obter lista de semente
			final List<Semente> sementes = 
					wsServidor.obterSemente(arquivo);

			//encontrar semente que obter o arquivo
			final Semente semente = encontrarSemente(sementes, arquivo);

			//realizar o download do arquivo
			baixarArquivo(semente, arquivo);

			if(wsLocal != null) {
				//registrar o arquivo para servi
				wsLocal.registrarNovoArquivo(arquivo);
			}
		}
	}

	/**
	 * 
	 * @param sementes
	 * @param arquivo
	 */
	private Semente encontrarSemente(List<Semente> sementes, String arquivo) {
		TransWebServiceCliente wsSemente = null;

		//embaralhar sementes
		Collections.shuffle(sementes);

		//varrer sementes
		for (Semente semente : sementes) {
			//criar novo cliente web service
			wsSemente = new TransWebServiceCliente(semente.getEndereco());

			//verificar se a semente tem o arquivo
			final RequisicaoArquivo response = wsSemente.isArquivo(arquivo);

			// a semente tem o arquivo?
			if(response.isTemArquivo() && response.isDisponivel()) {
				return semente;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param semente
	 * @param arquivo
	 * @throws IOException 
	 */
	private void baixarArquivo(Semente semente, String arquivo) throws IOException {
		//endereco 
		String endereco = servidorFtp;

		if(semente != null) {
			endereco = semente.getEndereco();
		}

		//criar cliente FTP
		final TransFtpCliente tfSemente = new TransFtpCliente();

		FTPClient ftp = null;

		try {
			//conectar ao servidor
			ftp = tfSemente.conectar(endereco, portaFtp, usuarioFtp, senhaFtp);

			//baixar arquivo
			tfSemente.download(ftp, dirRemoto, dirOut, arquivo);

		} catch (IOException e) {
			logger.error("Nao foi possivel conectar a semente", e);

			throw e;

		} finally {

			if(ftp != null) {
				try {
					ftp.disconnect();

				} catch (IOException e1) {
					logger.error("erro ao desconectar da semente", e1);
				}
			}

		}
	}

	/**
	 * @return the wsServidor
	 */
	public TransWebServiceCliente getWsServidor() {
		return wsServidor;
	}

	/**
	 * @return the wsLocal
	 */
	public TransWebServiceCliente getWsLocal() {
		return wsLocal;
	}

	/**
	 * @return the usuarioFtp
	 */
	public String getUsuarioFtp() {
		return usuarioFtp;
	}

	/**
	 * @return the senhaFtp
	 */
	public String getSenhaFtp() {
		return senhaFtp;
	}

	/**
	 * @return the servidorFtp
	 */
	public String getServidorFtp() {
		return servidorFtp;
	}

	/**
	 * @return the portaFtp
	 */
	public int getPortaFtp() {
		return portaFtp;
	}

	/**
	 * @return the dirOut
	 */
	public String getDirOut() {
		return dirOut;
	}

	/**
	 * @return the dirRemoto
	 */
	public String getDirRemoto() {
		return dirRemoto;
	}

	/**
	 * @param wsServidor the wsServidor to set
	 */
	public void setWsServidor(TransWebServiceCliente wsServidor) {
		this.wsServidor = wsServidor;
	}

	/**
	 * @param wsLocal the wsLocal to set
	 */
	public void setWsLocal(TransWebServiceCliente wsLocal) {
		this.wsLocal = wsLocal;
	}

	/**
	 * @param usuarioFtp the usuarioFtp to set
	 */
	public void setUsuarioFtp(String usuarioFtp) {
		this.usuarioFtp = usuarioFtp;
	}

	/**
	 * @param senhaFtp the senhaFtp to set
	 */
	public void setSenhaFtp(String senhaFtp) {
		this.senhaFtp = senhaFtp;
	}

	/**
	 * @param servidorFtp the servidorFtp to set
	 */
	public void setServidorFtp(String servidorFtp) {
		this.servidorFtp = servidorFtp;
	}

	/**
	 * @param portaFtp the portaFtp to set
	 */
	public void setPortaFtp(int portaFtp) {
		this.portaFtp = portaFtp;
	}

	/**
	 * @param dirOut the dirOut to set
	 */
	public void setDirOut(String dirOut) {
		this.dirOut = dirOut;
	}

	/**
	 * @param dirRemoto the dirRemoto to set
	 */
	public void setDirRemoto(String dirRemoto) {
		this.dirRemoto = dirRemoto;
	}
}