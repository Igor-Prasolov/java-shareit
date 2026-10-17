package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Component
public class RequestMapper {

    public Request toRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setDescription(requestDto.getDescription());

        return request;
    }

    public RequestDto toRequestDto(Request request, List<RequestItemDto> requestItemDtoList) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setDescription(request.getDescription());
        requestDto.setItems(requestItemDtoList);

        return requestDto;
    }

    public RequestItemDto toRequestItemDto(Item item) {
        RequestItemDto requestItemDto = new RequestItemDto();
        requestItemDto.setItemId(item.getId());
        requestItemDto.setOwnerId(item.getOwner().getId());
        requestItemDto.setName(item.getName());

        return requestItemDto;
    }



}
