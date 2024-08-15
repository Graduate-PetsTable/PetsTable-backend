package com.example.petstable.domain.pet.controller;

import com.example.petstable.domain.pet.dto.request.PetRegisterNewPetRequest;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.request.PetUpdateRequest;
import com.example.petstable.domain.pet.dto.response.*;
import com.example.petstable.domain.pet.message.PetMessage;
import com.example.petstable.domain.pet.service.PetService;
import com.example.petstable.global.auth.ios.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.petstable.domain.pet.message.PetMessage.*;

@Tag(name = "반려동물 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    @Operation(summary = "온보딩 ( 초기 반려동물 추가 )", description = "이름, 나이, 몸무게 정보 입력 받는 API")
    @PostMapping()
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<PetRegisterResponse> initAddPet(@RequestBody @Valid PetRegisterRequest pet, @LoginUserId Long memberId) {

        PetRegisterResponse response = petService.registerPet(memberId, pet);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "새로운 반려동물 추가", description = "새로운 반려동물 추가 API ( 모든 정보 입력 받음 )")
    @PostMapping("/pets/new")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<PetRegisterNewPetResponse> addPet(@RequestBody @Valid PetRegisterNewPetRequest pet, @LoginUserId Long memberId) {

        PetRegisterNewPetResponse response = petService.registerNewPet(memberId, pet);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "반려동물 정보 수정", description = "기존에 등록한 반려동물 정보 수정하는 API")
    @PatchMapping("/{petId}")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> updatePet(@RequestBody @Valid PetUpdateRequest pet, @PathVariable("petId") Long petId, @LoginUserId Long memberId) {

        petService.updatePet(petId, memberId, pet);

        return ResponseEntity.ok(PET_INFO_UPDATE_SUCCESS.getMessage());
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

    @Operation(summary = "반려동물 사진 등록")
    @PatchMapping(value = "/{petId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<PetImageResponse> addPetImage(@LoginUserId Long memberId, @PathVariable("petId") Long petId, @RequestPart("image") MultipartFile image) {

        PetImageResponse response = petService.registerPetImage(memberId, petId, image);

        return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_PET_IMAGE);
    }

    @Operation(summary = "반려동물 사진 삭제")
    @DeleteMapping(value = "/{petId}/image")
    @SecurityRequirement(name = "JWT")
    public PetsTableApiResponse<PetImageResponse> deletePetImage(@LoginUserId Long memberId, @PathVariable("petId") Long petId) {

        PetImageResponse response = petService.deletePetImage(memberId, petId);

        return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_PET_IMAGE);
    }
}
