package de.ihrigb.fwla.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

@SuppressWarnings("unchecked")
public class ReceivingMessageHandlerTest {

	private ReceivingMessageHandler<String> testee;

	private EmailSenderFilter emailSenderFilter;
	private EmailHandler<String> emailHandler;

	@Before
	public void setUp() throws Exception {

		emailSenderFilter = Mockito.mock(EmailSenderFilter.class);
		emailHandler = Mockito.mock(EmailHandler.class);

		testee = new ReceivingMessageHandler<>(Optional.of(emailSenderFilter), __ -> "body", emailHandler);
	}

	@Test
	public void testHandleNoAddress() throws Exception {
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.getFrom()).thenReturn(null);

		Message<MimeMessage> message = Mockito.mock(Message.class);
		Mockito.when(message.getPayload()).thenReturn(mimeMessage);

		testee.handleMessage(message);

		Mockito.verifyNoInteractions(emailHandler);
	}

	@Test
	public void testHandleSenderRejected() throws Exception {
		InternetAddress address = Mockito.mock(InternetAddress.class);
		Mockito.when(address.getAddress()).thenReturn("sender");

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.getFrom()).thenReturn(new Address[] { address });

		Message<MimeMessage> message = Mockito.mock(Message.class);
		Mockito.when(message.getPayload()).thenReturn(mimeMessage);

		Mockito.when(emailSenderFilter.filter("sender")).thenReturn(FilterResult.REJECTED);

		testee.handleMessage(message);

		Mockito.verifyNoInteractions(emailHandler);
	}

	@Test
	public void testHandleSenderAccepted() throws Exception {
		InternetAddress address = Mockito.mock(InternetAddress.class);
		Mockito.when(address.getAddress()).thenReturn("sender");

		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
		Mockito.when(mimeMessage.getFrom()).thenReturn(new Address[] { address });
		Mockito.when(mimeMessage.getSubject()).thenReturn("subject");

		MessageHeaders messageHeaders = Mockito.mock(MessageHeaders.class);
		Mockito.when(messageHeaders.getTimestamp()).thenReturn(System.currentTimeMillis());

		Message<MimeMessage> message = Mockito.mock(Message.class);
		Mockito.when(message.getPayload()).thenReturn(mimeMessage);
		Mockito.when(message.getHeaders()).thenReturn(messageHeaders);

		Mockito.when(emailSenderFilter.filter("sender")).thenReturn(FilterResult.ACCEPTED);

		testee.handleMessage(message);

		ArgumentCaptor<Email<String>> argumentCaptor = ArgumentCaptor.forClass(Email.class);
		Mockito.verify(emailHandler, Mockito.times(1)).handle(argumentCaptor.capture());

		Email<String> email = argumentCaptor.getValue();
		assertEquals("subject", email.getSubject());
		assertEquals("body", email.getBody());
		assertEquals("sender", email.getSender());
		assertNotNull(email.getTimestamp());
	}
}
