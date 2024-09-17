package ru.practicum.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    private String name;
    private String email;
}
