package br.pelommedrado.transobj.server.command;

import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.pelommedrado.trans.util.FileUtils;
import br.pelommedrado.trans.util.ParseChecksumCommand;

/**
 * @author Andre Leite
 */
public class CHKSUM extends AbstractCommand {

	/** Objeto de saida de mensagens no console. */
	private Logger logger = LoggerFactory.getLogger(CHKSUM.class);

	/**
	 * Construtor da classe
	 */
	public CHKSUM() {
		super();
	}

	@Override
	public void execute(FtpIoSession session, FtpServerContext context,
			FtpRequest request) throws IOException, FtpException {

		//recuperar argumentos
		ParseChecksumCommand pChk = new ParseChecksumCommand(request.getArgument());

		// argument check
		if (pChk.getArquivo() == null) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, pChk.getArquivo()));
			return;
		}

		// get file object
		FtpFile file = null;
		try {
			file = session.getFileSystemView().getFile(pChk.getArquivo());

		} catch (Exception ex) {
			logger.debug("Exception getting file object", ex);
		}

		if (file == null) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, pChk.getArquivo()));

			return;
		}

		// check file existance
		if (!file.doesExist()) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, pChk.getArquivo()));

			return;
		}

		// check valid file
		if (!file.isFile()) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, pChk.getArquivo()));
			return;
		}

		// check permission
		if (!file.isReadable()) {
			session.write(new DefaultFtpReply(
					FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, pChk.getArquivo()));

			return;
		}

		long checksum = -1;

		//checksum bloco
		if(pChk.getTipoVericacao() == ParseChecksumCommand.CHECKSUM_BLOCO) {
			final long off = pChk.getOff();
			final byte[] buffer = new byte[pChk.getLen()];

			CheckedInputStream cis = null;

			try {
				cis = new CheckedInputStream(file.createInputStream(off), new CRC32());
				cis.read(buffer);

				checksum = cis.getChecksum().getValue();

			} catch (Exception e) {
				logger.debug("Exception getting file object", e);

				session.write(new DefaultFtpReply(
						FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, request.getArgument()));

				return;

			} finally {
				if(cis != null) {
					cis.close();
				}

			}	

			//checksum arquivo
		} else if(pChk.getTipoVericacao() == ParseChecksumCommand.CHECKSUM_ARQUIVO) {
			try {
				checksum = FileUtils.gerarChecksum(new File(file.getAbsolutePath()));

			} catch (Exception e) {
				logger.debug("Exception getting file object", e);

				session.write(new DefaultFtpReply(
						FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, request.getArgument()));

				return;
			}
		}

		session.write(new DefaultFtpReply(FtpReply.REPLY_200_COMMAND_OKAY, String.valueOf(checksum)));
	}
}