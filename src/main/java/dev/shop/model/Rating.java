package dev.shop.model;

import dev.shop.model.id.RatingId;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Rating {
    @EmbeddedId
    private RatingId id = new RatingId();

    @ManyToOne
    @MapsId("username")
    @JoinColumn(name="username")
    private User user;

    @ManyToOne
    @MapsId("seriesId")
    @JoinColumn(name="series_id")
    private Series series;

    private int rating;

    public Rating(User user, Series series, int rating) {
        this.user = user;
        this.series = series;
        this.rating = rating;

        this.id = new RatingId(user.getUsername(), series.getSeriesId());
    }
}
