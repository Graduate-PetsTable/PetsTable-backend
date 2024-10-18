package com.example.petstable.domain.board.repository;

import com.example.petstable.domain.board.dto.request.BoardFilteringRequest;
import com.example.petstable.domain.board.dto.response.BoardReadWithBookmarkResponse;
import com.example.petstable.domain.board.entity.BoardEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.petstable.domain.board.entity.QBoardEntity.boardEntity;
import static com.example.petstable.domain.board.entity.QDetailEntity.detailEntity;
import static com.example.petstable.domain.board.entity.QIngredientEntity.ingredientEntity;
import static com.example.petstable.domain.board.entity.QTagEntity.tagEntity;
import static com.example.petstable.domain.bookmark.entity.QBookmarkEntity.bookmarkEntity;

@Repository
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public BoardCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<BoardReadWithBookmarkResponse> findRecipesByQueryDslWithTitleAndContent(BoardFilteringRequest filteringRequest, Long memberId, Pageable pageable) {
        List<BoardEntity> recipeList = jpaQueryFactory
                .selectFrom(boardEntity)
                .leftJoin(detailEntity).on(detailEntity.post.eq(boardEntity))
                .where(
                        eqKeyword(filteringRequest.getKeyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortPostList(pageable))
                .fetch();

        return recipeList.stream()
                .map(board -> new BoardReadWithBookmarkResponse(board, checkBookmarkStatus(board, memberId)))
                .toList();
    }

    @Override
    public List<BoardReadWithBookmarkResponse> findRecipesByQueryDslWithTagAndIngredients(BoardFilteringRequest filteringRequest, Long memberId, Pageable pageable) {
        List<BoardEntity> recipeList = jpaQueryFactory
                .selectFrom(boardEntity)
                .leftJoin(tagEntity).on(tagEntity.post.eq(boardEntity))
                .leftJoin(ingredientEntity).on(ingredientEntity.post.eq(boardEntity))
                .where(
                        eqTag(filteringRequest.getTagNames()),
                        eqIngredients(filteringRequest.getIngredients())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortPostList(pageable))
                .fetch();

        return recipeList.stream()
                .map(board -> new BoardReadWithBookmarkResponse(board, checkBookmarkStatus(board, memberId)))
                .toList();
    }

    // 북마크 상태 확인 메소드
    private boolean checkBookmarkStatus(BoardEntity board, Long memberId) {
        return jpaQueryFactory
                .select(bookmarkEntity.status)
                .from(bookmarkEntity)
                .where(
                        bookmarkEntity.post.eq(board),
                        bookmarkEntity.member.id.eq(memberId),
                        bookmarkEntity.status.isTrue()  // 북마크 상태가 true인 경우만 확인
                )
                .fetchOne() != null;
    }

    // 제목 혹은 내용으로 검색
    private BooleanExpression eqKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return boardEntity.title.contains(keyword)
                .or(detailEntity.description.contains(keyword));
    }

    // 태그 필터링 ( 복수 ) 검색
    private BooleanBuilder eqTag(List<String> tagNames){
        if(tagNames == null || tagNames.isEmpty()){
            return null;
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        for (String tagName : tagNames){
            booleanBuilder.or(tagEntity.name.eq(tagName));
        }

        return booleanBuilder;
    }

    // 재료 필터링 ( 복수 ) 검색
    private BooleanBuilder eqIngredients(List<String> ingredients){
        if(ingredients == null || ingredients.isEmpty()){
            return null;
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        for (String ingredient : ingredients){
            booleanBuilder.or(ingredientEntity.name.eq(ingredient));
        }

        return booleanBuilder;
    }

    // 정렬
    private OrderSpecifier<?> sortPostList(Pageable page){
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : page.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()){
                    case "createdTime":
                        return new OrderSpecifier(direction, boardEntity.createdTime);
                    case "mostViewed":
                        return new OrderSpecifier(direction, boardEntity.view_count);
                }
            }
        }
        return new OrderSpecifier(Order.DESC, boardEntity.modifiedTime);
    }
}