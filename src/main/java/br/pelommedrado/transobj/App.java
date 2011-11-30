/**
 * 
 */
package br.pelommedrado.transobj;

import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.pelommedrado.transobj.server.AppServer;

/**
 * @author Andre Leite
 */
public class App {

	/** Identificador da aplicacao servidor **/
	private static final String APP_SERVER = "appServer";
	
	/** Application Context **/
	private final static ApplicationContext classPathXmlApplicationContext;

	/** Servidor **/
	private AppServer server;

	static {
		classPathXmlApplicationContext =
				new ClassPathXmlApplicationContext ("ftp-server.xml", "applicationContext.xml");
	}


	/**
	 * Construtor da classe.
	 */
	public App() {
		server = (AppServer) classPathXmlApplicationContext.getBean(APP_SERVER);
	}

	/**
	 * Iniciar aplicacao
	 * @throws FtpException 
	 */
	public void iniciar() throws FtpException {
		server.start();
	}
	
	/**
	 * @param args
	 * @throws FtpException 
	 */
	public static void main(String[] args) throws FtpException {
		new App().iniciar();
	}
}