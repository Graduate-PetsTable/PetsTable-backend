//package com.example.petstable.service;
//
//import com.example.petstable.global.support.SlackService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class SlackServiceTest {
//
//    @Autowired
//    private SlackService slackService;
//
////    @DisplayName("에러 발생 시 슬랙 알람 테스트")
////    @Test
////    void sendSlackError() {
////
////        Exception testException = new Exception("Test exception");
////        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
////
////        Mockito.when(mockRequest.getHeader("X-FORWARDED-FOR")).thenReturn(null);
////        Mockito.when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");
////        Mockito.when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
////        Mockito.when(mockRequest.getMethod()).thenReturn("GET");
////
////        slackService.sendSlackError(testException, mockRequest);
////    }
//}
