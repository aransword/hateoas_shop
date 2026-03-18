package dev.shop.controller;

import dev.shop.model.User;
import dev.shop.repository.UserRepository;
import dev.shop.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name="사용자 정보 API", description = "사용자 자신의 정보를 조회하는 API")
public class UserController {
    private final UserRepository userRepository;

    /**
     * username 기반으로 사용자 정보 조회
     * @param username
     * @param userDetails
     * @return
     */
    @Operation(summary = "사용자 정보 조회", description = "로그인된 사용자의 정보를 username 기반으로 조회한다.")
    @GetMapping("/{username}")
    public EntityModel<UserResponse> getUser(@Parameter(description = "조회할 유저의 아이디", example = "user") @PathVariable String username, @AuthenticationPrincipal UserDetails userDetails) {
        if (!username.equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신의 정보만 조회할 수 있습니다.");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserResponse response = new UserResponse();
        response.setUsername(user.getUsername());
        response.setCountry(user.getCountry());

        return EntityModel.of(response,
                linkTo(methodOn(UserController.class).getUser(username, null)).withSelfRel(),
                linkTo(methodOn(SeriesController.class).getSeriesList(null)).withRel("all-series")
        );
    }
}
