package com.example.petstable.domain.pet.repository;

import com.example.petstable.domain.pet.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    boolean existsByName(String name);
    Optional<PetEntity> findByName(String name);
}
