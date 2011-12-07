/**
 * 
 */
package br.pelommedrado.trans.download;

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
	 */
	public DownloadManager(FTPClient ftp, String fileLocal, String fileRemoto) {
		super();

		this.ftp = ftp;
		this.fileLocal = fileLocal;
		this.fileRemoto = fileRemoto;
		this.downloaded = 0;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public boolean download() throws IOException {
		logger.debug("iniciando configuracoes dos dados baixados");

		//arquivo de controle do download
		fileData = new File(fileLocal + ".trans");

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

		boolean ok =  baixar();
		if(ok) {
			fileData.delete();
		}
		return ok;
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private boolean baixar() throws IOException {
		logger.debug("baixando os dados do servidor");

		//configurar posicao de leitura
		ftp.setRestartOffset(downloaded);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

		RandomAccessFile out = null;
		InputStream in = null;
		int bytes;
		final byte[] buffer = new byte[MAX_BUFFER_SIZE];

		try {
			//abrir o arquivo
			out = new RandomAccessFile(fileLocal, "rw");
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

		return ftp.completePendingCommand();
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

}