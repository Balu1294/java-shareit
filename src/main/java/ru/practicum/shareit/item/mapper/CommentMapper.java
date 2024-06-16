package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                UserMapper.toUserDto(comment.getAuthor()),
                comment.getRating(),
                comment.getText(),
                comment.getTimeOfCreation(),
                ItemMapper.toItemDto(comment.getItem()));
    }
    public static Comment toComment(CommentDto commentDto, User user) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                ItemMapper.toItem(commentDto.getItem(), user),
                UserMapper.toUser(commentDto.getAuthor()),
                commentDto.getRating(),
                commentDto.getCreated());
    }
}
