package dev.shop.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(itemRelation = "user", collectionRelation = "users")
public class UserResponse extends RepresentationModel<UserResponse> {
    private String username;
    private String country;
}
