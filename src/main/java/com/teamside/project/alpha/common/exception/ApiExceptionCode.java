package com.teamside.project.alpha.common.exception;

import lombok.Getter;

@Getter
public enum ApiExceptionCode {
    NONE (0,""),
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    INTERNAL_ERROR(500, "Internal Error"),
    DUPLICATE_NAME(600, "이름이 중복입니다."),
    DUPLICATE_PHONE(601, "휴대폰 번호가 중복입니다."),
    MEMBER_NOT_FOUND(700, "멤버를 찾을 수 없습니다."),
    AUTH_FAIL(701, "인증 실패"),
    MEMBER_ALREADY_EXIST(702, "멤버가 이미 존재합니다."),
    INVALID_AUTH_TYPE(703, "유효하지 않은 인증 타입입니다."),
    MEMBER_IS_WITHDRAWAL(704, "탈퇴한 멤버입니다."),

    GROUP_NOT_FOUND(800, "그룹을 찾을 수 없습니다."),
    ALREADY_JOINED_GROUP(801, "멤버가 이미 참여하고 있습니다."),
    PASSWORD_IS_INCORRECT(802, "비밀번호가 틀렸습니다."),
    CAN_NOT_PARTICIPANT(803, "20개의 그룹에 참여하여 더이상 참여할 수 없습니다."),
    MEMBER_QUANTITY_IS_FULL(804, "해당 그룹의 멤버 수용 인원이 꽉 찼습니다."),
    INVALID_GROUP_TYPE(805, "유효하지 그룹 타입입니다."),
    GROUP_MEMBER_NOT_FOUND(806, "내 그룹을 찾을 수 없습니다."),
    CAN_NOT_CREATE_GROUP(807, "10개 그룹을 생성하여 더이상 생성할 수 없습니다."),
    INVALID_GROUP_REQUEST(808, "잘못된 그룹 요청입니다."),
    DUPLICATE_ORD(809, "중복된 순서가 존재합니다."),
    GROUP_IS_NOT_MATCH(810, "내 그룹이 아닌 그룹이 존재합니다."),
    REVIEW_ALREADY_EXIST(811, "리뷰가 이미 존재합니다."),
    PLACE_NOT_EXIST(812, "장소가 존재하지 않습니다."),
    NOT_PARTICIPANT_IN_GROUP(813, "그룹에 속해있지 않습니다."),
    REVIEW_NOT_EXIST(814, "리뷰가 존재하지 않습니다."),
    DAILY_NOT_EXIST(815, "일상글이 존재하지 않습니다."),
    COMMENT_NOT_ACCESS(816, "대댓글 댓글의 리뷰가 아닙니다."),
    COMMENT_NOT_EXIST(817, "댓글이 존재하지 않습니다."),
    EXILED_GROUP(818, "해당 그룹은 그룹장에 의해 강제 탈퇴 되어 참여할 수 없는 그룹입니다."),
    DELETED_REVIEW(819, "삭제된 리뷰입니다."),
    INVALID_ALARM_TYPE(820, "유효하지 않는 알람 타입입니다."),
    FOLLOW_NOT_FOUND(821, "팔로우를 찾을 수 없습니다."),
    DELETED_DAILY(822, "삭제된 일상입니다."),
    DELETED_GROUP(823, "삭제된 그룹입니다."),
    INVALID_VIEW_TYPE(824, "유효하지 않는 타입입니다."),

    NOTICE_NOT_FOUND(825, "공지사항을 찾을 수 없습니다."),
    GROUP_CHECK_FORBIDDEN(826, "그룹 접근 권한이 없습니다."),

    INVITE_NOT_EXIST(827, "초대장이 존재하지 않습니다."),
    INVITE_EXPIRED(828, "만료된 초대장입니다."),
    INVITE_IS_NOT_MATCH(829, "요청 정보와 초대장 정보가 매칭되지 않습니다."),


    FORBIDDEN_NAME(995, "이름에 금지된 단어가 포함되어 있습니다."),
    IMAGE_CONTENT_TYPE_INVALID(996, "이미지 확장자가 올바르지 않습니다"),
    IMAGE_TYPE_INVALID(997, "이미지 타입이 올바르지 않습니다."),
    VALIDATION_ERROR(998, "밸리데이션이 올바르지 않습니다."),
    SYSTEM_ERROR(999, "System Error"),
    ;


    private final int apiCode;
    private final String message;

    ApiExceptionCode(int code, String message) {
        this.apiCode = code;
        this.message = message;
    }
}
