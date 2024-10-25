package com.example.petstable.domain.point.service;

import com.example.petstable.domain.member.entity.MemberEntity;
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

    public List<PointResponse> getPointHistoryByMemberId(Long memberId) {
        List<PointEntity> pointEntities = pointRepository.findByMemberId(memberId);
        if (pointEntities.isEmpty()) {
            throw new PetsTableException(POINT_NOT_FOUND.getStatus(), POINT_NOT_FOUND.getMessage(), 404);
        }
        return pointEntities.stream()
                .map(pointEntity -> PointResponse.builder()
                        .point(pointEntity.getPoint())
                        .description(pointEntity.getDescription())
                        .transactionType(pointEntity.getTransactionType().getDescription())
                        .createTime(pointEntity.getCreatedTime())
                        .build())
                .collect(Collectors.toList());
    }

    public PointMyBalanceResponse getPointBalance(Long memberId) {
        PointEntity point = pointRepository.findFirstByMemberIdOrderByCreatedTimeDesc(memberId);
        int balance = (point != null) ? point.getBalance() : 0;
        return PointMyBalanceResponse.builder()
                .point(balance)
                .build();
    }

    @Transactional
    public void increasePoints(MemberEntity member, int point, String description) {
        int currentBalance = getPointBalance(member.getId()).getPoint();
        int newBalance = currentBalance + point;

        PointEntity pointEntity = PointEntity.createPointEntity(
                member,
                newBalance,
                point,
                TransactionType.POINT_GAINED,
                description
        );
        pointRepository.save(pointEntity);
    }

    @Transactional
    public void subtractPoints(MemberEntity member, int points, String description) {
        int currentBalance = getPointBalance(member.getId()).getPoint();

        if (currentBalance < points) {
            throw new PetsTableException(INSUFFICIENT_BALANCE.getStatus(), INSUFFICIENT_BALANCE.getMessage(), 400);
        }

        int newBalance = currentBalance - points;

        PointEntity pointEntity = PointEntity.createPointEntity(
                member,
                newBalance,
                -points,
                TransactionType.POINT_USED,
                description
        );

        member.addPoints(pointEntity);
        pointRepository.save(pointEntity); // 포인트 차감 내역 저장
    }
}