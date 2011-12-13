package br.pelommedrado.cegonha.cliente;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.pelommedrado.cegonha.download.util.FileDownload;

/**
 * @author Andre Leite
 */
public class FtpClienteTest {

	/** Cliente FTP **/
	private FtpCliente ftpClite;

	/** Arquivo local **/
	private static String fileLocal;

	/** Arquivo remoto **/
	private static String fileRemoto;

	/** Arquivo a ser baixado **/
	private FileDownload fileDownload;

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		//conectar ao servidor
		ftpClite = new FtpCliente();
		ftpClite.setServidor("localhost");
		ftpClite.setPorta(2121);
		ftpClite.setUsuario("admin");
		ftpClite.setSenha("admin");

		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";

		fileDownload = new FileDownload(fileLocal, fileRemoto);
	}

	/**
	 * 
	 * @throws SocketException
	 * @throws IOException
	 */
	@Test
	public void testParamConexao() throws SocketException, IOException {
		assertEquals("localhost", ftpClite.getServidor());
		assertEquals("admin", ftpClite.getUsuario());
		assertEquals("admin", ftpClite.getSenha());
		assertEquals(2121, ftpClite.getPorta());
	}

	/**
	 * 
	 * @throws SocketException
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testConectarConexaoAtiva() throws SocketException, IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(true);
		ftpClite.setFtp(ftpMock);
		ftpClite.conectar();
	}

	/**
	 * 
	 * @throws SocketException
	 * @throws IOException
	 */
	@Test
	public void testConectarConexaoRespostaNegativa() throws SocketException, IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				return "called with arguments: " + args;
			}
		}).when(ftpMock).connect(anyString(), anyInt());

		Mockito.when(ftpMock.getReplyCode()).thenReturn(500);

		ftpClite.setFtp(ftpMock);
		ftpClite.conectar();
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDownload() throws IOException {
		ftpClite.conectar();
		assertEquals(true, ftpClite.download(fileDownload));
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testDownloadNaoconectado() throws IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);
		ftpClite.setFtp(ftpMock);
		ftpClite.download(fileDownload);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDesconecteFtpNull() throws IOException {
		ftpClite.setFtp(null);
		ftpClite.desconectar();
	}

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDesconecteFtpDesconectado() throws IOException {
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.isConnected()).thenReturn(false);
		ftpClite.setFtp(ftpMock);
		ftpClite.desconectar();
	}
}