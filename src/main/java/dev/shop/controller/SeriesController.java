package dev.shop.controller;

import dev.shop.model.Series;
import dev.shop.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {
    private final SeriesRepository seriesRepository;
    private final PagedResourcesAssembler<Series> assembler;

    /**
     * 페이징된 series 리스트
     * @param pageable
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<PagedModel<EntityModel<Series>>> getSeriesList(@PageableDefault(size = 5) Pageable pageable) {

        Page<Series> seriesPage = seriesRepository.findAll(pageable);

        PagedModel<EntityModel<Series>> pagedModel = assembler.toModel(seriesPage, series ->
                EntityModel.of(series,
                        linkTo(methodOn(SeriesController.class).getSeriesDetail(series.getSeriesId())).withSelfRel()
                )
        );

        pagedModel.add(linkTo(methodOn(SeriesController.class).getSeriesList(pageable)).withSelfRel());

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * seriesId 기반으로 시리즈 조회
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public EntityModel<Series> getSeriesDetail(@PathVariable Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("시리즈를 찾을 수 없습니다."));

        return EntityModel.of(series,
                linkTo(methodOn(SeriesController.class).getSeriesDetail(id)).withSelfRel(),
                linkTo(methodOn(SeriesController.class).getSeriesList(null)).withRel("list"));
    }
}
