/**
 * 
 */
package br.pelommedrado.cegonha.download.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.cegonha.download.IDownloadManager;
import br.pelommedrado.cegonha.download.IFileChecksum;
import br.pelommedrado.cegonha.download.IFileRecuperar;
import br.pelommedrado.cegonha.download.util.FileDownload;


/**
 * @author Andre Leite
 */
public class DownloadManager implements IDownloadManager {
	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(DownloadManager.class);

	/** Max size of download buffer.*/
	public static final int MAX_BUFFER_SIZE = 1024;

	/** Extensao do arquivo a ser manipulado **/
	public static final String EXT_FILE = ".cegonha";

	/** Extensao do arquivo de properties **/
	public static final String EXT_PROPERTIES = ".trans";

	/** Classe responsavel por ler o estado do download **/
	private Properties props = new Properties();

	/** Arquivo de controle **/
	private File fileCtl = null;

	/**  Numero do bytes baixados **/
	private long downloaded; 
	
	/** Referencia do arquivo a ser baixado **/
	private FileDownload fileDownload;
	
	/** Entrada de dados **/
	private FTPClient ftp = null;

	/** Verificar de integridade do arquivo **/
	private IFileChecksum fileChecksum = null;

	/** Recuperador de arquivo corrompido **/
	private IFileRecuperar fileRecupera;

	/**
	 * Construtor da classe.
	 * @param ftp
	 * @param fileDownload
	 */
	public DownloadManager(FTPClient ftp, FileDownload fileDownload) {
		super();

		this.ftp = ftp;
		this.fileDownload = fileDownload;
		this.downloaded = 0;
		this.fileRecupera = new FileRecuperaFtp();
		this.fileChecksum = new FileChecksumFtp();
		this.fileChecksum.setFileDownload(fileDownload);
	}

	/**
	 * 
	 * @throws IOException
	 */
	public boolean download() throws IOException {
		logger.debug("iniciando configuracoes dos dados baixados");

		//preparar para iniciar o download
		preparar();

		//configurar Input
		final InputStream in = prepararFtp();
		//configurar arquivo local
		final File file = new File(fileDownload.getFileLocal() + EXT_FILE);
		//abrir o arquivo local
		final RandomAccessFile out = new RandomAccessFile(file, "rw");
		//configurar posicao de escrita
		out.seek(downloaded);

		//arquivo copiado?
		boolean ok = copiarFile(in, out, file);

		//o arquivo esta corrompido?
		if(ok && fileChecksum.verificarFileCorrompido(ftp)) {
			//recuperar arquivo
			ok = recuperarFile();
		}

		//remover continuacao
		fileCtl.delete();

		return ok;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void preparar() throws IOException {
		logger.debug("analizar as configuracoes dos dados baixados");

		//arquivo de controle do download
		fileCtl = new File(fileDownload.getFileLocal() + EXT_PROPERTIES);

		//existe um download nao concluido?
		if(fileCtl.exists()) {
			carregarConfig();

		} else {
			logger.debug("preparando para iniciar o download");
			//criar arquivo de controle
			fileCtl.createNewFile();

			salvarConfig();
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private InputStream prepararFtp() throws IOException {
		//configurar posicao de leitura
		ftp.setRestartOffset(downloaded);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

		//obter entrada de dados
		InputStream in = ftp.retrieveFileStream(fileDownload.getFileRemoto());

		//operacao realizada com sucesso?
		if(in != null) {
			return in;

		} else {
			throw new IOException("nao foi possivel se conectar ao arquivo remoto");
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void carregarConfig() throws IOException {
		logger.debug("carregando download nao concluido");

		final FileInputStream fi = new FileInputStream(fileCtl);
		props.load(fi);
		fi.close();

		//indice corrente
		downloaded = Integer.valueOf(props.getProperty("download"));
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void salvarConfig() throws IOException {
		props.setProperty("download", String.valueOf(downloaded));

		final FileOutputStream fo = new FileOutputStream(fileCtl);
		props.store(fo, "parametros de controle do download");
		fo.flush();
		fo.close();
	}

	/**
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private boolean copiarFile(InputStream in, RandomAccessFile out, File file) throws IOException {
		logger.debug("copiando o arquivo");

		//buffer de leitura
		final byte[] buffer = new byte[MAX_BUFFER_SIZE];
		int bytes;

		try {
			while ((bytes = in.read(buffer)) != -1) {

				// Technically, some read(byte[]) methods may return 0 and we cannot
				// accept that as an indication of EOF.
				if (bytes == 0) {

					bytes = in.read();

					if (bytes < 0) {
						break;
					}

					out.write(bytes);

					++downloaded;

					continue;
				}

				out.write(buffer, 0, bytes);

				downloaded += bytes;

				salvarConfig();
			}
		} catch (IOException e) {
			throw new CopyStreamException(
					"IOException caught while copying.", downloaded, e);

		} finally {
			try {in.close();
			}catch (Exception e) {}

			out.close();
		}

		boolean ok = ftp.completePendingCommand();
		//download concluiu
		if(ok) {
			//renomear arquivo
			file.renameTo(new File(fileDownload.getFileLocal()));

		} else {
			file.delete();
		}

		return ok;
	}

	/**
	 * 
	 * @param fCheck
	 * @param ftp
	 * @return
	 * @throws IOException
	 */
	private boolean recuperarFile() throws IOException {
		//o arquivo nao deve ser recuperado?
		if(!fileDownload.isRecuperar()) {
			return false;
		}

		logger.debug("preparar para recuperar o arquivo corrompido");

		//scaniar os pacotes corrompidos
		//obter numero de pacotes corrompidos
		final int nPkgLoss = fileChecksum.verificarPacoteCorrompido(ftp);

		//foi possivel identificar pacotes corrompidos?
		if(fileChecksum.isPacoteCorrompido()) {
			fileRecupera.setFtp(ftp);
			fileRecupera.setFileDownload(fileChecksum.getFileDownload());

			//numero de pacotes recuperados e igual?
			if(fileRecupera.recuperar() != nPkgLoss) {
				return false;
			}

			return true;

		} else {
			throw new IOException("nao foi possivel recuperar o arquivo");
		}
	}

	/**
	 * 
	 * @param fileChecksum
	 */
	public void setFileChecksum(IFileChecksum fileChecksum) {
		this.fileChecksum = fileChecksum;
	}

	/**
	 * 
	 * @param fileFtpRecupera
	 */
	public void setFileRecupera(IFileRecuperar fileRecupera) {
		this.fileRecupera = fileRecupera;
	}
}