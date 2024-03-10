package site.termterm.api.domain.inquiry.aop;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import site.termterm.api.global.handler.exceptions.CustomApiException;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MailSendUtil {
    private final ResourceLoader resourceLoader;
    private final JavaMailSender mailSender;

    @Async
    public void sendAcceptMail(String email){
        try {
            Resource resource = resourceLoader.getResource("classpath:/mail/index.html");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // get inputStream object
            InputStream inputStream = resource.getInputStream();
            // convert inputStream into a byte array
            byte[] dataAsBytes = FileCopyUtils.copyToByteArray(inputStream);

            // convert the byte array into a String
            String html = new String(dataAsBytes, StandardCharsets.UTF_8);

            helper.setText(html, true);
            helper.setFrom("termterm.contact@gmail.com");
            helper.setTo(email);
            helper.setSubject("[텀텀] 고객님의 문의가 정상적으로 접수되었습니다.");

            mailSender.send(mimeMessage);
        }catch(Exception e){
            throw new CustomApiException("메일 전송에 실패하였습니다.", email);
        }
    }
}
