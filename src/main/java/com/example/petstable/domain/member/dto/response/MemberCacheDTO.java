package com.example.petstable.domain.member.dto.response;

import com.example.petstable.domain.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCacheDTO {
    private Long id;
    private int totalPoint;

    public MemberCacheDTO(MemberEntity member) {
        this.totalPoint = member.getTotalPoint();
        this.id = member.getId();
    }
}
