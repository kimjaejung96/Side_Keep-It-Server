package com.teamside.project.alpha.group.model.entity;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.DailyKeepEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.ReviewKeepEntity;
import com.teamside.project.alpha.group.model.converter.CategoryConverter;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.model.enumurate.Category;
import com.teamside.project.alpha.group.model.enumurate.GroupMemberStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "GROUP_LIST")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupEntity extends TimeEntity {
    @Id
    @Column(name = "GROUP_ID", columnDefinition = "char(36)")
    private String groupId;

    @Column(name = "SEQ", columnDefinition = "BIGINT(20) NOT NULL UNIQUE KEY auto_increment")
    private Long seq;

    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    @Column(name = "DESCRIPTION", columnDefinition = "varchar(200)")
    private String description;

    @Column(name = "PASSWORD", columnDefinition = "varchar(8)")
    private String password;

    @Column(name = "USE_PRIVATE", columnDefinition = "boolean")
    private Boolean usePrivate;

    @Column(name = "MEMBER_QUANTITY", columnDefinition = "int")
    private Integer memberQuantity;

    @Column(name = "PROFILE_URL", columnDefinition = "varchar(255)")
    private String profileUrl;

    @Convert(converter = CategoryConverter.class)
    @Column(name = "CATEGORY", columnDefinition = "varchar(50)")
    private Category category;
    @Column(name = "MASTER_MID", columnDefinition = "char(36)")
    private String masterMid;

    @Column(name = "IS_DELETE", columnDefinition = "boolean")
    private Boolean isDelete;

    @Column(name = "DELETE_DT", columnDefinition = "DATETIME")
    private LocalDateTime deleteDt;

    @OneToMany(mappedBy = "group",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupMemberMappingEntity> groupMemberMappingEntity;

    @OneToMany(mappedBy = "group",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntities;

    @OneToMany(mappedBy = "group",  cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyEntity> dailyEntities;
    @OneToMany(mappedBy = "group",  cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MemberFollowEntity> memberFollowEntities;

    public GroupEntity(GroupDto group) {
        this.groupId = UUID.randomUUID().toString();
        this.name = group.getName();
        this.description = group.getDescription();
        this.password = group.getUsePrivate() ?  group.getPassword() : "";
        this.usePrivate = group.getUsePrivate();
        this.memberQuantity = group.getMemberQuantity();
        this.profileUrl = group.getProfileUrl();
        this.category = group.getCategory();
        this.isDelete = false;

        this.groupMemberMappingEntity = new ArrayList<>();
        setMasterMember();

        this.reviewEntities = new ArrayList<>();
        this.dailyEntities = new ArrayList<>();
        this.memberFollowEntities = new ArrayList<>();
    }

    private void setMasterMember(){
        this.masterMid = CryptUtils.getMid();
    }
    public void addMember(String mid) {
        Optional<GroupMemberMappingEntity> groupMemberMapping = this.getGroupMemberMappingEntity().stream()
                .filter(g -> g.getMid().equals(CryptUtils.getMid()))
                .findAny();

        if (groupMemberMapping.isPresent()) {
            groupMemberMapping.get().updateStatus(GroupMemberStatus.JOIN);
        } else {
            this.groupMemberMappingEntity.add(new GroupMemberMappingEntity(mid, this.groupId));
        }
    }
    public void removeMember(String mid) {
        GroupMemberMappingEntity findEntity = this.groupMemberMappingEntity.stream()
                .filter(entity -> entity.getMid().equals(mid))
                .filter(entity -> entity.getGroupId().equals(this.groupId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));
        this.groupMemberMappingEntity.remove(findEntity);
    }

    public void updateGroup(GroupDto group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.usePrivate = group.getUsePrivate();
        this.password = group.getUsePrivate() ? group.getPassword() : "";
        this.memberQuantity = group.getMemberQuantity();
        this.profileUrl = group.getProfileUrl();
    }

    public void checkJoinPossible(GroupEntity group, String password) {
        if (Boolean.TRUE.equals(group.getUsePrivate())) {
            if (password == null || !password.equals(group.getPassword())) {
                throw new CustomRuntimeException(ApiExceptionCode.PASSWORD_IS_INCORRECT);
            }
        }

        Optional<GroupMemberMappingEntity> groupMemberMapping = group.getGroupMemberMappingEntity().stream()
                .filter(g -> g.getMid().equals(CryptUtils.getMid()))
                .findAny();

        if (groupMemberMapping.isPresent()) {
            GroupMemberStatus status = groupMemberMapping.get().getStatus();

            if (status.equals(GroupMemberStatus.JOIN)) {
                throw new CustomRuntimeException(ApiExceptionCode.ALREADY_JOINED_GROUP);
            } else if (status.equals(GroupMemberStatus.EXILE)) {
                throw new CustomRuntimeException(ApiExceptionCode.EXILED_GROUP);
            }
        }

        long joinMemberCount = group.getGroupMemberMappingEntity().stream()
                .filter(g -> g.getStatus().equals(GroupMemberStatus.JOIN))
                .count();

        if (joinMemberCount >= this.memberQuantity) {
            throw new CustomRuntimeException(ApiExceptionCode.MEMBER_QUANTITY_IS_FULL);
        }
    }

    public String createReview(ReviewEntity review) {
        this.reviewEntities.add(review);
        return review.getReviewId();
    }

    public String createDaily(DailyEntity dailyEntity) {
        this.dailyEntities.add(dailyEntity);
        return dailyEntity.getDailyId();
    }

    public void checkExistReview(long placeId) {
        if (this.reviewEntities
                .stream()
                .filter(place -> placeId == place.getPlace().getPlaceId())
                .anyMatch(reviewEntity -> reviewEntity.getMasterMid().equals(CryptUtils.getMid()))) {
            throw new CustomRuntimeException(ApiExceptionCode.REVIEW_ALREADY_EXIST);
        }
    }

    public void checkExistMember(String mid) {
        if (this.groupMemberMappingEntity.stream()
                .noneMatch(member -> member.getMember().getMid().equals(mid))) {
            throw new CustomRuntimeException(ApiExceptionCode.NOT_PARTICIPANT_IN_GROUP);
        }
    }

    public boolean checkIsMember() {
        return this.groupMemberMappingEntity.stream()
                .anyMatch(m -> m.getMid().equals(CryptUtils.getMid()));
    }

    public GroupEntity(String groupId) {
        this.groupId = groupId;
    }

    public void checkGroupMaster() {
        if (!this.masterMid.equals(CryptUtils.getMid())) {
            throw new CustomRuntimeException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void checkReviewMaster(String reviewId) throws CustomException {
        if (this.getReviewEntities().stream()
                .filter(r -> r.getReviewId().equals(reviewId))
                .noneMatch(r -> r.getMasterMid().equals(CryptUtils.getMid()))) {
            throw new CustomException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void checkDailyMaster(String dailyId) throws CustomException {
        if (this.getDailyEntities().stream()
                .filter(d -> d.getDailyId().equals(dailyId))
                .noneMatch(d -> d.getMasterMid().equals(CryptUtils.getMid()))) {
            throw new CustomException(ApiExceptionCode.FORBIDDEN);
        }
    }

    public void deleteReview(String reviewId) throws CustomException {
        this.checkReviewMaster(reviewId);
        ReviewEntity review = this.getReviewEntities().stream()
                .filter(r-> r.getReviewId().equals(reviewId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.REVIEW_NOT_EXIST));

        review.deleteReview();
    }

    public void deleteDaily(String dailyId) throws CustomException {
        this.checkDailyMaster(dailyId);
        DailyEntity daily = this.getDailyEntities().stream()
                .filter(d -> d.getDailyId().equals(dailyId))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.DAILY_NOT_EXIST));

        daily.deleteDaily();
    }

    public Boolean follow(String groupId, String targetMid) {
        String mid = CryptUtils.getMid();

        Optional<MemberFollowEntity> memberFollowEntity = this.getMemberFollowEntities().stream()
                .filter(d -> d.getMid().equals(mid)
                        && d.getTargetMid().equals(targetMid)
                )
                .findAny();

        if (memberFollowEntity.isEmpty()) {
            this.memberFollowEntities.add(new MemberFollowEntity(groupId, mid, targetMid));
            return true;
        } else {
            memberFollowEntity.get().updateFollowStatus();
            return false;
        }
    }
    public void exileMember(String targetMid) {
        GroupMemberMappingEntity groupMemberMapping = this.groupMemberMappingEntity.stream().filter(d -> d.getMid().equals(targetMid)).findAny().orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));
        groupMemberMapping.updateStatus(GroupMemberStatus.EXILE);

        deleteKeepsInGroup(targetMid);
        deleteFollowsInGroup(targetMid);
    }

    public void deleteGroup() {
        this.isDelete = true;
        this.deleteDt = LocalDateTime.now();
    }

    public void leaveGroup() {
        GroupMemberMappingEntity groupMemberMapping = this.groupMemberMappingEntity.stream()
                .filter(d -> d.getMid().equals(CryptUtils.getMid()))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.MEMBER_NOT_FOUND));

        groupMemberMapping.updateStatus(GroupMemberStatus.EXIT);

        deleteKeepsInGroup(CryptUtils.getMid());
        deleteFollowsInGroup(CryptUtils.getMid());
    }
    private void deleteKeepsInGroup(String targetMid) {
        this.getReviewEntities().stream()
                .filter(f -> f.getGroup().getGroupId().equals(groupId))
                .filter(f -> !f.getIsDelete())
                .forEach(d -> {
                    List<ReviewKeepEntity> reviewKeepList = d.getReviewKeepEntities().stream()
                            .filter(f -> f.getMemberMid().equals(targetMid))
                            .collect(Collectors.toList());
                    d.getReviewKeepEntities().removeAll(reviewKeepList);
                });
        this.getDailyEntities().stream()
                .filter(f -> f.getGroup().getGroupId().equals(groupId))
                .filter(f -> !f.getIsDelete())
                .forEach(d -> {
                    List<DailyKeepEntity> reviewKeepList = d.getDailyKeepEntities().stream()
                            .filter(f -> f.getMemberMid().equals(targetMid))
                            .collect(Collectors.toList());
                    d.getDailyKeepEntities().removeAll(reviewKeepList);
                });
    }
    private void deleteFollowsInGroup(String targetMid) {
        List<MemberFollowEntity> followEntities = this.getMemberFollowEntities().stream()
                .filter(f -> f.getMid().equals(targetMid) ||
                        f.getTargetMid().equals(targetMid))
                .collect(Collectors.toList());

        this.getMemberFollowEntities().removeAll(followEntities);
    }

    public void checkDeletedReview(String reviewId) {
        this.reviewEntities.stream()
                .filter(review -> review.getReviewId().equals(reviewId) && review.getIsDelete())
                .findAny().ifPresent(m -> {throw new CustomRuntimeException(ApiExceptionCode.DELETED_REVIEW);});
    }

    public void updateFollowAlarm(String mid, String targetMid) {
        MemberFollowEntity memberFollowEntity = this.getMemberFollowEntities().stream()
                .filter(d -> d.getMid().equals(mid)
                        && d.getTargetMid().equals(targetMid))
                .findAny()
                .orElseThrow(() -> new CustomRuntimeException(ApiExceptionCode.FOLLOW_NOT_FOUND));

        memberFollowEntity.updateFollowAlarm();
    }

    public void checkDeletedDaily(String dailyId) {
        this.dailyEntities.stream()
                .filter(daily -> daily.getDailyId().equals(dailyId) && daily.getIsDelete())
                .findAny().ifPresent(m -> {throw new CustomRuntimeException(ApiExceptionCode.DELETED_DAILY);});
    }

    public void checkGroupStatus() {
        if (this.isDelete) {
            throw new CustomRuntimeException(ApiExceptionCode.DELETED_GROUP);
        }
    }

    public Long getParticipantCount() {
        Long cnt = this.getGroupMemberMappingEntity().stream()
                .filter(i -> i.getStatus().equals(GroupMemberStatus.JOIN))
                .count();

        return cnt;
    }

    public void inviteJoinPossible(GroupEntity group) {
        // group status
        this.checkGroupStatus();

        // already join
        Optional<GroupMemberMappingEntity> groupMemberMapping = group.getGroupMemberMappingEntity().stream()
                .filter(g -> g.getMid().equals(CryptUtils.getMid()))
                .findAny();

        if (groupMemberMapping.isPresent()) {
            GroupMemberStatus status = groupMemberMapping.get().getStatus();

            if (status.equals(GroupMemberStatus.JOIN)) {
                throw new CustomRuntimeException(ApiExceptionCode.ALREADY_JOINED_GROUP);
            } else if (status.equals(GroupMemberStatus.EXILE)) {
                throw new CustomRuntimeException(ApiExceptionCode.EXILED_GROUP);
            }
        }

        // memberQuantity
        long joinMemberCount = group.getGroupMemberMappingEntity().stream()
                .filter(g -> g.getStatus().equals(GroupMemberStatus.JOIN))
                .count();

        if (joinMemberCount >= this.memberQuantity) {
            throw new CustomRuntimeException(ApiExceptionCode.MEMBER_QUANTITY_IS_FULL);
        }
    }
}
