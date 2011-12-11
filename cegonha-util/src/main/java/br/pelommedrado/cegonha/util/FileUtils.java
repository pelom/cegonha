/**
 * 
 */
package br.pelommedrado.cegonha.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.google.common.io.Files;

/**
 * @author Andre Leite
 */
public class FileUtils {

	/**
	 * 
	 * @param source
	 * @param dest
	 * @param bufferSize
	 * @param totalBytes
	 * @return
	 * @throws IOException
	 */
	public static final long copyStream(InputStream source, OutputStream dest,
			int bufferSize, long totalBytes) throws IOException {
		int bytes;
		long total;
		byte[] buffer;

		buffer = new byte[bufferSize];
		total = 0;

		try {
			while ((bytes = source.read(buffer)) != -1 && total <= totalBytes) {
				// Technically, some read(byte[]) methods may return 0 and we cannot
				// accept that as an indication of EOF.

				if (bytes == 0) {
					bytes = source.read();
					if (bytes < 0)
						break;

					dest.write(bytes);

					++total;

					continue;
				}

				dest.write(buffer, 0, bytes);
				dest.flush();

				total += bytes;
			}

		} finally {
			// Close file.
			if (dest != null) {
				try {
					dest.close();
				} catch (Exception e) {}
			}

			if (source != null) {
				try {
					source.close();
				} catch (Exception e) {}
			}
		}

		return total;
	}

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