package de.ihrigb.fwla.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Searches for a 'text/*' part in the email and returns it. Prefers html over plain.
 */
@Slf4j
public class TextEmailBodyConverter implements EmailBodyConverter<String> {

	@Override
	public String convert(MimeMessage mimeMessage) {
		try {
			return getText(mimeMessage);
		} catch (MessagingException | IOException e) {
			log.error("Convertion failed.", e);
			throw new EmailBodyConvertionException("Convertion failed.", e);
		}
	}

	private String getText(Part p) throws javax.mail.MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			Object content = p.getContent();
			return content == null ? null : content.toString();
		}

		if (p.isMimeType("multipart/alternative")) {
			// Prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null) {
						text = getText(bp);
					}
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null) {
						return s;
					}
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null) {
					return s;
				}
			}
		}

		return null;
	}
}
