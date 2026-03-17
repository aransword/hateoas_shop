package dev.shop.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(itemRelation = "series", collectionRelation = "seriesList")
public class SeriesResponse extends RepresentationModel<SeriesResponse> {
    private long seriesId;
    private String seriesName;
    private String seriesUrl;
}
