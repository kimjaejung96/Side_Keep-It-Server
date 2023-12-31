package com.teamside.project.alpha.member.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.model.dto.AlarmDto;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@DynamicUpdate
@Table(name = "ALARM_SETTING")
public class AlarmSetting extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @OneToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "ALL_SETTING", columnDefinition = "boolean")
    private boolean allSetting;

    //새 멤버 참여
    @Column(name = "NEW_MEMBER", columnDefinition = "boolean")
    private boolean newMember;

    // 새 리뷰 글
    @Column(name = "NEW_REVIEW", columnDefinition = "boolean")
    private boolean newReview;

    // 새 일상 글
    @Column(name = "NEW_DAILY", columnDefinition = "boolean")
    private boolean newDaily;

    // 댓글 알림
    @Column(name = "COMMENT", columnDefinition = "boolean")
    private boolean comment;

    // 내 글이 킵 됐을때 알림
    @Column(name = "KEEP", columnDefinition = "boolean")
    private boolean keep;

    //탈퇴, 초대 알림
    @Column(name = "JOIN_OUT", columnDefinition = "boolean")
    private boolean joinOut;

    // 팔로우 되었을 때
    @Column(name = "FOLLOW", columnDefinition = "boolean")
    private boolean follow;

    public AlarmSetting(MemberEntity member, boolean allSetting, boolean newMember, boolean newReview, boolean newDaily, boolean comment, boolean keep, boolean joinOut, boolean follow) {
        this.member = member;
        this.allSetting = allSetting;
        this.newMember = newMember;
        this.newReview = newReview;
        this.newDaily = newDaily;
        this.comment = comment;
        this.keep = keep;
        this.joinOut = joinOut;
        this.follow = follow;
    }

    public AlarmSetting() {

    }

    public void updateAlarm(AlarmDto alarmDto) {
        this.allSetting = alarmDto.isAllSetting();
        this.newMember = alarmDto.isNewMember();
        this.newReview = alarmDto.isNewReview();
        this.newDaily = alarmDto.isNewDaily();
        this.comment = alarmDto.isComment();
        this.keep = alarmDto.isKeep();
        this.joinOut = alarmDto.isJoinOut();
        this.follow = alarmDto.isFollow();
    }
}
