package br.pelommedrado.transobj.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.sun.jersey.spi.resource.Singleton;

import br.pelommedrado.trans.model.RequisicaoArquivo;
import br.pelommedrado.trans.model.Semente;
import br.pelommedrado.trans.util.MapaSemente;
import br.pelommedrado.transobj.server.ServidorFtp;

/**
 * @author Andre Leite
 */
@Path("/arquivo")
@Singleton
public class ArquivoResource extends SpringBeanAutowiringSupport {

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
	public List<Semente> listaSemente(@PathParam("arquivo") String arquivo) {
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
	@GET
	@Path("/registrar-arquivo/{arquivo}")
	public Response registrarNovoArquivo(@PathParam("arquivo") String arquivo) {

		meusArquivo.put(arquivo, "SIM");

		return Response.ok().build();
	}
}