package br.pelommedrado.cegonha.ws;

import java.io.File;
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
import br.pelommedrado.cegonha.server.SementeFtplet;
import br.pelommedrado.cegonha.util.MapaSemente;
import br.pelommedrado.cegonha.util.PropertiesUtil;

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

	/** meus arquivos **/
	private Map<String, File> meusArquivo;

	/** Path base **/
	private String pathBase;

	/**
	 * Construtor da classe.
	 */
	public ArquivoResource() {
		super();

		this.pathBase = PropertiesUtil.getProperty("pasta.download");
		this.meusArquivo = new HashMap<String, File>();
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

		final File file = meusArquivo.get(arquivo);
		
		if(file != null) {
			request.setTemArquivo(file.exists());
			request.setDisponivel(SementeFtplet.count < MAX);
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
		final File file = new File(pathBase + File.separator + arquivo);

		if(file.exists()) {
			meusArquivo.put(arquivo, file);
		}
	}
}