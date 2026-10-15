package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ItemDto {

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;

}
