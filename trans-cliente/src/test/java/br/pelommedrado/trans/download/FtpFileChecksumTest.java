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

		final DownloadManager download =  new DownloadManager(ftp, fileLocal, fileRemoto);
		assertEquals(true, download.download());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tfSemente.desconectar(ftp);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFileCorrompido() throws IOException {
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		assertEquals(false, fCheck.verificarFileCorrompido(ftp));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCorrompimentoTresPacote() throws IOException {
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(3, fCheck.getDownloadFile().getPacotes().size());
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPacoteCorrompido() throws IOException {
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0}, DownloadManager.MAX_BUFFER_SIZE);
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(true, fCheck.isPacoteCorrompido());
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPacoteNaoCorrompido() throws IOException {
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(false, fCheck.isPacoteCorrompido());
	}
}