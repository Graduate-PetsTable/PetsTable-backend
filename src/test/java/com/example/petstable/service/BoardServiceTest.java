package com.example.petstable.service;

import com.example.petstable.domain.board.dto.request.BoardWithDetailsRequestAndTagRequest;
import com.example.petstable.domain.board.dto.request.DetailRequest;
import com.example.petstable.domain.board.dto.request.TagRequest;
import com.example.petstable.domain.board.dto.response.BoardReadAllResponse;
import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.DetailEntity;
import com.example.petstable.domain.board.entity.TagEntity;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.board.repository.DetailRepository;
import com.example.petstable.domain.board.repository.TagRepository;
import com.example.petstable.domain.board.service.BoardService;
import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.support.AwsS3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardService boardService;

    @MockBean
    private AwsS3Uploader awsS3Uploader;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private DetailRepository detailRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("게시글에 이미지, 설명, 태그들을 넣고 저장에 성공한다.")
    void savePost() throws IOException {
        String email = "ssg@naver.com";
        String socialId = "123456789";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        memberRepository.save(member);
        OAuthMemberSignUpRequest signUpRequest = new OAuthMemberSignUpRequest("Seung", SocialType.APPLE.getValue(), socialId);
        memberService.signUpByOAuthMember(signUpRequest);

        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("test_img2", "test_img2.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img2.jpg"));
        MockMultipartFile mockMultipartFile3 = new MockMultipartFile("test_img3", "test_img3.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img3.jpg"));
        when(awsS3Uploader.uploadImage(mockMultipartFile1)).thenReturn("test_img.jpg");
        when(awsS3Uploader.uploadImage(mockMultipartFile2)).thenReturn("test_img2.jpg");
        when(awsS3Uploader.uploadImage(mockMultipartFile3)).thenReturn("test_img3.jpg");

        DetailRequest detailRequest = DetailRequest.builder()
                .image_url(mockMultipartFile1)
                .description("설명1")
                .build();
        DetailRequest detailRequest2 = DetailRequest.builder()
                .image_url(mockMultipartFile2)
                .description("설명2")
                .build();
        DetailRequest detailRequest3 = DetailRequest.builder()
                .image_url(mockMultipartFile3)
                .description("설명3")
                .build();

        TagRequest tagRequest = TagRequest.builder().tagType("기능별").tagName("모질개선").build();
        TagRequest tagRequest2 = TagRequest.builder().tagType("기능별").tagName("다이어트").build();
        TagRequest tagRequest3 = TagRequest.builder().tagType("크기별").tagName("소형").build();

        BoardWithDetailsRequestAndTagRequest request = BoardWithDetailsRequestAndTagRequest.builder()
                .title("레시피 테스트")
                .details(List.of(detailRequest, detailRequest2, detailRequest3))
                .tags(List.of(tagRequest, tagRequest2, tagRequest3))
                .build();

        boardService.writePost(member.getId(), request);

        BoardEntity boardEntity = boardRepository.findByTitle("레시피 테스트").orElseThrow();
        List<DetailEntity> details = detailRepository.findDetailsByPostId(boardEntity.getId());
        List<TagEntity> tags = tagRepository.findTagsByPostId(boardEntity.getId());


        assertThat(boardEntity).isNotNull();
        assertThat(details.size()).isEqualTo(3);
        assertThat(tags.size()).isEqualTo(3);
        assertThat(details.get(0).getDescription()).isEqualTo("설명1");
        assertThat(tags.get(0).getName()).isEqualTo("모질개선");
    }

    @Test
    @DisplayName("게시글 목록 전체 조회 테스트")
    void readAllRecipePost() {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("ssg@naver.com")
                .nickName("Seunggu")
                .socialType(SocialType.TEST)
                .build();
        memberRepository.save(member);

        BoardEntity post1 = BoardEntity.builder()
                .title("제목")
                .build();
        BoardEntity post2 = BoardEntity.builder()
                .title("제목2")
                .build();
        BoardEntity post3 = BoardEntity.builder()
                .title("제목3")
                .build();
        boardRepository.saveAll(List.of(post1, post2, post3));

        // when
        Pageable pageable = PageRequest.of(0, 5);
        BoardReadAllResponse actual = boardService.getAllPost(pageable);

        // then
        assertThat(actual.recipes().size()).isEqualTo(3);
    }

}
