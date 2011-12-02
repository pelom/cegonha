package br.pelommedrado.trans.cliente;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.pelommedrado.trans.model.RequisicaoArquivo;
import br.pelommedrado.trans.model.Semente;
import br.pelommedrado.trans.ws.ClienteWs;

import com.sun.jersey.api.client.GenericType;

/**
 * @author Andre Leite
 */
public class TransWebServiceCliente {

	/** Strings da URL **/
	private static final String REST 	= "rest";
	private static final String ARQUIVO = "arquivo";

	/** Cliente web service **/
	private ClienteWs clienteWs = null;

	/**
	 * 
	 * @param server
	 */
	public TransWebServiceCliente(String server) {
		super();

		//criar cliente
		clienteWs = new ClienteWs(server);
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	public List<Semente> obterSemente(String arquivo) {
		List<Semente> listaSemente = clienteWs.getWebResource().
				path(REST).
				path(ARQUIVO).
				path("lista-semente").path(arquivo).
				accept(MediaType.TEXT_XML).get(new GenericType<List<Semente>>(){});

		return listaSemente;
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	public RequisicaoArquivo isArquivo(String arquivo) {
		return clienteWs.getWebResource().
				path(REST).
				path(ARQUIVO).
				path("is-arquivo").path(arquivo).
				accept(MediaType.TEXT_XML).get(RequisicaoArquivo.class);
	}

	/**
	 * 
	 * @param arquivo
	 */
	public void registrarNovoArquivo(String arquivo) {
		clienteWs.getWebResource().
		path(REST).
		path(ARQUIVO).
		path("registrar-arquivo").path(arquivo).
		accept(MediaType.TEXT_XML).get(Response.class);
	}
}