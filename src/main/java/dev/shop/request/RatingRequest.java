package dev.shop.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RatingRequest {
    // username 필드 삭제!
    @Schema(description = "평점을 남길 시리즈의 ID", example = "3")
    private Long seriesId;

    @Schema(description = "부여할 평점", example = "5")
    private int rating;
}