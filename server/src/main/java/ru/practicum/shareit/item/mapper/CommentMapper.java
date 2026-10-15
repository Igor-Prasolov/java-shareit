package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());

        return comment;
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

}
