package com.example.petstable.domain.report.service;

import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.board.repository.BoardRepository;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.report.dto.response.ReportPostResponse;
import com.example.petstable.domain.report.entity.ReportEntity;
import com.example.petstable.domain.report.repository.ReportRepository;
import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.petstable.domain.board.message.BoardMessage.*;
import static com.example.petstable.domain.member.message.MemberMessage.MEMBER_NOT_FOUND;
import static com.example.petstable.domain.report.message.ReportMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public ReportPostResponse reportPost(Long memberId, Long postId, String reportReason) {

        MemberEntity reporter = memberRepository.findById(memberId)
                .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));

        BoardEntity post = boardRepository.findById(postId)
                .orElseThrow(() -> new PetsTableException(POST_NOT_FOUND.getStatus(), POST_NOT_FOUND.getMessage(), 404));

        createReport(reporter, post, reportReason);

        // 만약 해당 게시글 작성자가 탈퇴한 경우
        if (post.isMemberDeleted()) {

            // 만약 해당 게시글의 신고 횟수가 5회 이상일 경우
            if (post.isReportThresholdExceeded()) {
                clearReportPost(post);
            }

        } else {
            MemberEntity writer = post.getMember();
            validateReporter(reporter, post);
            validatePostThreshold(post);
            writer.increaseReportCount();
        }

        return new ReportPostResponse(post.getReportCount());
    }

    public void createReport(MemberEntity reporter, BoardEntity post, String reportReason) {
        ReportEntity report = new ReportEntity(reporter, post, reportReason);

        // 만약 이미 신고한 게시글일 경우
        if (post.hasAlreadyReported(reporter)) {
            throw new PetsTableException(ALREADY_REPORT.getStatus(), ALREADY_REPORT.getMessage(), 400);
        }

        // 연관 관계 설정
        post.addReport(report);
        report.setPost(post);

        reportRepository.save(report);
    }

    public void validateReporter(MemberEntity reporter, BoardEntity post) {
        if (post.isWrittenBySelf(reporter)) {
            throw new PetsTableException(SELF_REPORT_NOT_ALLOWED.getStatus(), SELF_REPORT_NOT_ALLOWED.getMessage(), 400);
        }
    }

    public void validatePostThreshold(BoardEntity post) {
        if (post.isReportThresholdExceeded()) {
            clearReportPost(post);
        }
    }

    public void clearReportPost(BoardEntity post) {
        post.clearPost();
    }
}
