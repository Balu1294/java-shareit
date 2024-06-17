package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                UserMapper.toUserDto(comment.getAuthor()),
                UserMapper.toUserDto(comment.getAuthor()).getName(),
                UserMapper.toUserDto(comment.getAuthor()).getId(),
                comment.getRating(),
                comment.getText(),
                comment.getTimeOfCreation(),
                ItemMapper.toItemDto(comment.getItem()),
                ItemMapper.toItemDto(comment.getItem()).getId());
    }
    public static Comment toComment(CommentDto commentDto, User user, Item item) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getRating(),
                commentDto.getCreated());
    }
    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Comment comment : comments) {
            commentDtos.add(toCommentDto(comment));
        }

        return commentDtos;
    }
}
