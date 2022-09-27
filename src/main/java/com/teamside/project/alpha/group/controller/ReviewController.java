package com.teamside.project.alpha.group.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createReview(@Valid @RequestBody ReviewDto review) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        reviewService.createReview(review);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
    @PatchMapping("")
    public ResponseEntity<ResponseObject> updateReview(@Valid @RequestBody ReviewDto.UpdateReviewDto review) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.updateReview(review);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

}
