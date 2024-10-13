package com.example.petstable.domain.point.service;

import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.petstable.domain.member.message.MemberMessage.*;
import static com.example.petstable.domain.point.message.PointMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    public PointResponse getPointByMemberId(Long memberId) {
        PointEntity pointEntity = pointRepository.findByMemberId(memberId).orElseThrow(
                () -> new PetsTableException(POINT_NOT_FOUND.getStatus(), POINT_NOT_FOUND.getMessage(), 404));

        return PointResponse.builder()
                .point(pointEntity.getPoint())
                .build();
    }

    @Transactional
    public void increasePoints(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
                () -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        PointEntity pointEntity = pointRepository.findByMemberId(memberId).orElseThrow(
                () -> new PetsTableException(POINT_NOT_FOUND.getStatus(), POINT_NOT_FOUND.getMessage(), 404));

        pointEntity.increasePoint();
    }
}
