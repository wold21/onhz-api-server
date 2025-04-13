package com.onhz.server.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final SesClient sesClient;
    @Value("${aws.ses.from-email}")
    private String senderEmail;

    public void sendPasswordResetEmail(String to, String userName, String newPassword){
        String subject = "[OnHz] 임시 비밀번호 안내";
        String htmlBody = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<h2>임시 비밀번호가 발급되었습니다.</h2>" +
                        "<p>%s님, 안녕하세요.</p>" +
                        "<p>요청하신 임시 비밀번호가 생성되었습니다:</p>" +
                        "<div style='background-color: #f4f4f4; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                        "<h3 style='text-align: center; margin: 0;'>%s</h3>" +
                        "</div>" +
                        "<p>보안을 위해 로그인 후 즉시 비밀번호를 변경해주세요.</p>" +
                        "<p>본인이 요청하지 않았다면, 계정 보안을 위해 즉시 비밀번호를 변경하시기 바랍니다.</p>" +
                        "<p>감사합니다.</p>" +
                        "</div>",
                userName, newPassword);

        String textBody = String.format(
                "%s님, 임시 비밀번호가 발급되었습니다.\n" +
                        "임시 비밀번호: %s\n" +
                        "보안을 위해 로그인 후 즉시 비밀번호를 변경해주세요.\n" +
                        "본인이 요청하지 않았다면, 계정 보안을 위해 즉시 비밀번호를 변경하시기 바랍니다.",
                userName, newPassword);

        sendEmail(to, subject, htmlBody, textBody);
    }

    private void sendEmail(String to, String subject, String htmlBody, String textBody) {
        Destination destination = Destination.builder()
                .toAddresses(to)
                .build();

        Content subjectContent = Content.builder()
                .data(subject)
                .charset("UTF-8")
                .build();

        Body body = Body.builder()
                .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                .text(Content.builder().data(textBody).charset("UTF-8").build())
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(body)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .source(senderEmail)
                .destination(destination)
                .message(message)
                .build();

        try {
            sesClient.sendEmail(request);
        } catch (SesException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
