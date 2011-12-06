/**
 * 
 */
package br.pelommedrado.trans.download;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.trans.cliente.TransFtpCliente;

/**
 * @author pelom
 *
 */
public class FtpFileChecksumTest {

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
	public void testPacoteCorrompido() throws IOException {
		DownloadManager download = new DownloadManager(
				ftp.retrieveFileStream(fileRemoto), fileLocal);

		download.download();

		tfSemente.desconectar(ftp);
		ftp = tfSemente.conectar("localhost", 2121, "anonymous", "");

		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(true, fCheck.isPacoteCorrompido());
	}

	@Test
	public void testPacoteNaoCorrompido() throws IOException {
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

		DownloadManager download = new DownloadManager(
				ftp.retrieveFileStream(fileRemoto), fileLocal);

		download.download();

		tfSemente.desconectar(ftp);
		ftp = tfSemente.conectar("localhost", 2121, "anonymous", "");

		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(false, fCheck.isPacoteCorrompido());
	}
}