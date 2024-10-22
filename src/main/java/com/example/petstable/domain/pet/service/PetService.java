package com.example.petstable.domain.pet.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.pet.dto.request.PetRegisterNewPetRequest;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.request.PetUpdateRequest;
import com.example.petstable.domain.pet.dto.response.*;
import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.pet.repository.PetRepository;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.support.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import static com.example.petstable.domain.member.message.MemberMessage.*;
import static com.example.petstable.domain.pet.message.PetMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    public PetRegisterResponse registerPet(Long memberId, PetRegisterRequest petRegisterRequest) {
        MemberEntity memberEntity = validateMember(memberId);

        PetEntity petEntity = PetEntity.createPet(petRegisterRequest);

        memberEntity.addPets(petEntity);
        petEntity.setMember(memberEntity);

        petRepository.save(petEntity);

        return PetRegisterResponse.builder()
                .id(petEntity.getId())
                .name(petEntity.getName())
                .weight(petEntity.getWeight())
                .age(petEntity.getAge())
                .build();
    }

    public MemberEntity validateMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
    }

    public PetEntity validatePet(Long id, Long memberId) {
        return petRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new PetsTableException(PET_NOT_FOUND.getStatus(), PETS_NOT_FOUND.getMessage(), 404));
    }

    public boolean duplicatePet(Long memberId, String name, String kind) {
        return petRepository.existsByMemberIdAndNameAndKind(memberId, name, kind);
    }

    @Transactional
    public PetRegisterNewPetResponse registerNewPet(Long memberId, PetRegisterNewPetRequest request) {

        MemberEntity member = validateMember(memberId);

        if (duplicatePet(member.getId(), request.getName(), request.getKind())) {
            throw new PetsTableException(ALREADY_EXISTS_PET.getStatus(), ALREADY_EXISTS_PET.getMessage(), 400);
        }

        PetEntity pet = PetEntity.createNewPet(request);
        pet.setMember(member);
        member.addPets(pet);
        petRepository.save(pet);

        return PetRegisterNewPetResponse.builder()
                .name(pet.getName())
                .kind(pet.getKind())
                .gender(pet.getGender())
                .build();
    }

    @Transactional
    public void updatePet(Long petId, Long memberId, PetUpdateRequest petUpdateRequest) {

        MemberEntity member = validateMember(memberId);
        PetEntity pet = validatePet(petId, memberId);

        pet.updatePet(petUpdateRequest);
    }

    public PetInfoResponse getMyPetInfo(Long memberId, Long petId) {
        MemberEntity findMember = validateMember(memberId);

        PetEntity petEntity = validatePet(petId, memberId);

        return PetEntity.toPetInfoResponse(petEntity);
    }

    public List<PetInfoResponse> getAllMyPets(Long memberId) {
        MemberEntity findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        return petRepository.findByMemberId(findMember.getId())
                .stream()
                .map(PetEntity::toPetInfoResponse)
                .toList();
    }

    @Transactional
    public PetImageResponse registerPetImage(Long memberId, Long petId, MultipartFile multipartFile) {

        MemberEntity member = validateMember(memberId);

        PetEntity pet = validatePet(petId, memberId);

        String imageUrl = awsS3Uploader.uploadImage("pet", multipartFile);

        pet.updateImage(imageUrl);

        return PetImageResponse.builder()
                .memberId(member.getId())
                .petId(pet.getId())
                .image_url(imageUrl)
                .build();
    }

    @Transactional
    public PetImageResponse deletePetImage(Long memberId, Long petId) {

        MemberEntity member = validateMember(memberId);

        PetEntity pet = validatePet(petId, memberId);

        pet.updateImage(null);

        return PetImageResponse.builder()
                .memberId(member.getId())
                .petId(pet.getId())
                .image_url(null)
                .build();
    }
}
