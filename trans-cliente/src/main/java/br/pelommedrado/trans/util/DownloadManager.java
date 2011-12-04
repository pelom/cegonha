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

/**
 * @author Andre Leite
 */
public class DownloadManager {
	// Max size of download buffer.
	private static final int MAX_BUFFER_SIZE = 1024;

	/** donwload ativo **/
	public static final int DOWNLOADING = 0;

	/** download pausado **/
	public static final int PAUSED = 1;

	/** download completo **/
	public static final int COMPLETE = 2;

	/** download com erro **/
	public static final int ERROR = 3;

	/** Arquivo de controle **/
	private File fileData = null;

	/** Tamanho do arquivo a ser baixado **/
	private int size;

	/**  Numero do bytes baixados **/
	private int downloaded; 

	/** Situacao do download **/
	private int status; 

	/** Path de saida dos dados baixados **/
	private String pathOut;

	/** Entrada de dados **/
	private InputStream in = null;

	/** Classe responsavel por ler o estado do download **/
	private Properties props = new Properties();

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
		this.status = DOWNLOADING;
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

			status = DOWNLOADING;

			fi.close();

		} else {
			//criar arquivo de controle
			fileData.createNewFile();

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

				// Write buffer to file.
				fileOut.write(buffer, 0, read);

				downloaded += read;

				salvar();
			}

			fileData.delete();

		} finally {
			// Close file.
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (Exception e) {}
			}

			// Close connection to server.
			//if (in != null) {
			//	try {
			//		in.close();
			//	} catch (Exception e) {}
			//}
		}
	}
}