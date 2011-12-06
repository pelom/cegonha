/**
 * 
 */
package br.pelommedrado.trans.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

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

	/** Path de saida dos dados baixados **/
	private String pathOut;

	/** Entrada de dados **/
	private InputStream in = null;
	
	/**
	 * Construtor da classe.
	 * 
	 * @param in
	 * 		Entrada padrao de dados
	 * 
	 * @param pathOut
	 * 		Path para onde os dados seram descarregados
	 */
	public DownloadManager(InputStream in, String pathOut) {
		super();

		this.in = new BufferedInputStream(in, MAX_BUFFER_SIZE);
		this.pathOut = pathOut;
		this.downloaded = 0;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void download() throws IOException {
		logger.debug("iniciando configuracoes dos dados baixados");

		//arquivo de controle do download
		fileData = new File(pathOut + ".trans");

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

		baixar();
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public void baixar() throws IOException {
		logger.debug("baixando os dados do servidor");

		RandomAccessFile fileOut = null;
		int bytes;
		final byte[] buffer = new byte[MAX_BUFFER_SIZE];

		try {
			//abrir o arquivo
			fileOut = new RandomAccessFile(pathOut, "rw");

			//configurar posicao de escrita
			fileOut.seek(downloaded);

			//configurar posicao de leitura
			in.skip(downloaded);

			while ((bytes = in.read(buffer)) != -1) {

				// Technically, some read(byte[]) methods may return 0 and we cannot
				// accept that as an indication of EOF.

				if (bytes == 0) {

					bytes = in.read();

					if (bytes < 0) {
						break;
					}

					fileOut.write(bytes);

					++downloaded;

					continue;
				}

				fileOut.write(buffer, 0, bytes);

				downloaded += bytes;

				salvar();
			}

			logger.debug("download concluido");

			fileData.delete();

		} catch (IOException e) {
			throw new CopyStreamException(
					"IOException caught while copying.", downloaded, e);

		} finally {
			// Close file.
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (Exception e) {}
			}

			// Close connection to server.
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {}
			}
		}
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