package com.example.petstable.domain.fcm.service;

import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayNotificationScheduler {

    private final PetRepository petRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 밤 00 시에 실행
    public void sendBirthDayNotifications() {

        LocalDate today = LocalDate.now();
        List<PetEntity> birthdayPets = petRepository.findByBirth(today);

        for (PetEntity pet : birthdayPets) {
            fcmService.sendBirthdayMessage(pet.getName(), pet.increaseAge(), pet.getMember().getId());
        }
    }
}
