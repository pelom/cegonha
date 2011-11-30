package br.pelommedrado.transobj;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Cliente {

	public static void main(String[] args) throws SocketException, IOException {

	}

	private static void listar() throws SocketException, IOException {
		final FTPClient client = new FTPClient( );

		//connect to the FTP server as anonymous
		client.connect( "localhost", 2121 );
		client.login( "anonymous", "" );

		String remoteDir = "/";

		// List the contents of the remote directory
		FTPFile[] remoteFiles = client.listFiles( remoteDir );

		System.out.println( "Files in " + remoteDir );

		for (int i = 0; i < remoteFiles.length; i++) {
			String name = remoteFiles[i].getName( );
			long length = remoteFiles[i].getSize( );

			String readableLength = FileUtils.byteCountToDisplaySize( length );

			System.out.println( name + ":\t\t" + readableLength );
		}

		client.disconnect( );
	}

	private static void download() {
		final FTPClient client = new FTPClient( );

		OutputStream outStream = null;

		try {

			//connect to the FTP server as anonymous
			client.connect( "localhost", 2121 );
			client.login( "anonymous", "" );

			String remoteFile = "Maid with the Flaxen Hair.20111129125735.mp3";

			//write the contents of the remote file to a FileOutputStream
			outStream = new FileOutputStream( "/home/pelom/Área de trabalho/Maid with the Flaxen Hair.20111129125735.mp3" );

			if(client.retrieveFile( remoteFile, outStream )) {

			} else {

			}

		} catch(IOException ioe) {
			System.out.println( "Error communicating with FTP server." );

		} finally {

			IOUtils.closeQuietly( outStream );

			try {
				client.disconnect();

			} catch (IOException e) {
				System.out.println( "Problem disconnecting from FTP server" );
			}
		}
	}
}