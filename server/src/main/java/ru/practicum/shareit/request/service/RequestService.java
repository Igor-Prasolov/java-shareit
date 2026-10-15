package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(RequestDto requestDto, Long userId);

    List<RequestDto> findAllRequestByUser(Long userId);

    List<RequestDto> findAllRequestOtherUsers(Long userId);

    RequestDto findRequestById(Long requestId);

}
