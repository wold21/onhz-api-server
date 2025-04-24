package com.onhz.server.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final RedisTemplate<String, String> redisTemplate;
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

    public void sendVerificationEmail(String to, String code) {
        String subject = "[OnHz] 이메일 인증 코드";
        String htmlBody = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<h2>이메일 인증 코드</h2>" +
                        "<p>환영합니다!</p>" +
                        "<p>아래의 인증 코드를 입력하여 이메일을 인증해주세요:</p>" +
                        "<div style='background-color: #f4f4f4; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                        "<h3 style='text-align: center; margin: 0;'>%s</h3>" +
                        "</div>" +
                        "<p>감사합니다.</p>" +
                        "</div>",
                code);

        String textBody = String.format(
                "이메일 인증 코드입니다.\n" +
                        "인증 코드: %s\n" +
                        "아래의 인증 코드를 입력하여 이메일을 인증해주세요. \n" +
                        "인증코드 만료 시간은 5분입니다.",
                code);

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

        List<MessageTag> tags = new ArrayList<>();
        tags.add(MessageTag.builder().name("X-Priority").value("1").build());
        tags.add(MessageTag.builder().name("X-MSMail-Priority").value("High").build());
        tags.add(MessageTag.builder().name("Importance").value("High").build());

        SendEmailRequest request = SendEmailRequest.builder()
                .source(senderEmail)
                .destination(destination)
                .message(message)
                .returnPath(senderEmail)
                .replyToAddresses(senderEmail)
                .tags(tags)
                .build();

        try {
            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("Email sent successfully. Message ID: {}", response.messageId());
        } catch (SesException e) {
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public void saveVerificationCode(String email, String code, String ip) {
        try{
            log.info("이메일 인증 코드 저장: email={}, code={}, ip={}", email, code, ip);
            String key = "email:verification:" + email;
            String value = code + "@" + ip;
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
            log.info("이메일 인증 코드 저장 완료: key={}, value={}", key, value);
        } catch(Exception e){
            log.error("이메일 인증 코드 저장 실패: email={}, code={}, ip={}", email, code, ip, e);
            throw new RuntimeException("인증코드 발행 실패: " + e.getMessage(), e);
        }
    }

    public boolean verifyCode(String email, String code, String ip) {
        String key = "email:verification:" + email;
        String storedValue = redisTemplate.opsForValue().get(key);

        if (storedValue == null) {
            throw new RuntimeException("인증코드가 만료되었습니다.");
        }

        String[] parts = storedValue.split("@");
        String storedCode = parts[0];
        String storedIp = parts[1];

        boolean isValid = storedCode.equals(code) && storedIp.equals(ip);

        if (isValid) {
            // 검증 성공 시 코드 삭제
            redisTemplate.delete(key);
        }

        return isValid;
    }
}
