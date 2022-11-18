package com.teamside.project.alpha.notification.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping
    public ResponseEntity<ResponseObject> getNotifications(){
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        notificationService.getNotifications();
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}