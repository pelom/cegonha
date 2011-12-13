/**
 * 
 */
package br.pelommedrado.cegonha.download.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.pelommedrado.cegonha.cliente.FtpCliente;
import br.pelommedrado.cegonha.download.IFileRecuperar;
import br.pelommedrado.cegonha.download.util.FileDownload;

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

	/** Arquivo a ser baixado **/
	private FileDownload fileDownload;

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

		//obter informacoes do arquivo a ser baixado
		final FTPFile ftpFile = tFtpCliente.getFtp().listFiles(fileRemoto)[0];
		//tamanho do arquivo
		final long size = ftpFile.getSize();

		fileDownload = new FileDownload(fileLocal, fileRemoto);
		fileDownload.setLen(size);
		fileDownload.setRecuperar(true);

		//baixar o arquivo.
		DownloadManager download =  new DownloadManager(tFtpCliente.getFtp(), fileDownload);
		//download concluido?
		assertEquals(true, download.download());

		fCheck = new FileChecksumFtp();
		fCheck.setFileDownload(fileDownload);

		//nao ocorreu erro no download?
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));

		//corromper o arquivo
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);

		//arquivo foi corrompido?
		assertEquals(3, fCheck.verificarPacoteCorrompido(tFtpCliente.getFtp()));
		assertEquals(true, fCheck.isPacoteCorrompido());
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
		IFileRecuperar fRecuperar = new FileRecuperaFtp();
		fRecuperar.setFileDownload(fileDownload);
		fRecuperar.setFtp(tFtpCliente.getFtp());

		assertEquals(3, fRecuperar.recuperar());
		assertEquals(0, fCheck.verificarPacoteCorrompido(tFtpCliente.getFtp()));
		assertEquals(false, fCheck.isPacoteCorrompido());
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testRecuperacaoFileNaoConectado() throws IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);

		IFileRecuperar fRecuperar = new FileRecuperaFtp();
		fRecuperar.setFileDownload(fileDownload);
		fRecuperar.setFtp(ftpMock);
		fRecuperar.recuperar();
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testRecuperacaoFileRespostaServidorNegativa() throws IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(true);
		Mockito.when(ftpMock.getReplyCode()).thenReturn(500);

		IFileRecuperar fRecuperar = new FileRecuperaFtp();
		fRecuperar.setFileDownload(fileDownload);
		fRecuperar.setFtp(ftpMock);
		fRecuperar.recuperar();
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testRecuperacaoPorcentualMax() throws IOException {
		
		fileDownload.setPorcentualMax(0);
		
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(true);
		Mockito.when(ftpMock.getReplyCode()).thenReturn(200);

		IFileRecuperar fRecuperar = new FileRecuperaFtp();
		fRecuperar.setFileDownload(fileDownload);
		fRecuperar.setFtp(ftpMock);
		fRecuperar.recuperar();
	}
}