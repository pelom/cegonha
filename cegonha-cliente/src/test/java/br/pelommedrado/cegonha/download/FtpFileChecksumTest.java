/**
 * 
 */
package br.pelommedrado.cegonha.download;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.pelommedrado.cegonha.cliente.FtpCliente;

/**
 * @author Andre Leite
 */
public class FtpFileChecksumTest {

	/** criar cliente FTP **/
	private FtpCliente tFtpCliente;

	/** Arquivo local **/
	private String fileLocal;

	/** Arquivo remoto **/
	private String fileRemoto;

	/** Verificador de arquivo **/
	private FileFtpChecksum fCheck;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		//conectar ao servidor
		tFtpCliente = new FtpCliente();
		tFtpCliente.setServidor("localhost");
		tFtpCliente.setPorta(2121);
		tFtpCliente.setUsuario("admin");
		tFtpCliente.setSenha("admin");
		tFtpCliente.conectar();

		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";

		final DownloadManager download =  new DownloadManager(tFtpCliente.getFtp(), fileLocal, fileRemoto, false);
		assertEquals(true, download.download());
	}

	/**
	 * 
	 */
	@After
	public void verificarFile() {
		assertEquals(true, new File(fileLocal).exists());
		assertEquals(false, new File(fileLocal + DownloadManager.EXT_PROPERTIES).exists());
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
	public void testVerificarFileNaoEstaCorrompido() throws IOException {
		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testVerificarFileCorrompimento() throws IOException {
		//corromper 3 pacotes
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, 
				DownloadManager.MAX_BUFFER_SIZE);

		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		assertEquals(true, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testVerificarPacoteCorrompido() throws IOException {
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0}, DownloadManager.MAX_BUFFER_SIZE);
		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(true, fCheck.isPacoteCorrompido());
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testVerificarPacoteNaoCorrompido() throws IOException {
		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(false, fCheck.isPacoteCorrompido());
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testVerificarTresPacotesEstaCorrompimento() throws IOException {
		//corromper 3 pacotes
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, 
				DownloadManager.MAX_BUFFER_SIZE);

		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(3, fCheck.getDownloadFile().getPacotes().size());
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testVerificarPacoteCorrompidoNaoConectado() throws IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);
		
		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftpMock);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testVerificarPacoteRespostaServidorNegativa() throws IOException {

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(true);
		Mockito.when(ftpMock.doCommand(anyString(), anyString())).thenReturn(true);
		Mockito.when(ftpMock.getReplyCode()).thenReturn(500);

		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftpMock);

	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testVerificarFileSemRespostaServidor() throws IOException {

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(true);
		Mockito.when(ftpMock.doCommand(anyString(), anyString())).thenReturn(true);
		Mockito.when(ftpMock.getReplyCode()).thenReturn(500);

		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.verificarFileCorrompido(ftpMock);

	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testVerificarFileNaoconectado() throws IOException {

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);

		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.verificarFileCorrompido(ftpMock);

	}
}