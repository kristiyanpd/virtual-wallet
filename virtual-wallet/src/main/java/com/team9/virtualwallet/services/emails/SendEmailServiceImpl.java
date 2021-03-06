package com.team9.virtualwallet.services.emails;

import com.team9.virtualwallet.models.*;
import com.team9.virtualwallet.services.contracts.ConfirmationTokenService;
import com.team9.virtualwallet.services.contracts.InvitationTokenService;
import com.team9.virtualwallet.services.contracts.TransactionVerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.team9.virtualwallet.configs.ApplicationConstants.FREE_BONUS_AMOUNT;
import static com.team9.virtualwallet.configs.ApplicationConstants.LARGE_TRANSACTION_AMOUNT;

@Service
@PropertySource("classpath:messages.properties")
public class SendEmailServiceImpl implements SendEmailService {

    @Value("${mail.sender}")
    private String sender;

    private final JavaMailSender javaMailSender;
    private final String confirmationMailTemplate;
    private final String invitationMailTemplate;
    private final String transactionVerificationMailTemplate;
    private final ConfirmationTokenService confirmationTokenService;
    private final InvitationTokenService invitationTokenService;
    private final TransactionVerificationTokenService transactionVerificationTokenService;

    @Autowired
    public SendEmailServiceImpl(JavaMailSender javaMailSender, ConfirmationTokenService confirmationTokenService, InvitationTokenService invitationTokenService, TransactionVerificationTokenService transactionVerificationTokenService) throws IOException {
        this.javaMailSender = javaMailSender;
        this.confirmationTokenService = confirmationTokenService;
        this.invitationTokenService = invitationTokenService;
        this.transactionVerificationTokenService = transactionVerificationTokenService;
        this.confirmationMailTemplate = Files.readString(ResourceUtils.getFile("classpath:templates/email-templates/verify-email-template.html").toPath());
        this.invitationMailTemplate = Files.readString(ResourceUtils.getFile("classpath:templates/email-templates/invitation-email-template.html").toPath());
        this.transactionVerificationMailTemplate = Files.readString(ResourceUtils.getFile("classpath:templates/email-templates/transaction-verification-email-template.html").toPath());
    }

    @Override
    public void sendEmailConfirmation(User user, Optional<String> invitationTokenUUID) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        String time = timestamp.toString();
        ConfirmationToken confirmationToken = confirmationTokenService.create(user);
        String url = confirmationToken.getConfirmationToken();
        if (invitationTokenUUID.isPresent()) {
            url += String.format("&invitation-token=%s", invitationTokenUUID.get());
        }
        String html = String.format(confirmationMailTemplate, user.getFirstName(), user.getLastName(), url, time);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setSubject("Confirm your Registration!");
            mimeMessage.setContent(html, "text/html; charset=utf-8");
        };
        sendMail(messagePreparator);
    }

    @Override
    public void sendEmailInvitation(User invitingUser, String recipientEmail) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        String time = timestamp.toString();
        InvitationToken invitationToken = invitationTokenService.create(invitingUser, recipientEmail);
        String html = String.format(invitationMailTemplate, FREE_BONUS_AMOUNT, invitationToken.getInvitationToken(), time);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setSubject(String.format("A Friend Invited To Join Virtual Wallet - Get %s BGN Free Bonus!", FREE_BONUS_AMOUNT));
            mimeMessage.setContent(html, "text/html; charset=utf-8");
        };
        sendMail(messagePreparator);
    }

    @Override
    public void sendEmailTransactionVerification(Transaction transaction) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        String time = timestamp.toString();
        TransactionVerificationToken transactionVerificationToken = transactionVerificationTokenService.create(transaction);
        String html = String.format(transactionVerificationMailTemplate, LARGE_TRANSACTION_AMOUNT, transaction.getAmount(), transaction.getRecipient().getFirstName(), transaction.getRecipient().getLastName(), transactionVerificationToken.getVerificationToken(), time);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(transaction.getSender().getEmail()));
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setSubject("Large Transaction Verification - Virtual Wallet");
            mimeMessage.setContent(html, "text/html; charset=utf-8");
        };
        sendMail(messagePreparator);
    }

    @Async
    @Override
    public void sendMail(MimeMessagePreparator email) {
        javaMailSender.send(email);
    }

}
