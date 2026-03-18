package dev.shop.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(itemRelation = "user", collectionRelation = "users")
public class UserResponse extends RepresentationModel<UserResponse> {
    @Schema(name = "사용자 ID", example = "user")
    private String username;

    @Schema(name = "사용자 국가", example = "South Korea")
    private String country;
}
