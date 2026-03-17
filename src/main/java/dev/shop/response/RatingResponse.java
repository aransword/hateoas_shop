package dev.shop.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class RatingResponse extends RepresentationModel<RatingResponse> {
    private String username;
    private long seriesId;
    private int rating;
}
