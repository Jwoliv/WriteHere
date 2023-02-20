package com.example.WriteHere.service.report;

import com.example.WriteHere.model.report.ReportByPost;
import com.example.WriteHere.repository.report.ReportPostRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportPostService extends ReportService<ReportPostRepository, ReportByPost> {
    protected ReportPostService(ReportPostRepository repository) {
        super(repository);
    }
}
