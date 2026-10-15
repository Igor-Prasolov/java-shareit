package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import java.util.List;


@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private List<CommentDto> comments;

    private ItemBookingDto lastBooking;

    private ItemBookingDto nextBooking;

    private Long requestId;

}
