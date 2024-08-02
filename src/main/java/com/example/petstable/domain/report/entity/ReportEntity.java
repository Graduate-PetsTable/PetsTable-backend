package com.example.petstable.domain.report.entity;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.member.entity.BaseTimeEntity;
import com.example.petstable.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@Table(name = "report") @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity reporter;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity post;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_reason")
    private ReportReason reportReason;

    public ReportEntity(MemberEntity reporter, BoardEntity post, String reportReason) {
        this.reporter = reporter;
        this.post = post;
        this.reportReason = ReportReason.from(reportReason);
    }

    public void setPost(BoardEntity post) {
        this.post = post;
        post.getReports().add(this);
    }
}
