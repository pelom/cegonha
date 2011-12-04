/**
 * 
 */
package br.pelommedrado.trans.cliente;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.model.RequisicaoArquivo;
import br.pelommedrado.trans.model.Semente;
import br.pelommedrado.trans.util.FileUtils;

/**
 * @author Andre Leite
 */
public class TransCliente {

	/** Gerenciador de logs **/
	private static Logger logger = LoggerFactory.getLogger(TransFtpCliente.class);

	/** Cliente Web service servidor **/
	private TransWebServiceCliente wsServidor = null;

	/** Cliente Web service local **/
	private TransWebServiceCliente wsLocal = null;

	/** Login do usuario **/
	private String usuarioFtp = "";

	/** Senha do usuario **/
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
	 */
	public boolean obterArquivos(List<String> arquivos) {
		logger.info("obter lista de arquivos");

		//varrer os arquivos
		for (String arquivo : arquivos) {

			//obter lista de semente
			final List<Semente> sementes = 
					wsServidor.obterSemente(arquivo);

			//encontrar semente que obter o arquivo
			final Stack<Semente> pilhaSemente = obterSementeAtiva(sementes, arquivo);

			//iniciar processo de download
			if(!iniciarProcesso(pilhaSemente, arquivo)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @param pilhaSemente
	 * @param arquivo
	 * @return
	 */
	private boolean iniciarProcesso(Stack<Semente> pilhaSemente, String arquivo) {
		logger.info("inciando o processo de download do arquivo:" + arquivo);

		boolean download = false;
		Semente semente = null;

		do {
			logger.debug("selecionando semente...");

			//nao a sementes ativas?
			if(pilhaSemente.isEmpty()) {
				logger.debug("usar a semente servidora");

				//usar a semente servidora
				semente = new Semente();
				semente.setEndereco(servidorFtp);

			} else {
				logger.debug("obter semente da pilha");

				//obter uma semente da pilha
				semente = pilhaSemente.pop();

			}

			//realizar o download do arquivo
			download = baixarArquivo(semente, arquivo);

			if(download) {
				logger.debug("download concluido com sucesso");

				if(wsLocal != null) {
					//registrar o arquivo para servi
					wsLocal.registrarNovoArquivo(arquivo);
				}

			} else {
				logger.warn("nao foi possivel baixa dessa semente:" + semente);

			}

			//o download nao foi concluido e a pilha nao esta vazia?
		} while(!download && !pilhaSemente.isEmpty());

		return download;
	}

	/**
	 * 
	 * @param sementes
	 * @param arquivo
	 */
	private Stack<Semente> obterSementeAtiva(List<Semente> sementes, String arquivo) {
		logger.info("iniciando a busca por semente que obtenha o arquivo:" + arquivo);

		final Stack<Semente> sementesAtiva = new Stack<Semente>();

		TransWebServiceCliente wsSemente = null;

		//embaralhar sementes
		Collections.shuffle(sementes);

		//varrer sementes
		for (Semente semente : sementes) {
			//criar novo cliente web service
			wsSemente = new TransWebServiceCliente(wsServidor.formataUrl(semente.getEndereco()));

			//verificar se a semente tem o arquivo
			final RequisicaoArquivo response = wsSemente.isArquivo(arquivo);

			// a semente tem o arquivo?
			if(response.isTemArquivo() && response.isDisponivel()) {
				//sementes ativas
				sementesAtiva.push(semente);
			}
		}

		return sementesAtiva;
	}

	/**
	 * 
	 * @param semente
	 * @param arquivo
	 * @throws IOException 
	 */
	private boolean baixarArquivo(Semente semente, String arquivo) {
		//endereco 
		String endereco = servidorFtp;

		if(semente != null) {
			endereco = semente.getEndereco();
		}

		logger.info("baixando o arquivo:" + arquivo + " da semente:" + endereco);

		//criar cliente FTP
		final TransFtpCliente tfSemente = new TransFtpCliente();

		FTPClient ftp = null;

		try {
			//conectar ao servidor
			ftp = tfSemente.conectar(endereco, portaFtp, usuarioFtp, senhaFtp);

			//arquivo de saida
			String fileOut = dirOut + File.separator + arquivo;
			//arquivo de entrada
			String fileIn = dirRemoto + File.separator + arquivo;

			//baixar arquivo
			boolean download = tfSemente.download(ftp, fileIn, fileOut);

			if(download) {
				final String chechsum = ftp.getStatus().split(" ")[1].trim();
				logger.debug("chechsum:" + chechsum);

				if(FileUtils.isCorrompido(fileOut, Long.valueOf(chechsum))) {
					logger.warn("O arquivo esta corrompido " + fileOut);

					download = false;
				}
			}

			return download;

		} catch (IOException e) {
			logger.error("Nao foi possivel conectar a semente", e);

			return false;

		} finally {
			if(ftp != null && ftp.isConnected()) {
				try {
					ftp.logout();
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