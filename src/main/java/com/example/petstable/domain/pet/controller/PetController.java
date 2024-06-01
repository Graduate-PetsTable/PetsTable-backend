package com.example.petstable.domain.pet.controller;

import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterResponse;
import com.example.petstable.domain.pet.service.PetService;
import com.example.petstable.global.auth.ios.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.petstable.global.exception.message.PetMessage.*;

@Tag(name = "반려동물 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 추가")
    @PostMapping()
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<PetRegisterResponse> addPet(@RequestBody @Valid PetRegisterRequest pet, @LoginUserId Long memberId) {

        PetRegisterResponse response = petService.registerPet(memberId, pet);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "반려동물 상세 정보 조회")
    @GetMapping("/{petId}")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<PetInfoResponse> getPetInfo(@LoginUserId Long memberId, @PathVariable(name = "petId") Long petId) {

        PetInfoResponse response = petService.getMyPetInfo(memberId, petId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "반려동물 전체 조회")
    @GetMapping("/myPets")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<PetInfoResponse>> getAllPets(@LoginUserId Long memberId) {

        List<PetInfoResponse> response = petService.getAllMyPets(memberId);

        return ResponseEntity.ok(response);
    }

}
