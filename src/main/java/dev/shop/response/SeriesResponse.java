package dev.shop.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(itemRelation = "series", collectionRelation = "seriesList")
public class SeriesResponse extends RepresentationModel<SeriesResponse> {
    @Schema(name = "시리즈 ID", example = "6")
    private long seriesId;

    @Schema(name = "시리즈 이름", example = "React and Spring Integration")
    private String seriesName;

    @Schema(name = "시리즈 URL", example = "https://example.com/react-spring")
    private String seriesUrl;
}
