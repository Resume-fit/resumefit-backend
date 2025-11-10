package com.resumefit.resumefit_backend.domain.review.repository;

import com.resumefit.resumefit_backend.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
