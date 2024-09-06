package com.example.petstable.domain.pet.controller;

import com.example.petstable.domain.pet.dto.request.PetRegisterNewPetRequest;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.request.PetUpdateRequest;
import com.example.petstable.domain.pet.dto.response.*;
import com.example.petstable.domain.pet.service.PetService;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.petstable.domain.pet.message.PetMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController implements PetApi {

    private final PetService petService;

    @PostMapping()
    public ResponseEntity<PetRegisterResponse> initAddPet(@RequestBody @Valid PetRegisterRequest pet, @LoginUserId Long memberId) {

        PetRegisterResponse response = petService.registerPet(memberId, pet);

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<List<PetInfoResponse>> getAllPets(@LoginUserId Long memberId) {

        List<PetInfoResponse> response = petService.getAllMyPets(memberId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/new")
    public ResponseEntity<PetRegisterNewPetResponse> addPet(@RequestBody @Valid PetRegisterNewPetRequest pet, @LoginUserId Long memberId) {

        PetRegisterNewPetResponse response = petService.registerNewPet(memberId, pet);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<String> updatePet(@RequestBody @Valid PetUpdateRequest pet, @PathVariable("petId") Long petId, @LoginUserId Long memberId) {

        petService.updatePet(petId, memberId, pet);

        return ResponseEntity.ok(PET_INFO_UPDATE_SUCCESS.getMessage());
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetInfoResponse> getPetInfo(@LoginUserId Long memberId, @PathVariable(name = "petId") Long petId) {

        PetInfoResponse response = petService.getMyPetInfo(memberId, petId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{petId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PetsTableApiResponse<PetImageResponse> addPetImage(@LoginUserId Long memberId, @PathVariable("petId") Long petId, @RequestPart("image") MultipartFile image) {

        PetImageResponse response = petService.registerPetImage(memberId, petId, image);

        return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_PET_IMAGE);
    }

    @DeleteMapping(value = "/{petId}/image")
    public PetsTableApiResponse<PetImageResponse> deletePetImage(@LoginUserId Long memberId, @PathVariable("petId") Long petId) {

        PetImageResponse response = petService.deletePetImage(memberId, petId);

        return PetsTableApiResponse.createResponse(response, SUCCESS_REGISTER_PET_IMAGE);
    }
}
