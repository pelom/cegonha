/**
 * 
 */
package br.pelommedrado.cegonha.download.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.pelommedrado.cegonha.cliente.FtpCliente;
import br.pelommedrado.cegonha.download.util.FileDownload;
import br.pelommedrado.cegonha.util.FileUtils;

/**
 * @author Andre Leite
 */
public class DownloadManagerTest {

	/** Arquivo local **/
	private static String fileLocal;

	/** Arquivo remoto **/
	private static String fileRemoto;

	/** Arquivo local para mock **/
	private static String stgFile = "src/test/resources/Capture_20111205.wmv";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fileLocal = "/home/pelom/Capture_20111205.wmv";
		fileRemoto = "Capture_20111205.wmv";
	}

	/*@AfterClass
	public static void verificarFile() {
		assertEquals(true, new File(fileLocal).exists());
		assertEquals(false, new File(fileLocal + DownloadManager.EXT_PROPERTIES).exists());
	}*/

	/**
	 * 
	 * @return
	 */
	private FtpCliente criarFtpClienteReal() {
		FtpCliente tFtpCliente = new FtpCliente();
		tFtpCliente.setServidor("localhost");
		tFtpCliente.setPorta(2121);
		tFtpCliente.setUsuario("admin");
		tFtpCliente.setSenha("admin");

		return tFtpCliente;
	}

	/**
	 * 
	 * @param ftpCliente
	 * @return
	 * @throws IOException
	 */
	private FileDownload criarFileDownloadReal(FtpCliente ftpCliente) throws IOException {
		return new FileDownload(fileLocal, fileRemoto);
	}

	/**
	 * 
	 * @param FtpCliente
	 * @return
	 * @throws IOException 
	 */
	private DownloadManager criarDownloadManagerReal(FtpCliente ftpCliente) throws IOException {
		return new DownloadManager(ftpCliente.getFtp(), criarFileDownloadReal(ftpCliente));
	}

	/**
	 *  Testar o download iniciando do inicio do arquivo
	 * @throws IOException
	 */
	@Test
	public void testDownload() throws IOException {
		//conectar ao servidor
		FtpCliente ftpCliente = criarFtpClienteReal();
		ftpCliente.conectar();

		DownloadManager download = criarDownloadManagerReal(ftpCliente);
		assertEquals(true, download.download());
	}

	/**
	 * Testar o download iniciando a parti de um pedaco do arquivo
	 * @throws IOException
	 */
	@Test
	public void testDownloadContinuacao() throws IOException {
		//copiar um pedaco do arquivo
		FileInputStream source = new FileInputStream(stgFile);
		FileOutputStream dest = new FileOutputStream(fileLocal + DownloadManager.EXT_FILE);
		long size = FileUtils.copyStream(source, dest, 
				DownloadManager.MAX_BUFFER_SIZE, DownloadManager.MAX_BUFFER_SIZE * 10);

		Properties props = new Properties();
		props.setProperty("download", size+"");
		final FileOutputStream fo = new FileOutputStream(fileLocal + DownloadManager.EXT_PROPERTIES);
		props.store(fo, "parametros de controle do download");
		fo.flush();
		fo.close();

		//conectar ao servidor
		FtpCliente ftpCliente = criarFtpClienteReal();
		ftpCliente.conectar();

		DownloadManager download = criarDownloadManagerReal(ftpCliente);
		assertEquals(true, download.download());
	}

	/**
	 * Testar o download executando o processo de recuperacao do arquivo corrompido
	 * @throws IOException
	 */
	@Test
	public void testDownloadRecuperacaoArquivoCorrompido() throws IOException {
		//criar entrada de dados moquiada
		final FileInputStream in = new FileInputStream(stgFile);

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(in);
		Mockito.when(ftpMock.completePendingCommand()).thenReturn(true);
		Mockito.when(ftpMock.isConnected()).thenReturn(true);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);

		//criar download manager com o mock do FTP
		DownloadManager download = new DownloadManager(ftpMock, fileDownload);

		//crair mock do Chechsum File
		final FileChecksumFtp fileFtpChecksumMock = Mockito.mock(FileChecksumFtp.class);
		Mockito.when(fileFtpChecksumMock.getFileDownload()).thenReturn(fileDownload);
		Mockito.when(fileFtpChecksumMock.verificarFileCorrompido(ftpMock)).thenReturn(true);
		Mockito.when(fileFtpChecksumMock.verificarPacoteCorrompido(ftpMock)).thenReturn(0);
		Mockito.when(fileFtpChecksumMock.isPacoteCorrompido()).thenReturn(true);

		//setar Chechsum File moquiado
		download.setFileChecksum(fileFtpChecksumMock);

		assertEquals(true, download.download());
	}

	/**
	 * Testar o download executando o processo de recuperacao do arquivo 
	 * corrompido nao sendo possivel encontrar a parte corrompida
	 * 
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testDownloadRecuperacaoArquivoCorrompidoNaoCompleta() throws IOException {
		//criar entrada de dados moquiada
		final FileInputStream in = new FileInputStream(stgFile);

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(in);
		Mockito.when(ftpMock.completePendingCommand()).thenReturn(true);
		Mockito.when(ftpMock.isConnected()).thenReturn(true);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);

		//criar download manager com o mock do FTP
		DownloadManager download = new DownloadManager(ftpMock, fileDownload);

		//crair mock do Chechsum File
		final FileChecksumFtp fileFtpChecksumMock = Mockito.mock(FileChecksumFtp.class);
		Mockito.when(fileFtpChecksumMock.getFileDownload()).thenReturn(fileDownload);
		Mockito.when(fileFtpChecksumMock.verificarFileCorrompido(ftpMock)).thenReturn(true);
		Mockito.when(fileFtpChecksumMock.verificarPacoteCorrompido(ftpMock)).thenReturn(0);
		Mockito.when(fileFtpChecksumMock.isPacoteCorrompido()).thenReturn(false);

		//setar Chechsum File moquiado
		download.setFileChecksum(fileFtpChecksumMock);
		download.download();
	}

	/**
	 * Testar o download executando o processo de recuperacao do arquivo 
	 * corrompido como o numero de pacotes recuperados diferente ao corrompido
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDownloadRecuperacaoArquivoCorrompidoNaoCompletaNumPacote() throws IOException {
		//criar entrada de dados moquiada
		final FileInputStream in = new FileInputStream(stgFile);

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(in);
		Mockito.when(ftpMock.completePendingCommand()).thenReturn(true);
		Mockito.when(ftpMock.isConnected()).thenReturn(true);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);
		//criar download manager com o mock do FTP
		DownloadManager download = new DownloadManager(ftpMock, fileDownload);

		//crair mock do Chechsum File
		final FileChecksumFtp fileFtpChecksumMock = Mockito.mock(FileChecksumFtp.class);
		Mockito.when(fileFtpChecksumMock.getFileDownload()).thenReturn(fileDownload);
		Mockito.when(fileFtpChecksumMock.verificarFileCorrompido(ftpMock)).thenReturn(true);
		Mockito.when(fileFtpChecksumMock.verificarPacoteCorrompido(ftpMock)).thenReturn(0);
		Mockito.when(fileFtpChecksumMock.isPacoteCorrompido()).thenReturn(true);
		//setar Chechsum File moquiado
		download.setFileChecksum(fileFtpChecksumMock);

		final FileRecuperaFtp fileFtpRecuperaMock = Mockito.mock(FileRecuperaFtp.class);
		Mockito.when(fileFtpRecuperaMock.recuperar()).thenReturn(1);

		//setar Ftp Recuperar moquiado
		download.setFileRecupera(fileFtpRecuperaMock);

		assertEquals(false, download.download());
	}

	/**
	 * Testar o download executando o processo de recuperacao do arquivo 
	 * corrompido com o valor da variavel de recuperacao igual a false
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDownloadRecuperacaoValorFalse() throws IOException {
		//criar entrada de dados moquiada
		final FileInputStream in = new FileInputStream(stgFile);

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(in);
		Mockito.when(ftpMock.completePendingCommand()).thenReturn(true);
		Mockito.when(ftpMock.isConnected()).thenReturn(true);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);
		fileDownload.setRecuperar(false);

		//crair mock do Chechsum File
		final FileChecksumFtp fileFtpChecksumMock = Mockito.mock(FileChecksumFtp.class);
		Mockito.when(fileFtpChecksumMock.getFileDownload()).thenReturn(fileDownload);
		Mockito.when(fileFtpChecksumMock.verificarFileCorrompido(ftpMock)).thenReturn(true);
		Mockito.when(fileFtpChecksumMock.verificarPacoteCorrompido(ftpMock)).thenReturn(0);
		Mockito.when(fileFtpChecksumMock.isPacoteCorrompido()).thenReturn(true);

		//criar download manager com o mock do FTP
		DownloadManager download = new DownloadManager(ftpMock, fileDownload);

		//setar Chechsum File moquiado
		download.setFileChecksum(fileFtpChecksumMock);
		assertEquals(false, download.download());
	}

	/**
	 *  Testar o download com erro na leitura do arquivo no servidor
	 * @throws IOException
	 */
	@Test(expected=CopyStreamException.class)
	public void testDownloadErroLeitura() throws IOException {
		final FileInputStream inMock  = Mockito.mock(FileInputStream.class);
		Mockito.doThrow(new IOException()).when(inMock).read(new byte[DownloadManager.MAX_BUFFER_SIZE]);

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(inMock);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);

		//criar download manager com o mock do FTP
		DownloadManager download = new DownloadManager(ftpMock, fileDownload);
		download.download();
	}

	/**
	 *  Testar o download com o comando de recusao do servidor
	 *  o download e concluido poram o servidor informa que o download esta com erro
	 * @throws IOException
	 */
	@Test()
	public void testDownloadRecusandoServidor() throws IOException {
		//criar entrada de dados moquiada
		final FileInputStream in = new FileInputStream(stgFile);

		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(in);
		Mockito.when(ftpMock.completePendingCommand()).thenReturn(false);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);

		//criar download manager com o mock do FTP
		DownloadManager download = new DownloadManager(ftpMock, fileDownload);
		download.download();
	}

	/**
	 *  Testar o download que por algum motivo o servidor nao conseguiu abrir o file
	 * @throws IOException
	 */
	@Test(expected=IOException.class)
	public void testDownloadProblemaFileRemoto() throws IOException {
		//crair mock FTP
		final FTPClient ftpMock = Mockito.mock(FTPClient.class); 
		Mockito.when(ftpMock.retrieveFileStream(anyString())).thenReturn(null);

		FileDownload fileDownload =	new FileDownload(fileLocal, fileRemoto);

		DownloadManager download = new DownloadManager(ftpMock, fileDownload);
		download.download();
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