/**
 * 
 */
package br.pelommedrado.cegonha.cliente;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.cegonha.model.RequisicaoArquivo;
import br.pelommedrado.cegonha.model.Semente;

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

	/** Cliente ftp **/
	private TransFtpCliente ftpCliente = null;

	/** Tentar recuperar o arquivo caso esteja corrompido **/
	private boolean recuperar = true;

	/** Servidor **/
	private String servidorFtp = null;

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
			if(!processar(pilhaSemente, arquivo)) {
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
	private boolean processar(Stack<Semente> pilhaSemente, String arquivo) {
		logger.info("processar o download do arquivo:" + arquivo);

		boolean download = false;

		do {
			//selecionar uma semente para download
			final Semente semente = selecionarSemente(pilhaSemente);	

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
	 * @param pilhaSemente
	 * @return
	 */
	private Semente selecionarSemente(Stack<Semente> pilhaSemente) {
		logger.debug("selecionando semente...");
		Semente semente = null;

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

		return semente;
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

		//alguma semente foi encontrada?
		if(semente != null) {
			endereco = semente.getEndereco();
		}

		logger.info("ativar o download  do arquivo:" + arquivo + " na semente:" + endereco);

		ftpCliente.setServidor(endereco);

		try {
			//conectar ao servidor
			//conexao falhou?
			if(!ftpCliente.conectar()) {
				return false;
			}

			//arquivo local
			final String fileLocal = dirOut + File.separator + arquivo;

			//arquivo remoto
			final String fileRemoto = dirRemoto + File.separator + arquivo;

			//baixar arquivo
			return ftpCliente.download(fileRemoto, fileLocal, recuperar);

		} catch (IOException e) {
			logger.error("Nao foi possivel conectar a semente", e);

			return false;

		} finally {
			try {
				ftpCliente.desconectar();

			} catch (IOException e1) {
				logger.error("erro ao desconectar da semente", e1);
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
	 * @return the servidorFtp
	 */
	public String getServidorFtp() {
		return servidorFtp;
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
	 * @param servidorFtp the servidorFtp to set
	 */
	public void setServidorFtp(String servidorFtp) {
		this.servidorFtp = servidorFtp;
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

	/**
	 * @return the ftpCliente
	 */
	public TransFtpCliente getFtpCliente() {
		return ftpCliente;
	}

	/**
	 * @return the recuperar
	 */
	public boolean isRecuperar() {
		return recuperar;
	}

	/**
	 * @param ftpCliente the ftpCliente to set
	 */
	public void setFtpCliente(TransFtpCliente ftpCliente) {
		this.ftpCliente = ftpCliente;
	}

	/**
	 * @param recuperar the recuperar to set
	 */
	public void setRecuperar(boolean recuperar) {
		this.recuperar = recuperar;
	}
}