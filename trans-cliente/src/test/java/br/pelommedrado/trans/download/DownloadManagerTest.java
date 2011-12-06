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
 * @author Andre Leite
 */
public class DownloadManagerTest {

	/** criar cliente FTP **/
	private TransFtpCliente tfSemente;

	/** Cliente FTP **/
	private FTPClient ftp;

	/** Arquivo local **/
	private String fileLocal;

	/** Arquivo remoto **/
	private String fileRemoto;

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
	public void testDownload() throws IOException {
		DownloadManager download = new DownloadManager(
				ftp.retrieveFileStream(fileRemoto), fileLocal);

		download.download();
		
		assertEquals(true, ftp.completePendingCommand());
	}
}