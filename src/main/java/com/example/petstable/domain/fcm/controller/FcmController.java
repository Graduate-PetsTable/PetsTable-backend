package com.example.petstable.domain.fcm.controller;

import com.example.petstable.domain.fcm.dto.request.BirthDayNotificationRequest;
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
public class FcmController implements FcmApi {

    private final FcmService fcmService;

    @PostMapping("/message")
    public PetsTableApiResponse<String> pushMessage(@RequestBody NotificationRequest request) {

        fcmService.sendMessage(request);

        return PetsTableApiResponse.createResponse("메시지 전송 성공", SUCCESS_SEND_MESSAGE);
    }

    @PostMapping("/birthday")
    public PetsTableApiResponse<String> pushBirthdayMessage(@RequestBody BirthDayNotificationRequest birthDayNotificationRequest) {

        fcmService.sendBirthdayMessage(birthDayNotificationRequest);

        return PetsTableApiResponse.createResponse("메시지 전송 성공", SUCCESS_SEND_MESSAGE);
    }
}
