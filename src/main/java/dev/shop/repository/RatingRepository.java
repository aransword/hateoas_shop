package dev.shop.repository;

import dev.shop.model.Rating;
import dev.shop.model.id.RatingId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    // 1. 특정 유저(username)가 남긴 평점 목록 페이징 조회
    Page<Rating> findByUser_Username(String username, Pageable pageable);

    // 2. 특정 시리즈(seriesId)에 달린 평점 목록 페이징 조회
    Page<Rating> findBySeries_SeriesId(Long seriesId, Pageable pageable);
}
