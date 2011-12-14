package br.pelommedrado.cegonha.util;

import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import br.pelommedrado.cegonha.model.Semente;

/**
 * @author Andre Leite
 */
public class MapaSementeTest extends TestCase {

	/** Mapa de sementes **/
	private MapaSemente mapaSemente;

	/** Arquivo **/
	private String arquivo;

	/** Arquivo **/
	private String outroArquivo;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.arquivo = "Capture_20111205.wmv";
		this.outroArquivo = "test_Capture_20111205.wmv";
		this.mapaSemente = new MapaSemente();
	}

	@Test
	public void testAddSemente() {
		Semente semente = new Semente("1");
		mapaSemente.addSemente(arquivo, semente);

		assertEquals(1, mapaSemente.size());
	}

	@Test
	public void testAddSementeIgual() {
		Semente semente = new Semente("1");
		Semente semente1 = new Semente("1");

		Semente semente2 = new Semente("2");

		mapaSemente.addSemente(arquivo, semente);
		mapaSemente.addSemente(arquivo, semente1);
		mapaSemente.addSemente(outroArquivo, semente);
		mapaSemente.addSemente(outroArquivo, semente1);

		mapaSemente.addSemente(arquivo, semente2);
		mapaSemente.addSemente(outroArquivo, semente2);

		assertEquals(4, mapaSemente.size());
	}

	@Test
	public void testObterSemente() {
		Semente semente = new Semente("1");
		Semente semente1 = new Semente("1");

		Semente semente2 = new Semente("2");

		mapaSemente.addSemente(arquivo, semente);
		mapaSemente.addSemente(arquivo, semente1);
		mapaSemente.addSemente(outroArquivo, semente);
		mapaSemente.addSemente(outroArquivo, semente1);

		mapaSemente.addSemente(arquivo, semente2);
		mapaSemente.addSemente(outroArquivo, semente2);

		Set<Semente> sementes = mapaSemente.getListaSementes(arquivo);

		assertEquals(4, sementes.size());
	}

	@Test
	public void testRemocaoSementeInativas() {
		Semente semente = new Semente("1");
		Semente semente1 = new Semente("1");

		Semente semente2 = new Semente("localhost");

		mapaSemente.addSemente(arquivo, semente);
		mapaSemente.addSemente(arquivo, semente1);
		mapaSemente.addSemente(outroArquivo, semente);
		mapaSemente.addSemente(outroArquivo, semente1);

		mapaSemente.addSemente(arquivo, semente2);
		mapaSemente.addSemente(outroArquivo, semente2);

		mapaSemente.verificarSementeAtiva();
		mapaSemente.removerSementeInativa();

		assertEquals(1, mapaSemente.size());
	}
}
