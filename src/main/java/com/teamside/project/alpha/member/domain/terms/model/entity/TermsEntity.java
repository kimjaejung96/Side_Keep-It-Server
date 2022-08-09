package com.teamside.project.alpha.member.domain.terms.model.entity;

import com.teamside.project.alpha.common.model.entity.entitiy.TimeEntity;
import com.teamside.project.alpha.member.model.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "TERMS")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TermsEntity extends TimeEntity {
    @Id
    @Column(name = "MID", columnDefinition = "char(16)")
    private String mid;

    @MapsId
    @OneToOne
    @JoinColumn(name = "MID", referencedColumnName = "MID")
    private MemberEntity member;

    @Column(name = "TERMS", columnDefinition = "boolean")
    private boolean terms;

    @Column(name = "COLLECT", columnDefinition = "boolean")
    private boolean collect;

    @Column(name = "MARKETING", columnDefinition = "boolean")
    private boolean marketing;

    @Column(name = "ALARM", columnDefinition = "boolean")
    private boolean alarm;

}
