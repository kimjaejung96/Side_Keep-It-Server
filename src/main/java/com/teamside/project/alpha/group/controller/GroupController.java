package com.teamside.project.alpha.group.controller;


import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.Category;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.group.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createGroup(@RequestBody @Valid GroupDto group) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        groupService.createGroup(group);

        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }

    @PostMapping("/{groupId}")
    public ResponseEntity<ResponseObject> joinGroup(@PathVariable Long groupId, @RequestParam(required = false) String password) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.joinGroup(groupId, password);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ResponseObject> deleteGroup(@PathVariable Long groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.deleteGroup(groupId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @GetMapping("/{groupId}")
    public ResponseEntity<ResponseObject> selectGroup(@PathVariable Long groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroup(groupId));

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PatchMapping
    public ResponseEntity<ResponseObject> updateGroup(@RequestBody @Valid GroupDto group) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.updateGroup(group);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @GetMapping("/{groupName}/exists")
    public ResponseEntity<ResponseObject> isExistGroupName(@Pattern (regexp = KeepitConstant.REGEXP_GROUP_NAME)@PathVariable String groupName) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.isExistGroupName(groupName);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseObject> selectGroups(@RequestParam(required = false) Long groupId,
                                                       @RequestParam Long pageSize) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroups(groupId, pageSize));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchGroups(@RequestParam(required = false) Long groupId,
                                                       @RequestParam Long pageSize,
                                                       @RequestParam String search) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.searchGroup(groupId, pageSize, search));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/my-groups")
    public ResponseEntity<ResponseObject> selectMyGroups(@RequestParam MyGroupType type) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectMyGroups(type));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{groupId}/favorite")
    public ResponseEntity<ResponseObject> editFavorite(@PathVariable Long groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.editFavorite(groupId);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PostMapping("/{groupId}/members/{memberName}")
    public ResponseEntity<ResponseObject> editFavorite(@PathVariable Long groupId,
                                                       @PathVariable String memberName) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.inviteMember(groupId, memberName);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/random")
    public ResponseEntity<ResponseObject> random(){
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.random());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
