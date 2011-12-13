package br.pelommedrado.cegonha.cliente;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andre Leite
 */
public class CegonhaClienteTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public void testCegonha() {
		FtpCliente tFtpCliente = new FtpCliente();
		tFtpCliente.setServidor("localhost");
		tFtpCliente.setPorta(2121);
		tFtpCliente.setUsuario("admin");
		tFtpCliente.setSenha("admin");
		
		WsCliente wsServidor = new WsCliente("localhost", "8080", "cegonha-web");
		
		CegonhaCliente cegonha = new CegonhaCliente();
		cegonha.setDirOut("/home/pelom/");
		cegonha.setDirRemoto("/");
		cegonha.setRecuperar(true);
		cegonha.setServidorFtp("localhost");
		
		cegonha.setFtpCliente(tFtpCliente);
		cegonha.setWsServidor(wsServidor);
		
		final List<String> arquivos = new ArrayList<String>();
		arquivos.add("Capture_20111205.wmv");
		
		assertEquals(true, cegonha.obterArquivos(arquivos));
	}
	/**
	 * 
	 */
	@Test
	public void testCegonhaListaVazia() {
		CegonhaCliente cegonha = new CegonhaCliente();
		final List<String> arquivos = new ArrayList<String>();
		assertEquals(false, cegonha.obterArquivos(arquivos));
	}
}