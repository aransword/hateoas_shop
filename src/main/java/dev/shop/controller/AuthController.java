package dev.shop.controller;

import dev.shop.authentication.JwtTokenProvider;
import dev.shop.model.User;
import dev.shop.repository.UserRepository;
import dev.shop.response.JwtResponse;
import dev.shop.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관련 API", description = "인증과 관련한 동작을 수행한다.")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 수행한다.")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        String token = tokenProvider.createToken(user.getUsername());

        // HATEOAS를 적용하고 싶다면 여기서 EntityModel.of(token) 형태도 가능해!
        return ResponseEntity.ok(new JwtResponse(token));
    }
}