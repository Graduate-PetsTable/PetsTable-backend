package com.example.petstable.domain.pet.controller;

import com.example.petstable.domain.pet.dto.request.PetRegisterNewPetRequest;
import com.example.petstable.domain.pet.dto.request.PetRegisterRequest;
import com.example.petstable.domain.pet.dto.request.PetUpdateRequest;
import com.example.petstable.domain.pet.dto.response.PetImageResponse;
import com.example.petstable.domain.pet.dto.response.PetInfoResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterNewPetResponse;
import com.example.petstable.domain.pet.dto.response.PetRegisterResponse;
import com.example.petstable.global.auth.LoginUserId;
import com.example.petstable.global.exception.PetsTableApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "반려동물 관련 API", description = "")
@SecurityRequirement(name = "JWT")
public interface PetApi {

    @Operation(summary = "온보딩 ( 초기 반려동물 추가 ) API", description = "이름, 나이, 몸무게 정보 입력 받는 API 입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PetRegisterResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "name": "Buddy",
                              "age": 3,
                              "weight": 15.5
                            }
                            """)
                    ),
                            description = "초기 반려동물 등록에 성공하였습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<PetRegisterResponse> initAddPet(
            @RequestBody(description = "초기 반려동물 등록 요청 데이터", required = true) @Valid PetRegisterRequest pet,
            @Parameter(hidden = true) @LoginUserId Long memberId
    );

    @Operation(summary = "새로운 반려동물 추가 API ", description = "새로운 반려동물 추가 API 입니다. ( 모든 정보 입력 받음 )",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PetRegisterNewPetResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 2,
                              "name": "장군이",
                              "kind": "말티즈,
                              "gender": "남"
                            }
                            """)
                    ),
                            description = "새로운 반려동물 등록에 성공하였습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<PetRegisterNewPetResponse> addPet(
            @RequestBody(description = "반려동물 등록 요청 데이터", required = true) @Valid PetRegisterNewPetRequest pet,
            @Parameter(hidden = true) @LoginUserId Long memberId
    );

    @Operation(summary = "반려동물 정보 수정 API", description = "기존에 등록한 반려동물 정보 수정하는 API 입니다.",
            parameters = {
                    @Parameter(name = "petId", description = "반려동물 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "반려동물 정보 수정에 성공하였습니다.", content = @Content(
                            examples = @ExampleObject(value = """
                            {
                              "message": "반려동물 정보 수정에 성공하였습니다.",
                              "status": 200
                            }
                            """)
                    )),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<String> updatePet(
            @RequestBody(description = "반려동물 정보 업데이트 요청 데이터", required = true) @Valid PetUpdateRequest pet,
            @PathVariable("petId") Long petId,
            @Parameter(hidden = true) @LoginUserId Long memberId
    );

    @Operation(summary = "반려동물 상세 정보 조회", description = "특정 반려동물의 상세 정보를 조회하는 API",
            parameters = @Parameter(name = "petId", description = "반려동물 ID", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PetInfoResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "name": "장군이",
                              "age": 5,
                              "weight": 4.7,
                              "size": "소형"
                              "kind": "말티즈",
                              "gender": "남",
                              "imageUrl": "https://example.com/image1.jpg",
                              "ownerNickname": "Blue"
                            }
                            """)
                    ),
                            description = "반려동물 상세 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<PetInfoResponse> getPetInfo(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @PathVariable(name = "petId") Long petId
    );

    @Operation(summary = "반려동물 전체 조회", description = "회원이 등록한 모든 반려동물 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = List.class),
                            examples = @ExampleObject(value = """
                            [
                              {
                                "id": 1,
                                "name": "파랑",
                                "age": 5,
                                "weight": 3.1,
                                "size": "소형,
                                "kind": "말티즈",
                                "gender": "남",
                                "imageUrl": "https://example.com/image1.jpg",
                                "ownerNickname": "Blue"
                              },
                              {
                                "id": 12
                                "name": "처록",
                                "age": 8,
                                "weight": 4.2,
                                "size": "소형,
                                "kind": "푸들",
                                "gender": "남",
                                "imageUrl": "https://example.com/image2.jpg",
                                "ownerNickname": "Blue"
                              }
                            ]
                            """)
                    ),
                            description = "반려동물 목록 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<List<PetInfoResponse>> getAllPets(
            @Parameter(hidden = true) @LoginUserId Long memberId
    );

    @Operation(summary = "반려동물 사진 등록", description = "반려동물의 사진을 등록하는 API",
            parameters = {
                    @Parameter(name = "petId", description = "반려동물 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PetImageResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "memberId": 5,
                              "petId": 401,
                              "imageUrl": "https://example.com/new-image.jpg"
                            }
                            """)
                    ),
                            description = "반려동물 사진 등록에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    PetsTableApiResponse<PetImageResponse> addPetImage(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @PathVariable("petId") Long petId,
            @RequestPart("image") MultipartFile image
    );

    @Operation(summary = "반려동물 사진 삭제", description = "반려동물의 사진을 삭제하는 API",
            parameters = {
                    @Parameter(name = "petId", description = "반려동물 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(
                            schema = @Schema(implementation = PetImageResponse.class),
                            examples = @ExampleObject(value = """
                            {
                              "memberId": 5,
                              "petId": 401,
                              "imageUrl": "https://example.com/new-image.jpg"
                            }
                            """)
                    ),
                            description = "반려동물 사진 삭제에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "액세스 토큰이 올바르지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    PetsTableApiResponse<PetImageResponse> deletePetImage(
            @Parameter(hidden = true) @LoginUserId Long memberId,
            @PathVariable("petId") Long petId
    );
}