package com.example.petstable.domain.point.controller;

import com.example.petstable.domain.point.dto.response.PointMyBalanceResponse;
import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.domain.point.service.PointService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.petstable.domain.point.message.PointMessage.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController implements PointApi {

    private final PointService pointService;

    @GetMapping
    public PetsTableApiResponse<PointMyBalanceResponse> getMyPoint(@LoginUserId Long memberId) {
        PointMyBalanceResponse currentPoint = pointService.getPointBalance(memberId);
        return PetsTableApiResponse.createResponse(currentPoint, SUCCESS_GET_POINT);
    }

    @GetMapping("/history")
    public PetsTableApiResponse<List<PointResponse>> getMyPointHistory(@LoginUserId Long memberId) {
        List<PointResponse> response = pointService.getPointHistoryByMemberId(memberId);
        return PetsTableApiResponse.createResponse(response, SUCCESS_GET_POINT);
    }
}
