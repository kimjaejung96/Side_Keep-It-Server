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
    GROUP_NOT_FOUND(800, "그룹을 찾을 수 없습니다."),
    ALREADY_JOINED_GROUP(801, "멤버가 이미 참여하고 있습니다."),
    PASSWORD_IS_INCORRECT(802, "비밀번호가 틀렸습니다."),
    CAN_NOT_PARTICIPANT(803, "10개의 그룹에 참여하여 더이상 참여할 수 없습니다."),
    MEMBER_QUANTITY_IS_FULL(804, "해당 그룹의 멤버 수용 인원이 꽉 찼습니다."),
    INVALID_GROUP_TYPE(805, "유효하지 그룹 타입입니다."),
    GROUP_MEMBER_NOT_FOUND(806, "내 그룹을 찾을 수 없습니다."),
    SYSTEM_ERROR(999, "System Error"),
    ;


    private final int apiCode;
    private final String message;

    ApiExceptionCode(int code, String message) {
        this.apiCode = code;
        this.message = message;
    }
}
