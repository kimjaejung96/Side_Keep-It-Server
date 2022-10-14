package com.teamside.project.alpha.group.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.util.CryptUtils;
import com.teamside.project.alpha.group.common.dto.CommentDto;
import com.teamside.project.alpha.group.common.dto.QCommentDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.DailyDto;
import com.teamside.project.alpha.group.domain.daily.model.dto.QDailyDto_DailyDetail;
import com.teamside.project.alpha.group.domain.daily.model.dto.QDailyDto_DailyInGroup;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyCommentEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyEntity;
import com.teamside.project.alpha.group.domain.daily.model.entity.QDailyKeepEntity;
import com.teamside.project.alpha.group.domain.review.model.dto.*;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewCommentEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewEntity;
import com.teamside.project.alpha.group.domain.review.model.entity.QReviewKeepEntity;
import com.teamside.project.alpha.group.model.dto.*;
import com.teamside.project.alpha.group.model.entity.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.QGroupEntity;
import com.teamside.project.alpha.group.model.entity.QGroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.QStatReferralGroupEntity;
import com.teamside.project.alpha.group.model.enumurate.MyGroupType;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import com.teamside.project.alpha.member.model.entity.QMemberEntity;
import com.teamside.project.alpha.member.model.entity.QMemberFollowEntity;
import com.teamside.project.alpha.place.model.entity.QPlaceEntity;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GroupRepositoryDSLImpl implements GroupRepositoryDSL {
    private final JPAQueryFactory jpaQueryFactory;

    QReviewKeepEntity reviewKeep = QReviewKeepEntity.reviewKeepEntity;
    QGroupEntity group = QGroupEntity.groupEntity;
    QReviewCommentEntity reviewComment = QReviewCommentEntity.reviewCommentEntity;
    QPlaceEntity place = QPlaceEntity.placeEntity;
    QReviewEntity review = QReviewEntity.reviewEntity;

    QDailyEntity daily = QDailyEntity.dailyEntity;

    QDailyKeepEntity dailyKeep = QDailyKeepEntity.dailyKeepEntity;
    QDailyCommentEntity dailyComment = QDailyCommentEntity.dailyCommentEntity;
    QMemberEntity member = QMemberEntity.memberEntity;
    QGroupMemberMappingEntity groupMemberMapping = QGroupMemberMappingEntity.groupMemberMappingEntity;
    QMemberFollowEntity memberFollow = QMemberFollowEntity.memberFollowEntity;

    QStatReferralGroupEntity statReferralGroup = QStatReferralGroupEntity.statReferralGroupEntity;

    public GroupRepositoryDSLImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<GroupDto.SearchGroupDto> searchGroup(Long lastGroupId, Long pageSize, String search) {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .where(
                        gtGroupId(lastGroupId),
                        containSearch(search)
                )
                .limit(pageSize)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate)
                .orderBy(group.groupId.asc())
                .fetch();
    }

    public BooleanExpression gtGroupId(Long lastGroupId) {
        return lastGroupId != null ? group.groupId.gt(lastGroupId) : null;
    }

    public BooleanExpression containSearch(String search) {
        return (search != null && !Strings.isBlank(search)) ? group.name.contains(search) : null;
    }

    @Override
    public List<GroupDto.MyGroupDto> selectMyGroups(String mId, MyGroupType type) {
        return jpaQueryFactory
                .select(new QGroupDto_MyGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(groupMemberMapping.count())
                                        .from(groupMemberMapping)
                                        .where(groupMemberMapping.groupId.eq(group.groupId)),
                        "participantCount"),
                        groupMemberMapping.favorite,
                        new CaseBuilder()
                                .when(group.master.mid.eq(groupMemberMapping.mid))
                                .then(true)
                                .otherwise(false)
                                .as("isMaster"),
                        groupMemberMapping.ord
                    ))
                .from(groupMemberMapping)
                .innerJoin(group).on(groupMemberMapping.groupId.eq(group.groupId))
                .where(
                        groupMemberMapping.member.mid.eq(mId),
                        isFavorite(type))
                .fetch();
    }

    public BooleanExpression isFavorite(MyGroupType type) {
        return type == MyGroupType.FAVORITE ? groupMemberMapping.favorite.eq(true) : null;
    }
    @Override
    public List<GroupDto.SearchGroupDto> random() {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .orderBy(Expressions.numberTemplate(Long.class,"function('rand')").asc())
                .limit(10)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate)
                .fetch();
    }

    @Override
    public Optional<GroupMemberMappingEntity> selectGroupMemberMappingEntity(String mid, Long groupId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(groupMemberMapping)
                .from(group)
                .innerJoin(groupMemberMapping)
                    .on(group.groupId.eq(groupMemberMapping.groupId))
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.groupId.eq(groupId)))
                .fetchOne());
    }

    @Override
    public Optional<Integer> selectLatestFavoriteOrd(String mid) {
        return Optional.ofNullable(jpaQueryFactory
                .select(groupMemberMapping.ord)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.favorite.eq(true)))
                .orderBy(groupMemberMapping.ord.desc())
                .fetchFirst());
    }

    @Override
    public List<GroupMemberMappingEntity> selectFavoriteMappingGroups(String mid) {
        return jpaQueryFactory
                .select(groupMemberMapping)
                .from(groupMemberMapping)
                .where(groupMemberMapping.mid.eq(mid)
                        .and(groupMemberMapping.favorite.eq(true)))
                .fetch();
    }

    @Override
    public GroupDto.GroupInfoDto selectGroup(Long groupId) {
        BooleanExpression booleanExpression = new CaseBuilder()
                .when(groupMemberMapping.mid.eq(CryptUtils.getMid()).and(groupMemberMapping.favorite.eq(true)))
                .then(1)
                .otherwise(0)
                .sum().eq(1);

        GroupDto.GroupInfoDto groupInfoDto = jpaQueryFactory.select(
                        new QGroupDto_GroupInfoDto(
                                group,
                                group.master.mid,
                                new CaseBuilder()
                                        .when(booleanExpression)
                                        .then(true)
                                        .otherwise(false),
                                groupMemberMapping.count(),
                                review.count()
                        )
                )
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .leftJoin(review).on(group.groupId.eq(review.group.groupId))
                .where(group.groupId.eq(groupId))
                .groupBy(group.groupId)
                .fetchOne();


        if (groupInfoDto == null) throw new CustomRuntimeException(ApiExceptionCode.GROUP_NOT_FOUND);

        Objects.requireNonNull(groupInfoDto).addGroupInfoMembers(
                jpaQueryFactory.select(
                        new QGroupDto_GroupInfoDto_MembersDto(
                                member.name,
                                member.mid,
                                member.profileUrl,
                                new CaseBuilder()
                                        .when(memberFollow.mid.isNotNull())
                                        .then(true)
                                        .otherwise(false)
                                )
                        )
                        .from(groupMemberMapping)
                        .innerJoin(member).on(groupMemberMapping.mid.eq(member.mid))
                        .leftJoin(memberFollow).on(memberFollow.mid.eq(CryptUtils.getMid()).and(memberFollow.targetMid.eq(member.mid)))
//                        .where(groupMemberMapping.groupId.eq(groupId).and(member.mid.ne(CryptUtils.getMid())))
                        .where(groupMemberMapping.groupId.eq(groupId))
                        .fetch()
        );

        return groupInfoDto;
    }

    @Override
    public List<GroupDto.SearchGroupDto> statGroups(String referralType, String category) {
        // STAT_DT , RANK로 정렬
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(statReferralGroup)
                .innerJoin(group).on(statReferralGroup.groupId.eq(group.groupId))
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .where(statReferralGroup.referralType.eq(referralType), statReferralGroup.category.eq(category))
                .limit(10)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate, statReferralGroup.statDt, statReferralGroup.rankNum)
                .orderBy(statReferralGroup.statDt.desc(), statReferralGroup.rankNum.asc())
                .fetch();
    }

    @Override
    public List<GroupDto.SearchGroupDto> selectGroups(Long lastGroupId, Long pageSize) {
        return jpaQueryFactory
                .select(new QGroupDto_SearchGroupDto(
                        group.groupId,
                        group.name,
                        group.category,
                        group.profileUrl,
                        group.usePrivate,
                        groupMemberMapping.count().as("participantCount")))
                .from(group)
                .innerJoin(groupMemberMapping).on(group.groupId.eq(groupMemberMapping.groupId))
                .where(ltGroupId(lastGroupId))
                .limit(pageSize)
                .groupBy(group.groupId, group.name, group.category, group.profileUrl, group.usePrivate)
                .orderBy(group.groupId.desc())
                .fetch();
    }

    public BooleanExpression ltGroupId(Long lastGroupId) {
        return lastGroupId != null ? group.groupId.lt(lastGroupId) : null;
    }

    @Override
    public GroupDto.GroupMemberProfileDto groupMemberProfile(Long groupId, String memberId) {
        // mid - targetMid
        return jpaQueryFactory
                .select(new QGroupDto_GroupMemberProfileDto(
                        member.mid,
                        member.name,
                        new CaseBuilder()
                                .when(memberFollow.mid.isNull())
                                .then(false)
                                .otherwise(true),
                        review.count(),
                        daily.count()
                ))
                .from(member)
                .leftJoin(review)
                    .on(review.master.mid.eq(member.mid).and(review.group.groupId.eq(groupId)))
                .leftJoin(daily)
                    .on(daily.master.mid.eq(member.mid).and(daily.group.groupId.eq(groupId)))
                .leftJoin(memberFollow)
                    .on(memberFollow.mid.eq(member.mid).and(memberFollow.targetMid.eq(CryptUtils.getMid())))
                .where(member.mid.eq(memberId))
                .fetchOne();
    }

    @Override
    public List<ReviewDto.SelectReviewsInGroup> selectReviewsInGroup(Long groupId, String targetId, Long pageSize, Long lastReviewId) {
         return jpaQueryFactory.select(new QReviewDto_SelectReviewsInGroup(
                new QReviewDto_SelectReviewsInGroup_Review(
                        review.reviewId,
                        review.content,
                        review.reviewCommentEntities.size(),
                        review.createTime,
                        review.images,
                        review.reviewKeepEntities.size(),
                        reviewKeep.isNotNull()),
                        new QReviewDto_SelectReviewsInGroup_Member(
                                member.mid,
                                member.name,
                                member.profileUrl
                        ),
                        new QReviewDto_SelectReviewsInGroup_Place(
                                place.placeId,
                                place.placeName,
                                place.roadAddress
                        ))
                 )
                 .from(review)
                 .innerJoin(member).on(review.master.mid.eq(member.mid))
                 .innerJoin(place).on(review.place.placeId.eq(place.placeId))
                 .leftJoin(reviewKeep).on(review.reviewId.eq(reviewKeep.review.reviewId).and(reviewKeep.member.mid.eq(CryptUtils.getMid())))
                 .where(review.group.groupId.eq(groupId), eqReviewMaster(targetId), ltReviewId(lastReviewId))
                 .orderBy(review.reviewId.desc())
                 .limit(pageSize)
                 .fetch();
    }

    @Override
    public List<DailyDto.DailyInGroup> selectDailyInGroup(Long groupId, String targetId, Long pageSize, Long lastDailyId) {
        return jpaQueryFactory
                .select(new QDailyDto_DailyInGroup(
                        daily.dailyId,
                        daily.title,
                        daily.image,
                        member.name,
                        daily.dailyCommentEntities.size(),
                        daily.createTime.stringValue()
                ))
                .from(daily)
                .join(daily.master, member)
                .where(daily.group.groupId.eq(groupId), eqDailyMaster(targetId), ltDailyId(lastDailyId))
                .orderBy(daily.dailyId.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltReviewId(Long lastReviewId) {
        return lastReviewId != null ? review.reviewId.lt(lastReviewId) : null;
    }


    private BooleanExpression eqReviewMaster(String targetId) {
        return targetId != null ? review.master.mid.eq(targetId) : null;
    }

    private BooleanExpression ltDailyId(Long lastDailyId) {
        return lastDailyId != null ? daily.dailyId.lt(lastDailyId) : null;
    }


    private BooleanExpression eqDailyMaster(String targetId) {
        return targetId != null ? daily.master.mid.eq(targetId) : null;
    }

    @Override
    public ReviewDto.ResponseReviewDetail selectReviewDetail(Long groupId, Long reviewId) {
        ReviewDto.ReviewDetail reviewDetail = jpaQueryFactory
                .select(new QReviewDto_ReviewDetail(
                        review, member, place
                ))
                .from(review)
                .innerJoin(member).on(member.mid.eq(review.master.mid))
                .innerJoin(place).on(review.place.placeId.eq(place.placeId))
                .where(review.reviewId.eq(reviewId))
                .fetchFirst();
        List<CommentDto> comments = getReviewComments(reviewId);
        MemberEntity loginMember = jpaQueryFactory.select(member).from(member).where(member.mid.eq(CryptUtils.getMid())).fetchOne();

        return new ReviewDto.ResponseReviewDetail(reviewDetail, comments, loginMember);
    }

    private List<CommentDto> getReviewComments(Long reviewId) {
        List<CommentDto> result = jpaQueryFactory.select(new QCommentDto(reviewComment, member))
                .from(reviewComment)
                .innerJoin(member).on(member.mid.eq(reviewComment.master.mid))
                .where(reviewComment.review.reviewId.eq(reviewId))
                .orderBy(reviewComment.commentId.desc())
                .fetch();

        result.stream()
                .filter(commentDto ->  commentDto.getParentCommentId() != null)
//                .sorted(Comparator.comparing(CommentDto::getCommentId))
                .forEach(comment -> result.stream()
                        .filter(co -> Objects.equals(co.getCommentId(), comment.getParentCommentId()))
                        .forEach(dd -> dd.insertChildComments(comment)));
        result.removeIf(d -> d.getParentCommentId() != null);


        return result;
    }

    public GroupDto.GroupHome selectGroupHome(Long groupId) {

        return null;
    }

    @Override
    public DailyDto.ResponseDailyDetail selectDaily(Long groupId, Long dailyId) {
        String mid = CryptUtils.getMid();

        DailyDto.DailyDetail dailyDetail = jpaQueryFactory
                .select(new QDailyDto_DailyDetail(
                        daily.title,
                        member.mid,
                        member.name,
                        member.profileUrl,
                        daily.createTime.stringValue(),
                        daily.content,
                        daily.image,
                        new CaseBuilder()
                                .when(dailyKeep.keepId.isNull())
                                .then(Boolean.FALSE)
                                .otherwise(Boolean.TRUE)
                        )
                )
                .from(daily)
                .innerJoin(daily.master, member)
                .leftJoin(daily.dailyKeepEntities, dailyKeep).on(dailyKeep.member.mid.eq(mid))
                .where(daily.dailyId.eq(dailyId), daily.group.groupId.eq(groupId))
                .fetchOne();

        List<CommentDto> comments = this.getDailyComments(dailyId);
        MemberEntity loginMember = jpaQueryFactory.select(member).from(member).where(member.mid.eq(mid)).fetchOne();

        return new DailyDto.ResponseDailyDetail(dailyDetail, comments, loginMember);
    }

    public List<CommentDto> getDailyComments(Long dailyId) {
        List<CommentDto> result = jpaQueryFactory
                .select(new QCommentDto(dailyComment, member))
                .from(dailyComment)
                .innerJoin(dailyComment.master, member)
                .where(dailyComment.daily.dailyId.eq(dailyId))
                .orderBy(dailyComment.commentId.desc())
                .fetch();

        result.stream()
                .filter(commentDto ->  commentDto.getParentCommentId() != null)
//                .sorted(Comparator.comparing(CommentDto::getCommentId))
                .forEach(comment -> result.stream()
                        .filter(co -> Objects.equals(co.getCommentId(), comment.getParentCommentId()))
                        .forEach(dd -> dd.insertChildComments(comment)
                        ));
        result.removeIf(d -> d.getParentCommentId() != null);

        return result;
    }
}