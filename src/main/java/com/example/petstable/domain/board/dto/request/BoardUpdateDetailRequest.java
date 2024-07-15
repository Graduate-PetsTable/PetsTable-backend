package com.example.petstable.domain.board.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class BoardUpdateDetailRequest {
    private MultipartFile image;
    private String description;
}
