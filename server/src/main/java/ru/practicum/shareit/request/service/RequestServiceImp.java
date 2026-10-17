package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImp implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Transactional
    @Override
    public RequestDto createRequest(RequestDto requestDto, Long userId) {
        User user = getUserOrThrow(userId);
        Request request = requestMapper.toRequest(requestDto);
        request.setRequestor(user);
        Request savedReq = requestRepository.save(request);

        return requestMapper.toRequestDto(savedReq, new ArrayList<>());
    }

    @Override
    public List<RequestDto> findAllRequestByUser(Long userId) {
        getUserOrThrow(userId);
        List<Request> requestList = requestRepository.findAllRequestByRequestorId(userId);

        return toRequestDtoList(requestList);
    }

    @Override
    public List<RequestDto> findAllRequestOtherUsers(Long userId) {
        getUserOrThrow(userId);
        List<Request> requestList = requestRepository.findAllRequestOtherUsers(userId);

        return toRequestDtoList(requestList);
    }

    @Override
    public RequestDto findRequestById(Long requestId) {
        Request request = getRequestOrThrow(requestId);

        return toRequestDtoList(List.of(request)).get(0);
    }


    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", userId);
                    return new NotFoundException("Пользователь не найден");
                });
    }

    private Request getRequestOrThrow(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос с ID {} не найден", requestId);
                    return new NotFoundException("Запрос не найден");
                });
    }

    private RequestDto toRequestDto(Request request, Map<Long, List<Item>> itemMap) {
        List<Item> itemList = itemMap.get(request.getId());
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        List<RequestItemDto> itemDtoList = itemList.stream()
                .map(requestMapper::toRequestItemDto)
                .toList();

        return requestMapper.toRequestDto(request, itemDtoList);
    }

    private List<RequestDto> toRequestDtoList(List<Request> requestList) {
        List<Long> requestIdList = requestList.stream()
                .map(Request::getId)
                .toList();

        List<Item> itemList = itemRepository.findAllByRequestIds(requestIdList);

        Map<Long, List<Item>> itemMap = itemList.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requestList.stream()
                .map(request -> toRequestDto(request, itemMap))
                .toList();
    }

}
