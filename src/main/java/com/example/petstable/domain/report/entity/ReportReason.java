package com.example.petstable.domain.report.entity;

import com.example.petstable.domain.report.message.ReportMessage;
import com.example.petstable.global.exception.PetsTableException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

import static com.example.petstable.domain.report.message.ReportMessage.*;

@AllArgsConstructor @NoArgsConstructor
@Getter
public enum ReportReason {

    FISHING_HARASSMENT_SPAM("스팸"),
    LEAKING_FRAUD("정보유출"),
    PORNOGRAPHY("성적인 내용"),
    INAPPROPRIATE_CONTENT("부적절한 컨텐츠"),
    INSULT("모욕"),
    COMMERCIAL_AD("광고"),
    POLITICAL_CONTENT("정치적 발언");

    private String value;

    public static ReportReason from(String value) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.value, value))
                .findFirst().orElseThrow(() -> new PetsTableException(INVALID_REASON.getStatus(), INVALID_REASON.getMessage(), 400));
    }
}