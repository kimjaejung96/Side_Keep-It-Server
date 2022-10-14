package com.teamside.project.alpha.group.domain.review.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.domain.review.model.dto.ReviewDto;
import com.teamside.project.alpha.group.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     * @param groupId
     * @param review
     * @return
     */
    @PostMapping("")
    public ResponseEntity<ResponseObject> createReview(@PathVariable Long groupId, @Valid @RequestBody ReviewDto review) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        reviewService.createReview(groupId, review);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 리뷰 삭제
     * @param groupId
     * @param reviewId
     * @return
     * @throws CustomException
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseObject> deleteReview(@PathVariable Long groupId, @PathVariable Long reviewId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.deleteReview(groupId, reviewId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 리뷰 수정(업데이트)
     * @param groupId
     * @param review
     * @return
     */
    @PatchMapping("")
    public ResponseEntity<ResponseObject> updateReview(@PathVariable Long groupId, @Valid @RequestBody ReviewDto.UpdateReviewDto review) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.updateReview(groupId, review);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 리뷰 상세 조회
     * @param reviewId
     * @return
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseObject> selectReviewDetail(@PathVariable Long groupId, @PathVariable Long reviewId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(reviewService.selectReviewDetail(groupId, reviewId));

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }


    /**
     * 리뷰에 댓글, 대댓글 작성 API
     * @param comment 댓글 dto
     *                parentId -> nullable(대댓글 작성용)
     * @param reviewId 댓글 작성할 리뷰 id
     * @return
     */
    @PostMapping("/{reviewId}/comment")
    public ResponseEntity<ResponseObject> createComment(@PathVariable Long groupId, @Valid @RequestBody CommentDto.CreateComment comment, @PathVariable Long reviewId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        reviewService.createComment(groupId, comment, reviewId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 댓글, 대댓글 수정
     * @param groupId
     * @param comment
     * @param reviewId
     * @param commentId
     * @return
     */
    @PatchMapping("/{reviewId}/comment/{commentId}")
    public ResponseEntity<ResponseObject> updateComment(@PathVariable Long groupId, @Valid @RequestBody CommentDto.CreateComment comment, @PathVariable Long reviewId, @PathVariable Long commentId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.updateComment(groupId, comment, reviewId, commentId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 댓글, 대댓글 삭제 (상태값 변경)
     * @param groupId
     * @param reviewId
     * @param commentId
     * @return
     */
    @DeleteMapping("/{reviewId}/comment/{commentId}")
    public ResponseEntity<ResponseObject> deleteComment(@PathVariable Long groupId, @PathVariable Long reviewId, @PathVariable Long commentId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.deleteComment(groupId, reviewId, commentId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    /**
     * 리뷰 킵
     * @param groupId
     * @param reviewId
     * @return
     */
    @PostMapping("/{reviewId}/keep")
    public ResponseEntity<ResponseObject> keepReview(@PathVariable Long groupId, @PathVariable Long reviewId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.keepReview(groupId, reviewId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

}