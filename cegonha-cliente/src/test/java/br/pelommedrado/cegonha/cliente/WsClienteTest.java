package br.pelommedrado.cegonha.cliente;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.SocketException;

import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.cegonha.model.RequisicaoArquivo;

/**
 * @author Andre Leite
 */
public class WsClienteTest {

	/** Cliente do Web Service **/
	private WsCliente wsCliente;

	/** Nome do arquivo **/
	private String arquivo;

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		wsCliente = new WsCliente("localhost", "8080", "cegonha-web");
		arquivo = "Capture_20111205.wmv";
	}

	/**
	 * 
	 */
	@Test
	public void testObterSemente() {
		assertEquals(1, wsCliente.obterSemente(arquivo).size());
	}

	/**
	 * 
	 * @throws SocketException
	 * @throws IOException
	 */
	@Test
	public void testParamConexao() throws SocketException, IOException {
		assertEquals("cegonha-web", wsCliente.getNome());
		assertEquals("8080",  wsCliente.getPorta());
	}
	
	/**
	 * 
	 */
	@Test
	public void testIsArquivo() {
		RequisicaoArquivo request = wsCliente.isArquivo(arquivo);
		assertEquals(false, request.isTemArquivo());
		assertEquals(false, request.isDisponivel());
	}

	/**
	 * 
	 */
	@Test
	public void testPlantaSemente() {
		wsCliente.plantarSemente(arquivo);

		RequisicaoArquivo request = wsCliente.isArquivo(arquivo);
		assertEquals(true, request.isTemArquivo());
		assertEquals(true, request.isDisponivel());
	}
}