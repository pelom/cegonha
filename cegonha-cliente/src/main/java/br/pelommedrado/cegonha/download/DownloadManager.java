/**
 * 
 */
package br.pelommedrado.cegonha.download;

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

/**
 * @author Andre Leite
 */
public class DownloadManager {
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

	/** Arquivo local **/
	private String fileLocal;

	/** Arquivo remoto **/
	private String fileRemoto;

	/** Entrada de dados **/
	private FTPClient ftp = null;

	/** Tentar recuperar o arquivo caso esteja corrompido **/
	private boolean recuperar = true;

	/** Verificar de integridade do arquivo **/
	private FileFtpChecksum fileFtpChecksum = null;

	/**
	 * Construtor da classe.
	 * 
	 * @param ftp
	 * 		Cliente FTP
	 * 
	 * @param fileLocal
	 * 		Arquivo local
	 * 
	 * @param fileRemoto
	 * 		Arquivo remoto
	 * 
	 * @param recuperar
	 * 		True para recuperar o arquivo caso corrompa
	 */
	public DownloadManager(FTPClient ftp, String fileLocal, String fileRemoto, boolean recuperar) {
		super();

		this.ftp = ftp;
		this.fileLocal = fileLocal;
		this.fileRemoto = fileRemoto;
		this.downloaded = 0;
		this.recuperar = recuperar;
		this.fileFtpChecksum = new FileFtpChecksum(fileLocal, fileRemoto);
	}

	/**
	 * 
	 * @throws IOException
	 */
	public boolean download() throws IOException {
		logger.debug("iniciando configuracoes dos dados baixados");

		//preparar para iniciar o download
		preparar();

		//arquivo copiado?
		boolean ok = copiarFile();

		//o arquivo esta corrompido?
		if(ok && fileFtpChecksum.verificarFileCorrompido(ftp)) {
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
		fileCtl = new File(fileLocal + EXT_PROPERTIES);

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
	 */
	private boolean copiarFile() throws IOException {
		logger.debug("baixando os dados do servidor");

		//configurar posicao de leitura
		ftp.setRestartOffset(downloaded);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

		//buffer de leitura
		final byte[] buffer = new byte[MAX_BUFFER_SIZE];
		//arquivo local
		final File file = new File(fileLocal + EXT_FILE);

		RandomAccessFile out = null;
		InputStream in = null;
		int bytes;

		try {
			//abrir o arquivo
			out = new RandomAccessFile(file, "rw");
			//configurar posicao de escrita
			out.seek(downloaded);

			in = ftp.retrieveFileStream(fileRemoto);

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
			// Close file.
			if (out != null) {
				out.close();
			}

			if (in != null) {
				in.close();
			}
		}

		boolean ok = ftp.completePendingCommand();
		//download concluiu
		if(ok) {
			//renomear arquivo
			file.renameTo(new File(fileLocal));

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
		if(!recuperar) {
			return false;
		}

		logger.debug("preparar para recuperar o arquivo corrompido");

		//scaniar os pacotes corrompidos
		fileFtpChecksum.scaniarPacoteCorrompido(ftp);

		//foi possivel identificar pacotes corrompidos?
		if(fileFtpChecksum.isPacoteCorrompido()) {
			//obter numero de pacotes corrompidos
			int nPkg = fileFtpChecksum.getDownloadFile().getPacotes().size();

			final FileFtpRecupera fRecuperar = 
					new FileFtpRecupera(ftp, fileFtpChecksum.getDownloadFile());

			//numero de pacotes recuperados e igual?
			if(fRecuperar.recuperar() != nPkg) {
				return false;
			}

			return true;

		} else {
			throw new IOException("nao foi possivel recuperar o arquivo");
		}
	}

	/**
	 * @param fileFtpChecksum the fileFtpChecksum to set
	 */
	public void setFileFtpChecksum(FileFtpChecksum fileFtpChecksum) {
		this.fileFtpChecksum = fileFtpChecksum;
	}
}