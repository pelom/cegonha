/**
 * 
 */
package br.pelommedrado.trans.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * @author Andre Leite
 */
public class FtpFileRecupera {

	/** Arquivo a ser recuperado **/
	private FileDownload fileDownload = null;

	/**
	 * Construtor da classe.
	 * @param fileDownload
	 * 		Arquivo para ser recuperado
	 */
	public FtpFileRecupera(FileDownload fileDownload) {
		super();

		this.fileDownload = fileDownload;
	}

	/**
	 * 
	 * @param in
	 * @throws IOException 
	 */
	public void recuperar(InputStream in) throws IOException {
		//abrir o arquivo
		RandomAccessFile fileOut = new 
				RandomAccessFile(fileDownload.getFileLocal(), "rw");

		int read = 0;

		try {
			//varrer os pacotes corrompidos
			for (PacoteMal pc : fileDownload.getPacotes()) {

				//configurar posicao de leitura.
				in.skip(pc.getOff());

				//configurar posicao de escrita
				fileOut.seek(pc.getOff());

				//criar buffer de leitura
				byte[] buffer = new byte[pc.getLen()];

				//ler
				read = in.read(buffer);

				if (read < 0) {
					break;
				}

				if (read == 0) {
					read = in.read();

					if (read < 0) {
						break;
					}

					fileOut.write(read);
					continue;
				}

				//recuperar dados
				fileOut.write(buffer);
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