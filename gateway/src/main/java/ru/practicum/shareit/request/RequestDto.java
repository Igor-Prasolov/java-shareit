package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestDto {

    @NotBlank
    @Size(max = 200)
    private String description;

}
