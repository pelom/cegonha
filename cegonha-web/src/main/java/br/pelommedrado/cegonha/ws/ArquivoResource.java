package br.pelommedrado.cegonha.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.pelommedrado.cegonha.model.RequisicaoArquivo;
import br.pelommedrado.cegonha.model.Semente;
import br.pelommedrado.cegonha.server.ServidorFtp;
import br.pelommedrado.cegonha.util.MapaSemente;

import com.sun.jersey.spi.resource.Singleton;

/**
 * @author Andre Leite
 */
@Path("/arquivo")
@Component
@Singleton
public class ArquivoResource {

	/** Maximo de conexao para download**/
	private static final int MAX = 2;

	@Autowired
	private MapaSemente mapaSemente;

	@Autowired
	private ServidorFtp server;

	/** meus arquivos **/
	private Map<String, String> meusArquivo;

	/**
	 * Construtor da classe.
	 */
	public ArquivoResource() {
		super();

		meusArquivo = new HashMap<String, String>();
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	@GET
	@Path("/lista-semente/{arquivo}")
	public Set<Semente> listaSemente(@PathParam("arquivo") String arquivo) {
		return mapaSemente.getListaSementes(arquivo);
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	@GET
	@Path("/is-arquivo/{arquivo}")
	public RequisicaoArquivo isArquivo(@PathParam("arquivo") String arquivo) {
		//criar resposta
		RequisicaoArquivo request = new RequisicaoArquivo();

		String t = meusArquivo.get(arquivo);
		if(t != null) {
			request.setTemArquivo(true);
			request.setDisponivel(server.getCount() < MAX);
		}

		return request;
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	@PUT
	@Path("/plantasemente/{arquivo}")
	public void plantarSemente(@PathParam("arquivo") String arquivo) {
		meusArquivo.put(arquivo, "SIM");
	}
}