package com.example.petstable.apple;

import com.example.petstable.global.auth.apple.ClientSecretGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ClientSecretGeneratorTest {

    @Autowired
    private ClientSecretGenerator clientSecretGenerator;

    @Test
    @DisplayName("Client Secret 생성 성공")
    void createClientSecret() throws Exception {
        String clientSecret = clientSecretGenerator.createClientSecret();

        assertThat(clientSecret).isNotNull();
        assertThat(clientSecret).contains("ey");
        assertThat(clientSecret.split("\\.")).hasSize(3);
    }
}