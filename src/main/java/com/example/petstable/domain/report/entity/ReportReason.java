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

    FISHING_HARASSMENT_SPAM("fishing_harassment_spam"),
    LEAKING_FRAUD("leaking_fraud"),
    PORNOGRAPHY("pornography"),
    INAPPROPRIATE_CONTENT("inappropriate_content"),
    INSULT("insult"),
    COMMERCIAL_AD("commercial_ad"),
    POLITICAL_CONTENT("political_content");

    private String value;

    public static ReportReason from(String value) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.value, value))
                .findFirst().orElseThrow(() -> new PetsTableException(INVALID_REASON.getStatus(), INVALID_REASON.getMessage(), 400));
    }
}