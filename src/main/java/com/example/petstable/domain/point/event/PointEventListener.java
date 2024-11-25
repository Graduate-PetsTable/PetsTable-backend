package com.example.petstable.domain.point.event;

import java.util.Map;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.domain.point.entity.PointEntity;
import com.example.petstable.domain.point.entity.TransactionType;
import com.example.petstable.domain.point.repository.PointRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.petstable.domain.point.message.PointMessage.INVALID_TRANSACTION_TYPE;
import static com.example.petstable.domain.point.message.PointMessage.POINT_CREATE_ERROR;


@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PointEventListener implements StreamListener<String, MapRecord<String, String, String>> {
    private final PointRepository pointRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplateForCluster;

    @Override
    @Transactional
    public void onMessage(final MapRecord<String, String, String> message) {
        try {
            String recordId = message.getId().getValue();
            Map<String, String> map = message.getValue();
            Long userId = Long.valueOf(map.get("memberId"));
            TransactionType type = TransactionType.valueOf(map.get("type"));
            MemberEntity member = memberService.validateMember(userId);
            int point = Integer.parseInt(map.get("point"));
            String description = map.get("description");
            int beforePoint = member.getTotalPoint();
            switch (type) {
                case POINT_GAINED:
                    member.setTotalPoint(member.getTotalPoint() + point);
                    break;
                case POINT_USED:
                    member.setTotalPoint(member.getTotalPoint() - point);
                    break;
                default:
                    throw new PetsTableException(INVALID_TRANSACTION_TYPE.getStatus(), INVALID_TRANSACTION_TYPE.getMessage(), 401);
            }
            memberService.saveMember(member);
            pointRepository.save(PointEntity.createPointEntity(member, point, type, description));
            redisTemplateForCluster.opsForStream().acknowledge("recipePoint", "recipePointGroup", recordId);
            log.info("Redis onMessage[POINT]:{}:{}:BEFORE:{} => AFTER:{}", member.getId(), type.getDescription(), beforePoint, member.getTotalPoint());
        } catch (RedisSystemException e) {
            log.error("Redis Listener Error: ERROR: {}", e.getMessage());
            throw new PetsTableException(POINT_CREATE_ERROR.getStatus(), POINT_CREATE_ERROR.getMessage(), 400);
        } catch (Exception e) {
            log.error("General Listener Error: ERROR: {}", e.getMessage());
            throw new PetsTableException(POINT_CREATE_ERROR.getStatus(), POINT_CREATE_ERROR.getMessage(), 400);
        }
    }
}