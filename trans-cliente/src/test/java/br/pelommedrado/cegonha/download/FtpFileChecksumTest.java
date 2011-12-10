/**
 * 
 */
package br.pelommedrado.cegonha.download;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.cegonha.cliente.TransFtpCliente;
import br.pelommedrado.cegonha.download.DownloadManager;
import br.pelommedrado.cegonha.download.FtpFileChecksum;

/**
 * @author pelom
 *
 */
public class FtpFileChecksumTest {

	/** criar cliente FTP **/
	private TransFtpCliente tFtpCliente;

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
		tFtpCliente = new TransFtpCliente();
		tFtpCliente.setServidor("localhost");
		tFtpCliente.setPorta(2121);
		tFtpCliente.setUsuario("anonymous");
		tFtpCliente.setSenha("");
		tFtpCliente.conectar();

		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";

		final DownloadManager download =  new DownloadManager(tFtpCliente.getFtp(), fileLocal, fileRemoto, false);
		assertEquals(true, download.download());
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
	public void testFileCorrompido() throws IOException {
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCorrompimentoTresPacote() throws IOException {
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

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
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(true, fCheck.isPacoteCorrompido());
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPacoteNaoCorrompido() throws IOException {
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(false, fCheck.isPacoteCorrompido());
	}
}