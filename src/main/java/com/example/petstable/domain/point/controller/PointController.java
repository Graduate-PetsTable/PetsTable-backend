package com.example.petstable.domain.point.controller;

import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.domain.point.service.PointService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.petstable.domain.point.message.PointMessage.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController implements PointApi {

    private final PointService pointService;

    @GetMapping
    public PetsTableApiResponse<PointResponse> getMyPoint(@LoginUserId Long memberId) {
        PointResponse currentPoint = pointService.getPointBalance(memberId);
        return PetsTableApiResponse.createResponse(currentPoint, SUCCESS_GET_POINT);
    }

    @GetMapping("/history")
    public PetsTableApiResponse<PointResponse> getMyPointHistory(@LoginUserId Long memberId) {
        PointResponse response = pointService.getPointHistoryByMemberId(memberId);
        return PetsTableApiResponse.createResponse(response, SUCCESS_GET_POINT);
    }
}
