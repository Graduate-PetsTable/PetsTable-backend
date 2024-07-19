package com.example.petstable.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileImageResponse {

    private Long id;
    private String imageUrl;
}
