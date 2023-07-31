package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    ParticipationRequestMapper INSTANCE = Mappers.getMapper(ParticipationRequestMapper.class);

//    @Mapping(target = "eventId", expression = "java(request.getEvent().getId())")
//    @Mapping(target = "requesterId", expression = "java(request.getEvent().getId())")
//    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request);

    default ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .event(request.getEvent().getId())
                .build();
    }
}