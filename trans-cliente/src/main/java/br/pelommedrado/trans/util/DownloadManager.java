/**
 * 
 */
package br.pelommedrado.trans.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andre Leite
 */
public class DownloadManager {
	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(DownloadManager.class);

	/** Max size of download buffer.*/
	private static final int MAX_BUFFER_SIZE = 1024;

	/** donwload ativo **/
	public static final int DOWNLOADING = 0;

	/** download pausado **/
	public static final int PAUSED = 1;

	/** download completo **/
	public static final int COMPLETE = 2;

	/** download com erro **/
	public static final int ERROR = 3;

	/** Classe responsavel por ler o estado do download **/
	private Properties props = new Properties();

	/** Arquivo de controle **/
	private File fileData = null;

	/** Tamanho do arquivo a ser baixado **/
	private int size;

	/**  Numero do bytes baixados **/
	private int downloaded; 

	/** Situacao do download **/
	private int status = -1; 

	/** Path de saida dos dados baixados **/
	private String pathOut;

	/** Entrada de dados **/
	private InputStream in = null;

	/** Cliente FTP **/
	private FTPClient ftp;

	/** Arquivo de entrada **/
	private String fileIn;
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String stg = "/home/pelom/Área de Trabalho/Vuze_4700_source.zip";
		File file = new File(stg);
		FileInputStream in = new FileInputStream(file);

		DownloadManager download = new DownloadManager(in, "/home/pelom/test.zip", file.length());
		download.iniciar();
	}

	/**
	 * 
	 * @param ftp
	 * @param in
	 * @param pathOut
	 * @param length
	 */
	public DownloadManager(FTPClient ftp, String fileIn, InputStream in, String pathOut, long length) {
		this(in, pathOut, length);

		this.ftp = ftp;
		this.fileIn = fileIn;
	}

	/**
	 * Construtor da classe.
	 * 
	 * @param in
	 * 		Entrada padrao de dados
	 * 
	 * @param pathOut
	 * 		Path para onde os dados seram descarregados
	 * 
	 * @param length
	 * 		Tamanho do arquivo a ser baixados
	 */
	public DownloadManager(InputStream in, String pathOut, long length) {
		super();

		this.in = new BufferedInputStream(in, MAX_BUFFER_SIZE);
		this.pathOut = pathOut;
		this.downloaded = 0;
		this.size = (int) length;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void iniciar() throws IOException {
		//arquivo de controle do download
		fileData = new File(pathOut + ".trans");

		//o arquivo existe?
		if(fileData.exists()) {

			final FileInputStream fi = new FileInputStream(fileData);
			props.load(fi);

			//tamanaho do arquivo
			size = Integer.valueOf(props.getProperty("size"));
			//indice corrente
			downloaded = Integer.valueOf(props.getProperty("download"));
			//situacao
			status = Integer.valueOf(props.getProperty("status"));

			fi.close();

		} else {
			//criar arquivo de controle
			fileData.createNewFile();

			this.status = DOWNLOADING;

			//salvar dados
			salvar();


		}

		baixar();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void salvar() throws IOException {
		props.setProperty("size", String.valueOf(size));
		props.setProperty("download", String.valueOf(downloaded));
		props.setProperty("status", String.valueOf(status));

		final FileOutputStream fo = new FileOutputStream(fileData);
		props.store(fo, "parametros de controle do download");
		fo.close();
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public void baixar() throws IOException {
		RandomAccessFile fileOut = null;

		try {
			//abrir o arquivo
			fileOut = new RandomAccessFile(pathOut, "rw");

			//configurar posicao de escrita
			fileOut.seek(downloaded);

			//configurar posicao de leitura
			in.skip(downloaded);

			while (status == DOWNLOADING) {
				/* Size buffer according to how much of the file is left to download. */
				byte buffer[];

				if (size - downloaded > MAX_BUFFER_SIZE) {
					buffer = new byte[MAX_BUFFER_SIZE];

				} else {
					buffer = new byte[size - downloaded];

				}

				// Read from server into buffer.
				int read = in.read(buffer);

				//ocorreu algum erro?
				if (read == -1) {
					status = ERROR;

					break;

					//download concluido?
				} else if (read == 0) {
					status = COMPLETE;

					break;
				}

				if(ftp != null) {
					//enviar o comando
					ftp.sendCommand(ParseChecksumCommand.CHECKSUM,
							ParseChecksumCommand.parse(fileIn, downloaded, read));

					//if(ftp.doCommand(ParseChecksumCommand.CHECKSUM,
					//		ParseChecksumCommand.parse(fileIn, downloaded, read))) {

					final String checksum = ftp.getStatus().split(" ")[1].trim();

					//bloco nao esta corrompido?
					if(!FileUtils.isCorrompido(buffer, Long.valueOf(checksum))) {
						// Write buffer to file.
						fileOut.write(buffer, 0, read);

						downloaded += read;

						salvar();

					} else {
						logger.debug("bloco corropindo");

						in.skip(downloaded);

					}
					//}

				} else {
					// Write buffer to file.
					fileOut.write(buffer, 0, read);

					downloaded += read;

					salvar();
				}
			}

			if(status == COMPLETE) {
				fileData.delete();
			}

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
}