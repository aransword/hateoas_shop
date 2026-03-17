package dev.shop.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RatingRequest {
    // username 필드 삭제!
    private Long seriesId;
    private int rating;
}