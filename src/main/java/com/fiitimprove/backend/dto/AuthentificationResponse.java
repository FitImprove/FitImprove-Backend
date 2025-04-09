package com.fiitimprove.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description="Answer with token")
public class AuthentificationResponse {
    private Long id;
    private String username;
    @Schema(description = "Token access")
    private String token;
}
