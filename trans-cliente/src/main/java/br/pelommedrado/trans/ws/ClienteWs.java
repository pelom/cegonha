package br.pelommedrado.trans.ws;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author Andre Leite
 */
public class ClienteWs {

	/** Cliente do ws **/
	private WebResource service;

	/**
	 * 
	 */
	public ClienteWs(String server) {
		final ClientConfig config = new DefaultClientConfig();
		final Client client = Client.create(config);

		service = client.resource(getBaseURI(server));
	}

	/**
	 * 
	 * @return
	 */
	public WebResource getWebResource() {
		return service;
	}

	/**
	 * 
	 * @return
	 */
	private static URI getBaseURI(String server) {
		return UriBuilder.fromUri(server).build();
	}
}