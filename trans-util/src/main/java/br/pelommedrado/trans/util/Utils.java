/**
 * 
 */
package br.pelommedrado.trans.util;

import java.net.InetAddress;

/**
 * @author Andre Leite
 */
public class Utils {

	public static boolean ping(String host) {
		return doPing(host, 3000); // 3 segundos
	}

	/**
	 * Verifica se determinado host esta atingivel
	 */
	public static boolean doPing(String host,int timeOut) {
		try {
			return InetAddress.getByName(host).isReachable(timeOut);
			
		} catch (Exception e) {
			return false;
		}
	}
}