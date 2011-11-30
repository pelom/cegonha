/**
 * 
 */
package br.pelommedrado.transobj.util;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

/**
 * @author Andre Leite
 */
public class FileUtils {

	/**
	 * 
	 * @param path
	 * 
	 * @return
	 * @throws IOException
	 */
	public static long gerarChecksum(String path) throws IOException {
		final File myfile = new File(path);
		return  Files.getChecksum(myfile , new java.util.zip.CRC32());
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public static boolean isCorrompido(String path, long checksum) throws IOException {
		return checksum != gerarChecksum(path);
	}
}