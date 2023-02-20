package com.example.WriteHere.service.report;

import com.example.WriteHere.model.report.ReportByComment;
import com.example.WriteHere.repository.report.ReportCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportCommentService extends ReportService<ReportCommentRepository, ReportByComment> {
    protected ReportCommentService(ReportCommentRepository repository) {
        super(repository);
    }
}
