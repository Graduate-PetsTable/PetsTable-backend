package com.example.petstable.domain.board.dto.response;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.entity.DetailEntity;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DetailResponse {

    private String title;
    private int view_count;
    private List<String> image_url;
    private List<String> description;

    public DetailResponse(BoardEntity post) {
        this.title = post.getTitle();
        this.view_count = post.getView_count();
        this.image_url = post.getDescription().stream()
                .map(DetailEntity::getImage_url)
                .collect(Collectors.toList());

        this.description = post.getDescription().stream()
                .map(DetailEntity::getDescription)
                .collect(Collectors.toList());
    }
}
