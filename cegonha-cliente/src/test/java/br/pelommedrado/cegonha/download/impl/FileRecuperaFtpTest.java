/**
 * 
 */
package br.pelommedrado.cegonha.download.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.pelommedrado.cegonha.cliente.FtpCliente;
import br.pelommedrado.cegonha.download.impl.DownloadManager;
import br.pelommedrado.cegonha.download.impl.FileChecksumFtp;
import br.pelommedrado.cegonha.download.impl.FileRecuperaFtp;

/**
 * @author Andre Leite
 */
public class FileRecuperaFtpTest {

	/** criar cliente FTP **/
	private FtpCliente tFtpCliente;

	/** Arquivo local **/
	private String fileLocal;

	/** Arquivo remoto **/
	private String fileRemoto;

	/** Verificador de arquivo **/
	private FileChecksumFtp fCheck;

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

		//baixar o arquivo.
		DownloadManager download =  new DownloadManager(tFtpCliente.getFtp(), fileLocal, fileRemoto, false);
		//download concluido?
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
	public void testRecuperacaoFile() throws IOException {
		fCheck = new FileChecksumFtp(fileLocal, fileRemoto);
		//nao ocorreu erro no download?
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));

		//corromper o arquivo
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);

		fCheck.verificarPacoteCorrompido(tFtpCliente.getFtp());
		//arquivo foi corrompido?
		assertEquals(true, fCheck.isPacoteCorrompido());

		//===================================================

		FileRecuperaFtp fRecuperar = new FileRecuperaFtp(tFtpCliente.getFtp(), fCheck.getDownloadFile());
		assertEquals(3, fRecuperar.recuperar());

		//===================================================

		fCheck = new FileChecksumFtp(fileLocal, fileRemoto);
		fCheck.verificarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(false, fCheck.isPacoteCorrompido());
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testRecuperacaoFileNaoConectado() throws IOException {
		fCheck = new FileChecksumFtp(fileLocal, fileRemoto);
		//nao ocorreu erro no download?
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));

		//corromper o arquivo
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);

		fCheck.verificarPacoteCorrompido(tFtpCliente.getFtp());
		//arquivo foi corrompido?
		assertEquals(true, fCheck.isPacoteCorrompido());

		//===================================================

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);

		FileRecuperaFtp fRecuperar = new FileRecuperaFtp(ftpMock, fCheck.getDownloadFile());
		fRecuperar.recuperar();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testRecuperacaoFileRespostaServidorNegativa() throws IOException {
		fCheck = new FileChecksumFtp(fileLocal, fileRemoto);
		//nao ocorreu erro no download?
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));

		//corromper o arquivo
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);

		fCheck.verificarPacoteCorrompido(tFtpCliente.getFtp());
		//arquivo foi corrompido?
		assertEquals(true, fCheck.isPacoteCorrompido());

		//===================================================

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(true);
		Mockito.when(ftpMock.getReplyCode()).thenReturn(500);
		
		FileRecuperaFtp fRecuperar = new FileRecuperaFtp(ftpMock, fCheck.getDownloadFile());
		fRecuperar.recuperar();
	}
}