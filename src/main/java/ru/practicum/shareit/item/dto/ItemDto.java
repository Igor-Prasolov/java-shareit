package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ItemBookingDto;

import java.util.List;


@Data
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean available;

    private List<CommentDto> comments;

    private ItemBookingDto lastBooking;

    private ItemBookingDto nextBooking;

}
