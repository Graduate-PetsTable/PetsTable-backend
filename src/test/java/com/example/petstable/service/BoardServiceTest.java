package com.example.petstable.service;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.BoardDetailReadResponse;
import com.example.petstable.domain.board.dto.response.BoardReadAllResponse;
import com.example.petstable.domain.board.entity.*;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.board.repository.DetailRepository;
import com.example.petstable.domain.board.repository.IngredientRepository;
import com.example.petstable.domain.board.repository.TagRepository;
import com.example.petstable.domain.board.service.BoardService;
import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.support.AwsS3Uploader;
import org.junit.jupiter.api.AfterEach;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.assertArg;
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

    @Autowired
    private IngredientRepository ingredientRepository;

    public void clearStore(){
        tagRepository.deleteAll();
        detailRepository.deleteAll();
        ingredientRepository.deleteAll();
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        clearStore();
    }

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
        when(awsS3Uploader.uploadImage("test", mockMultipartFile1)).thenReturn("test_img.jpg");
        when(awsS3Uploader.uploadImage("test", mockMultipartFile2)).thenReturn("test_img2.jpg");
        when(awsS3Uploader.uploadImage("test", mockMultipartFile3)).thenReturn("test_img3.jpg");

        TagRequest tagRequest = TagRequest.builder().tagType("기능별").tagName("모질개선").build();
        TagRequest tagRequest2 = TagRequest.builder().tagType("기능별").tagName("다이어트").build();
        DescriptionRequest descriptionRequest1 = new DescriptionRequest("설명1");
        DescriptionRequest descriptionRequest2 = new DescriptionRequest("설명2");
        DescriptionRequest descriptionRequest3 = new DescriptionRequest("설명3");

        BoardPostRequest request = BoardPostRequest.builder()
                .title("레시피 테스트")
                .descriptions(List.of(descriptionRequest1, descriptionRequest2, descriptionRequest3))
                .tags(List.of(tagRequest, tagRequest2))
                .build();

        boardService.writePost(member.getId(), request, mockMultipartFile1, List.of(mockMultipartFile1, mockMultipartFile2, mockMultipartFile3));

        BoardEntity boardEntity = boardRepository.findByTitle("레시피 테스트").orElseThrow();
        List<DetailEntity> details = detailRepository.findDetailsByPostId(boardEntity.getId());
        List<TagEntity> tags = tagRepository.findTagsByPostId(boardEntity.getId());


        assertThat(boardEntity).isNotNull();
        assertThat(details.size()).isEqualTo(3);
        assertThat(tags.size()).isEqualTo(2);
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
        BoardReadAllResponse actual = boardService.getAllPost(pageable, member.getId());

        // then
        assertThat(actual.recipes().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("레시피 내용을 상세 조회에 성공한다.")
    void getBoardDetail() throws Exception {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("ssg@naver.com")
                .nickName("Seung-9")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        when(awsS3Uploader.uploadImage("test", mockMultipartFile)).thenReturn("test_img.jpg");

        DescriptionRequest descriptionRequest = new DescriptionRequest("설명");
        TagRequest tagRequest = TagRequest.builder().tagType("기능별").tagName("모질개선").build();
        IngredientRequest ingredientRequest = IngredientRequest.builder().name("당근").weight("30g").build();

        BoardPostRequest request = BoardPostRequest.builder()
                .title("레시피 테스트")
                .descriptions(List.of(descriptionRequest))
                .ingredients(List.of(ingredientRequest))
                .tags(List.of(tagRequest))
                .build();

        boardService.writePost(member.getId(), request, mockMultipartFile, List.of(mockMultipartFile));
        BoardEntity post = boardRepository.findByTitle("레시피 테스트").orElseThrow();

        // when
        BoardDetailReadResponse actual = boardService.findDetailByBoardId(member.getId(), post.getId());
        LocalDateTime expectedCreatedTime = post.getCreatedTime();

        // then
        assertAll(
                () -> assertThat(actual.getDetails().get(0).getImage_url()).isEqualTo("test_img.jpg"),
                () -> assertThat(actual.getAuthor()).isEqualTo("Seung-9"),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(expectedCreatedTime)
        );
    }

    @Test
    @DisplayName("레시피 내용을 상세 조회하면 조회수가 증가한다.")
    void incViewCount() throws Exception {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("ssg@naver.com")
                .nickName("Seung-9")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        when(awsS3Uploader.uploadImage("test", mockMultipartFile)).thenReturn("test_img.jpg");

        DescriptionRequest descriptionRequest = new DescriptionRequest("설명");
        TagRequest tagRequest = TagRequest.builder().tagType("기능별").tagName("모질개선").build();

        BoardPostRequest request = BoardPostRequest.builder()
                .title("레시피 테스트")
                .descriptions(List.of(descriptionRequest))
                .tags(List.of(tagRequest))
                .build();

        boardService.writePost(member.getId(), request, mockMultipartFile, List.of(mockMultipartFile));
        BoardEntity post = boardRepository.findByTitle("레시피 테스트").orElseThrow();

        // when
        boardService.findDetailByBoardId(member.getId(), post.getId());
        BoardEntity actual = boardRepository.findById(post.getId()).orElseThrow();

        // then
        assertThat(actual.getView_count()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 제목을 성공적으로 수정한다.")
    void updatePostTitle() {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gamil.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity board = BoardEntity.builder()
                .title("제목 테스트")
                .build();

        boardRepository.save(board);

        BoardEntity beforeBoard = boardRepository.findByTitle("제목 테스트").orElseThrow();

        // when
        BoardUpdateTitleRequest updateTitle = new BoardUpdateTitleRequest("제목 수정 테스트");
        boardService.updatePostTitle(member.getId(), beforeBoard.getId(), updateTitle);

        BoardEntity after = boardRepository.findById(beforeBoard.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(after.getId()).isEqualTo(beforeBoard.getId()),
                () -> assertThat(after.getTitle()).isEqualTo("제목 수정 테스트")
        );
    }

    @Test
    @DisplayName("게시글 태그를 수정하면 기존에 있던 태그는 삭제되고 새로운 태그들이 등록된다..")
    void updatePostTag() {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gamil.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("태그 테스트")
                .build();

        TagEntity tag = TagEntity.builder()
                .type(TagType.AGE)
                .name("노견")
                .build();

        boardRepository.save(post);
        tagRepository.save(tag);

        // when
        BoardUpdateTagRequest updateTag = new BoardUpdateTagRequest("기능별", "모질개선");
        boardService.updatePostTags(member.getId(), post.getId(), List.of(updateTag));

        TagEntity beforeTag = tagRepository.findByName("노견");
        TagEntity newTag = tagRepository.findByName("모질개선");

        // then
        assertAll(
                () -> assertThat(beforeTag.getPost()).isNull(),
                () -> assertThat(newTag.getPost()).isNotNull()
        );
    }

    @Test
    @DisplayName("게시글 상세 내용의 사진만 변경한다..")
    void updateImageUrl() throws Exception {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gmail.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("상세 내용 변경 테스트")
                .build();

        boardRepository.save(post);

        DetailEntity detail = DetailEntity.builder()
                .image_url("static/test")
                .description("tttt")
                .build();

        detailRepository.save(detail);

        post.addDetails(List.of(detail));
        detail.setPost(post);

        MockMultipartFile expected = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        when(awsS3Uploader.uploadImage("test", expected)).thenReturn("test_img.jpg");

        // when
        boardService.updatePostDetail(member.getId(), post.getId(), detail.getId(), null, expected);

        DetailEntity actual = detailRepository.findById(detail.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(actual.getDescription()).isEqualTo("tttt"),
                () -> assertThat(actual.getImage_url()).isEqualTo("test_img.jpg")
        );

    }

    @Test
    @DisplayName("게시글 상세 내용의 설명만 변경한다..")
    void updateDescription() {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gmail.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("상세 내용 변경 테스트")
                .build();

        boardRepository.save(post);

        DetailEntity detail = DetailEntity.builder()
                .image_url("static/test")
                .description("tttt")
                .build();

        detailRepository.save(detail);

        post.addDetails(List.of(detail));
        detail.setPost(post);

        // when
        DetailUpdateRequest request = DetailUpdateRequest.builder()
                .description("변경 테스트")
                .build();

        boardService.updatePostDetail(member.getId(), post.getId(), detail.getId(), request, null);

        DetailEntity actual = detailRepository.findById(detail.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(actual.getDescription()).isEqualTo("변경 테스트"),
                () -> assertThat(actual.getImage_url()).isEqualTo("static/test")
        );

    }

    @Test
    @DisplayName("게시글 상세 내용의 사진과 설명 모두 변경한다..")
    void updateImageUrlAndDescription() throws Exception{

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gmail.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("상세 내용 변경 테스트")
                .build();

        boardRepository.save(post);

        DetailEntity detail = DetailEntity.builder()
                .image_url("static/test")
                .description("tttt")
                .build();

        detailRepository.save(detail);

        post.addDetails(List.of(detail));
        detail.setPost(post);

        MockMultipartFile expected = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        when(awsS3Uploader.uploadImage("test", expected)).thenReturn("test_img.jpg");

        // when
        DetailUpdateRequest request = DetailUpdateRequest.builder()
                .description("변경 테스트")
                .build();

        boardService.updatePostDetail(member.getId(), post.getId(), detail.getId(), request, expected);

        DetailEntity actual = detailRepository.findById(detail.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(actual.getDescription()).isEqualTo("변경 테스트"),
                () -> assertThat(actual.getImage_url()).isEqualTo("test_img.jpg")
        );

    }

    @Test
    @DisplayName("게시글 상세 내용을 삭제한다.")
    void deletePostDetail() {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gmail.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("게시글 삭제 테스트")
                .build();

        boardRepository.save(post);

        DetailEntity detail = DetailEntity.builder()
                .image_url("static/test")
                .description("tttt")
                .build();

        detailRepository.save(detail);

        post.addDetails(List.of(detail));
        detail.setPost(post);

        // when
        boardService.deletePostDetail(member.getId(), post.getId(), detail.getId());
        List<DetailEntity> actual = detailRepository.findDetailsByPostId(post.getId());

        // then
        assertThat(actual.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deletePost() {

        // given
        MemberEntity member = MemberEntity.builder()
                .email("test@gmail.com")
                .nickName("test")
                .socialType(SocialType.TEST)
                .build();

        memberRepository.save(member);

        BoardEntity post = BoardEntity.builder()
                .title("상세 내용 삭제 테스트")
                .build();

        boardRepository.save(post);

        TagEntity tag = TagEntity.builder()
                .type(TagType.AGE)
                .name("노견")
                .build();

        tagRepository.save(tag);

        DetailEntity detail = DetailEntity.builder()
                .image_url("static/test")
                .description("tttt")
                .build();

        detailRepository.save(detail);

        post.addDetails(List.of(detail));
        post.addTags(List.of(tag));
        tag.setPost(post);
        detail.setPost(post);

        // when
        boardService.deletePost(member.getId(), post.getId());

        // then
        assertThatThrownBy(() -> boardService.findDetailByBoardId(member.getId(), post.getId())).isInstanceOf(PetsTableException.class);
    }

    @DisplayName("게시글 작성 시 재료도 추가한다.")
    @Test
    void writePostWithIngredient() throws IOException {

        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("g")
                .build();

        memberRepository.save(member);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_img", "test_img.jpg", "jpg", new FileInputStream("src/test/resources/images/test_img.jpg"));
        when(awsS3Uploader.uploadImage("test", mockMultipartFile)).thenReturn("test_img.jpg");

        IngredientRequest ingredientRequest = IngredientRequest.builder()
                .name("당근")
                .weight("10g")
                .build();

        DescriptionRequest descriptionRequest = new DescriptionRequest("설명");
        TagRequest tagRequest = TagRequest.builder()
                .tagType("기능별")
                .tagName("모질개선")
                .build();

        BoardPostRequest request = BoardPostRequest.builder()
                .title("gg")
                .descriptions(List.of(descriptionRequest))
                .ingredients(List.of(ingredientRequest))
                .tags(List.of(tagRequest))
                .build();

        // when
        boardService.writePost(member.getId(), request, mockMultipartFile, List.of(mockMultipartFile));
        BoardEntity post = boardRepository.findByTitle("gg").orElseThrow();
        List<IngredientEntity> actual = ingredientRepository.findIngredientEntityByPostId(post.getId());

        // then
        assertThat(actual.get(0).getName()).isEqualTo("당근");
        assertThat(actual.get(0).getWeight()).isEqualTo("10g");

    }
}