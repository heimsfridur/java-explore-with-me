package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserRequest {
    @NotBlank(message = "Name should not be blank")
    @Size(min = 2, max = 250, message = "Min length for name should be 2. Max - 250")
    private String name;

    @Email(message = "Email must be valid")
    @NotBlank(message =  "Email should not be blank")
    @Size(min = 6, max = 254, message = "Min length for email should be 5. Max - 254")
    private String email;
}
