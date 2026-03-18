package dev.shop.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class RatingResponse extends RepresentationModel<RatingResponse> {
    @Schema(name = "사용자 ID", example = "user")
    private String username;

    @Schema(name = "시리즈 ID", example = "6")
    private long seriesId;

    @Schema(name = "평점", example = "4")
    private int rating;
}
