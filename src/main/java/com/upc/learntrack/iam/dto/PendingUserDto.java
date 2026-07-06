package com.upc.learntrack.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String roleName;
    private LocalDateTime createdAt;
}