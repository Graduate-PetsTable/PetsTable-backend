package com.example.petstable.domain.fcm.controller;

import com.example.petstable.domain.fcm.dto.request.BirthDayNotificationRequest;
import com.example.petstable.domain.fcm.dto.request.NotificationRequest;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FCM 관련 API")
public interface FcmApi {

    @Operation(summary = "알림 전송 API", description = "특정 회원에게 알림을 전송하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = PetsTableApiResponse.class),
                    examples = @ExampleObject(value = """
                    {
                        "message": "메시지 전송에 성공하였습니다.",
                        "status": 200
                    }
                    """)
            ),
                    description = "메시지 전송에 성공하였습니다."
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "FCM Token 이 존재하지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    PetsTableApiResponse<String> pushMessage (
            @RequestBody(description = "메시지 전송 데이터", required = true) NotificationRequest request
    );

    @Operation(summary = "생일 알림 전송 API", description = "반려동물 주인에게 생일 알림을 보내주는 API 입니다. ( 스케줄러를 사용하여 00시에 자동으로 전송됩니다 )", responses = {
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = PetsTableApiResponse.class),
                    examples = @ExampleObject(value = """
                    {
                        "message": "메시지 전송에 성공하였습니다.",
                        "status": 200
                    }
                    """)
            ),
                    description = "메시지 전송에 성공하였습니다."
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "FCM Token 이 존재하지 않습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
    })
    PetsTableApiResponse<String> pushBirthdayMessage(
            @RequestBody(description = "생일 알림 전송 데이터", required = true) BirthDayNotificationRequest birthDayNotificationRequest
    );
}
