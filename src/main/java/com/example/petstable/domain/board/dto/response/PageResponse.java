package com.example.petstable.domain.board.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse {
    private Integer totalPages;
    private Integer currentPage;
    private Long totalElements;
    private boolean isPageLast;
    private Integer size;
    private Integer numberOfElements;
    private boolean isPageFirst;
    private boolean isEmpty;

    public PageResponse(Page<?> page) {
        currentPage = page.getNumber() + 1;
        isPageLast = page.isLast();
        isPageFirst = page.isFirst();
        isEmpty = page.isEmpty();
        totalPages = page.getTotalPages();
        numberOfElements = page.getNumberOfElements();
        totalElements = page.getTotalElements();
        size = page.getSize();
    }
}
