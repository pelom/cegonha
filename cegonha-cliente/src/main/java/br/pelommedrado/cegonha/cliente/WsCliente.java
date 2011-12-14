package br.pelommedrado.cegonha.cliente;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import br.pelommedrado.cegonha.model.RequisicaoArquivo;
import br.pelommedrado.cegonha.model.Semente;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author Andre Leite
 */
public class WsCliente {

	/** Strings da URL **/
	private static final String REST 	= "rest";
	private static final String ARQUIVO = "arquivo";

	/** Cliente web service **/
	private WebResource service;

	/** Numero da porta **/
	private String porta;

	/** Nome do servico **/
	private String nome;

	/**
	 * 
	 * @param ip
	 * @param porta
	 * @param servico
	 */
	public WsCliente(String ip, String porta, String servico) {
		super();

		this.porta = porta;
		this.nome = servico;

		final String conexao = formataUrl(ip, porta, servico);
		final ClientConfig config = new DefaultClientConfig();
		final Client client = Client.create(config);

		this.service = client.resource(getBaseURI(conexao));
	}

	/**
	 * 
	 * @param ip
	 * @return
	 */
	public String formataUrl(String ip, String porta, String servico) {
		return  new StringBuffer("http://").
				append(ip).
				append(":").
				append(porta).
				append("/").
				append(servico).toString();
	}

	/**
	 * 
	 * @param arquivo
	 * @return
	 */
	public List<Semente> obterSemente(String arquivo) {
		final List<Semente> listaSemente = service.
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
		return service.
				path(REST).
				path(ARQUIVO).
				path("is-arquivo").path(arquivo).
				accept(MediaType.TEXT_XML).get(RequisicaoArquivo.class);
	}

	/**
	 * 
	 * @param arquivo
	 */
	public void plantarSemente(String arquivo) {
		service.
		path(REST).
		path(ARQUIVO).
		path("plantasemente").path(arquivo).put();
	}

	/**
	 * 
	 * @return
	 */
	private static URI getBaseURI(String server) {
		return UriBuilder.fromUri(server).build();
	}

	/**
	 * @return the porta
	 */
	public String getPorta() {
		return porta;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}
}