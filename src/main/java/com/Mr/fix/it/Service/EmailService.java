package com.Mr.fix.it.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import com.Mr.fix.it.Util.EmailTemplate;

@Service
@RequiredArgsConstructor
public class EmailService
{
    private final JavaMailSender mailSender;

    public void sendVerificationOrResetPasswordEmail(
        String to, String subject,String header,String starting,
        String ending, String buttonText, String link,
        String token
    ) throws MessagingException
    {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(EmailTemplate.VerificationOrResetTemplate(header, starting, ending, buttonText, link, token),true);

        mailSender.send(message);
    }

    public void sendEmail(
        String to, String subject,String body
    ) throws MessagingException
    {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body,true);

        mailSender.send(message);
    }
}