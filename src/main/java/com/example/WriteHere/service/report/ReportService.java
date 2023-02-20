package com.example.WriteHere.service.report;

import com.example.WriteHere.model.report.AbstractReport;
import com.example.WriteHere.repository.report.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public abstract class ReportService<R extends ReportRepository<T>, T extends AbstractReport> {
    private final R repository;

    @Autowired
    public ReportService(R repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }
    public T findById(Long id) {
        return repository.findById(id).orElse(null);
    }
    @Transactional
    public void save(T element) {
        repository.save(element);
    }
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
