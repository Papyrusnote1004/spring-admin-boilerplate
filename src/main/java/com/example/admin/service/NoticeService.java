package com.example.admin.service;

import com.example.admin.model.entity.Notice;
import com.example.admin.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public Page<Notice> getNotices(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return noticeRepository.searchByKeyword(keyword.trim(), pageable);
        }
        return noticeRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findActiveById(id);
    }

    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Notice updateNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("notice not found: " + id));
        notice.setDeleted(true);
        noticeRepository.save(notice);
    }

    public void incrementViewCount(Long id) {
        noticeRepository.incrementViewCount(id);
    }
}
