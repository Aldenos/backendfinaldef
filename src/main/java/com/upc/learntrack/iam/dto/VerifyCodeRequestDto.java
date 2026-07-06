package com.upc.learntrack.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;
}