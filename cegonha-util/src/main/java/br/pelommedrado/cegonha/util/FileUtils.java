/**
 * 
 */
package br.pelommedrado.cegonha.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.google.common.io.Files;

/**
 * @author Andre Leite
 */
public class FileUtils {

	/**
	 * 
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	public static long gerarCheckSum(byte[] buffer) throws IOException {
		CheckedInputStream cis = null;
		byte readBuffer[] = new byte[buffer.length];

		try {
			cis = new CheckedInputStream(new ByteArrayInputStream(buffer), new CRC32());
			cis.read(readBuffer);

			return cis.getChecksum().getValue();

		} finally {
			if(cis != null) {
				cis.close();
			}
		}
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static long gerarChecksum(String path) throws IOException {
		final File myfile = new File(path);
		return gerarChecksum(myfile);
	}
	/**
	 * 
	 * @param path
	 * 
	 * @return
	 * @throws IOException
	 */
	public static long gerarChecksum(File file) throws IOException {
		return  Files.getChecksum(file , new CRC32());
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public static boolean isCorrompido(byte[] buffer, long checksum) throws IOException {
		return checksum != gerarCheckSum(buffer);
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