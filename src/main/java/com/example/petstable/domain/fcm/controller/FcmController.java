package com.example.petstable.domain.fcm.controller;

import com.example.petstable.domain.fcm.dto.request.NotificationRequest;
import com.example.petstable.domain.fcm.service.FcmService;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.petstable.domain.fcm.message.FcmMessage.*;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @Operation(summary = "알림 전송", description = "원하는 내용 전송")
    @PostMapping("/message")
    public PetsTableApiResponse<String> pushMessage(@RequestBody NotificationRequest request, Long memberId) {

        fcmService.sendMessage(request, memberId);

        return PetsTableApiResponse.createResponse("메시지 전송 성공", SUCCESS_SEND_MESSAGE);
    }

    @Operation(summary = "생일 알림 전송", description = "생일 메세지 전송. !! 실제로 생일 알림은 스케줄러 사용해서 00시에 자동으로 전송됨!!")
    @PostMapping("/birthday")
    public PetsTableApiResponse<String> pushBirthdayMessage(String petName, int age, Long memberId) {

        fcmService.sendBirthdayMessage(petName, age, memberId);

        return PetsTableApiResponse.createResponse("메시지 전송 성공", SUCCESS_SEND_MESSAGE);
    }
}
