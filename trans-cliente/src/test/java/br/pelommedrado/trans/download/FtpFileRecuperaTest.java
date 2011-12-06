/**
 * 
 */
package br.pelommedrado.trans.download;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.trans.cliente.TransFtpCliente;

/**
 * @author Andre Leite
 */
public class FtpFileRecuperaTest {

	/** criar cliente FTP **/
	private TransFtpCliente tfSemente;

	/** Cliente FTP **/
	private FTPClient ftp;

	/** Arquivo local **/
	private String fileLocal;

	/** Arquivo remoto **/
	private String fileRemoto;

	/** Verificador de arquivo **/
	private FtpFileChecksum fCheck;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//conectar ao servidor
		tfSemente = new TransFtpCliente();
		ftp = tfSemente.conectar("localhost", 2121, "anonymous", "");

		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tfSemente.desconectar(ftp);
	}

	@Test
	public void testRecuperacaoFile() throws IOException {
		//baixar o arquivo.
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		DownloadManager download = new DownloadManager(
				ftp.retrieveFileStream(fileRemoto), fileLocal);
		download.download();
		//download concluido?
		assertEquals(true, ftp.completePendingCommand());

		//===================================================

		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		//nao ocorreu erro no download?
		assertEquals(false, fCheck.isFileCorrompido(ftp));

		//===================================================

		//corromper o arquivo
		corromperArquivo();

		fCheck.scaniarPacoteCorrompido(ftp);
		//arquivo foi corrompido?
		assertEquals(true, fCheck.isPacoteCorrompido());

		//===================================================

		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		FtpFileRecupera fRecuperar = new FtpFileRecupera(fCheck.getDownloadFile());
		fRecuperar.recuperar(ftp.retrieveFileStream(fileRemoto));

		//===================================================

		tfSemente.desconectar(ftp);
		ftp = tfSemente.conectar("localhost", 2121, "anonymous", "");

		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(false, fCheck.isPacoteCorrompido());
	}

	private void corromperArquivo() throws IOException {
		RandomAccessFile fileOut =  new RandomAccessFile(fileLocal, "rw");
		byte[] buf = new byte[1024];
		fileOut.write(buf);
		fileOut.close();
	}
}