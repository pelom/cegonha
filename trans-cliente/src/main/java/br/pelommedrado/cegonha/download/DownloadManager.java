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
	private File fileData = null;

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
	}

	/**
	 * 
	 * @throws IOException
	 */
	public boolean download() throws IOException {
		logger.debug("iniciando configuracoes dos dados baixados");

		//arquivo de controle do download
		fileData = new File(fileLocal + EXT_PROPERTIES);

		//o arquivo existe?
		if(fileData.exists()) {
			logger.debug("continuar download");

			final FileInputStream fi = new FileInputStream(fileData);
			props.load(fi);

			//indice corrente
			downloaded = Integer.valueOf(props.getProperty("download"));

			fi.close();

		} else {
			logger.debug("iniciar download");

			//criar arquivo de controle
			fileData.createNewFile();

			//salvar dados
			salvar();
		}

		//arquivo copiado?
		if(copiarFile()) {
			fileData.delete();

			//criar verificador de arquivo
			final FtpFileChecksum fCheck = new FtpFileChecksum(fileLocal, fileRemoto);

			//o arquivo esta corrompido?
			if(fCheck.verificarFileCorrompido(ftp)) {
				//recuperar arquivo
				return recuperarFile(fCheck);

			} else {
				return true;

			}
		}

		return false;
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

				salvar();
			}

		} catch (IOException e) {
			//remover arquivo
			file.delete();

			throw new CopyStreamException(
					"IOException caught while copying.", downloaded, e);

		} finally {
			// Close file.
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {}
			}

			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {}
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
	 * @throws IOException
	 */
	private void salvar() throws IOException {
		props.setProperty("download", String.valueOf(downloaded));

		final FileOutputStream fo = new FileOutputStream(fileData);
		props.store(fo, "parametros de controle do download");
		fo.flush();
		fo.close();
	}

	/**
	 * 
	 * @param fCheck
	 * @param ftp
	 * @return
	 * @throws IOException
	 */
	private boolean recuperarFile(final FtpFileChecksum fCheck) throws IOException {
		if(!recuperar) {
			return false;
		}

		logger.debug("preparar para recuperar o arquivo corrompido");

		//scaniar os pacotes corrompidos
		fCheck.scaniarPacoteCorrompido(ftp);

		//foi possivel identificar pacotes corrompidos?
		if(fCheck.isPacoteCorrompido()) {
			//obter numero de pacotes corrompidos
			int nPkg = fCheck.getDownloadFile().getPacotes().size();

			final FtpFileRecupera fRecuperar = 
					new FtpFileRecupera(ftp, fCheck.getDownloadFile());

			//numero de pacotes recuperados e igual?
			if(fRecuperar.recuperar() != nPkg) {
				return false;
			}

			return true;

		} else {
			throw new IOException("nao foi possivel recuperar o arquivo");
		}
	}
}