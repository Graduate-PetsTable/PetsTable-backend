package com.example.petstable.domain.fcm;

import com.example.petstable.global.exception.PetsTableException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.petstable.domain.fcm.message.FcmMessage.*;


// fcm 초기화
@Component
@Slf4j
public class FcmInitializer {

    @Value("${fcm.firebase_config_path}")
    private String firebaseConfigPath;

    @PostConstruct // 빈 객체가 생성되고 의존성 주입이 완료된 후에 초기화가 실행될 수 있도록 @PostConstruct 설정
    public void initialize() {

        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initializer");
            }
        } catch (IOException e) {
            throw new PetsTableException(FCM_INIT_EXCEPTION.getStatus(), FCM_INIT_EXCEPTION.getMessage(), 400);
        }
    }
}
