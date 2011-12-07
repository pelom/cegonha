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
public class FtpFileRecuperaTest {

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

		//baixar o arquivo.
		DownloadManager download =  new DownloadManager(ftp, fileLocal, fileRemoto);
		//download concluido?
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
	public void testRecuperacaoFile() throws IOException {
		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		//nao ocorreu erro no download?
		assertEquals(false, fCheck.verificarFileCorrompido(ftp));

		//corromper o arquivo
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);

		fCheck.scaniarPacoteCorrompido(ftp);
		//arquivo foi corrompido?
		assertEquals(true, fCheck.isPacoteCorrompido());

		//===================================================

		FtpFileRecupera fRecuperar = new FtpFileRecupera(ftp, fCheck.getDownloadFile());
		assertEquals(3, fRecuperar.recuperar());

		//===================================================

		fCheck = new FtpFileChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(ftp);

		assertEquals(false, fCheck.isPacoteCorrompido());
		assertEquals(false, fCheck.verificarFileCorrompido(ftp));
	}
}