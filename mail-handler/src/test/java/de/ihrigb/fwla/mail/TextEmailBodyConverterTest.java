package de.ihrigb.fwla.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TextEmailBodyConverterTest {

	private TextEmailBodyConverter testee;

	@Before
	public void setUp() throws Exception {
		testee = new TextEmailBodyConverter();
	}

	@Test
	public void testPlainTextMessageNullContent() throws Exception {
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(null);

		assertNull(testee.convert(mimeMessage));
	}

	@Test
	public void testPlainTextMessage() throws Exception {
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn("content");

		assertEquals("content", testee.convert(mimeMessage));
	}

	@Test
	public void testUnknownContentType() throws Exception {
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/*")).thenReturn(false);

		assertNull(testee.convert(mimeMessage));
	}

	@Test
	public void testEmptyMultipartAlternative() throws Exception {
		Multipart multipart = Mockito.mock(Multipart.class);
		Mockito.when(multipart.getCount()).thenReturn(0);

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

		assertNull(testee.convert(mimeMessage));
	}

	@Test
	public void testNonTextMultipartAlternative() throws Exception {
		BodyPart part = Mockito.mock(BodyPart.class);
		Mockito.when(part.isMimeType("text/plain")).thenReturn(false);
		Mockito.when(part.isMimeType("text/html")).thenReturn(false);
		Mockito.when(part.isMimeType("text/*")).thenReturn(false);
		Mockito.when(part.isMimeType("multipart/alternative")).thenReturn(false);
		Mockito.when(part.isMimeType("multipart/*")).thenReturn(false);

		Multipart multipart = Mockito.mock(Multipart.class);
		Mockito.when(multipart.getCount()).thenReturn(1);
		Mockito.when(multipart.getBodyPart(0)).thenReturn(part);

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

		assertNull(testee.convert(mimeMessage));
	}

	@Test
	public void testPlainTextMultipartAlternative() throws Exception {
		BodyPart part = Mockito.mock(BodyPart.class);
		Mockito.when(part.isMimeType("text/plain")).thenReturn(true);
		Mockito.when(part.isMimeType("text/*")).thenReturn(true);
		Mockito.when(part.getContent()).thenReturn("content");

		Multipart multipart = Mockito.mock(Multipart.class);
		Mockito.when(multipart.getCount()).thenReturn(1);
		Mockito.when(multipart.getBodyPart(0)).thenReturn(part);

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

		assertEquals("content", testee.convert(mimeMessage));
	}

	@Test
	public void testHtmlTextMultipartAlternative() throws Exception {
		BodyPart part = Mockito.mock(BodyPart.class);
		Mockito.when(part.isMimeType("text/plain")).thenReturn(false);
		Mockito.when(part.isMimeType("text/html")).thenReturn(true);
		Mockito.when(part.isMimeType("text/*")).thenReturn(true);
		Mockito.when(part.getContent()).thenReturn("content");

		Multipart multipart = Mockito.mock(Multipart.class);
		Mockito.when(multipart.getCount()).thenReturn(1);
		Mockito.when(multipart.getBodyPart(0)).thenReturn(part);

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

		assertEquals("content", testee.convert(mimeMessage));
	}

	@Test
	public void testEmptyMultipart() throws Exception {
		Multipart multipart = Mockito.mock(Multipart.class);
		Mockito.when(multipart.getCount()).thenReturn(0);

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/*")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

		assertNull(testee.convert(mimeMessage));
	}

	@Test
	public void testMultipartWithPlainPart() throws Exception {
		BodyPart part = Mockito.mock(BodyPart.class);
		Mockito.when(part.isMimeType("text/*")).thenReturn(true);
		Mockito.when(part.getContent()).thenReturn("content");

		Multipart multipart = Mockito.mock(Multipart.class);
		Mockito.when(multipart.getCount()).thenReturn(1);
		Mockito.when(multipart.getBodyPart(0)).thenReturn(part);

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.isMimeType("text/*")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/alternative")).thenReturn(false);
		Mockito.when(mimeMessage.isMimeType("multipart/*")).thenReturn(true);
		Mockito.when(mimeMessage.getContent()).thenReturn(multipart);

		assertEquals("content", testee.convert(mimeMessage));
	}
}
