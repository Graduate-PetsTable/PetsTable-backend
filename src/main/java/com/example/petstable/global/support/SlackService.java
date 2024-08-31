package com.example.petstable.global.support;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Component
public class SlackService {

    private final Slack slackClient = Slack.getInstance();

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Async
    public void sendSlackError(Exception e, RequestInfo request) {
        try {
            slackClient.send(webhookUrl, payload(p -> p
                    .text("서버 에러 발생") // 메시지 제목
                    .attachments(
                            List.of(generateSlackAttachment(e, request)) // 메시지 본문 내용
                    )
            ));
        } catch (IOException slackError) {
            log.debug("Slack과 통신 중 에러 발생");
        }
    }

    private Attachment generateSlackAttachment(Exception e, RequestInfo request) {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        String xffHeader = request.getHeader();
        return Attachment.builder()
                .color("FCCC00") // 색상
                .title(requestTime + " 발생 에러 로그") // 메시지 본문 내용
                .fields(List.of(
                                generateSlackField("Request IP", xffHeader == null ? request.getRemoteAddr() + "\n" : xffHeader + "\n"),
                                generateSlackField("Request URL", request.getRequestURL() + " " + request.getMethod() + "\n"),
                                generateSlackField("Error Message", e.getMessage())
                        )
                )
                .build();
    }

    // slack field 생성
    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }
}
