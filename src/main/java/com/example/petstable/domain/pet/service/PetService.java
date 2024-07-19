package com.example.petstable.domain.pet.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.response.PetImageResponse;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterResponse;
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
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        PetEntity petEntity = PetEntity.createPet(petRegisterRequest);

        memberEntity.addPets(petEntity);
        petEntity.setMember(memberEntity);

        petRepository.save(petEntity);

        return PetRegisterResponse.builder()
                .id(petEntity.getId())
                .type(petEntity.getType())
                .name(petEntity.getName())
                .kind(petEntity.getKind())
                .build();
    }

    public PetInfoResponse getMyPetInfo(Long memberId, Long petId) {
        MemberEntity findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        PetEntity petEntity = petRepository.findByIdAndMemberId(petId, findMember.getId())
                .orElseThrow(() -> new PetsTableException(PET_NOT_FOUND.getStatus(), PETS_NOT_FOUND.getMessage(), 404));

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

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        PetEntity pet = petRepository.findByIdAndMemberId(petId, member.getId())
                .orElseThrow(() -> new PetsTableException(PET_NOT_FOUND.getStatus(), PETS_NOT_FOUND.getMessage(), 404));

        String imageUrl = awsS3Uploader.uploadImage(multipartFile);

        pet.updateImage(imageUrl);

        return PetImageResponse.builder()
                .memberId(member.getId())
                .petId(pet.getId())
                .image_url(imageUrl)
                .build();
    }

    @Transactional
    public PetImageResponse deletePetImage(Long memberId, Long petId) {

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        PetEntity pet = petRepository.findByIdAndMemberId(petId, member.getId())
                .orElseThrow(() -> new PetsTableException(PET_NOT_FOUND.getStatus(), PETS_NOT_FOUND.getMessage(), 404));

        pet.updateImage(null);

        return PetImageResponse.builder()
                .memberId(member.getId())
                .petId(pet.getId())
                .image_url(null)
                .build();
    }
}
