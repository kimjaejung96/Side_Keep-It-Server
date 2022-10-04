package com.teamside.project.alpha.group.service;

import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.DailyDto;

public interface DailyService {
    void createDaily(Long groupId, DailyDto dailyDto) throws CustomException;
    void updateDaily(Long groupId, DailyDto.UpdateDailyDto dailyDto) throws CustomException;
    void createComment(Long groupId, Long dailyId, CommentDto.CreateComment comment) throws CustomException;
    void keepDaily(Long groupId, Long dailyId);
}