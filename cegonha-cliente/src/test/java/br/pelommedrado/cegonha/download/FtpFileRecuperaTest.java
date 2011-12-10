/**
 * 
 */
package br.pelommedrado.cegonha.download;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.cegonha.cliente.FtpCliente;
import br.pelommedrado.cegonha.download.DownloadManager;
import br.pelommedrado.cegonha.download.FileFtpChecksum;
import br.pelommedrado.cegonha.download.FileFtpRecupera;

/**
 * @author Andre Leite
 */
public class FtpFileRecuperaTest {

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
		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		//nao ocorreu erro no download?
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));

		//corromper o arquivo
		DownloadManagerTest.corromperArquivo(fileLocal, new int[]{0, 5, 10}, DownloadManager.MAX_BUFFER_SIZE);

		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());
		//arquivo foi corrompido?
		assertEquals(true, fCheck.isPacoteCorrompido());

		//===================================================

		FileFtpRecupera fRecuperar = new FileFtpRecupera(tFtpCliente.getFtp(), fCheck.getDownloadFile());
		assertEquals(3, fRecuperar.recuperar());

		//===================================================

		fCheck = new FileFtpChecksum(fileLocal, fileRemoto);
		fCheck.scaniarPacoteCorrompido(tFtpCliente.getFtp());

		assertEquals(false, fCheck.isPacoteCorrompido());
		assertEquals(false, fCheck.verificarFileCorrompido(tFtpCliente.getFtp()));
	}
}