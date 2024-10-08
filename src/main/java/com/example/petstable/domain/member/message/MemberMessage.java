package com.example.petstable.domain.member.message;

import com.example.petstable.global.exception.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberMessage implements ResponseMessage {

    MEMBER_NOT_FOUND("해당 유저는 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_MEMBER("해당 유저는 이미 존재합니다.", HttpStatus.CONFLICT),
    INVALID_EMAIL("해당 이메일은 이미 존재합니다.", HttpStatus.CONFLICT),
    INVALID_NICKNAME("해당 닉네임은 이미 존재합니다.", HttpStatus.CONFLICT),
    INACTIVE_MEMBER("해당 유저는 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_SOCIAL("소셜 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    SUCCESS_REGISTER_PROFILE_IMAGE("프로필 이미지 등록 성공", HttpStatus.OK),
    SUCCESS_DELETE_PROFILE_IMAGE("프로필 이미지 삭제 성공", HttpStatus.OK),
    SUCCESS_TOKEN("토큰 재발급 완료.", HttpStatus.OK),
    LOGIN_SUCCESS("로그인 성공", HttpStatus.OK),
    SUCCESS_LOGOUT("로그아웃 성공", HttpStatus.OK),
    SUCCESS_WITHDRAW("회원탈퇴 성공", HttpStatus.OK),
    FAILED_APPLE_REVOKE("애플유저 회원탈퇴 실패", HttpStatus.BAD_REQUEST),
    FAILED_GOOGLE_REVOKE("구글유저 회원탈퇴 실패", HttpStatus.BAD_REQUEST),
    JOIN_SUCCESS("회원가입 성공", HttpStatus.OK);

    private final String message;
    private final HttpStatus status;
}
