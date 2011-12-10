/**
 * 
 */
package br.pelommedrado.cegonha.download;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.cegonha.cliente.TransFtpCliente;
import br.pelommedrado.cegonha.download.DownloadManager;

/**
 * @author Andre Leite
 */
public class DownloadManagerTest {

	/** criar cliente FTP **/
	private TransFtpCliente tFtpCliente;

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
		tFtpCliente.setServidor("localhost");
		tFtpCliente.setPorta(2121);
		tFtpCliente.setUsuario("anonymous");
		tFtpCliente.setSenha("");

		tFtpCliente.conectar();

		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tFtpCliente.desconectar();
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDownloadContinuacao() throws IOException {
		//arquivo de controle do download
		File fileData = new File(fileLocal + DownloadManager.EXT_PROPERTIES);
		fileData.createNewFile();

		Properties props = new Properties();
		props.setProperty("download", String.valueOf(0));

		final FileOutputStream fo = new FileOutputStream(fileData);
		props.store(fo, "parametros de controle do download");
		fo.flush();
		fo.close();

		DownloadManager download = new DownloadManager(tFtpCliente.getFtp(), fileLocal, fileRemoto, true);
		assertEquals(true, download.download());
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDownload() throws IOException {
		DownloadManager download = new DownloadManager(tFtpCliente.getFtp(), fileLocal, fileRemoto, true);
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