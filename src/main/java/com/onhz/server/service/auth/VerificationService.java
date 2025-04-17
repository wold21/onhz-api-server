package com.onhz.server.service.auth;

import com.onhz.server.common.utils.CommonUtils;
import com.onhz.server.dto.request.EmailVerificationRequest;
import com.onhz.server.dto.response.common.CommonResponse;
import com.onhz.server.dto.response.common.NoticeResponse;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.UserRepository;
import com.onhz.server.service.common.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    public NoticeResponse sendVerification(EmailVerificationRequest emailVerificationRequest, HttpServletRequest request){
        String email = emailVerificationRequest.getEmail();
        String clientIp = CommonUtils.getClientIp(request);
        String code = CommonUtils.generateSecureCode();

        boolean existsByEmail = userRepository.existsByEmail(email);
        if (existsByEmail) {
            return NoticeResponse.of("이미 가입된 이메일입니다.");
        }

        emailService.saveVerificationCode(email, code, clientIp);

        emailService.sendVerificationEmail(email, code);
        return NoticeResponse.of("인증코드가 발송되었습니다, 인증 만료 시간은 5분입니다.");
    }

    public CommonResponse verifyEmail(EmailVerificationRequest emailVerificationRequest, HttpServletRequest request) {
        String email = emailVerificationRequest.getEmail();
        String code = emailVerificationRequest.getCode();
        String clientIp = CommonUtils.getClientIp(request);

        boolean isValid = emailService.verifyCode(email, code, clientIp);

        return CommonResponse.of(
                isValid ? "인증코드가 확인되었습니다." : "인증코드가 유효하지 않습니다.",
                isValid);
    }
}
