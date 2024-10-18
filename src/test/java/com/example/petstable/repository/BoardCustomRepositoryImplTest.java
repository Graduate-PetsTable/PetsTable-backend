package com.example.petstable.repository;

import com.example.petstable.domain.board.dto.request.*;
import com.example.petstable.domain.board.dto.response.BoardReadWithBookmarkResponse;
import com.example.petstable.domain.board.entity.*;
import com.example.petstable.domain.board.repository.*;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.config.QueryDSLConfig;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
@Import({BoardCustomRepositoryImpl.class, QueryDSLConfig.class})
@Transactional
public class BoardCustomRepositoryImplTest {

    @Autowired
    private BoardCustomRepositoryImpl boardCustomRepositoryImpl;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    @DisplayName("제목으로만 조회")
    void searchTitle() {
        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("테스트유저231")
                .socialType(SocialType.TEST)
                .build();
        memberRepository.save(member);

        String expected = "검색 필터링 테스트 제목";
        BoardEntity boardEntity = BoardEntity.builder()
                .title(expected)
                .details(null)
                .tags(null)
                .ingredients(null)
                .build();
        boardRepository.save(boardEntity);

        BoardFilteringRequest requestOnlyTitle = BoardFilteringRequest.builder()
                .keyword(expected)
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdTime").descending());

        // when
        List<BoardReadWithBookmarkResponse> actual = boardCustomRepositoryImpl.findRecipesByQueryDslWithTitleAndContent(requestOnlyTitle, member.getId(), pageable);

        // then
        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual.get(0).getTitle()).isEqualTo(expected);
    }

    @Test
    @DisplayName("상세 내용, 태그, 재료 연관관계 테스트")
    void testBoardDetailTagIngredientRelationships() {
        // given
        MemberEntity member = MemberEntity.builder()
                .nickName("테스트유저")
                .socialType(SocialType.TEST)
                .build();
        memberRepository.save(member);

        // 태그 생성
        TagEntity tag1 = TagEntity.builder()
                .name("테스트 태그 1")
                .build();

        TagEntity tag2 = TagEntity.builder()
                .name("테스트 태그 2")
                .build();

        // 재료 생성
        IngredientEntity ingredient1 = IngredientEntity.builder()
                .name("재료 1")
                .build();

        IngredientEntity ingredient2 = IngredientEntity.builder()
                .name("재료 2")
                .build();

        List<TagEntity> tags = Arrays.asList(tag1, tag2);
        List<IngredientEntity> ingredients = Arrays.asList(ingredient1, ingredient2);

        tagRepository.saveAll(tags);
        ingredientRepository.saveAll(ingredients);

        // BoardEntity 생성
        BoardEntity boardEntity = BoardEntity.builder()
                .title("검증용 제목")
                .tags(new ArrayList<>())
                .ingredients(new ArrayList<>())
                .build();

        // 연관관계 추가
        boardEntity.getTags().forEach(tag -> tag.getPost().addTags(List.of(tag)));
        boardEntity.getIngredients().forEach(ingredient -> ingredient.getPost().addIngredient(List.of(ingredient)));

        // 엔티티 저장
        boardRepository.save(boardEntity);

        tag1.setPost(boardEntity);
        tag2.setPost(boardEntity);
        ingredient1.setPost(boardEntity);
        ingredient2.setPost(boardEntity);
        tagRepository.saveAll(tags);
        ingredientRepository.saveAll(ingredients);

        // when
        BoardFilteringRequest request = BoardFilteringRequest.builder()
                .tagNames(List.of("테스트 태그 1", "테스트 태그 2"))
                .ingredients(List.of("재료 1", "재료 2"))
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdTime").descending());

        List<BoardReadWithBookmarkResponse> actual = boardCustomRepositoryImpl.findRecipesByQueryDslWithTagAndIngredients(request, member.getId(), pageable);

        // then
        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual.get(0).getTitle()).isEqualTo("검증용 제목");
    }
}
