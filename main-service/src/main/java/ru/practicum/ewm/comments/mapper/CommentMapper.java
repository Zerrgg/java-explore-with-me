package ru.practicum.ewm.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    Comment toComment(CommentDto commentDto);

    Comment toComment(NewCommentDto newCommentDto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "event.id", target = "eventId")
    CommentDto toCommentDto(Comment comment);
}
