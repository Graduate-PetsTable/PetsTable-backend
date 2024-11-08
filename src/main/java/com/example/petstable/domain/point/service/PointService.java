package com.example.petstable.domain.point.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.point.dto.response.PointMyBalanceResponse;
import com.example.petstable.domain.point.dto.response.PointResponse;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.entity.TransactionType;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.petstable.domain.point.message.PointMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    public List<PointResponse> getPointHistoryByMemberId(Long memberId) {
        List<PointEntity> pointEntities = pointRepository.findByMemberId(memberId);
        if (pointEntities.isEmpty()) {
            throw new PetsTableException(POINT_NOT_FOUND.getStatus(), POINT_NOT_FOUND.getMessage(), 404);
        }
        return pointEntities.stream()
                .map(pointEntity -> PointResponse.builder()
                        .point(pointEntity.getPoint())
                        .description(pointEntity.getDescription())
                        .transactionType(pointEntity.getTransactionType())
                        .createTime(pointEntity.getCreatedTime())
                        .build())
                .collect(Collectors.toList());
    }

    public PointMyBalanceResponse getPointBalance(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId).orElseThrow(
                () -> new PetsTableException(POINT_NOT_FOUND.getStatus(), POINT_NOT_FOUND.getMessage(), 404));

        return PointMyBalanceResponse.builder()
                .point(member.getTotalPoint())
                .build();
    }
}