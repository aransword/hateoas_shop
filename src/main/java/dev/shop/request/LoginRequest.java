package dev.shop.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "사용자 아이디", example = "user")
    private String username;

    @Schema(description = "사용자 비밀번호", example = "1234")
    private String password;
}