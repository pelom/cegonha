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

	/** Nome do web service **/
	private String nomeWs = null;

	/** Porta do web service **/
	private String porta = null;

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
	 * @param ip
	 * @return
	 */
	public String formataUrl(String ip) {
		return  new StringBuffer("http://").
				append(ip).
				append(":").
				append(porta).
				append("/").
				append(nomeWs).toString();
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

	/**
	 * @return the nomeWs
	 */
	public String getNomeWs() {
		return nomeWs;
	}

	/**
	 * @param nomeWs the nomeWs to set
	 */
	public void setNomeWs(String nomeWs) {
		this.nomeWs = nomeWs;
	}

	/**
	 * @return the porta
	 */
	public String getPorta() {
		return porta;
	}

	/**
	 * @param porta the porta to set
	 */
	public void setPorta(String porta) {
		this.porta = porta;
	}
}