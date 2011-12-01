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

import br.pelommedrado.transobj.model.RequisicaoArquivo;
import br.pelommedrado.transobj.model.Semente;
import br.pelommedrado.transobj.server.AppServer;
import br.pelommedrado.transobj.util.MapaSemente;

/**
 * @author Andre Leite
 */
@Path("/arquivo")
public class ArquivoResource extends SpringBeanAutowiringSupport {

	private static final int MAX = 2;

	@Autowired
	private MapaSemente mapaSemente;

	@Autowired
	private AppServer server;

	/** meus arquivos **/
	private Map<String, String> meusArquivo;

	/**
	 * construtor da classe.
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
	public List<Semente> listSemente(@PathParam("arquivo") String arquivo) {
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