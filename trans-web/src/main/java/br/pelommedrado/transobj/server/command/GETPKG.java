/**
 * 
 */
package br.pelommedrado.transobj.server.command;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.mina.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.util.ParseGetpkgCommand;

/**
 * @author Andre Leite
 */
public class GETPKG extends AbstractCommand {

	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(GETPKG.class);

	@Override
	public void execute(FtpIoSession session, FtpServerContext context,
			FtpRequest request) throws IOException, FtpException {

		final ParseGetpkgCommand parse = new ParseGetpkgCommand(request.getArgument());
		final String stgFile = parse.getArquivo();

		// argument check
		if (stgFile == null) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, stgFile));
			return;
		}

		// get file object
		FtpFile file = null;
		try {
			file = session.getFileSystemView().getFile(stgFile);

		} catch (Exception ex) {
			logger.debug("Exception getting file object", ex);
		}

		if (file == null) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, stgFile));

			return;
		}

		// check file existance
		if (!file.doesExist()) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, stgFile));

			return;
		}

		// check valid file
		if (!file.isFile()) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, stgFile));
			return;
		}

		// check permission
		if (!file.isReadable()) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, stgFile));

			return;
		}

		final long off = parse.getOff();
		final byte[] buffer = new byte[parse.getLen()];

		InputStream in = file.createInputStream(off);
		in.read(buffer);
		in.close();

		byte[] base64 = Base64.encodeBase64(buffer);  
		session.write(new DefaultFtpReply(FtpReply.REPLY_200_COMMAND_OKAY, new String(base64)));
	}
}