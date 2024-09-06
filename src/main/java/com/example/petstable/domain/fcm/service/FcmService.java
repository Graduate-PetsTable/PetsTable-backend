package com.example.petstable.domain.fcm.service;

import com.example.petstable.domain.fcm.dto.request.BirthDayNotificationRequest;
import com.example.petstable.domain.fcm.dto.request.NotificationRequest;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.exception.PetsTableException;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.petstable.domain.fcm.message.FcmMessage.FAIL_SEND_MESSAGE;
import static com.example.petstable.domain.fcm.message.FcmMessage.NOT_FOUND_FCM_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final MemberRepository memberRepository;

    public void sendMessage(NotificationRequest request) {

        // 사용자의 fcm 토큰 값을 조회
        String fcmToken = validateFcmToken(request.getMemberId());

        // 메시지 생성
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .setToken(fcmToken)
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setNotification(
                                        AndroidNotification.builder()
                                                .setTitle(request.getTitle())
                                                .setBody(request.getBody())
                                                .setClickAction("") // 이건 프론트와 상의 후 결정
                                                .build()
                                )
                                .build()
                )
                .setApnsConfig(
                        ApnsConfig.builder()
                                .setAps(Aps.builder()
                                        .setAlert(
                                                ApsAlert.builder()
                                                        .setTitle(request.getTitle())
                                                        .setBody(request.getBody())
                                                        .build()
                                        )
                                        .build())
                                .build()
                )
                .build();

        // 메세지 전송
        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("success send message");
        } catch (FirebaseMessagingException e) {
            throw new PetsTableException(FAIL_SEND_MESSAGE.getStatus(), FAIL_SEND_MESSAGE.getMessage(), 400);
        }
    }

    public void sendBirthdayMessage(BirthDayNotificationRequest birthDayNotificationRequest) {
        String title = birthDayNotificationRequest.getName() + "의 생일을 축하합니다!";
        String body = "오늘은 " + birthDayNotificationRequest.getName() + "의 " + birthDayNotificationRequest.getAge() + "번째 생일입니다.\n" + "맛있는 간식을 만들어주세요~!";
        NotificationRequest request = NotificationRequest.builder()
                .memberId(birthDayNotificationRequest.getMemberId())
                .title(title)
                .body(body)
                .build();

        sendMessage(request);
    }

    public String validateFcmToken(Long memberId) {
        return memberRepository.findFcmTokenById(memberId)
                .orElseThrow(() -> new PetsTableException(NOT_FOUND_FCM_TOKEN.getStatus(), NOT_FOUND_FCM_TOKEN.getMessage(), 404));
    }
}
