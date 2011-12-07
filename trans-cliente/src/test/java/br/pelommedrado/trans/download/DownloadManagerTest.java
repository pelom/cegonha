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
public class DownloadManagerTest {

	/** criar cliente FTP **/
	private TransFtpCliente tFtpCliente;

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
		tFtpCliente = new TransFtpCliente();
		ftp = tFtpCliente.conectar("localhost", 2121, "anonymous", "");

		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tFtpCliente.desconectar(ftp);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDownload() throws IOException {
		DownloadManager download = new DownloadManager(ftp, fileLocal, fileRemoto);
		assertEquals(true, download.download());
	}

	/**
	 * 
	 * @param fileLocal
	 * @param bufSize
	 * @throws IOException
	 */
	public static void corromperArquivo(String fileLocal, int[] n, int bufSize) throws IOException {
		final RandomAccessFile fileOut =  new RandomAccessFile(fileLocal, "rw");

		final byte[] buf = new byte[bufSize];
		for (int i : n) {
			fileOut.seek(bufSize * i);
			fileOut.write(buf);	
		}

		fileOut.close();
	}
}