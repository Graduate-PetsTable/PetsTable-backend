package com.example.petstable.domain.pet.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.pet.dto.request.PetInfoRequest;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.entity.PetEntity;
import com.example.petstable.domain.pet.repository.PetRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.petstable.global.exception.message.MemberMessage.*;
import static com.example.petstable.global.exception.message.PetMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;

    @Transactional
    public PetInfoResponse registerPet(Long memberId, PetInfoRequest petInfoRequest) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        validateDuplicatePet(petInfoRequest);

        PetEntity petEntity = PetEntity.createPet(petInfoRequest);

        memberEntity.addPets(petEntity);
        petEntity.setMember(memberEntity);

        petRepository.save(petEntity);

        return PetInfoResponse.builder()
                .type(petEntity.getType())
                .name(petEntity.getName())
                .kind(petEntity.getKind())
                .nickName(memberEntity.getNickName())
                .build();
    }


    private void validateDuplicatePet(PetInfoRequest petInfoRequest) {
        if (petRepository.existsByName(petInfoRequest.getName())) {
            throw new PetsTableException(PET_EXISTS.getStatus(), PET_EXISTS.getMessage(), 409);
        }
    }

}
