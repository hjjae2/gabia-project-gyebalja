package com.gabia.gyebalja.dto.board;

import com.gabia.gyebalja.domain.Board;
import com.gabia.gyebalja.dto.comment.CommentResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@ToString
@Getter
@Data
public class BoardResponseDto {

    private Long id;
    private String title;
    private String content;
    private int views;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long userId;
    private String userName;
    private Long educationId;
    private String educationTitle;
    private List<CommentResponseDto> commentList;

    public BoardResponseDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.views = board.getViews();
        this.createdDate = board.getCreatedDate();
        this.modifiedDate = board.getModifiedDate();
        this.userId = board.getUser().getId();
        this.userName = board.getUser().getName();
        this.educationId = board.getEducation().getId();
        this.educationTitle = board.getEducation().getTitle();
        this.commentList = new ArrayList<CommentResponseDto>();
    }

    public void changeCommentList(List<CommentResponseDto> commentResponseDtos){
        this.commentList = commentResponseDtos;
    }
}