package com.example.petstable.global.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor
public class RequestInfo {

    private StringBuffer requestURL;
    private String method;
    private String remoteAddr;
    private String header;
}
