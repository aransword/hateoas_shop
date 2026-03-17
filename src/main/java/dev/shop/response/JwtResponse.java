package dev.shop.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;

    // 필요하다면 나중에 private String type = "Bearer"; 같은 필드를 추가할 수도 있어요!
}