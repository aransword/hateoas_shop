package dev.shop.controller;

import dev.shop.model.Rating;
import dev.shop.model.Series;
import dev.shop.model.User;
import dev.shop.model.id.RatingId;
import dev.shop.repository.RatingRepository;
import dev.shop.repository.SeriesRepository;
import dev.shop.repository.UserRepository;
import dev.shop.request.RatingRequest;
import dev.shop.response.RatingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
@Tag(name = "평점 정보 API", description = "평점을 조회하고 남기거나 수정, 삭제할 수 있다.")
public class RatingController {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final PagedResourcesAssembler<Rating> assembler;

    @GetMapping("/search")
    @Operation(summary = "특정 평점 조회", description = "어떤 시리즈에 대해 특정 사용자가 남긴 평점을 조회한다.")
    public EntityModel<RatingResponse> getRating(@Parameter(name = "사용자 ID") @RequestParam String username, @Parameter(name = "시리즈 ID") @RequestParam long seriesId) {
        RatingId id = new RatingId(username, seriesId);
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new RuntimeException("평점을 찾을 수 없음."));

        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setUsername(rating.getUser().getUsername());
        ratingResponse.setSeriesId(rating.getSeries().getSeriesId());
        ratingResponse.setRating(rating.getRating());

        return EntityModel.of(ratingResponse,
                linkTo(methodOn(RatingController.class).getRating(username, seriesId)).withSelfRel()
        );
    }

    @GetMapping("/list")
    @Operation(summary = "평점 목록 조회", description = "모든 평점을 페이지 단위로 조회한다.")
    public PagedModel<EntityModel<RatingResponse>> getRatingList(@ParameterObject @PageableDefault(size = 5)Pageable pageable) {
        Page<Rating> ratingPage = ratingRepository.findAll(pageable);

        return assembler.toModel(ratingPage, rating -> {
            RatingResponse response = new RatingResponse();
            response.setUsername(rating.getUser().getUsername());
            response.setSeriesId(rating.getSeries().getSeriesId());
            response.setRating(rating.getRating());

            return EntityModel.of(response,
                    linkTo(methodOn(RatingController.class).getRating(response.getUsername(), response.getSeriesId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).getUser(response.getUsername(), null)).withRel("user"),
                    linkTo(methodOn(SeriesController.class).getSeriesDetail(response.getSeriesId())).withRel("series")
            );
        });
    }

    /**
     * 특정 유저가 남긴 모든 평점 조회
     * 예: GET /ratings/user/spring_fan?page=0&size=5
     */
    @GetMapping("/user/{username}")
    @Operation(summary = "유저 별 평점 조회", description = "특정 사용자가 남긴 모든 평점을 조회한다.")
    public PagedModel<EntityModel<RatingResponse>> getRatingsByUser(
            @Parameter(name = "사용자 ID", example = "user") @PathVariable String username,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable) {

        Page<Rating> ratingPage = ratingRepository.findByUser_Username(username, pageable);

        return assembler.toModel(ratingPage, rating -> {
            RatingResponse response = new RatingResponse();
            response.setUsername(rating.getUser().getUsername());
            response.setSeriesId(rating.getSeries().getSeriesId());
            response.setRating(rating.getRating());

            return EntityModel.of(response,
                    linkTo(methodOn(RatingController.class).getRating(response.getUsername(), response.getSeriesId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).getUser(response.getUsername(), null)).withRel("user"),
                    linkTo(methodOn(SeriesController.class).getSeriesDetail(response.getSeriesId())).withRel("series")
            );
        });
    }

    /**
     * 특정 시리즈에 달린 모든 평점 조회
     * 예: GET /ratings/series/3?page=0&size=5
     */
    @GetMapping("/series/{seriesId}")
    @Operation(summary = "시리즈 별 평점 조회", description = "특정 시리즈에 달린 모든 평점을 조회한다.")
    public PagedModel<EntityModel<RatingResponse>> getRatingsBySeries(
            @Parameter(name = "시리즈 ID") @PathVariable Long seriesId,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable) {

        Page<Rating> ratingPage = ratingRepository.findBySeries_SeriesId(seriesId, pageable);

        return assembler.toModel(ratingPage, rating -> {
            RatingResponse response = new RatingResponse();
            response.setUsername(rating.getUser().getUsername());
            response.setSeriesId(rating.getSeries().getSeriesId());
            response.setRating(rating.getRating());

            return EntityModel.of(response,
                    linkTo(methodOn(RatingController.class).getRating(response.getUsername(), response.getSeriesId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).getUser(response.getUsername(), null)).withRel("user"),
                    linkTo(methodOn(SeriesController.class).getSeriesDetail(response.getSeriesId())).withRel("series")
            );
        });
    }


    /**
     * 인증된 사용자의 토큰을 받아서 Rating을 등록한다.
     * @param request
     * @param userDetails
     * @return
     */
    @PostMapping
    @Operation(summary = "평점 등록", description = "인증된 사용자의 토큰을 받아서 평점을 등록한다.")
    public ResponseEntity<EntityModel<RatingResponse>> createRating(
            @RequestBody RatingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) { // 핵심 포인트!

        // 1. 토큰에서 추출한 안전한 username 가져오기
        String authenticatedUsername = userDetails.getUsername();

        // 2. 유저와 시리즈 검증
        User user = userRepository.findById(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new RuntimeException("해당 시리즈를 찾을 수 없습니다."));

        // 3. 평점 저장
        Rating rating = new Rating(user, series, request.getRating());
        Rating savedRating = ratingRepository.save(rating);

        // 4. 응답 DTO 변환
        RatingResponse response = new RatingResponse();
        response.setUsername(savedRating.getUser().getUsername());
        response.setSeriesId(savedRating.getSeries().getSeriesId());
        response.setRating(savedRating.getRating());

        // 5. HATEOAS 링크 달기
        EntityModel<RatingResponse> entityModel = EntityModel.of(response,
                linkTo(methodOn(RatingController.class).getRating(response.getUsername(), response.getSeriesId())).withSelfRel(),
                linkTo(methodOn(RatingController.class).getRatingList(null)).withRel("ratings-list")
        );

        return ResponseEntity
                .created(linkTo(methodOn(RatingController.class).getRating(response.getUsername(), response.getSeriesId())).toUri())
                .body(entityModel);
    }

    /**
     * 평점 수정 (PUT)
     * 예: PUT /ratings/3
     */
    @PutMapping("/{seriesId}")
    @Operation(summary = "평점 수정", description = "자신이 작성한 평점을 수정한다.")
    public ResponseEntity<EntityModel<RatingResponse>> updateRating(
            @PathVariable Long seriesId,
            @RequestBody RatingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. 토큰에서 유저 이름 추출
        String username = userDetails.getUsername();
        RatingId id = new RatingId(username, seriesId);

        // 2. 본인이 작성한 평점이 존재하는지 확인
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("수정할 평점을 찾을 수 없거나 권한이 없습니다."));

        // 3. 평점 값 업데이트 및 저장
        rating.setRating(request.getRating());
        ratingRepository.save(rating);

        // 4. 응답 DTO 변환
        RatingResponse response = new RatingResponse();
        response.setUsername(rating.getUser().getUsername());
        response.setSeriesId(rating.getSeries().getSeriesId());
        response.setRating(rating.getRating());

        // 5. HATEOAS 링크 달기
        EntityModel<RatingResponse> entityModel = EntityModel.of(response,
                linkTo(methodOn(RatingController.class).getRating(username, seriesId)).withSelfRel(),
                linkTo(methodOn(RatingController.class).getRatingList(null)).withRel("ratings-list")
        );

        return ResponseEntity.ok(entityModel);
    }

    /**
     * 평점 삭제 (DELETE)
     * 예: DELETE /ratings/3
     */
    @DeleteMapping("/{seriesId}")
    @Operation(summary = "평점 삭제", description = "자신이 작성한 평점을 삭제한다.")
    public ResponseEntity<?> deleteRating(
            @PathVariable Long seriesId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. 토큰에서 유저 이름 추출
        String username = userDetails.getUsername();
        RatingId id = new RatingId(username, seriesId);

        // 2. 본인이 작성한 평점이 존재하는지 확인
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("삭제할 평점을 찾을 수 없거나 권한이 없습니다."));

        // 3. 데이터 삭제
        ratingRepository.delete(rating);

        // 4. REST 표준에 따라 204 No Content 반환
        return ResponseEntity.noContent().build();
    }
}
