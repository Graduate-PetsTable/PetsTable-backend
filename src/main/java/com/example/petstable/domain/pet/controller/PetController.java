package com.example.petstable.domain.pet.controller;

import com.example.petstable.domain.pet.dto.request.PetInfoRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "반려동물 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 추가")
    @PostMapping()
    public ResponseEntity<PetInfoResponse> addPet(@RequestBody PetInfoRequest pet, Long memberId) {
        PetInfoResponse petInfoResponse = petService.registerPet(memberId, pet);
        return ResponseEntity.ok(petInfoResponse);
    }

}
