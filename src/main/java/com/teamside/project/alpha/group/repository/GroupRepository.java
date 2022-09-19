package com.teamside.project.alpha.group.repository;

import com.teamside.project.alpha.group.model.domain.GroupMemberMappingEntity;
import com.teamside.project.alpha.group.model.entity.GroupEntity;
import com.teamside.project.alpha.group.repository.dsl.GroupRepositoryDSL;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository  extends JpaRepository<GroupEntity, Long>, GroupRepositoryDSL{
    boolean existsByName(String name);
    Optional<GroupEntity> findByGroupId(Long groupId);
    Long countByGroupMemberMappingEntity(GroupMemberMappingEntity groupMemberMappingEntity);
    Long countByNameContaining(String search);
    Long countByMaster(MemberEntity memberMid);
}
